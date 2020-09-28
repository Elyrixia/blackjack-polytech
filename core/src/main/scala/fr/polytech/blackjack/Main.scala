package fr.polytech.blackjack

import scala.util.{Failure, Success}

object Main extends App {

  val blackjack = new Blackjack(100)

  blackjack.start() match {
    case Success(result) => println(s"Good game !! Result $result")
    case Failure(e) => println(s"Error while running the blackjack game \n$e")
  }

}
