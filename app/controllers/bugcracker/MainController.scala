package controllers.bugcracker

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.circe._
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{get => _, search => _, _}
import com.sksamuel.elastic4s.http.search.{SearchHit, SearchResponse}
import controllers.BaseController
import io.circe
import io.circe.generic.auto._
import io.circe.syntax._
import models.{Application, Bgbug, ESQuery, SearchResult}
import io.circe.generic.auto._
import io.circe.syntax._
import play.api._
import play.api.libs.circe.Circe
import play.api.mvc._
import io.circe.parser.decode
import com.sksamuel.elastic4s.circe._
import cats._
import cats.data._
import cats.implicits._
import util.Logging
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.searches.sort.SortOrder
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future
//import BgbugReader._
//import BgbugReader._
//import BgbugReader.BgbugReader
import cats.syntax.either._
import cats.Show
import scala.reflect.internal.util.Collections._

@javax.inject.Singleton
class MainController @javax.inject.Inject() (override val app: Application, config: Configuration, appLifecycle: ApplicationLifecycle) extends BaseController("main") {

  import app.contexts.webContext
  val elasticServer = config.get[String]("dhiNidhi.elasticServer")
  val elasticServerPort = config.get[Int]("dhiNidhi.elasticServerPort")

  val client = HttpClient(ElasticsearchClientUri(elasticServer, elasticServerPort))
  val detailsclient = HttpClient(ElasticsearchClientUri(elasticServer, elasticServerPort))
  val searchbgclient = HttpClient(ElasticsearchClientUri(elasticServer, elasticServerPort))
  val nfrclient = HttpClient(ElasticsearchClientUri(elasticServer, elasticServerPort))
  val relatedclient = HttpClient(ElasticsearchClientUri(elasticServer, elasticServerPort))
  val closedclient = HttpClient(ElasticsearchClientUri(elasticServer, elasticServerPort))

  val esIndex = config.get[String]("dhiNidhi.index")

  appLifecycle.addStopHook { () =>
    Future.successful(client.close())
  }

  //  val esIndex = "defects"
  def index = withSession("admin.index", admin = true) { implicit request => implicit td =>
    Future.successful(Ok(views.html.bugcracker.index(request.identity, app.config.debug)))
  }

  def searchES = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader

    val limit = 10
    val query = search(esIndex) query "test" limit {
      limit
    }

