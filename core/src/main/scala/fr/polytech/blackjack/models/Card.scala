package fr.polytech.blackjack.models

case class Card(suit: Suit, rank: Rank) {
  override def toString: String = s"$suit$rank (${rank.name} of ${suit.name})"
}
