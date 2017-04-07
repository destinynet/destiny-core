/**
 * @author smallufo 
 * Created on 2007/6/12 at 上午 2:19:02
 */
package destiny.astrology;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 星體類別
 */
public enum StarType
{
  PLANET("StarType.PLANET"), // 主星體 : 包括太陽, 月亮, 水星 ... 冥王星 , 不含地球
  ASTROID("StarType.ASTROID"), // 六個主要小行星 (major asteroids) , Ceres (1.穀神星) , Pallas (2.智神星) , Juno (3.婚神星) , Vesta (4.灶神星) , Chiron (2060.凱龍星) , Pholus (5145)
  HAMBURGER("StarType.HAMBURGER"), //漢堡派八虛星 : Cupido , Hades , Zeud , Kronos , Apollon , Admetos , Vulkanus , Poseidon
  APSIS("StarType.APSIS"); // 交點 : 近點,遠點,南交,北交

  private final static String resource = "destiny.astrology.Star";
  
  private final String nameKey;

  StarType(String nameKey)
  {
    this.nameKey = nameKey;
  }

  public String getName()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }

  @Override
  public String toString()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }
}
