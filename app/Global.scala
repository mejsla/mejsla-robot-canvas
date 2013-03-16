import akka.actor.Props
import controllers.StateActor
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings}
import play.api.Play.current

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    val stateActor = Akka.system.actorOf(Props[StateActor], name = "stateActor")


  }
}
