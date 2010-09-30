/**
 * @author smallufo
 * @date 2002/8/19 , @date 2009/6/23 改寫
 */
package destiny.IChing.divine;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import destiny.IChing.Hexagram;
import destiny.IChing.HexagramIF;
import destiny.IChing.HexagramSequenceIF;

/**
 * 京房卦序：乾為天,天風姤,天山遯,天地否...
 */
public class HexagramDivinationComparator implements Comparator<HexagramIF> , HexagramSequenceIF
{
  private final static Map<Hexagram , Integer> map = Collections.synchronizedMap(new HashMap<Hexagram , Integer>());
  static
  {
    map.put(Hexagram.乾, 1);
    map.put(Hexagram.姤, 2);
    map.put(Hexagram.遯, 3);
    map.put(Hexagram.否, 4);
    map.put(Hexagram.觀, 5);
    map.put(Hexagram.剝, 6);
    map.put(Hexagram.晉, 7);
    map.put(Hexagram.大有, 8);
    
    map.put(Hexagram.兌, 9);
    map.put(Hexagram.困, 10);
    map.put(Hexagram.萃, 11);
    map.put(Hexagram.咸, 12);
    map.put(Hexagram.蹇, 13);
    map.put(Hexagram.謙, 14);
    map.put(Hexagram.小過, 15);
    map.put(Hexagram.歸妹, 16);
    
    map.put(Hexagram.離, 17);
    map.put(Hexagram.旅, 18);
    map.put(Hexagram.鼎, 19);
    map.put(Hexagram.未濟, 20);
    map.put(Hexagram.蒙, 21);
    map.put(Hexagram.渙, 22);
    map.put(Hexagram.訟, 23);
    map.put(Hexagram.同人, 24);
    
    map.put(Hexagram.震, 25);
    map.put(Hexagram.豫, 26);
    map.put(Hexagram.解, 27);
    map.put(Hexagram.恆, 28);
    map.put(Hexagram.升, 29);
    map.put(Hexagram.井, 30);
    map.put(Hexagram.大過, 31);
    map.put(Hexagram.隨, 32);
    
    map.put(Hexagram.巽, 33);
    map.put(Hexagram.小畜, 34);
    map.put(Hexagram.家人, 35);
    map.put(Hexagram.益, 36);
    map.put(Hexagram.無妄, 37);
    map.put(Hexagram.噬嗑, 38);
    map.put(Hexagram.頤, 39);
    map.put(Hexagram.蠱, 40);
    
    map.put(Hexagram.坎, 41);
    map.put(Hexagram.節, 42);
    map.put(Hexagram.屯, 43);
    map.put(Hexagram.既濟, 44);
    map.put(Hexagram.革, 45);
    map.put(Hexagram.豐, 46);
    map.put(Hexagram.明夷, 47);
    map.put(Hexagram.師, 48);
    
    map.put(Hexagram.艮, 49);
    map.put(Hexagram.賁, 50);
    map.put(Hexagram.大畜, 51);
    map.put(Hexagram.損, 52);
    map.put(Hexagram.睽, 53);
    map.put(Hexagram.履, 54);
    map.put(Hexagram.中孚, 55);
    map.put(Hexagram.漸, 56);
    
    map.put(Hexagram.坤, 57);
    map.put(Hexagram.復, 58);
    map.put(Hexagram.臨, 59);
    map.put(Hexagram.泰, 60);
    map.put(Hexagram.大壯, 61);
    map.put(Hexagram.夬, 62);
    map.put(Hexagram.需, 63);
    map.put(Hexagram.比, 64);
  }
  
  public HexagramDivinationComparator()
  {
  }
  
  /**
   * 實作 HexagramSequenceIF
   * @return 傳回六爻卦序, 乾=1 , 姤=2 , 遯=3 , 否=4 ...
   */
  @Override
  public int getIndex(HexagramIF hexagram)
  {
    Hexagram h = Hexagram.getHexagram(hexagram.getUpperSymbol(), hexagram.getLowerSymbol());
    return map.get(h);
  }

  @Override
  public int compare(HexagramIF h1, HexagramIF h2)
  {
    return getIndex(h1)-getIndex(h2);
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
  
}
