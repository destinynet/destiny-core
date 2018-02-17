/**
 * Created by smallufo on 2018-02-17.
 */
package destiny.iching.divine

import destiny.iching.Hexagram
import destiny.iching.HexagramName
import destiny.iching.IHexagram
import destiny.iching.contentProviders.IHexagramNameFull
import destiny.iching.contentProviders.IHexagramNameShort
import java.util.*


interface IResult<out T : ICore> {
  fun getResult(): T
}

open class CoreContext(val src: IHexagram, val dst: IHexagram) : IResult<ICore> {
  override fun getResult(): ICore {
    val s = Hexagram.getHexagram(src)
    val d = Hexagram.getHexagram(dst)
    return Core(s, d)
  }
}

class CoreWithNameContext(val src: IHexagram,
                          val dst: IHexagram,
                          val locale: Locale = Locale.TAIWAN,
                          private val nameFullImpl: IHexagramNameFull,
                          private val nameShortImpl: IHexagramNameShort) : IResult<CoreWithNames> {
  override fun getResult(): CoreWithNames {
    val core: Core = CoreContext(src, dst).getResult() as Core
    val coreNames =
      CoreNames(HexagramName(nameShortImpl.getNameShort(src, locale), nameFullImpl.getNameFull(src, locale)),
                HexagramName(nameShortImpl.getNameShort(dst, locale), nameFullImpl.getNameFull(dst, locale)))
    return CoreWithNames(core, coreNames)
  }
}

class CoreWithMetaContext(val src: IHexagram,
                          val dst: IHexagram,
                          val locale: Locale = Locale.TAIWAN,
                          private val settings: ISettingsOfStemBranch,
                          private val hiddenEnergy: IHiddenEnergy) : IResult<ICoreWithMeta> {
  override fun getResult(): ICoreWithMeta {
    val core = CoreContext(src, dst).getResult() as Core
    val meta = Meta(settings.getTitle(locale), hiddenEnergy.getTitle(locale))

    return CoreWithMeta(core, meta)
  }
}

