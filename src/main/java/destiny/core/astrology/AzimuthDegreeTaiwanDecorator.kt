/**
 * @author smallufo
 * Created on 2008/5/9 at 上午 5:10:54
 */
package destiny.core.astrology

import destiny.tools.Decorator

/**
 * 地平方位角 的簡易描述
 * 地平方位角 , 以北為 0度，東為90度，南為 180度，西為 270度
 */
class AzimuthDegreeTaiwanDecorator : Decorator<Double> {
  override fun getOutputString(value: Double): String {
    return if (value <= 0.5 || value >= 359.5)
      "正北方"
    else if (value in 89.5..90.5)
      "正東方"
    else if (value in 179.5..180.5)
      "正南方"
    else if (value in 269.5..270.5)
      "正西方"
    else {
      val sb = StringBuilder()
      if (value <= 45)
        sb.append("北偏東").append(value.toString().substring(0, 4))
      else if (value > 45 && value < 90)
        sb.append("東偏北").append((90 - value).toString().substring(0, 4))
      else if (value > 90 && value <= 135)
        sb.append("東偏南").append((value - 90).toString().substring(0, 4))
      else if (value > 135 && value < 180)
        sb.append("南偏東").append((180 - value).toString().substring(0, 4))
      else if (value > 180 && value <= 225)
        sb.append("南偏西").append((value - 180).toString().substring(0, 4))
      else if (value > 225 && value < 270)
        sb.append("西偏南").append((270 - value).toString().substring(0, 4))
      else if (value > 270 && value <= 315)
        sb.append("西偏北").append((value - 270).toString().substring(0, 4))
      else
        sb.append("北偏西").append((360 - value).toString().substring(0, 4))
      sb.append("度")
      sb.toString()
    }
  }

}
