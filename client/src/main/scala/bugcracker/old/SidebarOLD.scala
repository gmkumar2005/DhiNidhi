package bugcracker

import com.thoughtworks.binding.{Binding, FutureBinding, Route, dom}
import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import org.scalajs.dom.{Event, Node, document, window}
final case class SidebarMenuItem(navBarName: String, hash: String, icon: String)
object Sidebar {
  import scalaz.std.vector.vectorInstance
  import scalaz.std._
  val dashboardMenu = SidebarMenuItem("Dashboard", "#/dashboard", "explore")
  //    val defaultMenu = SidebarMenuItem("Dashboard", "#/", "dashboard")
  val user = SidebarMenuItem("User Profile", "#/user", "person")
  val sidebarMenuItems = Vector(dashboardMenu, user)
  val route = Route.Hash(dashboardMenu)(new Route.Format[SidebarMenuItem] {
    override def unapply(hashText: String) = sidebarMenuItems.find(_.hash == window.location.hash)
    override def apply(state: SidebarMenuItem): String = state.hash
  })

  def getRoute = { this.route }
  val globalSearchWord = Var("")
  val esServerUrl = "http://10.91.10.13:9200"

  @dom def renderMenu(menuItems: Vector[SidebarMenuItem]): Binding[Node] =

    <ul class="nav">
      {
        // import scalaz.std.vector.vectorInstance // Enable list comprehension in a @dom method
        //    import scalaz.std.list._ // Type classes for List
        import scalaz.std.vector.vectorInstance
        import scalaz.std.list._
        route.watch()
        val filteredMenuItems = menuItems.filter((i) => {
          i.hash != "#/"
        })
        for (item <- filteredMenuItems) yield {
          <li class={ if (item.hash == route.state.bind.hash) "nav-item active" else "nav-item" }>
            <a class="nav-link" href={ item.hash }>
              <i class="material-icons">
                { item.icon }
              </i>
              <p>
                { item.navBarName }
              </p>
            </a>
          </li>
        }
      }
    </ul>

  @dom def renderSidebar: Binding[BindingSeq[Node]] = {
    <div class="logo">
      <a href="#" class="simple-text logo-normal">
        It Works
      </a>
    </div>
    <div class="sidebar-wrapper ps-container ps-theme-default">
      { renderMenu(sidebarMenuItems).bind }
    </div>

  }

}
