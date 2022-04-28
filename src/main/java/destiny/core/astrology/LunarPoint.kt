/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 5:24:56
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
import java.util.*
import kotlin.reflect.KClass


/**
 * 代表黃白道的交點，以及近遠點 , 繼承圖如下：
 *
 *       LunarPoint (Abstract)
 *              日月交點
 *              |
 *              |
 *     +--------+--------+
 *     |                 |
 * [LunarNode]       [LunarApsis]
 * [TRUE/MEAN]       [MEAN/OSCU]
 * North/South   PERIGEE (近)/APOGEE (遠)
 */
abstract class LunarPoint(nameKey: String, abbrKey: String, resource: String, unicode: Char? = null) : Star(nameKey, abbrKey, resource, unicode) {

  companion object : IPoints<LunarPoint> {

    override val type: KClass<out Point> = LunarPoint::class

    override val values: Array<LunarPoint> by lazy {
      arrayOf(*LunarNode.values, *LunarApsis.values)
    }

    override fun fromString(value: String, locale: Locale): LunarPoint? {
      return LunarNode.fromString(value, locale) ?: LunarApsis.fromString(value, locale)
    }
  }
}
/*
   * [WARN] 2017-04-08 : parent class 不應 reference 到 sub-class 的 field , class loading 可能會出現問題
   * 參考搜尋字串： referencing subclass from superclass initializer might lead to class loading deadlock
   *
  public final static LunarPoint[] values = {
    LunarNode.NORTH_MEAN , LunarNode.NORTH_TRUE ,       //北交點
    LunarNode.SOUTH_MEAN , LunarNode.SOUTH_TRUE,        //南交點
    LunarApsis.APOGEE_MEAN  , LunarApsis.APOGEE_OSCU ,  //遠地點
    LunarApsis.PERIGEE_MEAN , LunarApsis.PERIGEE_OSCU   //近地點
   };
   */
//internal constructor(nameKey: String, abbrKey: String, resource: String, unicode: Char? = null) : Star(nameKey, abbrKey, resource, unicode)
