/**
 * @author smallufo 
 * Created on 2008/5/9 at 上午 5:42:12
 */ 
package destiny.astrology;

import destiny.utils.Decorator;

public class AzimuthDegreeEnglishDecorator implements Decorator<Double>
{
  @Override
  public String getOutputString(Double d)
  {
    double value = d.doubleValue();
    if (value <= 0.5 || value >= 359.5)
      return "North";
    else if (value >= 89.5 && value <= 90.5)
      return "East";
    else if (value >= 179.5 && value <=180.5)
      return "South";
    else if (value >= 269.5 && value <=270.5)
      return "West";
    else
    {
      if (value <=45)
        return ("E by N " + String.valueOf(value).substring(0, 4));
      else if (value > 45 && value < 90)
        return ("N by E " + String.valueOf(90-value).substring(0,4));
      else if (value > 90 && value <= 135)
        return ("S by E " + String.valueOf(value-90).substring(0,4));
      else if (value > 135 && value < 180)
        return ("E by S " + String.valueOf(180-value).substring(0,4));
      else if (value > 180 && value <= 225)
        return ("W by S " + String.valueOf(value-180).substring(0,4));
      else if (value > 225 && value < 270)
        return ("S by W " + String.valueOf(270-value).substring(0,4));
      else if (value > 270 && value <= 315)
        return ("N by E " + String.valueOf(value-270).substring(0,4));
      else
        return ("W by N " + String.valueOf(360-value).substring(0,4));
    }
  }

}
