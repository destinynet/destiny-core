/**
 * @author smallufo
 * Created on 2007/11/24 at 上午 1:21:00
 */
package destiny.core.astrology

import destiny.core.astrology.Aspect.Importance.*
import destiny.tools.ILocaleString
import java.util.*
import kotlin.math.abs

fun Aspect.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.core.astrology.Astrology"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun Aspect.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

/** 交角 , Aspect  */
enum class Aspect(val nameKey: String,
                  /** 取得度數  */
                  val degree: Double,
                  /** 取得重要度  */
                  internal val importance: Importance) {
  /** 0 , 合   */
  CONJUNCTION("Aspect.CONJUNCTION", 0.0, HIGH),
  /** 30 , 十二分相 , 半六合  */
  SEMISEXTILE("Aspect.SEMISEXTILE", 30.0, MEDIUM),
  /** 36 , 十分相  */
  DECILE("Aspect.DECILE", 36.0, LOW),
  /** 40 , 九分相  */
  NOVILE("Aspect.NOVILE", 40.0, LOW),
  /** 45 , 半刑 , 八分相 , 半四分相  */
  SEMISQUARE("Aspect.SEMISQUARE", 45.0, MEDIUM),
  /** 51.428 , 七分相  */
  SEPTILE("Aspect.SEPTILE", 360 / 7.0, LOW),
  /** 60 , 六合  */
  SEXTILE("Aspect.SEXTILE", 60.0, HIGH),
  /** 72 , 五分相  */
  QUINTILE("Aspect.QUINTILE", 72.0, LOW),
  /** 80 , 倍九分相  */
  BINOVILE("Aspect.BINOVILE", 80.0, LOW),
  /** 90 , 刑  */
  SQUARE("Aspect.SQUARE", 90.0, HIGH),
  /** 102.857 , 倍七分相  */
  BISEPTILE("Aspect.BISEPTILE", 360 * 2 / 7.0, LOW),
  /** 108 , 補五分相  */
  SESQUIQUINTLE("Aspect.SESQUIQUINTLE", 108.0, LOW),
  /** 120 , 三合 , 拱  */
  TRINE("Aspect.TRINE", 120.0, HIGH),
  /** 135 , 補八分相  */
  SESQUIQUADRATE("Aspect.SESQUIQUADRATE", 135.0, MEDIUM),
  /** 144 , 倍五分相  */
  BIQUINTILE("Aspect.BIQUINTILE", 144.0, LOW),
  /** 150 , 補十二分相 , 十二分之五相  */
  QUINCUNX("Aspect.QUINCUNX", 150.0, MEDIUM),
  /** 154.285 , 七分之三分相  */
  TRISEPTILE("Aspect.TRISEPTILE", 360 * 3 / 7.0, LOW),
  /** 160 , 九分之四分相  */
  QUATRONOVILE("Aspect.QUATRONOVILE", 160.0, LOW),
  /** 180 , 沖  */
  OPPOSITION("Aspect.OPPOSITION", 180.0, HIGH);

  /** 重要度 : HIGH , MEDIUM , LOW  */
  enum class Importance {
    HIGH, MEDIUM, LOW
  }

  companion object {

    private val importanceAngles: Map<Importance, List<Aspect>> = values().groupBy { it.importance }

    /** 從「英文」的 aspect name 來反找 Aspect , 找不到則傳回 null  */
    fun getAspectFromName(value: String): Aspect? {
      return values().firstOrNull { name -> name.toString(Locale.ENGLISH).equals(value.trim { it <= ' '}, ignoreCase = true) }
    }

    /**
     * 取得某類重要度 (高/中/低) 的角度列表
     */
    fun getAngles(importance: Importance): List<Aspect> {
      return importanceAngles.getValue(importance)
    }


    /** 從 double 度數，找回符合的 Aspect  */
    fun getAspect(degree: Double): Aspect? {
      if (degree >= 360)
        return getAspect(degree - 360)

      if (degree < 0)
        return getAspect(degree + 360)

      return if (degree > 180)
        getAspect(360 - degree)
      else
        values().firstOrNull { aspect -> abs(aspect.degree - degree) <  0.01 }
    }

  }
}
