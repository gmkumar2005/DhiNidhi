package bugcracker
import com.thoughtworks.binding.{Binding, FutureBinding, Route, dom}
import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.{Event, Node, document, window}
import util.Logging
object SearchBar {

  @dom def renderSearchBar(): Binding[Node] = {

    <nav class="navbar navbar-expand-lg navbar-transparent navbar-absolute fixed-top ">
      <div class="container-fluid">
        <div class="navbar-wrapper">
          <a class="navbar-brand" href="#">
            { Sidebar.getRoute.state.bind.navBarName }
          </a>
        </div>
        <div class="collapse navbar-collapse justify-content-end">
          <form class="navbar-form container-fluid ">
            <div class="input-group no-border">
              <input type="text" value={ searchWord.bind } class="form-control" placeholder="Search..." id="searchInput" onchange={ handleWord(searchInput)(_) }/>
              <button type="submit" class="btn btn-white btn-round btn-just-icon">
                <i class="material-icons">search</i>
                <div class="ripple-container"></div>
              </button>
            </div>
          </form>
          <ul class="navbar-nav">
            <li class="nav-item">
              <a class="nav-link" href="#">
                <i class="material-icons">dashboard</i>
                <p class="d-lg-none d-md-block">
                  Stats
                </p>
              </a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link" href="http://example.com" id="navbarDropdownMenuLink" data:data-toggle="dropdown" data:aria-haspopup="true" data:aria-expanded="false">
                <i class="material-icons">notifications</i>
                <span class="notification">5</span>
                <p class="d-lg-none d-md-block">
                  Some Actions
                </p>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">
                <i class="material-icons">person</i>
                <p class="d-lg-none d-md-block">
                  Account
                </p>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>

  }

  val searchWord = Var("")

  def getSearchWord = { searchWord }

  def handleWord(input: HTMLInputElement)(e: Event) = {
    Logging.info(s"SearchBar.scala Input Changed")
    searchWord.value = input.value
  }
}
