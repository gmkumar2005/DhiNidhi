package bugcracker

import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import io.circe.parser._
import io.circe.syntax._
import models.{Bgbug, ESQuery, SearchResult}
import org.scalajs.dom.Node
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
case class Foo(name: String)
object BGSearchClient {
  val searchResults = Var(SearchResult(0, Seq.empty))
  val bugSeq = Vars.empty[Bgbug]
  @dom def search(): Binding[Node] = {
    val searchKeyWord = SearchBar.searchWord.bind
    println("BGSearchClient : searchKeyword has changed")
    val headers = Map("content-type" -> "application/json")
    val esQuery = ESQuery(searchKeyWord, 0, 10)
    if (!searchKeyWord.isEmpty) {
      FutureBinding(Ajax.post(
        "bugcracker/searchBG",
        esQuery.asJson.toString(), 0
      )).bind match {
        case None =>
        case Some(Success(response)) =>
          val r1 = decode[SearchResult](response.responseText)
          searchResults.value = r1.getOrElse(SearchResult(0, Seq.empty))
        case Some(Failure(exception)) => val e1 = "error"
      }
    }
    <div></div>
  }

}
