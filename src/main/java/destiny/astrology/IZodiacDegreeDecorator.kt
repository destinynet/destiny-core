/**
 * @author smallufo
 * Created on 2008/1/14 at 下午 11:08:27
 */
package destiny.astrology

import destiny.tools.Decorator

/** 黃道帶上的字串輸出  */
interface IZodiacDegreeDecorator : Decorator<Double> {

  override fun getOutputString(value: Double): String

  fun getSimpOutString(degree: Double): String
}
