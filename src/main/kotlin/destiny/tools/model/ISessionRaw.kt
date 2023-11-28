/**
 * Created by smallufo on 2023-01-11.
 */
package destiny.tools.model

import destiny.core.IBirthDataNamePlace
import destiny.core.iching.divine.IPairHexQuestion
import destiny.core.oracles.IOracleQuestion
import destiny.core.tarot.ITarotQuestion
import kotlinx.serialization.Serializable


@Serializable
sealed interface ISessionRaw<T> {

  val domain: Domain

  val model: T

  /** 由什麼字串產生出此 [model] */
  val raw: String?

}

data class SessionEw(override val model: IBirthDataNamePlace, override val raw: String? = null) : ISessionRaw<IBirthDataNamePlace> {
  override val domain: Domain = Domain.EW
}

data class SessionZiwei(override val model: IBirthDataNamePlace, override val raw: String? = null) : ISessionRaw<IBirthDataNamePlace> {
  override val domain: Domain = Domain.ZIWEI
}

data class SessionIChingRand(override val model: IPairHexQuestion, override val raw: String? = null) : ISessionRaw<IPairHexQuestion> {
  override val domain: Domain = Domain.ICHING_RAND
}

data class SessionHoroscope(override val model: IBirthDataNamePlace, override val raw: String? = null) : ISessionRaw<IBirthDataNamePlace> {
  override val domain: Domain = Domain.HOROSCOPE
}

@Serializable
data class SessionTarot(override val model: ITarotQuestion, override val raw: String? = null) : ISessionRaw<ITarotQuestion> {
  override val domain: Domain = Domain.TAROT
}

@Serializable
data class SessionChance(override val model: IOracleQuestion, override val raw: String? = null) : ISessionRaw<IOracleQuestion> {
  override val domain: Domain = Domain.CHANCE
}
