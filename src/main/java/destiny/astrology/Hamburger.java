/**
 * @author smallufo 
 * Created on 2007/6/12 at 上午 7:39:09
 */
package destiny.astrology;

import java.util.Arrays;
import java.util.List;



/** 漢堡學派 Uranian Astrology */
public final class Hamburger extends Star implements Comparable<Hamburger>
{
  private final static String resource = "destiny.astrology.Star";

  public final static Hamburger CUPIDO   = new Hamburger("Hamburger.CUPIDO");
  public final static Hamburger HADES    = new Hamburger("Hamburger.HADES"); 
  public final static Hamburger ZEUS     = new Hamburger("Hamburger.ZEUS");
  public final static Hamburger KRONOS   = new Hamburger("Hamburger.KRONOS"); 
  public final static Hamburger APOLLON  = new Hamburger("Hamburger.APOLLON");
  public final static Hamburger ADMETOS  = new Hamburger("Hamburger.ADMETOS");
  public final static Hamburger VULKANUS = new Hamburger("Hamburger.VULKANUS");
  public final static Hamburger POSEIDON = new Hamburger("Hamburger.POSEIDON");

  public final static Hamburger[] values = {CUPIDO , HADES , ZEUS , KRONOS , APOLLON , ADMETOS , VULKANUS , POSEIDON};
  
  protected Hamburger(String nameKey)
  {
    super(nameKey , resource);
  }
  
  @Override
  public int compareTo(Hamburger o)
  {
    if (this.equals(o))
      return 0;
    
    List<Hamburger> list = Arrays.asList(values);
    return list.indexOf(this) - list.indexOf(o);
  }
}
