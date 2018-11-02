package bugcracker

import org.scalajs.dom.{Event, Node, window}
import org.scalajs.dom.raw.HTMLInputElement
import util.Logging
import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.{Binding, FutureBinding, Route, dom}
import io.circe.parser._
import io.circe.syntax._
import models.{Bgbug, ESQuery, SearchResult}
import org.scalajs.dom.ext.Ajax
import util.Toastr

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object NavBarComponent {
  val searchWord = Var("")
  val searchResults = Var(SearchResult(0, Seq.empty))
  final case class NavbarMenuItem(navBarName: String, hash: String, icon: String)
  val home = NavbarMenuItem("Home", "#/home", "explore")
  val user = NavbarMenuItem("BGBug Details", "#/details", "person")
  val navMenuItems = Vector(home, user)
  val route = Route.Hash(home)(new Route.Format[NavbarMenuItem] {
    override def unapply(hashText: String) = {
      Logging.info("Hash unapply: " + window.location.hash.split("""\?""").head + " Original " + window.location.hash)
      navMenuItems.find(_.hash == window.location.hash.split("""\?""").head)
      for {
        matchingItem <- navMenuItems.find(_.hash == window.location.hash.split("""\?""").head)
        itemWithOriginalHash <- Some(matchingItem.copy(hash = window.location.hash))
      } yield { itemWithOriginalHash }
    }
    override def apply(state: NavbarMenuItem): String = {
      Logging.info("Route apply : " + state.hash)
      state.hash
    }
  })

  @dom def hashParameter: Binding[Node] = {
    <h1>{ window.location.hash }</h1>
  }

  @dom def renderNavBar: Binding[Node] =
    <nav class="navbar fixed-top navbar-expand-lg navbar-dark scrolling-navbar">
      <a class="navbar-brand" href="#">
        <strong>
          <i class="fa fa-compass" data:aria-hidden="true"></i>
        </strong>
      </a>
      <button class="navbar-toggler" type="button" data:data-toggle="collapse" data:data-target="#navbarSupportedContent" data:aria-controls="navbarSupportedContent" data:aria-expanded="false" data:aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
          <li class={ if ("#/home" == route.state.bind.hash) "nav-item active" else "nav-item" }>
            <a class="nav-link" href="#/home">
              Home
              <span class="sr-only">(current)</span>
            </a>
          </li><li class={ if ("#/details" == route.state.bind.hash) "nav-item active" else "nav-item" }>
                 <a class="nav-link" href="#/details?83">
                   Details
                   <span class="sr-only">(current)</span>
                 </a>
               </li>
        </ul>
        <div class="md-form mt-0 col-md-10 mb-0 p-0">
          <input type="text" value={ searchWord.bind } placeholder="Search... (e.g. Status:open AND Comments:msg)" id="searchInput2" class="form-control form-control-sm text-warning mb-0" onchange={ handleWord(searchInput2)(_) }/>
        </div>
        <a class="navbar-brand" href="#" onclick={ handleSearchLink()(_) }>
          <strong>
            <i class="fa fa-search" data:aria-hidden="true"></i>
          </strong>
        </a>
      </div>
    </nav>

  def handleWord(input: HTMLInputElement)(e: Event) = {
    Logging.info(s"SearchBar.scala Input Changed")
    route.state.value = NavbarMenuItem("Home", "#/home", "explore")
    searchWord.value = input.value
  }
  //TODO
  def handleSearchLink()(e: Event) = {
    Logging.info(s"TODO handleSearch")
    route.state.value = NavbarMenuItem("Home", "#/home", "explore")
  }

  @dom def notification(): Binding[Node] = {
    val res = searchResults.bind
    Toastr.info("Found " + res.totalHits.toString + " for " + searchWord.value)
    <div></div>
  }
  @dom def search(): Binding[Node] = {
    val searchKeyWord = searchWord.bind
    Logging.info("NavBarComponent.scala : Started Search")
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
