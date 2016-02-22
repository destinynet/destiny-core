/**
 * @author smallufo 
 * Created on 2008/5/9 at 上午 5:10:54
 */ 
package destiny.astrology;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

/**
 * 地平方位角 的簡易描述
 * 地平方位角 , 以北為 0度，東為90度，南為 180度，西為 270度
 */
public class AzimuthDegreeTaiwanDecorator implements Decorator<Double>
{
  @NotNull
  @Override
  public String getOutputString(@NotNull Double d)
  {
    double value = d;
    if (value <= 0.5 || value >= 359.5)
      return "正北方";
    else if (value >= 89.5 && value <= 90.5)
      return "正東方";
    else if (value >= 179.5 && value <=180.5)
      return "正南方";
    else if (value >= 269.5 && value <=270.5)
      return "正西方";
    else
    {
      StringBuffer sb = new StringBuffer();
      if (value <=45)
        sb.append("北偏東" + String.valueOf(value).substring(0, 4));
      else if (value > 45 && value < 90)
        sb.append("東偏北" + String.valueOf(90-value).substring(0,4));
      else if (value > 90 && value <= 135)
        sb.append("東偏南" + String.valueOf(value-90).substring(0,4));
      else if (value > 135 && value < 180)
        sb.append("南偏東" + String.valueOf(180-value).substring(0,4));
      else if (value > 180 && value <= 225)
        sb.append("南偏西" + String.valueOf(value-180).substring(0,4));
      else if (value > 225 && value < 270)
        sb.append("西偏南" + String.valueOf(270-value).substring(0,4));
      else if (value > 270 && value <= 315)
        sb.append("西偏北" + String.valueOf(value-270).substring(0,4));
      else
        sb.append("北偏西" + String.valueOf(360-value).substring(0,4));
      sb.append("度");
      return sb.toString();
    }
  }

}
