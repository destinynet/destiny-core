/**
 * Created by smallufo on 2018-05-05.
 */
package destiny.core.astrology.eclipse

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.IEventSpan
import kotlinx.serialization.Serializable

/** 日食、月食 的最上層 抽象 class  */
@Serializable
sealed interface IEclipse : IEventSpan {

  /** 不論是 全食、偏食、還是環食，都會有「最大值」  */
  val max: GmtJulDay
}

enum class SolarType {
  PARTIAL,
  TOTAL,
  ANNULAR,
  HYBRID    // 極為罕見的「全環食」
}

/** 日食 */
interface ISolarEclipse : IEclipse {

  val solarType: SolarType
}

/** 日偏食 , 沒有額外參數 */
sealed interface ISolarEclipsePartial : ISolarEclipse

/** 日全食 */
sealed interface ISolarEclipseTotal : ISolarEclipsePartial {
  val totalBegin: GmtJulDay
  val totalEnd: GmtJulDay
}

/** 日環食 , 為 , 日全食 的一種 */
sealed interface ISolarEclipseAnnular : ISolarEclipseTotal {
  val annularBegin: GmtJulDay
  val annularEnd: GmtJulDay
}

/** 混合型 , 全環食 , 非常罕見 */
sealed interface ISolarEclipseHybrid : ISolarEclipseAnnular


/** 中線 ， 開始與結束  */
sealed interface IEclipseCenter {
  val centerBegin: GmtJulDay
  val centerEnd: GmtJulDay
}

/** 日食 的集合 */
sealed class AbstractSolarEclipse : ISolarEclipse {

  /** 日偏食 */
  @Serializable
  data class SolarEclipsePartial(
    override val begin: GmtJulDay,
    override val max: GmtJulDay,
    override val end: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipsePartial {
    override val solarType: SolarType = SolarType.PARTIAL
  }

  /** 日全食 , 為 偏食 的一種 , 內定「無中線」 */
  @Serializable
  data class SolarEclipseTotal(
    private val partial: SolarEclipsePartial,
    override val totalBegin: GmtJulDay,
    override val totalEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipse by partial, ISolarEclipseTotal {
    override val solarType: SolarType = SolarType.TOTAL
  }

  /** 日全食 , 有中線 */
  @Serializable
  data class SolarEclipseTotalCentered(
    private val total: SolarEclipseTotal,
    override val centerBegin: GmtJulDay,
    override val centerEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipseTotal by total, IEclipseCenter {
    override val solarType: SolarType = SolarType.TOTAL
  }

  /** 日環食 , 為 全食 的一種 , 內定是「無中線」 */
  @Serializable
  data class SolarEclipseAnnular(
    private val total: SolarEclipseTotal,
    // NOTE : swisseph 尚未實作 annular Begin/End 之值， 都會傳回 0.0
    override val annularBegin: GmtJulDay,
    override val annularEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipseTotal by total, ISolarEclipseAnnular {
    override val solarType: SolarType = SolarType.ANNULAR
  }

  /** 日環食 , 有中線 */
  @Serializable
  data class SolarEclipseAnnularCentered(
    private val annular: SolarEclipseAnnular,
    override val centerBegin: GmtJulDay,
    override val centerEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipseAnnular by annular, IEclipseCenter {
    override val solarType: SolarType = SolarType.ANNULAR
  }

  /** 混合型 , 全環食 , 非常罕見 */
  @Serializable
  data class SolarEclipseHybrid(private val annularCentered: SolarEclipseAnnularCentered) : AbstractSolarEclipse(),
    ISolarEclipseAnnular by annularCentered, IEclipseCenter by annularCentered, ISolarEclipseHybrid {
    override val solarType: SolarType = SolarType.HYBRID
  }

}


/** ===============================================================
 * 月食示意圖
 * https://www.photopills.com/sites/default/files/articles/moon-eclipse/eclipse-moon-phases-en.jpg
 * 備份 https://imgur.com/2JLJNsO
 *
 * 簡介 https://www.photopills.com/articles/lunar-eclipse-photography-guide
 **/


enum class LunarType {
  TOTAL,
  PARTIAL,
  PENUMBRA  // 半影月食
}

/** 月食 */
interface ILunarEclipse : IEclipse {
  /** 半影開始 (P1) , 最早 , 可視為整個 eclipse 的 begin  */
  val penumbraBegin: GmtJulDay
    get() = begin


  /** 半影結束 (P4) , 最遲 , 可視為整個 eclipse 的 end    */
  val penumbraEnd: GmtJulDay
    get() = end

  val lunarType: LunarType
}

/** 月偏食 */
sealed interface ILunarEclipsePartial : ILunarEclipse {
  /** 月亮最先碰觸地球本影 (U1) , 此時月亮剛接觸 Umbra */
  val partialBegin: GmtJulDay

  /** 月亮完全離開地球本影 (U4) , 此時月亮剛離開 Umbra  */
  val partialEnd: GmtJulDay
}

/** 月全食 */
sealed interface ILunarEclipseTotal : ILunarEclipsePartial {
  /** 月亮全部進入地球本影 剛開始 (U2)  */
  val totalBegin: GmtJulDay

  /** 月亮全部進入地球本影 剛結束 (U3)  */
  val totalEnd: GmtJulDay
}

sealed class AbstractLunarEclipse : ILunarEclipse {

  /** 半影月食 */
  @Serializable
  data class LunarEclipsePenumbra(
    override val begin: GmtJulDay,
    override val max: GmtJulDay,
    override val end: GmtJulDay) : AbstractLunarEclipse() {
    override val lunarType: LunarType = LunarType.PENUMBRA
  }

  /** 月偏食 */
  @Serializable
  data class LunarEclipsePartial(
    private val penumbra: LunarEclipsePenumbra,
    override val partialBegin: GmtJulDay,
    override val partialEnd: GmtJulDay) : AbstractLunarEclipse(), ILunarEclipse by penumbra, ILunarEclipsePartial {
    override val lunarType: LunarType = LunarType.PARTIAL
  }

  /** 月全食 */
  @Serializable
  data class LunarEclipseTotal(
    private val partial: LunarEclipsePartial,
    override val totalBegin: GmtJulDay,
    override val totalEnd: GmtJulDay) : AbstractLunarEclipse(), ILunarEclipsePartial by partial, ILunarEclipseTotal {
    override val lunarType: LunarType = LunarType.TOTAL
  }

}
