/**
 * Created by smallufo on 2018-05-05.
 */
package destiny.astrology.eclipse

/** 日食、月食 的最上層 抽象 class  */
interface IEclipse {
  val begin: Double
  /** 不論是 全食、偏食、還是環食，都會有「最大值」  */
  val max: Double
  val end: Double
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
  val totalBegin: Double
  val totalEnd: Double
}

/** 日環食 , 為 , 日全食 的一種 */
interface ISolarEclipseAnnular : ISolarEclipseTotal {
  val annularBegin: Double
  val annularEnd: Double
}

/** 混合型 , 全環食 , 非常罕見 */
interface ISolarEclipseHybrid : ISolarEclipseAnnular

/** 日食 的集合 */
sealed class AbstractSolarEclipse2 : ISolarEclipse {

  /** 日偏食 */
  data class SolarEclipsePartial(
    override val begin: Double,
    override val max: Double,
    override val end: Double) : AbstractSolarEclipse2() , ISolarEclipsePartial {
    override val solarType: ISolarEclipse.SolarType
      get() = ISolarEclipse.SolarType.PARTIAL
  }

  /** 日全食 , 為 偏食 的一種 , 內定「無中線」 */
  data class SolarEclipseTotal(
    private val partial: SolarEclipsePartial,
    override val totalBegin: Double,
    override val totalEnd: Double) : AbstractSolarEclipse2(), ISolarEclipse by partial, ISolarEclipseTotal {
    override val solarType: ISolarEclipse.SolarType
      get() = ISolarEclipse.SolarType.TOTAL
  }

  /** 日全食 , 有中線 */
  data class SolarEclipseTotalCentered(
    private val total: SolarEclipseTotal,
    override val centerBegin: Double,
    override val centerEnd: Double) : AbstractSolarEclipse2(), ISolarEclipseTotal by total, IEclipseCenter {
    override val solarType: ISolarEclipse.SolarType
      get() = ISolarEclipse.SolarType.TOTAL
  }

  /** 日環食 , 為 全食 的一種 , 內定是「無中線」 */
  data class SolarEclipseAnnular(
    private val total: SolarEclipseTotal,
    // NOTE : swisseph 尚未實作 annular Begin/End 之值， 都會傳回 0.0
    override val annularBegin: Double,
    override val annularEnd: Double) : AbstractSolarEclipse2(), ISolarEclipseTotal by total, ISolarEclipseAnnular {
    override val solarType: ISolarEclipse.SolarType
      get() = ISolarEclipse.SolarType.ANNULAR
  }

  /** 日環食 , 有中線 */
  data class SolarEclipseAnnularCentered(
    private val annular: SolarEclipseAnnular,
    override val centerBegin: Double,
    override val centerEnd: Double) : AbstractSolarEclipse2(), ISolarEclipseAnnular by annular, IEclipseCenter {
    override val solarType: ISolarEclipse.SolarType
      get() = ISolarEclipse.SolarType.ANNULAR
  }

  /** 混合型 , 全環食 , 非常罕見 */
  data class SolarEclipseHybrid(private val annularCentered: SolarEclipseAnnularCentered) : AbstractSolarEclipse2(),
    ISolarEclipseAnnular by annularCentered, IEclipseCenter by annularCentered , ISolarEclipseHybrid {
    override val solarType: ISolarEclipse.SolarType
      get() = ISolarEclipse.SolarType.HYBRID
  }

}


/** =============================================================== */


/** 月食 */
interface ILunarEclipse : IEclipse {
  /** 半影開始 (P1) , 最早 , 可視為整個 eclipse 的 begin  */
  val penumbraBegin: Double
    get() = begin


  /** 半影結束 (P4) , 最遲 , 可視為整個 eclipse 的 end    */
  val penumbraEnd: Double
    get() = end

  enum class LunarType {
    TOTAL,
    PARTIAL,
    PENUMBRA  // 半影月食
  }
}

/** 月偏食 */
interface ILunarEclipsePartial : ILunarEclipse {
  /** 月亮最先碰觸地球本影 (U1)  */
  val partialBegin: Double

  /** 月亮完全離開地球本影 (U4)  */
  val partialEnd: Double
}

/** 月全食 */
interface ILunarEclipseTotal : ILunarEclipsePartial {
  /** 月亮全部進入地球本影 剛開始 (U2)  */
  val totalBegin: Double

  /** 月亮全部進入地球本影 剛結束 (U3)  */
  val totalEnd: Double
}

sealed class AbstractLunarEclipse2 : ILunarEclipse {

  /** 半影月食 */
  data class LunarEclipsePenumbra(
    override val begin: Double,
    override val max: Double,
    override val end: Double) : AbstractLunarEclipse2()

  /** 月偏食 */
  data class LunarEclipsePartial(
    private val penumbra: LunarEclipsePenumbra,
    override val partialBegin: Double,
    override val partialEnd: Double) : AbstractLunarEclipse2(), ILunarEclipse by penumbra, ILunarEclipsePartial

  /** 月全食 */
  data class LunarEclipseTotal(
    private val partial: LunarEclipsePartial,
    override val totalBegin: Double,
    override val totalEnd: Double) : AbstractLunarEclipse2(), ILunarEclipsePartial by partial, ILunarEclipseTotal

}