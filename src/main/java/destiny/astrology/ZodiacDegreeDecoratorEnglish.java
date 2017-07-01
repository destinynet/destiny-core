/**
 * @author smallufo
 * Created on 2008/1/19 at 上午 5:59:26
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ZodiacDegreeDecoratorEnglish implements ZodiacDegreeDecoratorIF {

  private double deg;

  @NotNull
  @Override
  public String getOutputString(double degree) {
    ZodiacSign sign = ZodiacSign.getZodiacSign(degree);
    this.deg = degree - sign.getDegree();

    return sign.toString(Locale.US) + getDeg() + "Deg " + getMin() + "Min " + getSec() + "Sec";
  }

  @NotNull
  @Override
  public String getSimpOutString(double degree) {
    ZodiacSign sign = ZodiacSign.getZodiacSign(degree);
    this.deg = degree - sign.getDegree();

    StringBuilder sb = new StringBuilder();
    if (getDeg() < 10)
      sb.append("0");
    sb.append(getDeg());
    sb.append(sign.getAbbreviation(Locale.US));
    if (getMin() < 10)
      sb.append("0");
    sb.append(getMin());
    return sb.toString();
  }

  private int getDeg() {
    return (int) deg;
  }

  private int getMin() {
    return (int) ((deg - getDeg()) * 60);
  }

  private double getSec() {
    return ((deg - getDeg()) * 60 - getMin()) * 60;
  }

}
