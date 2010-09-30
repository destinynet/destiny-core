/**
 * @author smallufo
 * @date 2002/8/19 , @date 2009/6/23 改寫
 * @time 下午 04:36:40
 */
package destiny.IChing;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 周易卦序Comparator
 */
public class HexagramDefaultComparator implements Comparator<HexagramIF> , HexagramSequenceIF , Serializable
{
  private final static Map<Hexagram , Integer> map = Collections.synchronizedMap(new HashMap<Hexagram , Integer>());
  static
  {
    map.put(Hexagram.乾, 1);
    map.put(Hexagram.坤, 2);
    map.put(Hexagram.屯, 3);
    map.put(Hexagram.蒙, 4);
    map.put(Hexagram.需, 5);
    map.put(Hexagram.訟, 6);
    map.put(Hexagram.師, 7);
    
    map.put(Hexagram.比, 8);
    map.put(Hexagram.小畜, 9);
    map.put(Hexagram.履, 10);
    map.put(Hexagram.泰, 11);
    map.put(Hexagram.否, 12);
    
    map.put(Hexagram.同人, 13);
    map.put(Hexagram.大有, 14);
    map.put(Hexagram.謙, 15);
    map.put(Hexagram.豫, 16);
    map.put(Hexagram.隨, 17);
    
    map.put(Hexagram.蠱, 18);
    map.put(Hexagram.臨, 19);
    map.put(Hexagram.觀, 20);
    map.put(Hexagram.噬嗑, 21);
    map.put(Hexagram.賁, 22);
    
    map.put(Hexagram.剝, 23);
    map.put(Hexagram.復, 24);
    map.put(Hexagram.無妄, 25);
    map.put(Hexagram.大畜, 26);
    map.put(Hexagram.頤, 27);
    
    map.put(Hexagram.大過, 28);
    map.put(Hexagram.坎, 29);
    map.put(Hexagram.離, 30);
    
    map.put(Hexagram.咸, 31);
    map.put(Hexagram.恆, 32);
    map.put(Hexagram.遯, 33);
    map.put(Hexagram.大壯, 34);
    
    map.put(Hexagram.晉, 35);
    map.put(Hexagram.明夷, 36);
    map.put(Hexagram.家人, 37);
    map.put(Hexagram.睽, 38);
    
    map.put(Hexagram.蹇, 39);
    map.put(Hexagram.解, 40);
    map.put(Hexagram.損, 41);
    map.put(Hexagram.益, 42);
    map.put(Hexagram.夬, 43);
    map.put(Hexagram.姤, 44);
    map.put(Hexagram.萃, 45);
    
    map.put(Hexagram.升, 46);
    map.put(Hexagram.困, 47);
    map.put(Hexagram.井, 48);
    map.put(Hexagram.革, 49);
    map.put(Hexagram.鼎, 50);
    map.put(Hexagram.震, 51);
    
    map.put(Hexagram.艮, 52);
    map.put(Hexagram.漸, 53);
    map.put(Hexagram.歸妹, 54);
    map.put(Hexagram.豐, 55);
    map.put(Hexagram.旅, 56);
    map.put(Hexagram.巽, 57);
    
    map.put(Hexagram.兌, 58);
    map.put(Hexagram.渙, 59);
    map.put(Hexagram.節, 60);
    map.put(Hexagram.中孚, 61);
    
    map.put(Hexagram.小過, 62);
    map.put(Hexagram.既濟, 63);
    map.put(Hexagram.未濟, 64);
  }
  
  
  public HexagramDefaultComparator()
  {}

  /** 實作 HexagramSequenceIF */
  @Override
  public int getIndex(HexagramIF hexagram)
  {
    Hexagram h = Hexagram.getHexagram(hexagram.getUpperSymbol(), hexagram.getLowerSymbol());
    return map.get(h);
  }

  @Override
  public Hexagram getHexagram(int index)
  {
    if (index > 64)
      index = index % 64;
    
    if (index <=0 )
      index = 64-(0-index) % 64;
    
    Iterator<Entry<Hexagram , Integer>> it = map.entrySet().iterator();
    while(it.hasNext())
    {
      Entry<Hexagram , Integer> entry = it.next();
      if (entry.getValue().intValue() == index)
        return entry.getKey();
    }
    return null;
  }
  
  @Override
  public int compare(HexagramIF hexagram1, HexagramIF hexagram2)
  {
    return getIndex(hexagram1) - getIndex(hexagram2);
  }
}
