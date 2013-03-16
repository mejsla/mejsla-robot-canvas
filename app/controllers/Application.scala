package controllers

import play.api._
import libs.concurrent.Akka
import libs.json._
import play.api.mvc._
import scala.concurrent.future
import akka.pattern.ask
import play.api.libs.functional.syntax._
import play.api.Play.current
import akka.util.Timeout
import concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller {

  implicit object commandWrites extends Writes[Command] {
    def writes(command: Command) = command match {

      case Start(ticks) => JsObject(Seq("command" -> JsString("start"), "tick" -> JsNumber(ticks)))
      case Left => move("left")
      case Right => move("right")
      case Forward => move("forward")

    }

    def move(how: String) = JsObject(Seq("command" -> JsString("move"), "how" -> JsString(how)))
  }

  implicit val akkaTimeout: Timeout = 5 seconds

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def giveMeState = Action { Async {

    val actor = Akka.system.actorFor("user/stateActor")

    (actor ? GiveMeState).mapTo[HereYouGo].map { hereYouGo =>
      Ok(Json.toJson(hereYouGo.commands))
    }
  }}



  
}