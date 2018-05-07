/**
 * Created by smallufo on 2018-05-06.
 */
package destiny.astrology.eclipse

import destiny.astrology.Azimuth
import java.io.Serializable

/**
 * 某時某地點，針對某日食、月食的觀測資料
 */
interface IEclipseObservation {
  /** 當下的時間點為何  */
  val gmtJulDay: Double
  /** 經度  */
  val lng: Double
  /** 緯度  */
  val lat: Double
  /** 高度 (米)  */
  val alt: Double
  /** 地平方位角  */
  val azimuth: Azimuth
}

/** 日食、月食 共有的觀測資料 */
data class EclipseObservation(
  override val gmtJulDay: Double,
  override val lng: Double,
  override val lat: Double,
  override val alt: Double,
  override val azimuth: Azimuth) : IEclipseObservation, Serializable


/** 日食 觀測資料 */
interface ISolarEclipseObservation : IEclipseObservation {
  /** 日食 的種類  */
  val type: ISolarEclipse.SolarType
  /** 半徑被蓋住的比例  */
  val magnitude: Double
  /** 面積被蓋住的比例  */
  val obscuration: Double
}

/** 日食觀測只有一種，不需要定義 sealed data class */
data class SolarEclipseObservation2(
  private val observation: EclipseObservation,
  override val type: ISolarEclipse.SolarType,
  override val magnitude: Double,
  override val obscuration: Double) : ISolarEclipseObservation, IEclipseObservation by observation


/** 月食 觀測資料 */
interface ILunarEclipseObservation : IEclipseObservation {
  /** 月食 的種類 */
  val type: ILunarEclipse.LunarType
  /** 本影強度 , magnitude of Umbra  */
  val magUmbra: Double
  /** 半影強度 , magnitude of Penumbra  */
  val magPenumbra: Double
  /** 食甚可見否?  */
  val maxVisible: Boolean
}

/**
 * 半影月食 的觀測資料
 */
interface ILunarEclipsePenumbraObservation : ILunarEclipseObservation {
  val penumbraBeginVisible: Boolean
  val penumbraEndVisible: Boolean
}

/** 月偏食 的觀測資料 */
interface ILunarEclipsePartialObservation : ILunarEclipsePenumbraObservation {
  val partialBeginVisible: Boolean
  val partialEndVisible: Boolean
}

/** 月全食 的觀測資料 */
interface ILunarEclipseTotalObservation : ILunarEclipsePartialObservation {
  val totalBeginVisible: Boolean
  val totalEndVisible: Boolean
}

sealed class AbstractLunarEclipseObservation : ILunarEclipseObservation {

  /** 半影月食 的觀測資料 */
  data class LunarEclipsePenumbraObservation(
    override val gmtJulDay: Double,
    override val lng: Double,
    override val lat: Double,
    override val alt: Double,
    override val azimuth: Azimuth,

    override val magUmbra: Double,
    override val magPenumbra: Double,
    override val maxVisible: Boolean,

    override val penumbraBeginVisible: Boolean,
    override val penumbraEndVisible: Boolean) : AbstractLunarEclipseObservation(),
    ILunarEclipsePenumbraObservation {
    override val type: ILunarEclipse.LunarType
      get() = ILunarEclipse.LunarType.PENUMBRA
  }

  /** 月偏食 的觀測資料 */
  data class LunarEclipsePartialObservation(
    private val penumbraObs: LunarEclipsePenumbraObservation,
    override val partialBeginVisible: Boolean,
    override val partialEndVisible: Boolean) : AbstractLunarEclipseObservation(), ILunarEclipsePartialObservation,
    ILunarEclipsePenumbraObservation by penumbraObs {
    override val type: ILunarEclipse.LunarType
      get() = ILunarEclipse.LunarType.PARTIAL
  }

  /** 月全食 的觀測資料 */
  data class LunarEclipseTotalObservation(
    private val partialObs : LunarEclipsePartialObservation,
    override val totalBeginVisible: Boolean,
    override val totalEndVisible: Boolean) : AbstractLunarEclipseObservation() , ILunarEclipseTotalObservation , ILunarEclipsePartialObservation by partialObs {
    override val type: ILunarEclipse.LunarType
      get() = ILunarEclipse.LunarType.TOTAL
  }


}
