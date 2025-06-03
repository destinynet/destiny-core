/**
 * Created by smallufo on 2021-10-01.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.NaYin
import destiny.core.iching.IHexagramText
import destiny.core.iching.contentProviders.IHexagramProvider
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.PersonFeature
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import javax.cache.Cache


@Serializable
data class HoloFullConfig(val viewGmtJulDay: GmtJulDay = GmtJulDay.nowCeilingToNoon(),
                          val holoConfig: HoloConfig = HoloConfig()): java.io.Serializable

interface IHoloFullFeature : PersonFeature<HoloFullConfig, Pair<IPoemHolo, List<HoloFullHexagram>>> {

  /** 取得 先天卦、後天卦 , 元氣、化工 等資訊 */
  fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloConfig = HoloConfig()): IPoemHolo
}

@Named
class HoloFullFeature(private val holoFeature: HoloFeature,
                      private val poemParser: IPoemProvider,
                      private val lifeDescParser: ILifeDescProvider,
                      private val goldenKeyProvider: IGoldenKeyProvider,
                      private val hexTextProvider: IHexagramProvider<IHexagramText>,
                      private val julDayResolver: JulDayResolver) : AbstractCachedPersonFeature<HoloFullConfig, Pair<IPoemHolo, List<HoloFullHexagram>>>(), IHoloFullFeature {

  @Inject
  @Transient
  private lateinit var holoFullCache: Cache<LmtCacheKey<*>, Pair<*, *>>

  override val lmtPersonCache: Cache<LmtCacheKey<HoloFullConfig>, Pair<IPoemHolo, List<HoloFullHexagram>>>
    get() {
      return holoFullCache as Cache<LmtCacheKey<HoloFullConfig> , Pair<IPoemHolo, List<HoloFullHexagram>>>
    }

  override val key: String = "holoFullFeature"


  override val defaultConfig: HoloFullConfig = HoloFullConfig()

  private fun getCongenital(holo: IHolo, locale: Locale): LifeHoloPoemHexagram {
    return LifeHoloPoemHexagram(holo.hexagramCongenital,
                                poemParser.getHexagramData(holo.hexagramCongenital, Locale.getDefault()) as IPoemHexagram,
                                lifeDescParser.getHexagramData(holo.hexagramCongenital, Locale.getDefault()) as IHoloLifeDescHexagram,
                                hexTextProvider.getHexagram(holo.hexagramCongenital, locale)
    )
  }

  private fun getAcquired(holo: IHolo, locale: Locale): LifeHoloPoemHexagram {
    return LifeHoloPoemHexagram(holo.hexagramAcquired,
                                poemParser.getHexagramData(holo.hexagramAcquired, Locale.getDefault()) as IPoemHexagram,
                                lifeDescParser.getHexagramData(holo.hexagramAcquired, Locale.getDefault()) as IHoloLifeDescHexagram,
                                hexTextProvider.getHexagram(holo.hexagramAcquired, locale)
    )
  }

  override fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloConfig): IPoemHolo {
    val holo: IHolo = holoFeature.getPersonModel(lmt, loc, gender, name, place, config)

    val locale = Locale.TAIWAN

    val hexagramCongenital = getCongenital(holo, locale)
    val hexagramAcquired = getAcquired(holo, locale)

    return PoemHolo(holo, hexagramCongenital, hexagramAcquired)
  }


  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloFullConfig): Pair<IPoemHolo, List<HoloFullHexagram>> {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }


  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloFullConfig): Pair<IPoemHolo, List<HoloFullHexagram>> {

    val locale = Locale.TAIWAN

    holoFeature.getHoloWithTime(lmt, loc, gender, config.viewGmtJulDay, name, place, config.holoConfig).let { (holo: IHolo, list: List<IHoloHexagram>) ->

      val hexagramCongenital = getCongenital(holo, locale)
      val hexagramAcquired = getAcquired(holo, locale)

      val poemHolo = PoemHolo(holo, hexagramCongenital, hexagramAcquired)

      return poemHolo to list.map { holoHexagram ->
        val hexText = hexTextProvider.getHexagram(holoHexagram, Locale.TAIWAN)

        // 如果是 流年、流月、流日 or 流時 卦象 , 尋找 金鎖銀匙的 歲運 歌訣
        val goldenKey = if (holoHexagram is IHoloHexagramWithStemBranch) {
          goldenKeyProvider.getFlowGoldenKey(NaYin.getFiveElement(holo.ew.year), holo.ew.day.branch, holoHexagram.stemBranch.branch)
        } else null


        HoloFullHexagram(holoHexagram
                         , poemParser.getHexagramData(holoHexagram, Locale.getDefault()) as IPoemHexagram
                         , lifeDescParser.getHexagramData(holoHexagram, Locale.getDefault())
                         , hexText, goldenKey
        )
      }
    }
  }


  companion object {
    const val CACHE_HOLO_FULL_FEATURE = "holoFullFeatureCache"
  }

}
