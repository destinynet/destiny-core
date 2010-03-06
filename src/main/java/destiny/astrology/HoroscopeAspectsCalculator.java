/**
 * @author smallufo 
 * Created on 2008/6/27 at 上午 4:28:26
 */ 
package destiny.astrology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 利用 HoroscopeAspectsCalculatorIF , 計算命盤之中，星體所呈現的交角，及其容許度 */
public class HoroscopeAspectsCalculator implements Serializable
{
  private Horoscope horoscope;
  
  private HoroscopeAspectsCalculatorIF calculator;
  
  public HoroscopeAspectsCalculator(Horoscope horoscope , HoroscopeAspectsCalculatorIF calculator)
  {
    this.horoscope = horoscope;
    this.calculator = calculator;
    this.calculator.setHoroscope(horoscope);
  }
  
  
  public Collection<HoroscopeAspectData> getAspectDataSet(Point... points)
  {
    return getAspectDataSet(Arrays.asList(points) , null);
  }
  
  public Collection<HoroscopeAspectData> getAspectDataSet(Collection<Point> points)
  {
    //List<Point> pointList = new ArrayList<Point>(points);
    return getAspectDataSet(points , null);
  }

  
  /** 
   * 計算 points 之間所形成的交角 . aspects 為要計算的交角 , 如果 aspects 為 null , 代表不過濾任何交角 <br/>
   * 如果沒有形成任何交角（不太可能 , 除非 points 很少 ），則傳回 size = 0 之 Set
   *  */
  public Collection<HoroscopeAspectData> getAspectDataSet(Collection<Point> points , Collection<Aspect> aspects)
  {
    Set<HoroscopeAspectData> dataSet = Collections.synchronizedSet(new HashSet<HoroscopeAspectData>());
    
    Iterator<Point> it = points.iterator();
    
    while(it.hasNext())
    {
      Point point = it.next();
      Map<Point , Aspect>  map = calculator.getPointAspect(point, points);
      if (map != null)
      {
        Iterator<Map.Entry<Point , Aspect>> it2 = map.entrySet().iterator();
        while (it2.hasNext())
        {
          Map.Entry<Point , Aspect> entry = it2.next();

          //處理過濾交角的事宜
          if (aspects == null || aspects.size() == 0 ||  (aspects != null  && aspects.contains(entry.getValue())) )
          {
            HoroscopeAspectData data = new HoroscopeAspectData(point , entry.getKey() , entry.getValue() , horoscope.getAspectError(point , entry.getKey() , entry.getValue()));
            dataSet.add(data);
          }
        }        
      } // map != null
    }
    return dataSet;
  }


  //FIXME : 為了解決 Collection<Point> 不吃 Collection<Planet> 所做的骯髒解法，目前只能這樣做，唉～
  public Collection<HoroscopeAspectData> getAspectDataSet(List<Planet> planets, List<Aspect> angles)
  {
    List<Point> points = new ArrayList<Point>(planets);
    return getAspectDataSet(points , angles);
  }
  
}
