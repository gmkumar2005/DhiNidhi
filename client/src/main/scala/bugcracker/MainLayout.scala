package bugcracker

import bugcracker.BgDetails.bgDoc
import bugcracker.NavBarComponent.{NavbarMenuItem, route}
import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, Route, dom}
import models.Bgbug
import models.entrypoint.Entrypoint
import org.scalajs.dom.{Event, Node, document, window}
import org.scalajs.jquery.JQueryStatic
import util.Logging

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

@js.native
@JSImport("jquery", JSImport.Namespace)
object $ extends JQueryStatic
@JSExportTopLevel("MainLayout")
class MainLayout extends Entrypoint("Bugcracker MainPage Layout") {
  dom.render(document.body, renderLayout)
  NavBarComponent.search().watch
  NavBarComponent.route.watch
  NavBarComponent.notification.watch
  BgDetails.getSingleDoc().watch
  //  BgDetails.searchRelated().watch
  //  BgDetails.searchRelatedNFR().watch
  BgDetails.searchRelatedU.watch()
  BgDetails.searchRelatedNFRU.watch()
  BgDetails.searchRelatedClosedU.watch()

  BgDetails.renderRightPanelTabs.watch
  @dom def renderContent: Binding[Node] = NavBarComponent.route.state.bind.navBarName match {
    case "Home" => renderMainContent.bind
    case "BGBug Details" => BgDetails.bgContent.bind
    case _ => renderMainContent.bind
  }

  @dom def renderMainContent: Binding[Node] =
    <main class="mt-5 indigo lighten-5">
      <div class="container-fluid">
        <div class="row wow fadeIn" data:data-wow-delay="0.2s">
          { renderBgTable.bind }
        </div>
      </div>
    </main>

  @dom def renderLayout: Binding[Node] =
    <body data:aria-busy="true" class="indigo lighten-5">
      { NavBarComponent.renderNavBar.bind }{ renderContent.bind }
    </body>

  @dom def renderBgTable: Binding[Node] =
    <div class="col-md-12">
      <div class="jumbotron p-2">
        <p class="card-text">
          Total Hits :
          { NavBarComponent.searchResults.bind.totalHits.toString }
        </p>
        <!--Table-->
        <div class='table-responsive'>
          <table id="tablePreview" class="table  table-hover table-sm">
            <!--Table head-->
            <thead>
              <tr>
                <th class="text-warning">#</th>
                <th>Assigned To</th>
                <th>Summary</th>
                <th>Description</th>
                <th>Status</th>
                <th>Type</th>
                <th>Product</th>
                <th>Priority</th>
                <th>Raised By</th>
              </tr>
            </thead>
            <!--Table head-->
            <!--Table body-->{ renderTable.bind }<!--Table body-->
          </table>
          <!--Table-->
        </div>
      </div>
    </div>

  @dom def renderTable(): Binding[Node] =
    <tbody>
      { Constants(NavBarComponent.searchResults.bind.bgBugs: _*).map { item => renderBgBug(item).bind } }
    </tbody>

  @dom def renderBgBug(bgBug: Bgbug): Binding[Node] =
    <tr>
      <td>
        <p>
          <a class="text-left text-primary" href={ "#/details?" + bgBug.`_id`.toString } onclick={ (e: Event) =>
            window.scroll(0, 0)
          }>
            { bgBug.`JiraKey` match { case Some("None") => bgBug.`_id`.toString case Some(jirakey) => jirakey case None => bgBug.`_id`.toString } }
          </a>
        </p>
      </td>
      <td>
        { bgBug.`Assigned To`.toString }
      </td>
      <td>
        { bgBug.`Summary`.toString }
      </td>
      <td>
        { bgBug.`Description`.toString }
      </td>
      <td>
        { bgBug.`Status`.toString }
      </td>
      <td>
        { bgBug.`Type`.toString }
      </td>
      <td>
        { bgBug.`Product`.toString }
      </td>
      <td>
        { bgBug.`Priority` }
      </td>
      <td>
        { bgBug.`Raised By` }
      </td>
    </tr>

}
