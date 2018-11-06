package bugcracker

import bugcracker.NavBarComponent.{NavbarMenuItem, route}
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import io.circe.parser.decode
import models.{Bgbug, ESQuery, SearchResult}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.{Event, Node, window}
import util.Logging
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.jquery.JQueryStatic

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.util.{Failure, Success}

@js.native
@JSImport("jquery", JSImport.Namespace)
object jquery extends JQueryStatic

object BgDetails {
  val bgDoc = Var(Bgbug("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
    "", "", "", "", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
  val relatedResults = Var(SearchResult(0, Seq.empty))
  val relatedNfrResults = Var(SearchResult(0, Seq.empty))

  @dom def getSingleDoc(): Binding[Node] = {
    val navItem = NavBarComponent.route.state.bind.navBarName
    val navHash = NavBarComponent.route.state.bind.hash
    val docId = navHash.substring(navHash.indexOf("""?""") + 1)
    if (navItem == "BGBug Details") {
      FutureBinding(Ajax.get(
        "bugcracker/bg/" + docId
      )).bind match {
        case None =>
        case Some(Success(response)) =>
          val r1 = decode[Bgbug](response.responseText)
          bgDoc.value = r1.getOrElse(Bgbug("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        case Some(Failure(exception)) => val e1 = "error"
      }
    }
    <div></div>
  }

  @dom def searchRelated(): Binding[Node] = {
    val navItem = NavBarComponent.route.state.bind.navBarName
    val navHash = NavBarComponent.route.state.bind.hash
    val docId = navHash.substring(navHash.indexOf("""?""") + 1)
    val esQuery = ESQuery(docId, 0, 5)
    if (navItem == "BGBug Details") {
      FutureBinding(Ajax.post(
        "bugcracker/searchRelated",
        esQuery.asJson.toString(), 0
      )).bind match {
        case None =>
        case Some(Success(response)) =>
          val r1 = decode[SearchResult](response.responseText)
          relatedResults.value = r1.getOrElse(SearchResult(0, Seq.empty))
        case Some(Failure(exception)) => Logging.error("BgDetails.scala searchRelated : " + exception.getMessage)
      }
    }
    <div></div>
  }

  @dom def searchRelatedNFR(): Binding[Node] = {
    val navItem = NavBarComponent.route.state.bind.navBarName
    val navHash = NavBarComponent.route.state.bind.hash
    val docId = navHash.substring(navHash.indexOf("""?""") + 1)
    val esQuery = ESQuery(docId, 0, 5)
    if (navItem == "BGBug Details") {
      FutureBinding(Ajax.post(
        "bugcracker/searchRelatedNFR",
        esQuery.asJson.toString(), 0
      )).bind match {
        case None =>
        case Some(Success(response)) =>
          val r1 = decode[SearchResult](response.responseText)
          relatedNfrResults.value = r1.getOrElse(SearchResult(0, Seq.empty))
        case Some(Failure(exception)) => Logging.error("BgDetails.scala searchRelated : " + exception.getMessage)
      }
    }
    <div></div>
  }

  @dom def bgContent: Binding[Node] =

    <main class="mt-5">
      <div class="container-fluid indigo lighten-5 ">
        <div class="row wow fadeIn" data:data-wow-delay="0.2s">
          <div class="col-md-12">
            <div class="jumbotron p-2 text-left">
              <div class="d-flex flex-row p-0 border-bottom border-light">
                <p>
                  <strong>
                    [
                    { bgDoc.bind.`Defect ID`.toString }
                    ]
                    &nbsp;
                  </strong>{ bgDoc.bind.`Summary` }
                  &nbsp;
                  { statusIcon(bgDoc.bind.`Status`).bind }
                </p>
              </div>
              <div class="d-flex justify-content-center align-self-stretch flex-row flex-wrap align-content-stretch">
                { leftPanel.bind }{ mainPanel.bind }{ rightPanel.bind }
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

  @dom def leftPanel: Binding[Node] = {
    <div class="d-flex p-2  text-wrap w-25 flex-column">
      <strong>Commits</strong>
      <p>
        <a class="text-left" href={ "#/details?1" }>152939</a>
        Comming Soon
        <div class="view overlay rounded">
          <img class="img-fluid" src="assets/images/comingsoon.png" alt="Comming Soon" width={ 150 } height={ 100 }/>
        </div>
      </p>
    </div>

  }

  @dom def mainPanel: Binding[Node] = {

    <div class="d-flex p-2 w-50 text-wrap flex-column">
      <strong>Description</strong>
      <p class="border-bottom border-light">
        { bgDoc.bind.`Description` }
      </p>
      <strong>Comments :</strong>
      <p class="border-bottom border-light">
        { bgDoc.bind.`Comments` }
      </p>
      <p>
        <button class="text-left btn btn-primary btn-sm" onclick={ (e: Event) =>
          NavBarComponent.searchWord.value += " AND Product:" + "\"" + bgDoc.value.`Product` + "\""
          route.state.value = NavbarMenuItem("Home", "#/home", "explore")
          window.scroll(0, 0)
        }>
          Product :&nbsp; &nbsp;{ bgDoc.bind.`Product` }
        </button>
        <button class="text-left btn btn-primary btn-sm " onclick={ (e: Event) =>
          NavBarComponent.searchWord.value += " AND Customer:" + "\"" + bgDoc.value.`Customer` + "\""
          route.state.value = NavbarMenuItem("Home", "#/home", "explore")
          window.scroll(0, 0)
        }>
          Customer : &nbsp; &nbsp;{ bgDoc.bind.`Customer` }
        </button>
        <button class="text-left btn btn-primary btn-sm" onclick={ (e: Event) =>
          NavBarComponent.searchWord.value += " AND Bucket:" + "\"" + bgDoc.value.`Bucket` + "\""
          route.state.value = NavbarMenuItem("Home", "#/home", "explore")
          window.scroll(0, 0)
        }>
          Bucket : &nbsp; &nbsp;{ bgDoc.bind.`Bucket` }
        </button>
        <button class="text-left btn btn-primary btn-sm" onclick={ (e: Event) =>
          NavBarComponent.searchWord.value += " AND Technical Component:" + "\"" + bgDoc.value.`Technical Component` + "\""
          route.state.value = NavbarMenuItem("Home", "#/home", "explore")
          window.scroll(0, 0)
        }>
          Component : &nbsp; &nbsp;{ bgDoc.bind.`Technical Component` }
        </button>
      </p>
    </div>

  }

  @dom def statusIcon(status: String): Binding[Node] = {
    status match {
      case "Closed NFR" => <i class="fa fa-bullseye mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ status }</i>
      case "Closed" => <i class="fa fa-check mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ status }</i>
      case "Open" => <i class="fa fa-opera  mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ status }</i>
      case "New" => <i class="fa fa-file-o  mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ status }</i>
      case "Suspected NFR" => <i class="fa fa-minus-circle mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ status }</i>
      case "Rejected" => <i class="fa fa-window-close-o  mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ status }</i>
      case _ => <i class="fa fa-opera  mr-1 font-italic text-muted small" data:aria-hidden="true">{ status }</i>
    }
  }
  @dom def renderCustomerIcon(customer: String): Binding[Node] =
    <i class="fa fa-university   mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ customer } </i>

  @dom def renderBucket(bucket: String): Binding[Node] =
    <i class="fa   fa-share-alt mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp;{ bucket }</i>

  @dom def rightPanel: Binding[Node] = {
    <div class="d-flex p-2  text-wrap w-25 flex-column yellow lighten-5 border-left border-light sticky">
      <h2>Related</h2>
      {
        Constants(relatedResults.bind.bgBugs: _*).map { item =>
          <p>
            <a class="text-left" href={ "#/details?" + item.`_id` }>{ item.`Defect ID`.toString }</a>
            { item.`Summary` }
            &nbsp;{ statusIcon(item.`Status`).bind }{ renderCustomerIcon(item.`Customer`).bind }
          </p>
        }
      }
      <h2>Related NFR</h2>
      {
        Constants(relatedNfrResults.bind.bgBugs: _*).map { item =>
          <p>
            <a class="text-left" href={ "#/details?" + item.`_id` } onclick={ (e: Event) =>
              window.scroll(0, 0)
            }>{ item.`Defect ID`.toString }</a>
            { item.`Summary` }
            &nbsp;{ statusIcon(item.`Status`).bind }{ renderCustomerIcon(item.`Customer`).bind }
          </p>
        }
      }
    </div>

  }
}

