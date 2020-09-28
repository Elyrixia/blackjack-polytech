package fr.polytech.blackjack.models

import enumeratum._

sealed trait Rank extends EnumEntry {
  val value: Int
  val name: String
}

object Rank extends Enum[Rank] {

  case object `🂡` extends Rank {
    override val value: Int = 1
    override val name: String = "Ace"
  }

  case object `🂢` extends Rank {
    override val value: Int = 2
    override val name: String = "Two"
  }

  case object `🂣` extends Rank {
    override val value: Int = 3
    override val name: String = "Three"
  }

  case object `🂤` extends Rank {
    override val value: Int = 4
    override val name: String = "Four"
  }

  case object `🂥` extends Rank {
    override val value: Int = 5
    override val name: String = "Five"
  }

  case object `🂦` extends Rank {
    override val value: Int = 6
    override val name: String = "Six"
  }

  case object `🂧` extends Rank {
    override val value: Int = 7
    override val name: String = "Seven"
  }

  case object `🂨` extends Rank {
    override val value: Int = 8
    override val name: String = "Eight"
  }

  case object `🂩` extends Rank {
    override val value: Int = 9
    override val name: String = "Nine"
  }

  case object `🂪` extends Rank {
    override val value: Int = 10
    override val name: String = "Ten"
  }

  case object `🂫` extends Rank {
    override val value: Int = 10
    override val name: String = "Jack"
  }

  case object `🂭` extends Rank {
    override val value: Int = 10
    override val name: String = "Queen"
  }

  case object `🂮` extends Rank {
    override val value: Int = 10
    override val name: String = "King"
  }

  override val values: IndexedSeq[Rank] = findValues

}
