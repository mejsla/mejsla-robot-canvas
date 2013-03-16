package controllers

import akka.actor.Actor
import play.api.libs.iteratee.{Enumerator, Concurrent}


sealed trait Command
case class Start(tick: Int) extends Command
case object Left extends Command
case object Right extends Command
case object Forward extends Command

class IndataActor {

   // TODO listen to socket or whatever

}


case object GiveMeState
case class HereYouGo(commands: Seq[Command])
case object ListenForCommands
case class Connected(enumerator: Enumerator[Command])

class StateActor extends Actor {

  var commands: Seq[Command] = Seq(Start(10), Right, Forward, Right, Left)

  val (commandEnumerator, commandChannel) = Concurrent.broadcast[Command]

  def receive = {

    case s :Start =>
      commands = Seq(s)
      informInterestedParties(s)

    case otherCommand: Command =>
      commands = commands :+ otherCommand
      informInterestedParties(otherCommand)

    case GiveMeState =>
      sender ! HereYouGo(commands)

    case ListenForCommands =>
      sender ! Connected(commandEnumerator)
      // commands up till now
      commands.foreach(informInterestedParties(_))


  }

  def informInterestedParties(command: Command) {
    commandChannel.push(command)
  }

  override def preStart() {
    println("State actor started")

  }

  override def postStop() {
    println("State actor stopped")
  }
}