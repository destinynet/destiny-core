/**
 * @author smallufo 
 * Created on 2008/6/30 at 上午 3:28:14
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** 
 * 對不同的 Point 做排序的動作 , 優先權： 行星 , 交點 , 小行星 , 恆星 , 漢堡
 */
@SuppressWarnings("rawtypes")
public class PointComparator implements Comparator<Point> , Serializable
{
  private final Class[] starClasses = {Planet.class , LunarNode.class , Asteroid.class , FixedStar.class , Hamburger.class};

  public PointComparator() {
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compare(@NotNull Point p1, @NotNull Point p2) {
    Class<? extends Point> p1class = p1.getClass();
    Class<? extends Point> p2class = p2.getClass();
    if (p1class.getName().equals(p2class.getName()))
      return ((Comparable<Point>) p1).compareTo(p2);
    else {
      List<Class> starClassesList = Arrays.asList(starClasses);
      return starClassesList.indexOf(p1class) - starClassesList.indexOf(p2class);
    }
  }

}
