package fr.polytech.blackjack

import fr.polytech.blackjack.models.{Card, Hand, Rank, Suit}

import scala.io.StdIn
import scala.util.{Random, Success, Try}

class Blackjack(initialCredits: Int) {

  private def readString(message: String): Try[String] = Try(StdIn.readLine(message + ": "))
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
    println()
    println("Current hands")
    println(s"Dealer hand ${dealerHand.showCards(asDealer = maskDealerCards).mkString(",")}")
    println(s"Player hand ${playerHand.showCards(asDealer = false).mkString(",")}")
  }

  private def checkCurrentResultForHand(hand: Hand): Blackjack.Result = {
    if (hand.isBlackJack) Blackjack.Result.Blackjack
    else if (hand.isBust) Blackjack.Result.Busted
    else Blackjack.Result.Unknown
  }

  private def checkFinalResult(playerHand: Hand, dealerHand: Hand): Blackjack.Result = {
    if (dealerHand.isBlackJack) Blackjack.Result.Loser
    else if (playerHand.isBlackJack) Blackjack.Result.Blackjack
    else if (playerHand.isBust) Blackjack.Result.Busted
    else if (dealerHand.isBust || playerHand.winsOver(dealerHand)) Blackjack.Result.Winner
    else Blackjack.Result.Loser
  }

  private def playerSteps(playerHand: Hand, deck: List[Card], dealerHand: Hand): Try[(Hand, List[Card])] = {
    showHands(playerHand, dealerHand, maskDealerCards = true)
    checkCurrentResultForHand(playerHand) match {
      case Blackjack.Result.Blackjack | Blackjack.Result.Busted =>
        Success((playerHand, deck))

      case _ =>
        println()
        readString("Do you want to hit (H) or stand (S) ?").flatMap {

          case "H" | "h" | "Hit" | "hit" =>

            val (newCard, restOfDeck) = deck.splitAt(1)
            println()
            println(s"== Player drew the card ${newCard.head}")
            val newHand = playerHand.addCard(newCard.head)
            playerSteps(newHand, restOfDeck, dealerHand)

          case "S" | "s" | "Stand" | "stand" =>

            println("Not drawing any new card, good luck !")
            Success((playerHand, deck))

          case _ =>

            println("Unknown decision, considering a stand, good luck !")
            Success((playerHand, deck))
        }
    }

  }

  private def dealerSteps(dealerHand: Hand, deck: List[Card], playerHand: Hand): Try[(Hand, List[Card])] = {
    showHands(playerHand, dealerHand, maskDealerCards = false)
    if (dealerHand.value >= 17) Success((dealerHand, deck))
    else {
      checkCurrentResultForHand(dealerHand) match {
        case Blackjack.Result.Blackjack | Blackjack.Result.Busted =>
          Success((dealerHand, deck))

        case _ =>
          println()
          val (newCard, restOfDeck) = deck.splitAt(1)
          println(s"== Dealer drew the card ${newCard.head}")
          val newHand = dealerHand.addCard(newCard.head)
          dealerSteps(newHand, restOfDeck, playerHand)
      }
    }
  }

  private def playRound(
    credits: Int,
    deck: List[Card]
  ): Try[(Int, Int, List[Card], Blackjack.Result)] = {
    for {
      _ <- startNewRound(credits, deck).toTry
      betValue <- bet(credits)
      (playerHand, dealerHand, restOfTheDeck) = dealCards(deck)
      _ = showHands(playerHand, dealerHand, maskDealerCards = true)
      (playerHand, restOfTheDeck) <- {
        if (playerHand.isBlackJack)
          Success((playerHand, restOfTheDeck))
        else {
          println()
          println("== Player turn")
          playerSteps(playerHand, restOfTheDeck, dealerHand)
        }
      }
      (dealerHand, restOfTheDeck) <- {
        if (playerHand.isBlackJack || playerHand.isBust)
          Success(dealerHand, restOfTheDeck)
        else {
          println()
          println("== Dealer turn")
          dealerSteps(dealerHand, restOfTheDeck, playerHand)
        }
      }
    } yield
      (credits, betValue, restOfTheDeck, checkFinalResult(playerHand, dealerHand))
  }

  def play(currentCredits: Int, currentDeck: List[Card]): Try[(Int, Int, List[Card], Blackjack.Result)] = {
    playRound(currentCredits, currentDeck).flatMap {
      case res@(currentCredits, roundBetValue, restOfTheDeck, roundResult) =>
        val newCredits = roundResult match {
          case Blackjack.Result.Loser | Blackjack.Result.Busted =>
            currentCredits - roundBetValue
          case Blackjack.Result.Blackjack =>
            currentCredits + roundBetValue * 2
          case Blackjack.Result.Winner =>
            currentCredits + roundBetValue
          case Blackjack.Result.Unknown =>
            currentCredits
        }

        println()
        println(s"The result of this round is $roundResult")
        println(s"Your current credits are $newCredits")
        println()
        if (newCredits <= 0) {
          println("You don't have any credits left, sorry !")
          Success(res)
        }
        else {
          readString("Do you want to play again ? Press y or anything else to quit")
            .flatMap {
              case "y" => play(newCredits, restOfTheDeck)
              case _ => Success(res)
            }
        }
    }
  }

  def start(): Try[(Int, Int)] = {
    println(s"Starting the blackjack game with a credit of $initialCredits")
    def initDeck =
      for {
        suit <- Suit.values
        rank <- Rank.values
      } yield Card(suit, rank)

    val shuffledDeck: List[Card] = Random.shuffle(initDeck.toList)

    play(initialCredits, shuffledDeck).map {
      case (finalCredits, _, _, _) => (initialCredits, finalCredits)
    }
  }

}

object Blackjack {

  sealed trait Result

  object Result {
    case object Blackjack extends Result
    case object Busted extends Result
    case object Winner extends Result
    case object Loser extends Result
    case object Unknown extends Result
  }

}