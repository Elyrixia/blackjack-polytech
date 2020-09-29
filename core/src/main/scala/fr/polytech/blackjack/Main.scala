package fr.polytech.blackjack

import scala.util.{Failure, Success}

object Main extends App {

  val blackjack = new Blackjack(100)

  blackjack.start() match {
    case Success((initCredits, endCredits)) =>
      println(s"Good game !! You started with $initCredits and ended up with $endCredits")
    case Failure(e) =>
      println(s"Error while running the blackjack game \n$e")
  }

}
