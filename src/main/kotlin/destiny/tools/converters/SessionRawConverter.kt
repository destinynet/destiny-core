package destiny.tools.converters

import destiny.core.IBirthDataNamePlace
import destiny.core.iching.divine.IPairHexQuestion
import destiny.core.oracles.IOracleQuestion
import destiny.core.tarot.ITarotQuestion
import destiny.tools.model.*


class SessionRawConverter<T>(val domain: Domain, private val modelConverter: IContextMap<T>) : IContextMap<ISessionRaw<T>> {

  override fun getMap(context: ISessionRaw<T>): Map<String, String> {
    return buildMap {
      putAll(DomainConverter.getMap(context.domain))
      context.raw?.also {
        put(RAW, it)
      }
      putAll(modelConverter.getMap(context.model))
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun getContext(map: Map<String, String>): ISessionRaw<T>? {
    return DomainConverter.getContext(map)?.let { domain ->
      val raw: String? = map[RAW]

      modelConverter.getContext(map)?.let { model: T ->
        when (domain) {
          Domain.ZIWEI       -> SessionZiwei(model as IBirthDataNamePlace, raw) as ISessionRaw<T>
          Domain.ICHING_RAND -> SessionIChingRand(model as IPairHexQuestion, raw) as ISessionRaw<T>
          Domain.HOROSCOPE   -> SessionHoroscope(model as IBirthDataNamePlace, raw) as ISessionRaw<T>
          Domain.TAROT       -> SessionTarot(model as ITarotQuestion, raw) as ISessionRaw<T>
          Domain.CHANCE      -> SessionChance(model as IOracleQuestion, raw) as ISessionRaw<T>
        }
      }
    }
  }

  companion object {
    const val RAW = "raw"
  }

}
