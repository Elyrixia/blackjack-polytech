package fr.polytech.blackjack

import fr.polytech.blackjack.models.{Card, Hand, Rank, Suit}

import scala.io.StdIn
import scala.util.{Random, Try}

class Blackjack(initialCredits: Int) {

  private def readString(message: String): Try[String] = Try(StdIn.readLine(message))
  private def readInt(): Try[Int] = Try(StdIn.readInt())

  private def startNewRound(credit: Int, deck: List[Card]): Either[Throwable, Unit] = {
    if (credit <= 0) Left(new Throwable("You cannot start a new round because you don't have any more money"))
    else if (deck.size < 10) Left(new Throwable("There is not enough cards to continue, please start a new game"))
    else Right(())
  }

  private def bet(currentCredit: Int): Try[Int] = {
    println(s"Please enter your bet for this round. Current credit : $currentCredit")
    println()
    readInt().filter(_ <= currentCredit)
  }

  private def dealCards(deck: List[Card]): (Hand, Hand, List[Card]) = {
    val (firsTwoCards, rest) = deck.splitAt(2)
    val (nextTwoCards, end) = rest.splitAt(2)
    (Hand(firsTwoCards), Hand(nextTwoCards), end)
  }

  private def showHands(playerHand: Hand, dealerHand: Hand, maskDealerCards: Boolean): Unit = {
    println(s"Dealer hand ${dealerHand.showCards(asDealer = maskDealerCards).mkString(",")}")
    println(s"Player hand ${playerHand.showCards(asDealer = false).mkString(",")}")
  }

  private def playRound(
    credits: Int,
    deck: List[Card]
  ): Try[(Int, Boolean)] = {
    for {
      _ <- startNewRound(credits, deck).toTry
      betValue <- bet(credits)
      (playerHand, dealerHand, restOfTheDeck) = dealCards(deck)
      _ = showHands(playerHand, dealerHand, maskDealerCards = true)
    } yield (1, true)
  }

  def start(): Try[(Int, Boolean)] = {
    println(s"Starting the blackjack game with a credit of $initialCredits")
    def initDeck =
      for {
        suit <- Suit.values
        rank <- Rank.values
      } yield Card(suit, rank)

    val shuffledDeck: List[Card] = Random.shuffle(initDeck.toList)

    playRound(initialCredits, shuffledDeck)
  }

}
