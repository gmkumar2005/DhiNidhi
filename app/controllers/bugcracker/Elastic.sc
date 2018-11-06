
import com.sksamuel.elastic4s.{ElasticsearchClientUri, Hit, HitReader}
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
import org.apache.http.impl.execchain.MainClientExec
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
import scala.concurrent.ExecutionContext.Implicits.global
//import BgbugReader._
//import BgbugReader._
import cats.syntax.either._

val client = HttpClient(ElasticsearchClientUri("10.91.10.13", 9200))
val query = search("defects") query "39110" limit {
  2
}
implicit object BgbugReader extends HitReader[Bgbug] {
  override def read(hit: Hit): Either[Throwable, Bgbug] = {
    Right(Bgbug(
      hit.id,
      hit.sourceAsMap.getOrElse("Detected on Date", "Empty").toString,
      hit.sourceAsMap.getOrElse("Modif_to_Reopen", "Empty").toString,
      hit.sourceAsMap.getOrElse("Bucket", "Empty").toString,
      hit.sourceAsMap.getOrElse("Time_Stamp_New", "Empty").toString,
      hit.sourceAsMap.getOrElse("Affected Fields", "Empty").toString,
      hit.sourceAsMap.getOrElse("Actual Fix Time", "Empty").toString,
      hit.sourceAsMap.getOrElse("Closing Date", "Empty").toString,
      hit.sourceAsMap.getOrElse("Prior_Fixed", "Empty").toString,
      hit.sourceAsMap.getOrElse("FRSD Name", "Empty").toString,
      hit.sourceAsMap.getOrElse("Main Project", "Empty").toString,
      hit.sourceAsMap.getOrElse("Subject", "Empty").toString,
      hit.sourceAsMap.getOrElse("Comments", "Empty").toString,
      hit.sourceAsMap.getOrElse("Original Detected in Project", "Empty").toString,
      hit.sourceAsMap.getOrElse("Delivery Date", "Empty").toString,
      hit.sourceAsMap.getOrElse("Detected By", "Empty").toString,
      hit.sourceAsMap.getOrElse("Close Counter Internal", "Empty").toString,
      hit.sourceAsMap.getOrElse("Sub Status", "Empty").toString,
      hit.sourceAsMap.getOrElse("QA (last)", "Empty").toString,
      hit.sourceAsMap.getOrElse("Previous Group", "Empty").toString,
      hit.sourceAsMap.getOrElse("Description", "Empty").toString,
      hit.sourceAsMap.getOrElse("Technical Component", "Empty").toString,
      hit.sourceAsMap.getOrElse("Product/Project", "Empty").toString,
      hit.sourceAsMap.getOrElse("Testing Phase", "Empty").toString,
      hit.sourceAsMap.getOrElse("ReTest Failed Counter", "Empty").toString,
      hit.sourceAsMap.getOrElse("FRSD Type", "Empty").toString,
      hit.sourceAsMap.getOrElse("Detected in time", "Empty").toString,
      hit.sourceAsMap.getOrElse("Defect ID", "Empty").asInstanceOf[Int],
      hit.sourceAsMap.getOrElse("View", "Empty").toString,
      hit.sourceAsMap.getOrElse("Open_date", "Empty").toString,
      hit.sourceAsMap.getOrElse("Number of 'ReOpen'", "Empty").toString,
      hit.sourceAsMap.getOrElse("Detected in Project", "Empty").toString,
      hit.sourceAsMap.getOrElse("Group", "Empty").toString,
      hit.sourceAsMap.getOrElse("Customer", "Empty").toString,
      hit.sourceAsMap.getOrElse("Priority", "Empty").toString,
      hit.sourceAsMap.getOrElse("Is_Old", "Empty").toString,
      hit.sourceAsMap.getOrElse("Prior Defect Status", "Empty").toString,
      hit.sourceAsMap.getOrElse("Prevent Copy", "Empty").toString,
      hit.sourceAsMap.getOrElse("Time_Stamp_Old", "Empty").toString,
      hit.sourceAsMap.getOrElse("All Relevant Info Supplied", "Empty").toString,
      hit.sourceAsMap.getOrElse("Check Field", "Empty").toString,
      hit.sourceAsMap.getOrElse("Detected in Release", "Empty").toString,
      hit.sourceAsMap.getOrElse("NFR  Rejected by Fund", "Empty").toString,
      hit.sourceAsMap.getOrElse("Last Leg", "Empty").toString,
      hit.sourceAsMap.getOrElse("Product", "Empty").toString,
      hit.sourceAsMap.getOrElse("Assigned To", "Empty").toString,
      hit.sourceAsMap.getOrElse("Summary", "Empty").toString,
      hit.sourceAsMap.getOrElse("Close Counter External", "Empty").toString,
      hit.sourceAsMap.getOrElse("Developer (last)", "Empty").toString,
      hit.sourceAsMap.getOrElse("Severity", "Empty").toString,
      hit.sourceAsMap.getOrElse("Modified", "Empty").toString,
      hit.sourceAsMap.getOrElse("Status", "Empty").toString,
      hit.sourceAsMap.getOrElse("Type", "Empty").toString,
      hit.sourceAsMap.getOrElse("Raised By", "Empty").toString
    )
    )
  }
}
val bglist :Future[Either[RequestFailure, RequestSuccess[SearchResponse]]] =  client.execute(query)
val sentence = "GUI- note fails to add to notes record listing after clicking 'apply' when trying to cancel payment or to add manual FX rates or Fees (Retrofit of Defect #184108)"
val stopWords = Set("you", "to")
val trimmedSentance = sentence.trim.replaceAll(" +", " ").replaceAll("[^A-Za-z0-9\\s]", "")
val wordList = trimmedSentance.split(" ")
val cleanList = wordList.filter(!stopWords.contains(_))
cleanList.mkString(" ")

val resp = client.execute(query).map {
      case Left(s) => s.asJson
      case Right(i) => {
//        i.result.to[Bgbug]
        i.result.hits.hits.head.sourceAsString
      }
    }.await

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
  val trimmedSentance = sentence.trim.replaceAll(" +", " ").replaceAll("[^A-Za-z0-9\\s]", "").toLowerCase
  val wordList = trimmedSentance.split(" ")
  val cleanList = wordList.filter(!stopWords.contains(_))
  cleanList.mkString(" ")
}

val cleanSentance = removeStopWords(resp.toString)
//List("hello", "to", "yes") filter (!testWords.contains(_))