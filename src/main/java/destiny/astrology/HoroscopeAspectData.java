/**
 * @author smallufo 
 * Created on 2008/6/27 at 上午 4:34:57
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/** 存放星體交角的資料結構 */
public class HoroscopeAspectData implements Comparable<HoroscopeAspectData> , Serializable
{
  @NotNull
  private PointComparator pointComp = new PointComparator();
  
  /** 存放形成交角的兩顆星體 */
  private Set<Point> twoPoints = Collections.synchronizedSet(new TreeSet<Point>( pointComp ));
  
  /** 兩星所形成的交角 */
  private Aspect aspect;
  
  /** orb 不列入 equals / hashCode 計算 */
  private double orb; 
  
  public HoroscopeAspectData(Point p1 , Point p2 , Aspect aspect , double orb)
  {
    twoPoints.add(p1);
    twoPoints.add(p2);
    this.aspect = aspect;
    this.orb = orb; 
  }
  
  @NotNull
  @Override
  public String toString()
  {
    return twoPoints.toString() + aspect.toString(Locale.TAIWAN) + " 誤差 " + String.valueOf(orb).substring(0,4) + " 度";
  }

  
  public Set<Point> getTwoPoints()
  {
    return twoPoints;
  }
  
  /** 傳入一個 point , 取得另一個 point , 如果沒有，則傳回 null */
  @Nullable
  public Point getAnotherPoint(Point thisPoint)
  {
    Iterator<Point> itp = twoPoints.iterator();
    while (itp.hasNext())
    {
      Point p = itp.next();
      if (p.equals(thisPoint))
        return itp.next();
      else
        return p;
    }
    return null;
  }

  public Aspect getAspect()
  {
    return aspect;
  }

  public double getOrb()
  {
    return orb;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((aspect == null) ? 0 : aspect.hashCode());
    result = prime * result + ((twoPoints == null) ? 0 : twoPoints.hashCode());
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final HoroscopeAspectData other = (HoroscopeAspectData) obj;
    if (aspect == null)
    {
      if (other.aspect != null)
        return false;
    }
    else if (!aspect.equals(other.aspect))
      return false;
    if (twoPoints == null)
    {
      if (other.twoPoints != null)
        return false;
    }
    else if (!twoPoints.equals(other.twoPoints))
      return false;
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(@NotNull HoroscopeAspectData o)
  {
    Iterator<Point> it1 = twoPoints.iterator();
    Iterator<Point> it2 = o.twoPoints.iterator();
    Point thisP0 = it1.next();
    Point thisP1 = it1.next();
    Point thatP0 = it2.next();
    Point thatP1 = it2.next();
    
    if(thisP0.getClass().getName().equals(thatP0.getClass().getName()) && thisP0.equals(thatP0))
    {
      if(thisP1.getClass().getName().equals(thatP1.getClass().getName()))
        return  ((Comparable<Point>)thisP1).compareTo(thatP1);
      else
        return pointComp.compare(thisP1, thatP1);
    }
    else
    {
      return pointComp.compare(thisP0, thatP0);
    }
      
  }

  
}
