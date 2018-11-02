import cats.Functor
import cats.instances.list._
import cats.instances.option._
import util.Logging
import io.circe.parser._
import io.circe.syntax._
import models.{Bgbug, ESQuery, SearchResult}
import cats.Functor
import cats.instances.list._
import cats.instances.option._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import cats.syntax.functor._, cats.syntax.flatMap._
import util.Logging
 "#/users?122".split("""\?""").head
 "#/users?122".indexOf("""?""")
 "#/users?122".substring("#/users?122".indexOf("""?""")+1)

 case class NavbarMenuItem(navBarName: String, hash: String, icon: String)
val home = NavbarMenuItem("Home", "#/home", "explore")
val user = NavbarMenuItem("User Profile", "#/users", "person")
val navMenuItems = Vector(home, user)
navMenuItems.find(_.hash == "#/users?122".split("""\?""").head)
val barwithNewHash =
for {
 bar1 <- navMenuItems.find(_.hash == "#/users?122".split("""\?""").head)
 barFresh <- Some(bar1.copy(hash="#/users?122"))
} yield {barFresh}

