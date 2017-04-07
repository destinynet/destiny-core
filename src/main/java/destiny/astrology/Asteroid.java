/**
 * @author smallufo 
 * Created on 2007/6/22 at 上午 5:14:53
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;



public final class Asteroid extends Star implements Comparable<Asteroid>
{
  private final static String resource = "destiny.astrology.Star";
  /** 穀神星 */
  public final static Asteroid CERES  = new Asteroid("Asteroid.CERES" , "Asteroid.CERES_ABBR" , 1);
  /** 智神星 */
  public final static Asteroid PALLAS = new Asteroid("Asteroid.PALLAS", "Asteroid.PALLAS_ABBR", 2);
  /** 婚神星 */
  public final static Asteroid JUNO   = new Asteroid("Asteroid.JUNO"  , "Asteroid.JUNO_ABBR"  , 3);
  /** 灶神星 */
  public final static Asteroid VESTA  = new Asteroid("Asteroid.VESTA" , "Asteroid.VESTA_ABBR" , 4);
  /** 凱龍星 */
  public final static Asteroid CHIRON = new Asteroid("Asteroid.CHIRON", "Asteroid.CHIRON_ABBR", 2060);
  /** 人龍星 */
  public final static Asteroid PHOLUS = new Asteroid("Asteroid.PHOLUS", "Asteroid.PHOLUS_ABBR", 5145);

  public final static Asteroid[] values = {CERES , PALLAS , JUNO , VESTA , CHIRON , PHOLUS};

  Asteroid(String nameKey, String abbrKey, int index)
  {
    super(nameKey, abbrKey , resource);
  }

  @Override
  public int compareTo(@NotNull Asteroid o)
  {
    if (this.equals(o))
      return 0;
    
    List<Asteroid> list = Arrays.asList(values);
    return list.indexOf(this) - list.indexOf(o);
  }

}