    val resp = client.execute(query).map {
      case Left(s) => NotFound
      case Right(i) => {
        Ok(SearchResult(i.result.totalHits, i.result.to[Bgbug]).asJson)
      }
    }
    resp

  }
  }

  def getbyid(id: String) = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader

    val resp = detailsclient.execute {
      get(id) from esIndex
    }.map {
      case Left(s) => NotFound
      case Right(i) => {
        Ok(i.result.to[Bgbug].asJson)
      }
    }
    //        Future.successful(Ok(foo.asJson))
    resp
  }
  }

  def searchBG = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader

    val esQuery = decode[ESQuery](request.body.asText.getOrElse("{}"))
    val recievedEsQuery = esQuery.getOrElse(ESQuery("", 0, 15))
    val query = search(esIndex) query { recievedEsQuery.query } limit { recievedEsQuery.limit } start
      { recievedEsQuery.start } sortByFieldDesc ("Defect ID")

    val resp = searchbgclient.execute(query).map {
      case Left(s) => NotFound
      case Right(i) => {
        val distinctItems = distinctBy(i.result.to[Bgbug].toList)((x) => x.`Defect ID`).take(10).sortBy(_.`Defect ID`).reverse
        Ok(SearchResult(i.result.totalHits, distinctItems).asJson)
      }
    }

    resp
  }
  }
  def searchRelated = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader

    // start of chaining
    val esQuery = decode[ESQuery](request.body.asText.getOrElse("{}"))
    val recievedEsQuery = esQuery.getOrElse(ESQuery("", 0, 10))
    // dont remove    val queryFuture: Future[Either[RequestFailure, RequestSuccess[SearchResponse]]] = client.execute(query)

    val relatedEsResults = for {
      r1 <- EitherT(relatedclient.execute(get(recievedEsQuery.query) from esIndex))
      r2 <- {
        val fullDoc = r1.result.to[Bgbug]
        val releatedEsQuery = search(esIndex) query removeStopWords(fullDoc.`Description` + " " + fullDoc.`Summary`) limit { 15 }
        EitherT(relatedclient.execute(releatedEsQuery))
      }
    } yield r2

    val relatedSearchResults = relatedEsResults.value.map {
      case Left(s) => NotFound
      case Right(i) => {
        val distinctItems = distinctBy(i.result.to[Bgbug].toList)((x) => x.`Defect ID`).slice(1, 10)
        /** .sortBy(_.`Defect ID`).reverse */
        Ok(SearchResult(i.result.totalHits, distinctItems).asJson)
      }
    }

    // end of  chaining

    relatedSearchResults
  }
  }

  def searchRelatedNFR = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader

    // start of chaining
    val esQuery = decode[ESQuery](request.body.asText.getOrElse("{}"))
    val recievedEsQuery = esQuery.getOrElse(ESQuery("", 0, 10))
    //    val queryFuture: Future[Either[RequestFailure, RequestSuccess[SearchResponse]]] = client.execute(query)

    val relatedEsResults = for {
      r1 <- EitherT(nfrclient.execute(get(recievedEsQuery.query) from esIndex))
      r2 <- {
        val fullDoc = r1.result.to[Bgbug]
        val relatedEsNFRQuery = search(esIndex) query """ Status.keyword:"Closed NFR" AND """ + removeStopWords(fullDoc.`Summary` + " " + fullDoc.`Description`) limit { 10 }
//        println(nfrclient.show(relatedEsNFRQuery))
        log.debug("relatedEsNFRQuery")
        log.debug(nfrclient.show(relatedEsNFRQuery))
        EitherT(nfrclient.execute(relatedEsNFRQuery))
      }
    } yield r2

    val relatedSearchResults = relatedEsResults.value.map {
      case Left(s) => NotFound
      case Right(i) => {
        val distinctItems = distinctBy(i.result.to[Bgbug].toList)((x) => x.`Defect ID`).take(10)
        /** .sortBy(_.`Defect ID`) */
        Ok(SearchResult(i.result.totalHits, distinctItems).asJson)

      }
    }
    // end of  chaining
    relatedSearchResults
  }
  }

  def searchRelatedClosed = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader

    // start of chaining
    val esQuery = decode[ESQuery](request.body.asText.getOrElse("{}"))
    val recievedEsQuery = esQuery.getOrElse(ESQuery("", 0, 10))
    //    val queryFuture: Future[Either[RequestFailure, RequestSuccess[SearchResponse]]] = client.execute(query)

    val relatedEsResults = for {
      r1 <- EitherT(closedclient.execute(get(recievedEsQuery.query) from esIndex))
      r2 <- {
        val fullDoc = r1.result.to[Bgbug]
        val relatedEsNFRQuery = search(esIndex) query """ Status.keyword:"Closed" AND """ + removeStopWords(fullDoc.`Summary` + " " + fullDoc.`Description`) limit { 10 }
//        println(closedclient.show(relatedEsNFRQuery))
        log.debug("relatedEsNFRQuery")
        log.debug(closedclient.show(relatedEsNFRQuery))
        EitherT(closedclient.execute(relatedEsNFRQuery))
      }
    } yield r2

    val relatedSearchResults = relatedEsResults.value.map {
      case Left(s) => NotFound
      case Right(i) => {
        val distinctItems = distinctBy(i.result.to[Bgbug].toList)((x) => x.`Defect ID`).take(10)
        /** .sortBy(_.`Defect ID`)*/
        Ok(SearchResult(i.result.totalHits, distinctItems).asJson)

      }
    }
    // end of  chaining
    relatedSearchResults
  }
  }

  def removeStopWords(sentence: String): String = {
    val stopWords = Set("gpp", "msg", "payment")
    val trimmedSentence = sentence.filter(_ >= ' ').trim.replaceAll(" +", " ").replaceAll("[^A-Za-z0-9\\s]", "").toLowerCase
    val wordList = trimmedSentence.split(" ")
    val cleanList = wordList.filter(!stopWords.contains(_))
    cleanList.mkString(" ")
  }

  val concatHits: (String, SearchHit) => String = (a, b) => a + b.sourceAsString

  def serverStatus = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader
    val limit = 10
    val query = search(esIndex) query "test" limit {
      limit
    }
    val resp = client.execute(query).map {
      case Left(s) => NotFound
      case Right(i) => {
        Ok(SearchResult(i.result.totalHits, i.result.to[Bgbug]).asJson)
      }
    }
    resp
  }
  }

}

