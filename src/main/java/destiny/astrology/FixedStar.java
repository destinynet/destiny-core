/**
 * @author smallufo 
 * Created on 2007/12/20 at 上午 12:16:32
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;



/** 恆星 */
@SuppressWarnings("WeakerAccess")
public final class FixedStar extends Star implements Comparable<FixedStar> {

  private final static String resource = Star.class.getName();
  
  /** Algol 大陵五 */
  public final static FixedStar ALGOL = new FixedStar("Fixed.ALGOL" , "Fixed.ALGOL_ABBR");
  /** Aldebaran 畢宿五 */
  public final static FixedStar ALDEBARAN = new FixedStar("Fixed.ALDEBARAN" , "Fixed.ALDEBARAN_ABBR");
  /** Rigel 參宿七 */
  public final static FixedStar RIGEL = new FixedStar("Fixed.RIGEL" , "Fixed.RIGEL_ABBR");
  /** Capella 五車二 */
  public final static FixedStar CAPELLA = new FixedStar("Fixed.CAPELLA" , "Fixed.CAPELLA_ABBR");
  /** Betelgeuse 參宿四 */
  public final static FixedStar BETELGEUSE = new FixedStar("Fixed.BETELGEUSE" , "Fixed.BETELGEUSE_ABBR");
  /** Sirius 天狼星 */
  public final static FixedStar SIRIUS = new FixedStar("Fixed.SIRIUS" , "Fixed.SIRIUS_ABBR");
  /** Canopus 老人星 */
  public final static FixedStar CANOPUS = new FixedStar("Fixed.CANOPUS" , "Fixed.CANOPUS_ABBR");
  /** Pollux 北河三 */
  public final static FixedStar POLLUX = new FixedStar("Fixed.POLLUX" , "Fixed.POLLUX_ABBR");
  /** Procyon 南河三 */
  public final static FixedStar PROCYON = new FixedStar("Fixed.PROCYON" , "Fixed.PROCYON_ABBR");
  /** Praesepe 鬼宿 */
  public final static FixedStar PRAESEPE = new FixedStar("Fixed.PRAESEPE" , "Fixed.PRAESEPE_ABBR");
  /** Alphard 星宿一 */
  public final static FixedStar ALPHARD = new FixedStar("Fixed.ALPHARD" , "Fixed.ALPHARD_ABBR");
  /** Regulus 軒轅十四 */
  public final static FixedStar REGULUS = new FixedStar("Fixed.REGULUS" , "Fixed.REGULUS_ABBR");
  /** Spica 角宿一 */
  public final static FixedStar SPICA   = new FixedStar("Fixed.SPICA" , "Fixed.SPICA_ABBR");
  /** Arcturus 大角 */
  public final static FixedStar ARCTURUS = new FixedStar("Fixed.ARCTURUS" , "Fixed.ARCTURUS_ABBR");
  /** Antares 心宿二 */
  public final static FixedStar ANTARES = new FixedStar("Fixed.ANTARES" , "Fixed.ANTARES_ABBR");
  /** Vega 織女星 */
  public final static FixedStar VEGA = new FixedStar("Fixed.VEGA" , "Fixed.VEGA_ABBR");
  /** Altair 牛郎星 */
  public final static FixedStar ALTAIR = new FixedStar("Fixed.ALTAIR" , "Fixed.ALTAIR_ABBR");
  /** Fomalhaut 北落師門 */
  public final static FixedStar FOMALHAUT = new FixedStar("Fixed.FOMALHAUT" , "Fixed.FOMALHAUT_ABBR");
  /** Deneb 天津四 */
  public final static FixedStar DENEB = new FixedStar("Fixed.DENEB" , "Fixed.DENEB_ABBR");
  
  public final static FixedStar[] values = {REGULUS , SPICA , ALGOL , ALDEBARAN , RIGEL , CAPELLA , BETELGEUSE , SIRIUS , CANOPUS , POLLUX , PROCYON ,
                                    PRAESEPE , ALPHARD , ARCTURUS , ANTARES , VEGA , ALTAIR , FOMALHAUT , DENEB};
  
  protected FixedStar(String nameKey , String abbrKey)
  {
    super(nameKey, abbrKey , resource);
  }
  
  @Override
  public int compareTo(@NotNull FixedStar o)
  {
    if (this.equals(o))
      return 0;
    
    List<FixedStar> list = Arrays.asList(values);
    return list.indexOf(this) - list.indexOf(o);
  }

}
