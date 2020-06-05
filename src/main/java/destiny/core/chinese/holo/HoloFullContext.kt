/**
 * Created by smallufo on 2019-03-10.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.chinese.NaYin
import destiny.iching.IHexagramText
import destiny.iching.contentProviders.IHexagramProvider
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

class HoloFullContext(
  val holoContext: HoloContext,
  val poemParser: IPoemProvider,
  val lifeDescParser: ILifeDescProvider,
  val hexTextProvider: IHexagramProvider<IHexagramText>) : IHoloPoemContext, IHoloContext by holoContext, Serializable {

  override fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?): IPoemHolo {
    val holo = holoContext.getHolo(lmt, loc, gender, name, place)

    val locale = Locale.TAIWAN
    val hexagramCongenital = LifeHoloPoemHexagram(holo.hexagramCongenital,
      poemParser.getHexagramData(holo.hexagramCongenital, Locale.getDefault()) as IPoemHexagram,
      lifeDescParser.getHexagramData(holo.hexagramCongenital, Locale.getDefault()) as IHoloLifeDescHexagram,
      hexTextProvider.getHexagram(holo.hexagramCongenital, locale)
    )

    val hexagramAcquired = LifeHoloPoemHexagram(holo.hexagramAcquired,
      poemParser.getHexagramData(holo.hexagramAcquired, Locale.getDefault()) as IPoemHexagram,
      lifeDescParser.getHexagramData(holo.hexagramAcquired, Locale.getDefault()) as IHoloLifeDescHexagram,
      hexTextProvider.getHexagram(holo.hexagramAcquired, locale)
    )

    return PoemHolo(holo, hexagramCongenital, hexagramAcquired)
  }

  override fun getHoloWithTimeFull(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, gmt: Double, name: String?, place: String?): Pair<IPoemHolo, List<HoloFullHexagram>> {

    val locale = Locale.TAIWAN

    return getHoloWithTime(lmt, loc, gender, gmt, name, place).let { (holo: IHolo, list: List<IHoloHexagram>) ->
      val hexagramCongenital = LifeHoloPoemHexagram(holo.hexagramCongenital,
        poemParser.getHexagramData(holo.hexagramCongenital, Locale.getDefault()) as IPoemHexagram,
        lifeDescParser.getHexagramData(holo.hexagramCongenital, Locale.getDefault()) as IHoloLifeDescHexagram,
        hexTextProvider.getHexagram(holo.hexagramCongenital, locale)
      )
      val hexagramAcquired = LifeHoloPoemHexagram(holo.hexagramAcquired,
        poemParser.getHexagramData(holo.hexagramAcquired, Locale.getDefault()) as IPoemHexagram,
        lifeDescParser.getHexagramData(holo.hexagramAcquired, Locale.getDefault()) as IHoloLifeDescHexagram,
        hexTextProvider.getHexagram(holo.hexagramAcquired, locale)
      )
      val poemHolo = PoemHolo(holo, hexagramCongenital, hexagramAcquired)

      poemHolo to list.map { holoHexagram ->
        val hexText = hexTextProvider.getHexagram(holoHexagram, Locale.TAIWAN)

        // 如果是 流年、流月、流日 or 流時 卦象 , 尋找 金鎖銀匙的 歲運 歌訣
        val goldenKey = if (holoHexagram is IHoloHexagramWithStemBranch) {
          holoContext.goldenKeyProvider.getFlowGoldenKey(NaYin.getFiveElement(holo.ew.year), holo.ew.day.branch, holoHexagram.stemBranch.branch)
        } else null


        HoloFullHexagram(holoHexagram
          , poemParser.getHexagramData(holoHexagram, Locale.getDefault()) as IPoemHexagram
          , lifeDescParser.getHexagramData(holoHexagram, Locale.getDefault())
          , hexText, goldenKey
        )
      }
    }
  }

}
