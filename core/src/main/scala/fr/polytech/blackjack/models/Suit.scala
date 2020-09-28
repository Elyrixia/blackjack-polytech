package fr.polytech.blackjack.models

import enumeratum._

sealed trait Suit extends EnumEntry {
  val name: String
}

object Suit extends Enum[Suit] {

  case object ♡ extends Suit {
    override val name: String = "Heart"
  }
  case object ♠ extends Suit {
    override val name: String = "Spade"
  }
  case object ♢ extends Suit {
    override val name: String = "Diamond"
  }
  case object ♣ extends Suit {
    override val name: String = "Club"
  }

  override val values: IndexedSeq[Suit] = findValues

}
