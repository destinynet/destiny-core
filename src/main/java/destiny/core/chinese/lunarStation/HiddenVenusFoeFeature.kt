/**
 * Created by smallufo on 2021-08-21.
 */
package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.Companion.aheadOf
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.tools.AbstractCachedFeature
import jakarta.inject.Named
import java.time.chrono.ChronoLocalDateTime

/**
 * 暗金伏斷
 */
@Named
class HiddenVenusFoeFeature(private val yearlyFeature: LunarStationYearlyFeature,
                            private val monthlyFeature: ILunarStationMonthlyFeature,
                            private val dailyFeature: LunarStationDailyFeature,
                            private val hourlyFeature: LunarStationHourlyFeature,
                            private val eightWordsFeature : EightWordsFeature,
                            private val chineseDateFeature: ChineseDateFeature,
                            private val julDayResolver: JulDayResolver) : AbstractCachedFeature<LunarStationConfig, Set<Pair<Scale, Scale>>>() {

  override val key: String = "hiddenVenusFoe"

  override val defaultConfig: LunarStationConfig = LunarStationConfig()

  /**
   * @return Pair<Scale,Scale> 前者代表「此時刻的什麼時段」 , 犯了 後者的 暗金伏斷煞
   */
  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: LunarStationConfig): Set<Pair<Scale, Scale>> {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: LunarStationConfig): Set<Pair<Scale, Scale>> {
    val yearly = yearlyFeature.getModel(lmt, loc, config.yearlyConfig).station
    val ew: IEightWords = eightWordsFeature.getModel(lmt, loc, config.ewConfig)
    val chineseDate = chineseDateFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig)
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month,
      chineseDate.leapMonth,
      ew.month.branch,
      chineseDate.day,
      config.monthlyConfig.monthAlgo
    )


    val monthlyStation = monthlyFeature.getMonthly(yearly, monthNumber, config.monthlyConfig.impl)
    val dailyStation = dailyFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig).station()
    val hourlyStation = hourlyFeature.getModel(lmt, loc, config.hourlyConfig)

    return mutableSetOf<Pair<Scale, Scale>>().apply {
      // 年
      if (isDayFoeForYear(yearly.planet, ew.day.branch, dailyStation)) {
        add(Scale.DAY to Scale.YEAR)
      }

      // 月
      if (monthFoeMap[monthNumber] == monthlyStation) {
        add(Scale.MONTH to Scale.MONTH)
      }

      // 日
      if (isDayFoeForDay(dailyStation, ew.day.branch)) {
        add(Scale.DAY to Scale.DAY)
      }

      // 時
      if (isHourFoeForHour(hourlyStation, ew.hour.branch)) {
        add(Scale.HOUR to Scale.HOUR)
      }
    }.toSet()

  }

  companion object {
    /**
     * 暗金
     *
     * 《選擇求真》
     * 暗金伏斷，訣曰：「七曜禽星會者稀，日虛月鬼火從箕；水畢木氐金奎位，土宿還從翼上推」。
     * 暗金伏斷者，取亢、婁、牛、鬼四金宿，以金能斷物，故名暗金伏斷，每以值年星在子上，尋起例之星也。
     * 七政值年，以日、月、火、水、木、金、土為度，週而復始，自康熙二十三年上元甲子畢月烏值年，至乾隆九年中元甲子鬼金羊值年，
     * 又至嘉慶九年下元甲子翼火蛇值年，至次上元甲子係氐土貉值年也。
     *
     * 如房、虛、昴、星四日星值年，從子上起虛宿順數，婁金在巳，故巳日房宿是暗金伏斷。
     * 如心、危、畢、張四月星值年，從子上起鬼宿順數，鬼金在子，亢金在未，故子日虛宿，未日張宿是暗金伏斷。
     * 如尾、室、觜、翼四火星值年，從子上起箕宿順數，亢金在寅，婁金在酉，故寅日室宿，酉日觜宿為暗金伏斷。
     * 如箕、璧、參、軫四水星值年，從子上起畢宿順數，鬼金在辰，亢金在亥，故辰日箕宿，亥日璧宿為暗金伏斷。
     * 如角、斗、奎、井四木星值年，從子上起氐宿順數，牛金在午，故午日角宿是暗金伏斷。
     * 如亢、婁、牛、鬼四金宿值年，從子上起奎宿順數，婁金在丑，鬼金在申，故丑日斗宿是暗金伏斷。
     * 如氐、女、胃、昴四土星值年，從子上起翼宿順數，亢金在卯，牛金在戌，故卯日女宿，戌日胃宿是暗金伏斷。
     */
    fun getYearHiddenVenus(yearPlanet: Planet): Set<Branch> {
      val ziStart: LunarStation = 虛.next(yearPlanet.aheadOf(Planet.SUN) * 12)
      return generateSequence((0 to ziStart)) {
        (it.first + 1 to it.second.next)
      }.take(12)
        .filter { it.second.planet == Planet.VENUS }
        .map { it.first }
        .map { 子.next(it) }
        .toSet()
    }


    /**
     * 此日是否犯了此年的「暗金伏斷」
     *
     * 《選擇求真》
     * 暗金伏斷，訣曰：「七曜禽星會者稀，日虛月鬼火從箕；水畢木氐金奎位，土宿還從翼上推」。
     * 暗金伏斷者，取亢、婁、牛、鬼四金宿，以金能斷物，故名暗金伏斷，每以值年星在子上，尋起例之星也。
     * 七政值年，以日、月、火、水、木、金、土為度，週而復始，自康熙二十三年上元甲子畢月烏值年，至乾隆九年中元甲子鬼金羊值年，
     * 又至嘉慶九年下元甲子翼火蛇值年，至次上元甲子係氐土貉值年也。
     *
     */
    fun isDayFoeForYear(yearPlanet: Planet, dayBranch: Branch, dayStation: LunarStation): Boolean {
      return (getYearHiddenVenus(yearPlanet).contains(dayBranch) && foeMap[dayBranch] == dayStation)
    }

    /**
     * 月禽伏斷
     * 關於四月 ，鍾義明 書中寫 [壁] , 但是根據 「太陰行度法歌」 , 四從 [畢] , 誰對誰誤 暫時不詳
     * 八月也不一致
     * 正室、二奎、三胃、四壁(畢？)、五井、六柳、七張、八軫 (翼?)、九角、十房、十一箕、十二牛
     *
     * 【約太陰行度法歌】
     * 欲識太陽行度時，正月之節起子危，
     * 一日出行十三度，五日兩富次第移。
     * 二奎三胃四從畢，五井六柳張居七，
     * 八月翼宿以為初，龍角季秋任遊歷，
     * 十月房宿作元辰，建子箕星細尋覓，
     * 丑月牽牛切要知，周天之度無差式，
     * 此是太陰行度方，人命身宮從此得。
     */
    val monthFoeMap: Map<Int, LunarStation> by lazy {
      mapOf(
        1 to 室, 2 to 奎, 3 to 胃, 4 to 壁,
        5 to 井, 6 to 柳, 7 to 張, 8 to 軫,
        9 to 角, 10 to 房, 11 to 箕, 12 to 牛
      )
    }

    /**
     * 七元伏斷 日
     * 此日是否犯了 日禽 的 暗金伏斷
     * */
    fun isDayFoeForDay(dailyStation: LunarStation, dayBranch: Branch): Boolean {
      return (foeMap[dayBranch] == dailyStation)
    }

    /**
     * 暗金伏斷時
     * 太陽子酉寅，太陰午一日。火宿丑卯戌，金申木亥辰。水未土來巳，伏斷寅時真。
     */
    fun isHourFoeForHour(hourlyStation: LunarStation, hourBranch: Branch): Boolean {
      return (foeMap[hourBranch] == hourlyStation)
    }

    /**
     * 伏斷者，即子日時逢虛，丑日時逢斗是也，此為惡宮。
     * 詩曰：『
     *    子虛丑斗寅嫌室，
     *    卯女辰箕巳泊房。
     *    午角未張申忌鬼，
     *    酉觜戌胃亥壁當。
     *  』
     * 此伏斷之惡極凶，只宜出兵截路把隘。
     */
    private val foeMap: Map<Branch, LunarStation> by lazy {
      mapOf(
        子 to 虛, 丑 to 斗, 寅 to 室, 卯 to 女,
        辰 to 箕, 巳 to 房, 午 to 角, 未 to 張,
        申 to 鬼, 酉 to 觜, 戌 to 胃, 亥 to 壁
      )
    }
  }
}
