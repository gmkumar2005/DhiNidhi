
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
//import com.sksamuel.elastic4s.http
import com.sksamuel.elastic4s.http.ElasticDsl._
import models.Bgbug
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.search.{SearchHit, SearchResponse}
import io.circe
import io.circe.generic.auto._
import io.circe.syntax._
//import models.{Application, Bgbug, ESQuery, SearchResult}
import io.circe.generic.auto._
import io.circe.syntax._
import play.api._
import play.api.libs.circe.Circe
import play.api.mvc._
import io.circe.parser.decode
import cats._
import cats.data._
import cats.implicits._

import scala.concurrent.Future
//import BgbugReader._
//import BgbugReader._
import cats.syntax.either._

val client = HttpClient(ElasticsearchClientUri("10.91.10.13", 9200))
val query = search("defects") query "test" limit {
  2
}
val bglist :Future[Either[RequestFailure, RequestSuccess[SearchResponse]]] =  client.execute(query)
val sentence = "GUI- note fails to add to notes record listing after clicking 'apply' when trying to cancel payment or to add manual FX rates or Fees (Retrofit of Defect #184108)"
val stopWords = Set("you", "to")
val trimmedSentance = sentence.trim.replaceAll(" +", " ").replaceAll("[^A-Za-z0-9\\s]", "")
val wordList = trimmedSentance.split(" ")
val cleanList = wordList.filter(!stopWords.contains(_))
cleanList.mkString(" ")

//List("hello", "to", "yes") filter (!testWords.contains(_))