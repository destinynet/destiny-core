/**
 * Created by smallufo on 2021-03-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.Companion.aheadOf
import destiny.core.astrology.Planet.SUN
import destiny.core.astrology.Planet.VENUS
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYearMonth
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch
import destiny.core.chinese.lunarStation.IHiddenVenusFoe.Companion.isDayFoeForDay
import destiny.core.chinese.lunarStation.IHiddenVenusFoe.Companion.isDayFoeForYear
import destiny.core.chinese.lunarStation.IHiddenVenusFoe.Companion.isHourFoeForHour
import destiny.core.chinese.lunarStation.IHiddenVenusFoe.Companion.monthFoeMap
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


/**
 * 暗金伏斷
 */
interface IHiddenVenusFoe {

  /**
   * @return Pair<Scale,Scale> 前者代表「此時刻的什麼時段」 , 犯了 後者的 暗金伏斷煞
   */
  fun getHiddenVenusFoe(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Pair<Scale, Scale>>

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
      val ziStart: LunarStation = 虛.next(yearPlanet.aheadOf(SUN) * 12)
      return generateSequence((0 to ziStart)) {
        (it.first + 1 to it.second.next)
      }.take(12)
        .filter { it.second.planet == VENUS }
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

/**
 * 《禽星易見・演禽賦》
 *
 * 暗曜，即 [亢] 、 [牛] 、 [婁] 、 [鬼] 四金也，乃天上之太白星。凡日時直之，即暗金也。諸事忌之，惟番禽得亢婁猶好。然其伏斷之惡本官，所犯非輕。
 *
 * 時直空亡，遇吉禽猶當取的；
 *
 * 空亡者，乃時上天干帶壬癸是也。歷書云：截路空亡，出行大忌，此時若出離門，諸事不利。
 * 《三軍一覽》云：此時用事，如人在路途中遇水，不能濟也。
 * 《禽書》云：凡選用時若天干帶壬癸，更直氐房心虛室奎婁昴觜鬼柳參此十二宿者，則截其路而不能濟也。此時縱得奇門，亦主阻滯，切不可用，此兵家之大忌。
 *
 */
class HiddenVenusFoeAnimalStar(private val yearlyImpl: ILunarStationYearly,
                               private val monthlyImpl: ILunarStationMonthly,
                               private val dailyImpl: ILunarStationDaily,
                               private val hourlyImpl: ILunarStationHourly,
                               val yearMonthImpl: IYearMonth,
                               private val chineseDateImpl: IChineseDate,
                               private val dayHourImpl: IDayHour,
                               val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS) :
  IHiddenVenusFoe, Serializable {

  @OptIn(ExperimentalStdlibApi::class)
  override fun getHiddenVenusFoe(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Pair<Scale, Scale>> {
    val yearlyStation = yearlyImpl.getYearlyStation(lmt, loc)
    val monthBranch = yearMonthImpl.getMonth(lmt, loc).branch
    val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl)
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month,
      chineseDate.leapMonth,
      monthBranch,
      chineseDate.day,
      monthAlgo
    )
    val monthlyStation = monthlyImpl.getMonthlyStation(yearlyStation, monthNumber)
    val daySb: StemBranch = dayHourImpl.getDay(lmt, loc)
    val dailyStation = dailyImpl.getDailyStation(lmt, loc).first
    val hourlyStation = hourlyImpl.getHourlyStation(lmt, loc)
    val hourBranch = dayHourImpl.getHour(lmt, loc)

    return buildSet {
      // 年
      if (isDayFoeForYear(yearlyStation.planet, daySb.branch, dailyStation)) {
        add(Scale.DAY to Scale.YEAR)
      }

      // 月
      if (monthFoeMap[monthNumber] == monthlyStation) {
        add(Scale.MONTH to Scale.MONTH)
      }

      // 日
      if (isDayFoeForDay(dailyStation, daySb.branch)) {
        add(Scale.DAY to Scale.DAY)
      }

      // 時
      if (isHourFoeForHour(hourlyStation, hourBranch)) {
        add(Scale.HOUR to Scale.HOUR)
      }
    }
  }
}
