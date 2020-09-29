package fr.polytech.blackjack.models

case class Hand(private val cards: List[Card]) {

  def containsAce: Boolean = cards.exists(c => c.rank == Rank.`🂡`)
  def value: Int = cards.foldLeft(0)((result, card) => result + card.rank.value)
  def specialValue: Option[Int] = if (containsAce) Some(value + 10) else None

  def isBlackJack: Boolean =
    value == Hand.blackjackValue || specialValue.contains(Hand.blackjackValue)
  def isBust: Boolean =
    value > Hand.blackjackValue

  def addCard(card: Card): Hand =
    copy(cards :+ card)
  def showCards(asDealer: Boolean): List[Card] =
    if (asDealer) cards.headOption.toList else cards

  def winsOver(other: Hand): Boolean = {
    val opponentBest = List(Some(other.value), other.specialValue).flatten.max
    val best = List(Some(value), specialValue).flatten.max
    best > opponentBest
  }
}

object Hand {

  val blackjackValue: Int = 21

  final case class Player(value: Hand) extends AnyVal {
    def addCard(card: Card): Player = copy(value.addCard(card))
  }
  object Player {
    def apply(cards: List[Card]): Player = Player(Hand(cards))
  }
  final case class Dealer(value: Hand) extends AnyVal {
    def addCard(card: Card): Dealer = copy(value.addCard(card))
  }
  object Dealer {
    def apply(cards: List[Card]): Dealer = Dealer(Hand(cards))
  }

}
