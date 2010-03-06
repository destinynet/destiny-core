/**
 * @author smallufo 
 * Created on 2008/1/14 at 下午 11:13:11
 */ 
package destiny.astrology;

import java.util.Locale;

public class ZodiacDegreeDecoratorTradChinese implements ZodiacDegreeDecoratorIF
{
  private double deg;

  @Override
  public String getOutputString(double degree)
  {
    ZodiacSign sign = ZodiacSign.getZodiacSign(degree);
    this.deg = degree-sign.getDegree();
    
    StringBuffer sb = new StringBuffer();
    sb.append(sign.toString(Locale.TAIWAN)+" ");
    if (getDeg() < 10)
      sb.append("0");
    sb.append(getDeg() + "度 ");
    if (getMin() < 10)
      sb.append("0");
    sb.append(getMin() + "分 ");
    if (getSec() < 10)
    {
      sb.append("0");
      sb.append(String.valueOf(getSec()).substring(0,1));
    }
    else
      sb.append(String.valueOf(getSec()).substring(0,2));
    sb.append("秒");
    return sb.toString();
  }

  @Override
  public String getSimpOutString(double degree)
  {
    ZodiacSign sign = ZodiacSign.getZodiacSign(degree);
    this.deg = degree-sign.getDegree();
    
    StringBuffer sb = new StringBuffer();
    if (getDeg() < 10)
      sb.append("0");
    sb.append(getDeg());
    sb.append(sign.getAbbreviation(Locale.TAIWAN));
    if (getMin() < 10)
      sb.append("0");
    sb.append(getMin());
    return sb.toString();
  }
  
  private int getDeg()
  {
    return (int) deg;
  }
  
  private int getMin()
  {
    return (int) ((deg - getDeg())*60);
  }
  
  private double getSec()
  {
    return (( deg - getDeg() ) * 60 - getMin() )*60;
  }

}
