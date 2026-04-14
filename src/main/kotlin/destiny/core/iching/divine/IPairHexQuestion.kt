package destiny.core.iching.divine

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.locationOf
import destiny.core.iching.IHexagram
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import java.util.*

interface IPairHexQuestion : ICombined {
  override val src: IHexagram
  override val dst: IHexagram
  val gender: Gender?
  val question: String?
}

@Serializable
data class PairHexQuestion(
  @Contextual override val src: IHexagram,
  @Contextual override val dst: IHexagram,
  override val gender: Gender? = null,
  override val question: String?
) : IPairHexQuestion

interface IRandomHex : IPairHexQuestion {
  val lmt: ChronoLocalDateTime<*>
  val loc: ILocation
}



data class RandomHex(private val pairHexQuestion: IPairHexQuestion,
                     override val lmt: ChronoLocalDateTime<*> = LocalDateTime.now(),
                     override val loc: ILocation = locationOf(Locale.TAIWAN)) : IRandomHex , IPairHexQuestion by pairHexQuestion
