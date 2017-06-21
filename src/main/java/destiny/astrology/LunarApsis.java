/**
 * @author smallufo 
 * Created on 2007/12/23 at 上午 5:30:01
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static destiny.astrology.Apsis.APHELION;
import static destiny.astrology.Apsis.PERIHELION;
import static destiny.astrology.LunarApsis.MeanOscu.MEAN;
import static destiny.astrology.LunarApsis.MeanOscu.OSCU;


public final class LunarApsis extends LunarPoint implements Comparable<LunarApsis>
{
  public enum MeanOscu {MEAN , OSCU}

  /** 只會用到 PERIHELION , APHELION */
  @NotNull
  private final Apsis apsis;

  @NotNull
  private final MeanOscu meanOscu;
  
  
  private final static String resource = Star.class.getName();
  
  /** 
   * 平均遠地點 , 月孛 , 水之餘 , Black Moon , Lilith，計算方法，以下兩者結果相同 :
   * <pre> 
   * ApsisImpl.getPosition(Planet.MOON , Apsis.APHELION , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarApsis.APOGEE_MEAN, gmt);
   * </pre> 
   */
  public final static LunarApsis APOGEE_MEAN = new LunarApsis("LunarApsis.APOGEE" , "LunarApsis.APOGEE_ABBR" , APHELION , MEAN );
  
  /**
   * 真實遠地點 , 月孛 , 水之餘 , Black Moon , Lilith，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.APHELION , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarApsis.APOGEE_OSCU, gmt);
   * </pre>
   */
  public final static LunarApsis APOGEE_OSCU = new LunarApsis("LunarApsis.APOGEE" , "LunarApsis.APOGEE_ABBR" , APHELION , OSCU );
  
  /**
   * 平均近地點，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.PERIHELION , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarApsis.PERIGEE_MEAN , gmt);
   * </pre>
   */
  public final static LunarApsis PERIGEE_MEAN = new LunarApsis("LunarApsis.PERIGEE" , "LunarApsis.PERIGEE_ABBR" , PERIHELION , MEAN);
  
  /**
   * 真實近地點，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.PERIHELION , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarApsis.PERIGEE_OSCU , gmt);
   * </pre>
   */
  public final static LunarApsis PERIGEE_OSCU = new LunarApsis("LunarApsis.PERIGEE" , "LunarApsis.PERIGEE_ABBR" , PERIHELION , OSCU);
  
  @NotNull
  public static final LunarApsis[] values = {APOGEE_MEAN , APOGEE_OSCU , PERIGEE_MEAN , PERIGEE_OSCU};
  
  private LunarApsis(String nameKey, String abbrKey, @NotNull Apsis apsis, @NotNull MeanOscu meanOscu) {
    super(nameKey, abbrKey, resource);
    this.meanOscu = meanOscu;
    this.apsis = apsis;
  }

  @NotNull
  public MeanOscu getMeanOscu()
  {
    return meanOscu;
  }

  @NotNull
  public Apsis getApsis()
  {
    return apsis;
  }

  @Override
  public int compareTo(@NotNull LunarApsis o)
  {
    if (this.equals(o))
      return 0;
    
    List<LunarApsis> list = Arrays.asList(values);
    return list.indexOf(this) - list.indexOf(o);
  }

}
