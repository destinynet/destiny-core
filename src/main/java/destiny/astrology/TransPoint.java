/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 04:22:40
 */
package destiny.astrology;

import destiny.tools.LocaleStringIF;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 四個角點，天底、東昇點、天頂、西落
 */
public enum TransPoint implements LocaleStringIF
{
  /*
  public final static Point RISING = new Point("Point.RISING");
  public final static Point MERIDIAN = new Point("Point.MERIDIAN");
  public final static Point SETTING = new Point("Point.SETTING");
  public final static Point NADIR = new Point("Point.NADIR");
  */
  /** 東昇 */
  RISING("TransPoint.RISING"),
  /** 天頂 */
  MERIDIAN("TransPoint.MERIDIAN"),
  /** 西落 */
  SETTING ("TransPoint.SETTING"),
  /** 天底 */
  NADIR("TransPoint.NADIR");
  
  private final static String resource = "destiny.astrology.Star";

  private String nameKey;

  TransPoint(String nameKey) {
    //super(nameKey , drb);
    this.nameKey = nameKey;
  }

  @Override
  public String toString()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }

  @Override
  public String toString(@NotNull Locale locale) {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey);
  }
  
  
  
}
