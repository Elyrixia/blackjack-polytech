package fr.polytech.blackjack

import fr.polytech.blackjack.models.{Card, Rank, Suit}
import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Try

class BlackjackSpec extends AnyFlatSpec {

  val blackjack = new Blackjack(100)

  behavior of "startNewRound"

  it should "disallow a new round if credits are below 0" in {
    assert(blackjack.startNewRound(-50, List.empty).isLeft)
  }

  it should "disallow a new round if deck is too small" in {
    assert(blackjack.startNewRound(50, List.empty).isLeft)
  }

  it should "allow a new round otherwise" in {
    val deck = for {
      suit <- Suit.values
      rank <- Rank.values
    } yield Card(suit, rank)
    assert(blackjack.startNewRound(50, deck.toList).isRight)
  }

  behavior of "bet"

  it should "allow to bet an amount below the current credits" in {
    assert(blackjack.bet(100, Try(5)).isSuccess)
  }

  it should "disallow to bet an amount above the current credits" in {
    assert(blackjack.bet(100, Try(150)).isFailure)
  }

}
