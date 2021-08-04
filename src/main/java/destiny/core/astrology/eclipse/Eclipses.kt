/**
 * Created by smallufo on 2018-05-05.
 */
package destiny.core.astrology.eclipse

import destiny.core.calendar.GmtJulDay

/** 日食、月食 的最上層 抽象 class  */
interface IEclipse {
  val begin: GmtJulDay

  /** 不論是 全食、偏食、還是環食，都會有「最大值」  */
  val max: GmtJulDay
  val end: GmtJulDay
}

/** 日食 */
interface ISolarEclipse : IEclipse {

  enum class SolarType {
    PARTIAL,
    TOTAL,
    ANNULAR,
    HYBRID    // 極為罕見的「全環食」
  }

  val solarType: SolarType
}

/** 日偏食 , 沒有額外參數 */
interface ISolarEclipsePartial : ISolarEclipse

/** 日全食 */
interface ISolarEclipseTotal : ISolarEclipsePartial {
  val totalBegin: GmtJulDay
  val totalEnd: GmtJulDay
}

/** 日環食 , 為 , 日全食 的一種 */
interface ISolarEclipseAnnular : ISolarEclipseTotal {
  val annularBegin: GmtJulDay
  val annularEnd: GmtJulDay
}

/** 混合型 , 全環食 , 非常罕見 */
interface ISolarEclipseHybrid : ISolarEclipseAnnular


/** 中線 ， 開始與結束  */
interface IEclipseCenter {
  val centerBegin: GmtJulDay
  val centerEnd: GmtJulDay
}

/** 日食 的集合 */
sealed class AbstractSolarEclipse : ISolarEclipse {

  /** 日偏食 */
  data class SolarEclipsePartial(
    override val begin: GmtJulDay,
    override val max: GmtJulDay,
    override val end: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipsePartial {
    override val solarType: ISolarEclipse.SolarType = ISolarEclipse.SolarType.PARTIAL
  }

  /** 日全食 , 為 偏食 的一種 , 內定「無中線」 */
  data class SolarEclipseTotal(
    private val partial: SolarEclipsePartial,
    override val totalBegin: GmtJulDay,
    override val totalEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipse by partial, ISolarEclipseTotal {
    override val solarType: ISolarEclipse.SolarType = ISolarEclipse.SolarType.TOTAL
  }

  /** 日全食 , 有中線 */
  data class SolarEclipseTotalCentered(
    private val total: SolarEclipseTotal,
    override val centerBegin: GmtJulDay,
    override val centerEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipseTotal by total, IEclipseCenter {
    override val solarType: ISolarEclipse.SolarType = ISolarEclipse.SolarType.TOTAL
  }

  /** 日環食 , 為 全食 的一種 , 內定是「無中線」 */
  data class SolarEclipseAnnular(
    private val total: SolarEclipseTotal,
    // NOTE : swisseph 尚未實作 annular Begin/End 之值， 都會傳回 0.0
    override val annularBegin: GmtJulDay,
    override val annularEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipseTotal by total, ISolarEclipseAnnular {
    override val solarType: ISolarEclipse.SolarType = ISolarEclipse.SolarType.ANNULAR
  }

  /** 日環食 , 有中線 */
  data class SolarEclipseAnnularCentered(
    private val annular: SolarEclipseAnnular,
    override val centerBegin: GmtJulDay,
    override val centerEnd: GmtJulDay) : AbstractSolarEclipse(), ISolarEclipseAnnular by annular, IEclipseCenter {
    override val solarType: ISolarEclipse.SolarType = ISolarEclipse.SolarType.ANNULAR
  }

  /** 混合型 , 全環食 , 非常罕見 */
  data class SolarEclipseHybrid(private val annularCentered: SolarEclipseAnnularCentered) : AbstractSolarEclipse(),
    ISolarEclipseAnnular by annularCentered, IEclipseCenter by annularCentered, ISolarEclipseHybrid {
    override val solarType: ISolarEclipse.SolarType = ISolarEclipse.SolarType.HYBRID
  }

}


/** ===============================================================
 * 月食示意圖
 * https://www.photopills.com/sites/default/files/articles/moon-eclipse/eclipse-moon-phases-en.jpg
 * 備份 https://imgur.com/2JLJNsO
 *
 * 簡介 https://www.photopills.com/articles/lunar-eclipse-photography-guide
 **/


/** 月食 */
interface ILunarEclipse : IEclipse {
  /** 半影開始 (P1) , 最早 , 可視為整個 eclipse 的 begin  */
  val penumbraBegin: GmtJulDay
    get() = begin


  /** 半影結束 (P4) , 最遲 , 可視為整個 eclipse 的 end    */
  val penumbraEnd: GmtJulDay
    get() = end

  enum class LunarType {
    TOTAL,
    PARTIAL,
    PENUMBRA  // 半影月食
  }

  val lunarType: LunarType
}

/** 月偏食 */
interface ILunarEclipsePartial : ILunarEclipse {
  /** 月亮最先碰觸地球本影 (U1) , 此時月亮剛接觸 Umbra */
  val partialBegin: Double

  /** 月亮完全離開地球本影 (U4) , 此時月亮剛離開 Umbra  */
  val partialEnd: Double
}

/** 月全食 */
interface ILunarEclipseTotal : ILunarEclipsePartial {
  /** 月亮全部進入地球本影 剛開始 (U2)  */
  val totalBegin: Double

  /** 月亮全部進入地球本影 剛結束 (U3)  */
  val totalEnd: Double
}

sealed class AbstractLunarEclipse : ILunarEclipse {

  /** 半影月食 */
  data class LunarEclipsePenumbra(
    override val begin: GmtJulDay,
    override val max: GmtJulDay,
    override val end: GmtJulDay) : AbstractLunarEclipse() {
    override val lunarType: ILunarEclipse.LunarType = ILunarEclipse.LunarType.PENUMBRA
  }

  /** 月偏食 */
  data class LunarEclipsePartial(
    private val penumbra: LunarEclipsePenumbra,
    override val partialBegin: Double,
    override val partialEnd: Double) : AbstractLunarEclipse(), ILunarEclipse by penumbra, ILunarEclipsePartial {
    override val lunarType: ILunarEclipse.LunarType = ILunarEclipse.LunarType.PARTIAL
  }

  /** 月全食 */
  data class LunarEclipseTotal(
    private val partial: LunarEclipsePartial,
    override val totalBegin: Double,
    override val totalEnd: Double) : AbstractLunarEclipse(), ILunarEclipsePartial by partial, ILunarEclipseTotal {
    override val lunarType: ILunarEclipse.LunarType = ILunarEclipse.LunarType.TOTAL
  }

}
