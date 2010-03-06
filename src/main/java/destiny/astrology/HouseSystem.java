/**
 * @author smallufo 
 * Created on 2007/5/29 at 上午 2:29:09
 */ 
package destiny.astrology;

import java.util.Locale;
import java.util.ResourceBundle;

import destiny.utils.LocaleStringIF;

/**
 * 分宮法 , Zodiac House Systems
 */
public enum HouseSystem implements LocaleStringIF
{
  PLACIDUS("HouseSystem.PLACIDUS"),
  KOCH("HouseSystem.KOCH"),
  /** 東昇/天頂 均等三等分*/
  PORPHYRIUS("HouseSystem.PORPHYRIUS"),
  REGIOMONTANUS("HouseSystem.REGIOMONTANUS"),
  CAMPANUS("HouseSystem.CAMPANUS"),
  EQUAL("HouseSystem.EQUAL"),
  VEHLOW_EQUAL("HouseSystem.VEHLOW_EQUAL"),
  AXIAL_ROTATION("HouseSystem.AXIAL_ROTATION"),
  HORIZONTAL("HouseSystem.HORIZONTAL"),
  ALCABITIUS("HouseSystem.ALCABITIUS");
  
  private final static String resource = "destiny.astrology.Astrology";
  
  private String nameKey;
  
  private HouseSystem(String nameKey)
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
