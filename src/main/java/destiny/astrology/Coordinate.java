/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:24:30
 */
package destiny.astrology;

import java.util.Locale;
import java.util.ResourceBundle;

import destiny.utils.LocaleStringIF;

/**
 * Zodiac selection <br/> 
 * 黃道(ECLIPTIC) / 赤道(TROPICAL) / 恆星(SIDEREAL) 座標系 參數
 */
public enum Coordinate implements LocaleStringIF
{
  /** 黃道座標系 */
  ECLIPTIC("Coordinate.ECLIPTIC"),
  /** 赤道座標系 */
  EQUATORIAL("Coordinate.EQUATORIAL"),
  /** 恆星座標系 */
  SIDEREAL("Coordinate.SIDEREAL");
  
  private final static String resource = "destiny.astrology.Astrology";
  
  private String nameKey;
    
  private Coordinate(String nameKey)
  {
    this.nameKey = nameKey;
  }
  
  @Override
  public String toString()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }
  
  @Override
  public String toString(Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }

}
