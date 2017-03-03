/**
 * @author smallufo 
 * Created on 2007/11/26 at 上午 5:40:57
 */ 
package destiny.astrology.classical;

import destiny.tools.LocaleStringIF;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 行星落入星座的 , 旺 廟 陷 落 
 */
public enum Dignity implements LocaleStringIF
{
  /** 旺 (+5) , 守護 */
  RULER     ("Dignity.RULER"),
  /** 廟 (+4) , 躍升 */
  EXALTATION("Dignity.EXALTATION"),
  /** 落 (-4) */
  FALL      ("Dignity.FALL"),
  /** 陷 (-5) */
  DETRIMENT ("Dignity.DETRIMENT");
  
  private String nameKey;
  
  private final static String resource = "destiny.astrology.classical.Classical";
  
  Dignity(String nameKey)
  {
    this.nameKey = nameKey;
  }

  @Override
  public String toString()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }
  
  @Override
  public String toString(@NotNull Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }
}
