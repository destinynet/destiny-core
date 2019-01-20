/**
 * Created by smallufo on 2018-06-02.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.IDayNight
import destiny.astrology.IRelativeTransit
import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.eightwords.*
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 具備現代資訊（經緯度、時區）的紫微 Context
 */
interface IZiweiModernContext : IZiweiContext {

  val chineseDateImpl: IChineseDate

  val solarTermsImpl : ISolarTerms

  val yearMonthImpl : IYearMonth

  val dayImpl: IDay

  val hourImpl: IHour

  val dayNightImpl : IDayNight

  val midnightImpl: IMidnight

  val relativeTransitImpl : IRelativeTransit

  val changeDayAfterZi: Boolean

  /** 輸入現代化的資料，計算本命盤  */
  fun getModernPlate(lmt: ChronoLocalDateTime<*>,
                     location: ILocation,
                     place: String?,
                     gender: Gender,
                     name: String?=null): Builder
}

class ZModernContext(
  val context: IZiweiContext,
  override val chineseDateImpl: IChineseDate,
  override val solarTermsImpl: ISolarTerms,
  override val yearMonthImpl: IYearMonth,
  override val dayImpl: IDay,
  override val hourImpl: IHour,
  override val dayNightImpl: IDayNight,
  override val midnightImpl: IMidnight,
  override val relativeTransitImpl: IRelativeTransit,
  override val changeDayAfterZi: Boolean = true) : IZiweiModernContext, IZiweiContext by context, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)!!

  private val intAgeZiweiImpl : IIntAge by lazy {
    IntAgeZiweiImpl(chineseDateImpl , relativeTransitImpl)
  }

  override fun getModernPlate(lmt: ChronoLocalDateTime<*>,
                              location: ILocation,
                              place: String?,
                              gender: Gender,
                              name: String?) : Builder {

    // 排盤之中所產生的註解 , Pair<KEY , parameters>
    val notesBuilders = mutableListOf<Pair<String, Array<Any>>>()

    val (命宮地支, 身宮地支, finalMonthNumForMainStars) = mainBodyHouseImpl.getMainBodyHouse(lmt, location)

    if (mainBodyHouseImpl is MainBodyHouseAstroImpl) {
      logger.warn("命宮、身宮 採用上升、月亮星座")
      notesBuilders.add(Pair("main_body_astro", arrayOf()))
    }

    logger.debug("命宮地支 = {} , 身宮地支 = {}", 命宮地支, 身宮地支)

    val t2 = TimeTools.getDstSecondOffset(lmt, location)
    val dst = t2.first
    val minuteOffset = t2.second / 60

    val cDate = chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl, midnightImpl, changeDayAfterZi)
    val cycle = cDate.cycleOrZero
    val yinYear = cDate.year

    val monthBranch = yearMonthImpl.getMonth(lmt, location).branch
    val solarYear = yearMonthImpl.getYear(lmt, location)

    val lunarMonth = cDate.month
    val solarTerms = solarTermsImpl.getSolarTerms(lmt, location)
    val lunarDays = cDate.day

    val hour = hourImpl.getHour(lmt, location)
    if (dst) {
      // 日光節約時間 特別註記
      notesBuilders.add(Pair("dst", arrayOf()))
      logger.info("[DST]:校正日光節約時間...")
      logger.info("lmt = {} , location = {} . location.hasMinuteOffset = {}", lmt, location, location.hasMinuteOffset)
      logger.info("loc tz = {} , minuteOffset = {}", location.timeZone.id, location.finalMinuteOffset)
      logger.info("日光節約時間： {} ,  GMT 時差 : {}", dst, minuteOffset)
      logger.info("時辰 = {} . hourImpl = {}", hour, hourImpl.javaClass.simpleName)
    }

    if (hourImpl is HourSolarTransImpl) {
      // 如果是真太陽時
      val hour2 = HourLmtImpl().getHour(lmt, location)
      if (hour != hour2) {
        // 如果真太陽時與LMT時間不一致，出現提醒
        notesBuilders.add(Pair("true_solar_time", arrayOf<Any>(hour, hour2)))
      }
    }

    val dayNight = dayNightImpl.getDayNight(lmt, location)

    // 虛歲時刻 (gmt Julian day)
    val vageMap = intAgeZiweiImpl.getRangesMap(gender, TimeTools.getGmtJulDay(lmt, location), location, 1, 130)

    return getBirthPlate(Pair(命宮地支, 身宮地支) , finalMonthNumForMainStars , cycle , yinYear , solarYear , lunarMonth
                  , cDate.isLeapMonth , monthBranch , solarTerms , lunarDays , hour , dayNight , gender , vageMap)
      .withLocalDateTime(lmt)
      .withLocation(location)
      .appendNotesBuilders(notesBuilders).apply {
        place?.also { withPlace(it) }
      }

  }
}

