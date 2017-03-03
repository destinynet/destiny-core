/**
 * @author smallufo 
 * Created on 2008/1/27 at 上午 2:19:44
 */ 
package destiny.core.calendar.eightwords;

import java.util.Locale;
import java.util.ResourceBundle;

import destiny.tools.LocaleStringIF;
import org.jetbrains.annotations.NotNull;

/** 界定南北半球的方法 , 赤道 還是 赤緯 */
public enum HemisphereBy implements LocaleStringIF
{
  /** 赤道 */
  EQUATOR("HemisphereBy.EQUATOR") ,
  /** 赤緯 */
  DECLINATION("HemisphereBy.DECLINATION");
  
  private String nameKey;
  
  private final static String resource = "destiny.core.calendar.eightwords.EightWords";
  
  HemisphereBy(String nameKey)
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
