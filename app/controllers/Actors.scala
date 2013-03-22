package controllers

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.iteratee.{Concurrent, Enumerator}


sealed trait Direction
case object None extends Direction
case object Forward extends Direction
case object Backward extends Direction

sealed trait Command
case class Start(tick: Int) extends Command
case class Move(left: Direction, right: Direction) extends Command

case class ListenForCommands(channel: Channel[Command])

class StateActor extends Actor {


  var listeners: Seq[Channel[Command]] = Seq()

  def receive = {

    case s :Start =>
      informInterestedParties(s)

    case otherCommand: Command =>
      informInterestedParties(otherCommand)

    case ListenForCommands(channel) =>
      listeners = listeners :+ channel

  }

  def informInterestedParties(command: Command) {
    listeners.foreach(_.push(command))
  }

  override def preStart() {
    println("State actor started")

  }

  override def postStop() {
    println("State actor stopped")
  }
}