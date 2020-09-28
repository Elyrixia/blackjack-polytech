package fr.polytech.blackjack

import fr.polytech.blackjack.models.{Card, Rank, Suit}

import scala.util.Random

object Main extends App {

  def initDeck =
    for {
      suit <- Suit.values
      rank <- Rank.values
    } yield Card(suit, rank)

  println(Random.shuffle(initDeck))

}
