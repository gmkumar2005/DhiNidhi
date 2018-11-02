package bugcracker
import com.thoughtworks.binding.{Binding, FutureBinding, Route, dom}
import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import org.scalajs.dom.{Event, Node, document, window}
object Footer {
  @dom def footer: Binding[Node] = {

    <footer class="footer">
      <div class="container-fluid">
        <nav class="float-left">
          <ul>
            <li>
              <a>
                Server Status :
              </a>
            </li>
            <li>
              <a href="#">
                About Us
              </a>
            </li>
            <li>
              <a href="#">
                Blog
              </a>
            </li>
            <li>
              <a href="#">
                Licenses
              </a>
            </li>
          </ul>
        </nav>
        <div class="copyright float-right">
          © 2018 Finastra. All rights reserved.
        </div>
      </div>
    </footer>
  }

}
