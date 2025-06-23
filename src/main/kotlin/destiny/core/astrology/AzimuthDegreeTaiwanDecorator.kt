/**
 * @author smallufo
 * Created on 2008/5/9 at 上午 5:10:54
 */
package destiny.core.astrology

import destiny.tools.Decorator
import destiny.tools.truncate

/**
 * 地平方位角 的簡易描述
 * 地平方位角 , 以北為 0度，東為90度，南為 180度，西為 270度
 */
class AzimuthDegreeTaiwanDecorator : Decorator<Double> {

  override fun getOutputString(value: Double): String {

    return crossMap.asSequence().firstOrNull { (range, _) ->
      value in range
    }?.value ?: run {
      val sb = StringBuilder()
      if (value <= 45)
        sb.append("北偏東").append(value.truncate(1))
      else if (value > 45 && value < 90)
        sb.append("東偏北").append((90 - value).truncate(1))
      else if (value > 90 && value <= 135)
        sb.append("東偏南").append((value - 90).truncate(1))
      else if (value > 135 && value < 180)
        sb.append("南偏東").append((180 - value).truncate(1))
      else if (value > 180 && value <= 225)
        sb.append("南偏西").append((value - 180).truncate(1))
      else if (value > 225 && value < 270)
        sb.append("西偏南").append((270 - value).truncate(1))
      else if (value > 270 && value <= 315)
        sb.append("西偏北").append((value - 270).truncate(1))
      else
        sb.append("北偏西").append((360 - value).truncate(1))
      sb.append("度")
      sb.toString()
    }
  }

  companion object {
    private val crossMap = mapOf(
      (0.0..0.5) to "正北方",
      (359.5..360.0) to "正北方",
      (89.5..90.5) to "正東方",
      (179.5..180.5) to "正南方",
      (269.5..270.5) to "正西方",
    )
  }
}
