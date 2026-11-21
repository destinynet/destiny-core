/**
 * Created by smallufo on 2021-09-19.
 */
package destiny.core.chinese.onePalm

import destiny.tools.KvCache
import destiny.core.Gender
import destiny.core.Scale
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.*
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.Branch
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.JSerializable
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime

enum class PositiveImpl {
  Gender,
  GenderYinYang
}

/**
 * 一掌經設定。
 *
 * 年支與月數出自同一套曆法，由 [monthAlgo] 決定：
 * [MonthAlgo.MONTH_SOLAR_TERMS] 用節氣（立春換年、節氣月）；其餘三種用農曆（正月初一換年、農曆月數，閏月依各自規則）。
 * 日數一律是農曆的初幾。
 */
@Serializable
data class PalmConfig(
  val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
  val positiveImpl: PositiveImpl = PositiveImpl.Gender,
  val monthAlgo: MonthAlgo = MonthAlgo.MONTH_LEAP_SPLIT15,
  val risingSignConfig: RisingSignConfig = RisingSignConfig(),
  val trueRisingSign: Boolean = false,
  val clockwiseHouse: Boolean = true,
  /** 農曆的月表與換月規則；日界跟著 [eightWordsConfig] 的換日設定 */
  val lunisolar: LunisolarConfig = LunisolarConfig(),
) : JSerializable


class PalmFeature(private val eightWordsFeature: EightWordsFeature,
                  private val chineseDateFeature: ChineseDateFeature,
                  private val risingSignFeature: RisingSignFeature,
                  private val julDayResolver: JulDayResolver) : AbstractCachedPersonFeature<PalmConfig, IPalmModel>() {

  override val key: String = "palmFeature"

  override val defaultConfig: PalmConfig = PalmConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: PalmConfig): IPalmModel {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }

  /**
   * 沒帶入節氣資料：[yearBranch] 是農曆年支，[monthNum]／[leap] 是農曆月。
   * [MonthAlgo.MONTH_SOLAR_TERMS] 需要節氣月，這裡不收。
   */
  fun getPalmWithoutSolarTerms(gender: Gender, yearBranch: Branch, leap: Boolean, monthNum: Int, dayNum: Int, hourBranch: Branch, config: PalmConfig): IPalmModel {
    val monthNumber = PalmMonthNumber(monthNum, leap, IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, dayNum, config.monthAlgo))
    return palmOf(gender, yearBranch, monthNumber, dayNum, hourBranch, null, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: PalmConfig): IPalmModel {

    val ew = eightWordsFeature.getModel(lmt, loc, config.eightWordsConfig)
    val chineseDate: ChineseDate = chineseDateFeature.getModel(lmt, loc, ChineseDateConfig(config.lunisolar, config.eightWordsConfig.dayHourConfig))

    // 年支與月數同一套曆法：節氣盤用立春年，其餘用農曆年
    val yearBranch: Branch = when (config.monthAlgo) {
      MonthAlgo.MONTH_SOLAR_TERMS -> ew.year.branch
      else                        -> chineseDate.year.branch
    }

    val monthNumber = PalmMonthNumber(
      chineseDate.month, chineseDate.leapMonth,
      IFinalMonthNumber.getFinalMonthNumber(chineseDate.month, chineseDate.leapMonth, ew.month.branch, chineseDate.day, config.monthAlgo)
    )

    // 上升星座
    val trueRising: Branch? = if (config.trueRisingSign) risingSignFeature.getModel(lmt, loc, config.risingSignConfig).branch else null

    return palmOf(gender, yearBranch, monthNumber, chineseDate.day, ew.hour.branch, trueRising, config)
  }

  /** @param trueRising 非 null 時以它為命宮（上升星座），否則以傳統法（生時起時、數到卯） */
  private fun palmOf(gender: Gender, yearBranch: Branch, monthNumber: PalmMonthNumber, dayNum: Int, hourBranch: Branch,
                     trueRising: Branch?, config: PalmConfig): IPalmModel {
    val positive = when (config.positiveImpl) {
      PositiveImpl.Gender        -> gender == Gender.M
      PositiveImpl.GenderYinYang -> gender === Gender.M && yearBranch.index % 2 == 0 || gender === Gender.F && yearBranch.index % 2 == 1
    }

    val positiveValue = if (positive) 1 else -1

    // 年上起月
    val month: Branch = yearBranch.next((monthNumber.number - 1) * positiveValue)

    // 月上起日
    val day = month.next((dayNum - 1) * positiveValue)

    // 日上起時
    val hour = day.next(hourBranch.index * positiveValue)

    // 命宮
    val main: Branch = trueRising ?: hour.next(Branch.卯.getAheadOf(hourBranch) * positiveValue)

    val houseMap = (0..11).map { i -> if (config.clockwiseHouse) main.next(i) else main.prev(i) }
      .zip(IPalmModel.House.entries.toTypedArray()).toMap()

    return PalmModel(gender, positive, yearBranch, monthNumber, month, day, hour, houseMap)
  }
}

