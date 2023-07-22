/**
 * Created by smallufo on 2021-09-19.
 */
package destiny.core.chinese.onePalm

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
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache

enum class PositiveImpl {
  Gender,
  GenderYinYang
}

@Serializable
data class PalmConfig(
  val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
  val positiveImpl: PositiveImpl = PositiveImpl.Gender,
  val monthAlgo: MonthAlgo = MonthAlgo.MONTH_FIXED_THIS,
  val risingSignConfig: RisingSignConfig = RisingSignConfig(),
  val trueRisingSign: Boolean = false,
  val clockwiseHouse: Boolean = true
) : java.io.Serializable

context(IEightWordsConfig , IRisingSignConfig)
@DestinyMarker
class PalmConfigBuilder : Builder<PalmConfig> {

  var positiveImpl: PositiveImpl = PositiveImpl.Gender

  var monthAlgo: MonthAlgo = MonthAlgo.MONTH_FIXED_THIS

  var trueRisingSign: Boolean = false

  var clockwiseHouse: Boolean = true

  override fun build(): PalmConfig {
    return PalmConfig(ewConfig, positiveImpl, monthAlgo, risingSignConfig, trueRisingSign, clockwiseHouse)
  }

  companion object {
    context(IEightWordsConfig, IRisingSignConfig)
    fun palmConfig(block: PalmConfigBuilder.() -> Unit = {}): PalmConfig {
      return PalmConfigBuilder().apply(block).build()
    }
  }
}


@Named
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

  /** 沒帶入節氣資料 */
  fun getPalmWithoutSolarTerms(gender: Gender, yearBranch: Branch, leap: Boolean, monthNum: Int, dayNum: Int, hourBranch: Branch, config: PalmConfig): IPalmModel {
    val positive = when (config.positiveImpl) {
      PositiveImpl.Gender        -> gender == Gender.男
      PositiveImpl.GenderYinYang -> gender === Gender.男 && yearBranch.index % 2 == 0 || gender === Gender.女 && yearBranch.index % 2 == 1
    }

    val positiveValue = if (positive) 1 else -1

    val finalMonthNum = IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, dayNum, config.monthAlgo)

    // 年上起月
    val month = yearBranch.next((finalMonthNum - 1) * positiveValue)

    // 月上起日
    val day = month.next((dayNum - 1) * positiveValue)

    // 日上起時
    val hour = day.next(hourBranch.index * positiveValue)

    // 命宮
    val steps = Branch.卯.getAheadOf(hourBranch)
    val main = hour.next(steps * positiveValue)

    val houseMap = (0..11).map { i -> if (config.clockwiseHouse) main.next(i) else main.prev(i) }.zip(IPalmModel.House.values()).toMap()

    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: PalmConfig): IPalmModel {

    val ew = eightWordsFeature.getModel(lmt, loc)

    val positive = when (config.positiveImpl) {
      PositiveImpl.Gender        -> gender == Gender.男
      PositiveImpl.GenderYinYang -> gender === Gender.男 && ew.year.branch.index % 2 == 0 || gender === Gender.女 && ew.year.branch.index % 2 == 1
    }

    val positiveValue = if (positive) 1 else -1

    val chineseDate: ChineseDate = chineseDateFeature.getModel(lmt, loc)

    val finalMonthNum = IFinalMonthNumber.getFinalMonthNumber(chineseDate.month, chineseDate.leapMonth, ew.month.branch, chineseDate.day, config.monthAlgo)

    // 年上起月
    val month: Branch = ew.year.branch.next((finalMonthNum - 1) * positiveValue)

    // 月上起日
    val day = month.next((chineseDate.day - 1) * positiveValue)

    // 日上起時
    val hour = day.next(ew.hour.branch.index * positiveValue)

    // 上升星座
    val trueRising: Branch = risingSignFeature.getModel(lmt, loc).branch

    // 命宮
    val main: Branch = when (config.trueRisingSign) {
      true -> trueRising
      false -> {
        val steps = Branch.卯.getAheadOf(ew.hour.branch)
        hour.next(steps * positiveValue)
      }
    }

    val houseMap = (0..11).map { i -> if (config.clockwiseHouse) main.next(i) else main.prev(i) }
      .zip(IPalmModel.House.values()).toMap()

    return PalmModel(gender, ew.year.branch, month, day, hour, houseMap)
  }
}

/**
 * 相同的 [PalmConfig] , 傳回 [IPalmMetaModel]
 */
@Named
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
    val cDate = chineseDateFeature.getModel(lmt, loc)
    val hourBranch = hourBranchFeature.getModel(lmt, loc, config.eightWordsConfig.dayHourConfig.hourBranchConfig)

    return PalmMetaModel(palmModel, lmt, loc, place, name, ChineseDateHour(cDate, hourBranch))
  }
}

@Named
class PalmMetaDescFeature(private val palmMetaFeature : PersonFeature<PalmConfig, IPalmMetaModel>,
                          private val branchDescImpl: IBranchDesc,
                          private val julDayResolver: JulDayResolver ,
                          @Transient
                          private val palmMetaDescCache : Cache<LmtCacheKey<*>, IPalmMetaModelDesc>) : AbstractCachedPersonFeature<PalmConfig, IPalmMetaModelDesc>() {

  override val key: String = "palmMetaDescFeature"

  override val defaultConfig: PalmConfig = palmMetaFeature.defaultConfig

  override val lmtPersonCache: Cache<LmtCacheKey<PalmConfig>, IPalmMetaModelDesc>
    get() = palmMetaDescCache as Cache<LmtCacheKey<PalmConfig>, IPalmMetaModelDesc>

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
