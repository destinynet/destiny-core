/**
 * @author smallufo 
 * Created on 2007/5/27 at 上午 2:18:45
 */ 
package destiny.astrology;

import java.util.Locale;
import java.util.ResourceBundle;

import destiny.utils.LocaleStringIF;

/**
 * 橢圓軌道的四個點：近點（Perihelion/Perigee）、遠點（Aphelion/Apogee），上升點（Acscending/North Node），下降點（Descending/South Node）
 */
public enum Apsis implements LocaleStringIF
{
  /** 近點 */
  PERIHELION("Apsis.PERIHELION"),
  /** 遠點 */
  APHELION("Apsis.APHELION"),
  /** 北交點/上升點 */
  ASCENDING("Apsis.ASCENDING"),
  /** 南交點/下降點 */
  DESCENGING("Apsis.DESCENGING");
  
  private final static String resource = "destiny.astrology.Star";
  
  private String nameKey;
  
  private Apsis(String nameKey)
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