/**
 * 相同的 [PalmConfig] , 傳回 [IPalmMetaModel]
 */
class PalmMetaFeature(private val palmFeature: PersonFeature<PalmConfig, IPalmModel>,
                      private val chineseDateFeature: ChineseDateFeature,
                      private val hourBranchFeature: IHourBranchFeature,
                      private val julDayResolver: JulDayResolver) : AbstractCachedPersonFeature<PalmConfig, IPalmMetaModel>() {

  override val key: String = "palmMetaFeature"

  override val defaultConfig: PalmConfig = palmFeature.defaultConfig


  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: PalmConfig): IPalmMetaModel {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }


  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: PalmConfig): IPalmMetaModel {
    val palmModel: IPalmModel = palmFeature.getPersonModel(lmt, loc, gender, name, place, config)
    val cDate = chineseDateFeature.getModel(lmt, loc, ChineseDateConfig(config.lunisolar, config.eightWordsConfig.dayHourConfig))
    val hourBranch = hourBranchFeature.getModel(lmt, loc, config.eightWordsConfig.dayHourConfig.hourBranchConfig)

    return PalmMetaModel(palmModel, lmt, loc, place, name, ChineseDateHour(cDate, hourBranch))
  }
}

class PalmMetaDescFeature(private val palmMetaFeature : PersonFeature<PalmConfig, IPalmMetaModel>,
                          private val branchDescImpl: IBranchDesc,
                          private val julDayResolver: JulDayResolver ,
                          @Transient
                          private val palmMetaDescCache : KvCache<LmtCacheKey<*>, IPalmMetaModelDesc>) : AbstractCachedPersonFeature<PalmConfig, IPalmMetaModelDesc>() {

  override val key: String = "palmMetaDescFeature"

  override val defaultConfig: PalmConfig = palmMetaFeature.defaultConfig

  override val lmtPersonCache: KvCache<LmtCacheKey<PalmConfig>, IPalmMetaModelDesc>
    @Suppress("UNCHECKED_CAST")
    get() = palmMetaDescCache as KvCache<LmtCacheKey<PalmConfig>, IPalmMetaModelDesc>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: PalmConfig): IPalmMetaModelDesc {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: PalmConfig): IPalmMetaModelDesc {
    val palmMetaModel: IPalmMetaModel = palmMetaFeature.getPersonModel(lmt, loc, gender, name, place, config)
    val palmModelDesc: IPalmModelDesc = getPalmModelDesc(palmMetaModel)
    return PalmMetaModelDesc(palmMetaModel, palmModelDesc)
  }

  private fun getPalmModelDesc(palmModel: IPalmModel): IPalmModelDesc {
    val houseDescriptions = palmModel.nonEmptyPillars.keys.map { branch ->
      val dao = IPalmModel.getDao(branch)
      val star = IPalmModel.getStar(branch)
      val houseIntro = branchDescImpl.getHouseIntro(branch)
      val map = palmModel.getPillars(branch).associateWith { pillar ->
        branchDescImpl.getContent(pillar, branch)
      }
      HouseDescription(branch, dao, star, houseIntro, map)
    }.toList()

    val hourBranch = palmModel.getBranch(Scale.HOUR)
    val hourPoem = branchDescImpl.getPoem(hourBranch)
    val hourContent = branchDescImpl.getContent(hourBranch)

    return PalmModelDesc(houseDescriptions, hourPoem, hourContent)
  }

  companion object {
    const val CACHE_PALM_META_DESC_FEATURE = "palmMetaDescFeatureCache"
  }


}
