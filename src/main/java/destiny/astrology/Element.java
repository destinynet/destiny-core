/**
 * @author smallufo 
 * Created on 2007/8/29 at 下午 2:42:41
 */ 
package destiny.astrology;

import destiny.tools.LocaleStringIF;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 四大元素 : 火 Fire , 土 Earth , 風 Air , 水 Water
 */
public enum Element implements LocaleStringIF
{
  /** 火 */
  FIRE("Element.FIRE"),
  /** 土 */
  EARTH("Element.EARTH"),
  /** 風 */
  AIR("Element.AIR"),
  /** 水 */
  WATER("Element.WATER");
  
  private String nameKey;
  
  private final static String resource = "destiny.astrology.Sign";
  
  Element(String nameKey)
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
