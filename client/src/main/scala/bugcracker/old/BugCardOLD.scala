package bugcracker

import com.thoughtworks.binding.Binding.{BindingSeq, Constants}
import com.thoughtworks.binding.{Binding, dom}
import models.Bgbug
import org.scalajs.dom.Node
import scalaz.std.vector.vectorInstance
import scalaz.std.list
//import scalaz.std.vector.vectorInstance
//import scalaz.std.list._
object BugCard {
  @dom def mainCard(name: String): Binding[Node] = {
    BGSearchClient.search().watch()
    <div class="card card-stats">
      <div class="card-header card-header-warning card-header-icon">
        <p class="card-category">{ if (SearchBar.getSearchWord.bind.isEmpty) "No Results" else "Total Hits: " + BGSearchClient.searchResults.bind.totalHits.toString }</p>
        <h3 class="card-title">
          Card Title
          <small>GB</small>
        </h3>
      </div>
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover">
            <thead class=" text-primary">
              <tr>
                <th>
                  Defect ID
                </th>
                <th>
                  Assigned To
                </th>
                <th>
                  Summary
                </th>
                <th>
                  Comments
                </th>
                <th>
                  Actual Fix Time
                </th>
              </tr>
            </thead>
            { renderTable.bind }
          </table>
        </div>
      </div>
      <div class="card-footer">
        <div class="stats">
          <i class="material-icons text-danger">warning</i>
          <a href="#">Get More Space...</a>
        </div>
      </div>
    </div>

  }

  @dom def renderTable(): Binding[Node] =
    <tbody>
      {
        Constants(BGSearchClient.searchResults.bind.bgBugs: _*).map { item => renderBgBug(item).bind }
      }
    </tbody>

  @dom def renderBgBug(bgBug: Bgbug): Binding[Node] =
    <tr>
      <td>
        { bgBug.`Defect ID`.toString }
      </td>
      <td>
        { bgBug.`Assigned To`.toString }
      </td>
      <td>
        <p class="text-muted">
          { bgBug.`Summary`.toString }
        </p>
      </td>
      <td>
        { bgBug.`Comments`.toString }
      </td>
      <td>
        { bgBug.`Actual Fix Time`.toString }
      </td>
    </tr>

}
