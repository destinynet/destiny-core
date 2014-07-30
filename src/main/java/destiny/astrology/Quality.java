/**
 * @author smallufo 
 * Created on 2007/8/29 at 下午 2:57:16
 */ 
package destiny.astrology;

import java.util.Locale;
import java.util.ResourceBundle;

import destiny.utils.LocaleStringIF;
import org.jetbrains.annotations.NotNull;

/**
 * 基本 Cardinal , 固定 Fixed , 變動 Mutable
 */
public enum Quality implements LocaleStringIF
{
  /** 基本 */
  CARDINAL("Quality.CARDINAL"),
  
  /** 固定 */
  FIXED("Quality.FIXED"),
  
  /** 變動 */
  MUTABLE("Quality.MUTABLE");
  
  private String nameKey;
  
  private final static String resource = "destiny.astrology.Sign";
  
  private Quality(String nameKey)
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
