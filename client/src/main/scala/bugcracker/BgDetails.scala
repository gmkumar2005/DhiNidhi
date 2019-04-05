package bugcracker

import bugcracker.BgDetails.{isLoadingDone, searchRelated, searchRelatedNFR, selectedTab}
import bugcracker.NavBarComponent.{NavbarMenuItem, route}
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var, Vars}
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
    "", "", "", "", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", None))
  val relatedResults = Var(SearchResult(0, Seq.empty))
  val relatedNfrResults = Var(SearchResult(0, Seq.empty))
  val selectedTab = Var("Related")
  val isLoadingDone = Var(true)
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
            "", "", "", "", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", None))
        case Some(Failure(exception)) => val e1 = "error"
      }
    }
    <div></div>
  }

  @dom def searchRelated: Binding[Node] = {
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

  @dom def searchRelatedU: Binding[Node] = {
    val tab = selectedTab.bind
    val navItem = NavBarComponent.route.state.bind.navBarName
    val navHash = NavBarComponent.route.state.bind.hash
    val docId = navHash.substring(navHash.indexOf("""?""") + 1)
    val esQuery = ESQuery(docId, 0, 5)
    if (tab == "Related" && navItem == "BGBug Details") {
      isLoadingDone.value = false
      FutureBinding(Ajax.post(
        "bugcracker/searchRelated",
        esQuery.asJson.toString(), 0
      )).bind match {
        case None => isLoadingDone.value = true
        case Some(Success(response)) =>
          val r1 = decode[SearchResult](response.responseText)
          relatedResults.value = r1.getOrElse(SearchResult(0, Seq.empty))
          isLoadingDone.value = true
        case Some(Failure(exception)) =>
          Logging.error("BgDetails.scala searchRelated : " + exception.getMessage)
          isLoadingDone.value = true
      }
    } else {
      relatedResults.value = SearchResult(0, Seq.empty)
    }
    <div></div>
  }

  @dom def searchRelatedClosedU: Binding[Node] = {
    val tab = selectedTab.bind
    val navItem = NavBarComponent.route.state.bind.navBarName
    val navHash = NavBarComponent.route.state.bind.hash
    val docId = navHash.substring(navHash.indexOf("""?""") + 1)
    val esQuery = ESQuery(docId, 0, 5)

    if (tab == "Closed" && navItem == "BGBug Details") {
      isLoadingDone.value = false
      FutureBinding(Ajax.post(
        "bugcracker/searchRelatedClosed",
        esQuery.asJson.toString(), 0
      )).bind match {
        case None => isLoadingDone.value = true
        case Some(Success(response)) =>
          val r1 = decode[SearchResult](response.responseText)
          relatedResults.value = r1.getOrElse(SearchResult(0, Seq.empty))
          isLoadingDone.value = true
        case Some(Failure(exception)) =>
          Logging.error("BgDetails.scala searchRelated : " + exception.getMessage)
          isLoadingDone.value = true
      }
    } else {
      relatedResults.value = SearchResult(0, Seq.empty)
    }
    <div></div>
  }

  @dom def searchRelatedNFRU: Binding[Node] = {
    val tab = selectedTab.bind
    val navHash = NavBarComponent.route.state.bind.hash
    val navItem = NavBarComponent.route.state.bind.navBarName
    val docId = navHash.substring(navHash.indexOf("""?""") + 1)
    val esQuery = ESQuery(docId, 0, 5)

    if (tab == "NFR" && navItem == "BGBug Details") {
      isLoadingDone.value = false
      FutureBinding(Ajax.post(
        "bugcracker/searchRelatedNFR",
        esQuery.asJson.toString(), 0
      )).bind match {
        case None => isLoadingDone.value = true
        case Some(Success(response)) =>
          val r1 = decode[SearchResult](response.responseText)
          relatedNfrResults.value = r1.getOrElse(SearchResult(0, Seq.empty))
          isLoadingDone.value = true
        case Some(Failure(exception)) =>
          Logging.error("BgDetails.scala searchRelated : " + exception.getMessage)
          isLoadingDone.value = true
      }
    } else {
      relatedNfrResults.value = SearchResult(0, Seq.empty)
    }
    <div></div>
  }

  @dom def searchRelatedNFR: Binding[Node] = {
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
                    { bgDoc.bind.`JiraKey` match { case Some("None") => bgDoc.bind.`_id`.toString case Some(jirakey) => jirakey case None => bgDoc.bind.`_id`.toString } }
                    ]
                    &nbsp;
                  </strong>{ bgDoc.bind.`Summary` }
                  &nbsp;
                  { statusIcon(bgDoc.bind.`Status`).bind }
                  { renderMissingStepsIcon.bind }
                  <i class="fa fa-bullseye mr-1 font-italic text-muted small text-danger" data:aria-hidden="true">&nbsp;{ "Missing Attachments" }</i>
                </p>
              </div>
              <div class="d-flex justify-content-center align-self-stretch flex-row flex-wrap align-content-stretch">
                { leftPanel.bind }{ mainPanel.bind }{ rightPanelWithTabs.bind }
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

  @dom def renderPeople: Binding[Node] = {

    val usereList = Vars.empty[String]
    usereList.value += bgDoc.bind.`Assigned To`.replaceAll("\\s", "+")
    usereList.value += bgDoc.bind.`Detected By`.replaceAll("\\s", "+")
    usereList.value += bgDoc.bind.`Detected By`.replaceAll("\\s", "+")
    println(" User List" + usereList)
    val cssclass = s"rounded-circle z-depth-1-half"
    //    val userListCleaned = usereList.value.filter(_.nonEmpty)
    val imageSource1 = s"https://ui-avatars.com/api/?name=" +
      bgDoc.bind.`Assigned To`.replaceAll("\\s", "+") +
      s"&background=0D8ABC&color=fff&size=32"
    val imageSource2 = s"https://ui-avatars.com/api/?name=" +
      bgDoc.bind.`Detected By`.replaceAll("\\s", "+") +
      s"&background=0D8ABC&color=fff&size=32"
    <div class="d-flex flex-row p-0 border-bottom border-light">
      {
        <h6 class="mt-2">People : &nbsp; &nbsp; </h6>
        <img src={ imageSource1 } class={ cssclass } data:data-toggle="tooltip" title={ bgDoc.bind.`Assigned To` }/><p></p>
        <img src={ imageSource2 } class={ cssclass } data:data-toggle="tooltip" title={ bgDoc.bind.`Detected By` }/>
      }
    </div>
  }

  @dom def renderTraceFileSmasher: Binding[Node] = {
    <div>
      <table class="table table-hover">
        <thead>
          <tr>
            <th data:scope="col"><strong>Trace File Smasher</strong></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <button class="text-left btn btn-primary btn-sm ">
                Upload Trace File
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <table class="table table-hover">
        <thead>
          <tr>
            <th data:scope="col"><i class="fa fa-quote-left pr-2"></i><strong>Errors found in trace</strong></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <a class="text-left" onclick={ (e: Event) =>
                NavBarComponent.searchWord.value += " AND " + "No Valid Mop Found"
                route.state.value = NavbarMenuItem("Home", "#/home", "explore")
                window.scroll(0, 0)
              }>No Valid Mop Found</a>
            </td>
          </tr>
          <tr>
            <td>
              <a class="text-left" onclick={ (e: Event) =>
                NavBarComponent.searchWord.value += " AND " + "Membership validation failed"
                route.state.value = NavbarMenuItem("Home", "#/home", "explore")
                window.scroll(0, 0)
              }>Membership validation failed</a>
            </td>
          </tr>
          <tr>
            <td>
              <a class="text-left" onclick={ (e: Event) =>
                NavBarComponent.searchWord.value += " AND " + "Failed to derive credit account number"
                route.state.value = NavbarMenuItem("Home", "#/home", "explore")
                window.scroll(0, 0)
              }>Failed to derive credit account number</a>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  }

  @dom def renderPotentialNFR: Binding[Node] = {
    {
      bgDoc.bind.Suggested_Resolution match {
        case "NFR" => <div class="d-flex flex-row p-0 ">
                        <strong>Suggested Resolution : &nbsp; </strong>
                        <strong class="font-weight-bold mb-3"><i class="fa fa-diamond pr-2"></i>NFR</strong>
                      </div>
        case _ => <div> <!-- empty content --> </div>
      }
    }
  }

  @dom def renderPotentialComponent: Binding[Node] = {
    <div class="d-flex flex-row p-0 ">
      <strong>
        Suggested Component :
        &nbsp;
      </strong>
      <strong class="font-weight-bold mb-3">
        <i class="fa fa-database pr-2"></i>
        DB
      </strong>
    </div>
  }

  @dom def leftPanel: Binding[Node] = {

    <div class="d-flex p-2  text-wrap w-25 flex-column border-right border-light ">
      { renderPeople.bind }
      <strong>Commits</strong>
      <p>
        <a class="text-left" href={ "http://isr-swarm.fundtech.isr/changes/92903" }>92903</a>
        NPP|SIT|DUPL flag is missing in CN-Pacs002 message of the duplicate rejected payment for incoming transaction
      </p>
      <p>
        <a class="text-left" href={ "http://isr-swarm.fundtech.isr/changes/96675" }>96675</a>
        automation added ID to html elements and align tests
      </p>
      { renderPotentialNFR.bind }
      { renderPotentialComponent.bind }
      { renderTraceFileSmasher.bind }
    </div>
  }

  @dom def mainPanel: Binding[Node] = {

    <div class="d-flex p-2 w-50 text-wrap flex-column">
      <strong>Description</strong>
      <p class="border-bottom border-light" innerHTML={ bgDoc.bind.`Description` }>
      </p>
      <strong>Solution :</strong>
      <p class="border-bottom border-light grey lighten-2">
        Data Not Available
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
      case _ => <i class="fa fa-opera  mr-1 font-italic text-muted small" data:aria-hidden="true">&nbsp; { status }</i>
    }
  }

  @dom def renderMissingStepsIcon(): Binding[Node] = {

    val pattern = "steps\\s+to\\s+(reproduce|create|recreate)".r

    val str = bgDoc.bind.`Summary` + bgDoc.bind.`Description` + bgDoc.bind.`Comments`
    val foundPattern = pattern findFirstIn str.toLowerCase

    val result = foundPattern match {
      case Some(i) => true
      case None => false
    }

    if (!result)
      <i class="fa fa-bullseye mr-1 font-italic text-muted small text-danger" data:aria-hidden="true">&nbsp;{ "Missing Steps to Reproduce" }</i>
    else
      <div></div>
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
            <a class="text-left" href={ "#/details?" + item.`_id` } onclick={ (e: Event) =>
              window.scroll(0, 0)
            }>{ item.`JiraKey` match { case Some("None") => item.`Defect ID`.toString case Some(jirakey) => jirakey case None => item.`Defect ID`.toString } } </a>
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
            }>{ item.`JiraKey` match { case Some("None") => item.`Defect ID`.toString case Some(jirakey) => jirakey case None => item.`Defect ID`.toString } }</a>
            { item.`Summary` }
            &nbsp;{ statusIcon(item.`Status`).bind }{ renderCustomerIcon(item.`Customer`).bind }
          </p>
        }
      }
    </div>

  }
  @dom def relatedPanel: Binding[Node] = {
    <div class="p-2">
      {
        if (relatedResults.bind.totalHits == 0 && isLoadingDone.bind) {
          <p>None</p>
        } else if (!isLoadingDone.bind) {
          <p>Loading ...</p>
        } else {
          <!-- empty content -->
        }
      }
      {
        Constants(relatedResults.bind.bgBugs: _*).map { item =>
          <p>
            <a class="text-left" href={ "#/details?" + item.`_id` } onclick={ (e: Event) =>
              window.scroll(0, 0)
            }>{ item.`JiraKey` match { case Some("None") => item.`Defect ID`.toString case Some(jirakey) => jirakey case None => item.`Defect ID`.toString } } </a>
            { item.`Summary` }
            &nbsp;{ statusIcon(item.`Status`).bind }{ renderCustomerIcon(item.`Customer`).bind }
          </p>
        }
      }
    </div>
  }

  @dom def relatedNFRPanel: Binding[Node] = {
    <div class="p-2">
      {
        if (relatedNfrResults.bind.totalHits == 0 && isLoadingDone.bind) {
          <p>None</p>
        } else if (!isLoadingDone.bind) {
          <p>Loading ...</p>
        } else {
          <!-- empty content -->
        }
      }
      {
        Constants(relatedNfrResults.bind.bgBugs: _*).map { item =>
          <p>
            <a class="text-left" href={ "#/details?" + item.`_id` } onclick={ (e: Event) =>
              window.scroll(0, 0)
            }>{ item.`JiraKey` match { case Some("None") => item.`Defect ID`.toString case Some(jirakey) => jirakey case None => item.`Defect ID`.toString } } </a>
            { item.`Summary` }
            &nbsp;{ statusIcon(item.`Status`).bind }{ renderCustomerIcon(item.`Customer`).bind }
          </p>
        }
      }
    </div>
  }

  @dom def relatedClosedPanel: Binding[Node] = {
    <div class="p-2">
      {
        if (relatedResults.bind.totalHits == 0 && isLoadingDone.bind) {
          <p>None</p>
        } else if (!isLoadingDone.bind) {
          <p>Loading ...</p>
        } else {
          <!-- empty content -->
        }
      }
      {
        Constants(relatedResults.bind.bgBugs: _*).map { item =>
          <p>
            <a class="text-left" href={ "#/details?" + item.`_id` } onclick={ (e: Event) =>
              window.scroll(0, 0)
            }>{ item.`JiraKey` match { case Some("None") => item.`Defect ID`.toString case Some(jirakey) => jirakey case None => item.`Defect ID`.toString } } </a>
            { item.`Summary` }
            &nbsp;{ statusIcon(item.`Status`).bind }{ renderCustomerIcon(item.`Customer`).bind }
          </p>
        }
      }
    </div>
  }

  @dom def rightPanelWithTabs: Binding[Node] = {
    <div class="d-flex p-2  text-wrap w-25 flex-column yellow lighten-5 border-left border-light sticky">
      <ul class="nav nav-tabs">
        <li class="nav-item">
          <a class={ if (selectedTab.bind == "Related") "nav-link text-default active" else "nav-link text-default " } href="#" onclick={ (e: Event) =>
            selectedTab.value = "Related"
          }>Related</a>
        </li>
        <li class="nav-item">
          <a class={ if (selectedTab.bind == "NFR") "nav-link text-default active" else "nav-link text-default " } href="#" onclick={ (e: Event) =>
            selectedTab.value = "NFR"
          }>NFR</a>
        </li>
        <li class="nav-item">
          <a class={ if (selectedTab.bind == "Closed") "nav-link text-default active" else "nav-link text-default" } href="#" onclick={ (e: Event) =>
            selectedTab.value = "Closed"
          }>Closed</a>
        </li>
      </ul>
      { renderRightPanelTabs.bind }
    </div>
  }

  @dom def renderRightPanelTabs: Binding[Node] = {
    val tab = selectedTab.bind
    tab match {
      case "Related" =>
        { relatedPanel.bind }
      case "NFR" =>
        { relatedNFRPanel.bind }
      case "Closed" =>
        { relatedClosedPanel.bind }
      case _ => { relatedPanel.bind }
    }
  }
}

