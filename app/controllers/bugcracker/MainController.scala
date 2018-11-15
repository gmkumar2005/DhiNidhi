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

import scala.concurrent.Future
//import BgbugReader._
//import BgbugReader._
//import BgbugReader.BgbugReader
import cats.syntax.either._
import cats.Show
import scala.reflect.internal.util.Collections._

@javax.inject.Singleton
class MainController @javax.inject.Inject() (override val app: Application) extends BaseController("main") {

  import app.contexts.webContext
  val client = HttpClient(ElasticsearchClientUri("10.91.10.13", 9200))
  def index = withSession("admin.index", admin = true) { implicit request => implicit td =>
    Future.successful(Ok(views.html.bugcracker.index(request.identity, app.config.debug)))

  }

  def searchES = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader

    val limit = 10
    val query = search("defects") query "test" limit {
      limit
    }

    val resp = client.execute(query).map {
      case Left(s) => Ok(s.asJson)
      case Right(i) => {
        Ok(SearchResult(i.result.totalHits, i.result.to[Bgbug]).asJson)
      }
    }
    resp

  }
  }

  def getbyid(id: String) = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader
    val client = HttpClient(ElasticsearchClientUri("10.91.10.13", 9200))

    val resp = client.execute {
      get(id) from "defects"
    }.map {
      case Left(s) => Ok(s.asJson)
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
    val client = HttpClient(ElasticsearchClientUri("10.91.10.13", 9200))
    val query = search("defects") query { recievedEsQuery.query } limit { recievedEsQuery.limit } start { recievedEsQuery.start }

    val resp = client.execute(query).map {
      case Left(s) => Ok(s.asJson)
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
      r1 <- EitherT(client.execute(get(recievedEsQuery.query) from "defects"))
      r2 <- {
        val fullDoc = r1.result.to[Bgbug]
        val releatedEsQuery = search("defects") query removeStopWords(fullDoc.`Description` + fullDoc.`Summary`) limit { 15 }
        EitherT(client.execute(releatedEsQuery))
      }
    } yield r2

    val relatedSearchResults = relatedEsResults.value.map {
      case Left(s) => Ok(s.asJson)
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
      r1 <- EitherT(client.execute(get(recievedEsQuery.query) from "defects"))
      r2 <- {
        val fullDoc = r1.result.to[Bgbug]
        val relatedEsNFRQuery = search("defects") query appendStatusQuery(fullDoc.`Summary`) limit { 10 }
        //        println(client.show(relatedEsNFRQuery))
        log.debug("relatedEsNFRQuery")
        log.debug(client.show(relatedEsNFRQuery))
        EitherT(client.execute(relatedEsNFRQuery))
      }
    } yield r2

    val relatedSearchResults = relatedEsResults.value.map {
      case Left(s) => Ok(s.asJson)
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
      r1 <- EitherT(client.execute(get(recievedEsQuery.query) from "defects"))
      r2 <- {
        val fullDoc = r1.result.to[Bgbug]
        val relatedEsNFRQuery = search("defects") query removeStopWords(fullDoc.`Summary`) + """ AND Status: "Closed" """ limit { 10 }
        //        println(client.show(relatedEsNFRQuery))
        log.debug("relatedEsNFRQuery")
        log.debug(client.show(relatedEsNFRQuery))
        EitherT(client.execute(relatedEsNFRQuery))
      }
    } yield r2

    val relatedSearchResults = relatedEsResults.value.map {
      case Left(s) => Ok(s.asJson)
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

  def appendStatusQuery(queryWords: String): String = {
    removeStopWords(queryWords) + """ AND Status: "Closed NFR" """
  }
  def removeStopWords(sentence: String): String = {
    val stopWords = Set("gpp", "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "youre", "youve", "youll",
      "youd", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "shes", "her", "hers",
      "herself", "it", "its", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom",
      "this", "that", "thatll", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
      "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with",
      "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on",
      "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few",
      "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don",
      "dont", "should", "shouldve", "now", "d", "ll", "m", "o", "re", "ve", "y", "ain", "aren", "arent", "couldn", "couldnt", "didn", "didnt", "doesn", "doesnt",
      "hadn", "hadnt", "hasn", "hasnt", "haven", "havent", "isn", "isnt", "ma", "mightn", "mightnt", "mustn", "mustnt", "needn", "neednt", "shan", "shant",
      "shouldn", "shouldnt", "wasn", "wasnt", "weren", "werent", "won", "wont", "wouldn", "wouldnt")
    val trimmedSentance = sentence.filter(_ >= ' ').trim.replaceAll(" +", " ").replaceAll("[^A-Za-z0-9\\s]", "").toLowerCase
    val wordList = trimmedSentance.split(" ")
    val cleanList = wordList.filter(!stopWords.contains(_))
    cleanList.mkString(" ")
  }

  val concatHits: (String, SearchHit) => String = (a, b) => a + b.sourceAsString

  def serverStatus = withSession("admin.index", admin = true) { implicit request => implicit td => {
    import BgbugReader.BgbugReader
    val client = HttpClient(ElasticsearchClientUri("10.91.10.13", 9200))
    val limit = 10
    val query = search("defects") query "test" limit {
      limit
    }
    val resp = client.execute(query).map {
      case Left(s) => Ok(s.asJson)
      case Right(i) => {
        Ok(SearchResult(i.result.totalHits, i.result.to[Bgbug]).asJson)
      }
    }
    resp
  }
  }

}

