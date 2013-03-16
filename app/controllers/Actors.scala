package controllers

import akka.actor.Actor


sealed trait Command
case class Start(tick: Int) extends Command
case object Left extends Command
case object Right extends Command
case object Forward extends Command

class IndataActor {

   // TODO listen to socket or whatever

}


sealed trait InfoRequest
case object GiveMeState
case class HereYouGo(commands: Seq[Command])


class StateActor extends Actor {

  var commands: Seq[Command] = Seq(Start(10), Right, Forward, Right, Left)

  def receive = {

    case s :Start =>
      commands = Seq(s)
      informInterestedPartiesAboutReset(s.tick)

    case otherCommand: Command =>
      commands = commands :+ otherCommand
      informInterestedParties(otherCommand)

    case GiveMeState =>
      sender ! HereYouGo(commands)

  }

  def informInterestedPartiesAboutReset(tick: Int) {

  }

  def informInterestedParties(command: Command) {
    // no-one is interested, duuh
  }

  override def preStart() {
    println("State actor started")

  }

  override def postStop() {
    println("State actor stopped")
  }
}