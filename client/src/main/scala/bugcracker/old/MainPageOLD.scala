package bugcracker

import models.entrypoint.Entrypoint

import scala.scalajs.js.annotation.JSExportTopLevel
import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import com.thoughtworks.binding.{Binding, FutureBinding, Route, dom}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.{Event, Node, document, window}

@JSExportTopLevel("MainPage")
class MainPage extends Entrypoint("Bugcracker MainPage") {

  dom.render(document.body, wrapper)

  @dom
  def wrapper: Binding[BindingSeq[Node]] = {
    <div class="sidebar" data:data-color="purple" data:data-background-color="white" data:data-image="../assets/img/sidebar-1.jpg">
      { Sidebar.renderSidebar.bind }
    </div>
    <div class="main-panel">
      { SearchBar.renderSearchBar.bind }{ renderContent.bind }{ Footer.footer.bind }
    </div>
  }

  @dom def renderContent: Binding[Node] = Sidebar.getRoute.state.bind.navBarName match {
    case "Dashboard" => dashboardContent.bind
    case _ => dashboardContent.bind
  }

  @dom def dashboardContent: Binding[Node] = {
    <div class="content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-lg-12 col-md-6 col-sm-6">
            { BugCard.mainCard("Dashboard").bind }
          </div>
        </div>
      </div>
    </div>
  }

}
