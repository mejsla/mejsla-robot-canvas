package controllers

import play.api._
import libs.concurrent.Akka
import libs.iteratee.{Concurrent, Iteratee}
import libs.json._
import play.api.mvc._
import scala.concurrent.future
import akka.pattern.ask
import play.api.libs.functional.syntax._
import play.api.Play.current
import akka.util.Timeout
import concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import akka.actor.ActorRef

object Application extends Controller {

  implicit val writesDirection = new Writes[Direction] {
    def writes(d: Direction): JsValue = d match {
      case Forward => JsString("forward")
      case Backward => JsString("backward")
      case None => JsString("none")
    }
  }

  implicit val writesMove = new Writes[Move] {
    def writes(move: Move): JsValue = JsObject(Seq(
      "command" -> JsString("move"),
      "left" -> Json.toJson[Direction](move.left),
      "right" -> Json.toJson[Direction](move.right)
    ).filterNot(_._2 == JsNull))
  }

  implicit val commandWrites = new Writes[Command] {
    def writes(command: Command) = command match {
      case Start(ticks) => JsObject(Seq("command" -> JsString("start"), "tick" -> JsNumber(ticks)))
      case m: Move => Json.toJson[Move](m)
    }
  }

  implicit val akkaTimeout: Timeout = 5 seconds


  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def stateActor: ActorRef = Akka.system.actorFor("user/stateActor")

  def giveMeState = WebSocket.async[JsValue] { request =>

    future {
      val enumerator = Concurrent.unicast[Command] { channel =>
        stateActor ! ListenForCommands(channel)
      }

      val jsonEnumerator = enumerator.map { command =>
        println(s"Enumerating ${command}")
        Json.toJson[Command](command)
      }

      val ignoreInput = Iteratee.ignore[JsValue]

      (ignoreInput, jsonEnumerator)
    }
  }


  def start(tick: Int) = Action {
    stateActor ! Start(tick)
    Ok("Finemang")
  }

  def move(left: Option[String], right: Option[String]) = Action {
    def optToDirection(value: Option[String]) = value.map {
      case "forward" => Forward
      case "backward" => Backward
    }.getOrElse(None)

    stateActor ! Move(optToDirection(left), optToDirection(right))
    Ok("Finemang")
  }

  
}