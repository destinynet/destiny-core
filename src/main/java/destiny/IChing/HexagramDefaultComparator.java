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
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * 周易卦序Comparator
 */
public class HexagramDefaultComparator implements Comparator<HexagramIF> , HexagramSequenceIF , Serializable
{
  private static final BiMap<Hexagram,Integer> map = new ImmutableBiMap.Builder<Hexagram , Integer>()
    .put(Hexagram.乾, 1)
    .put(Hexagram.坤, 2)
    .put(Hexagram.屯, 3)
    .put(Hexagram.蒙, 4)
    .put(Hexagram.需, 5)
    .put(Hexagram.訟, 6)
    .put(Hexagram.師, 7)
    
    .put(Hexagram.比, 8)
    .put(Hexagram.小畜, 9)
    .put(Hexagram.履, 10)
    .put(Hexagram.泰, 11)
    .put(Hexagram.否, 12)
    
    .put(Hexagram.同人, 13)
    .put(Hexagram.大有, 14)
    .put(Hexagram.謙, 15)
    .put(Hexagram.豫, 16)
    .put(Hexagram.隨, 17)
    
    .put(Hexagram.蠱, 18)
    .put(Hexagram.臨, 19)
    .put(Hexagram.觀, 20)
    .put(Hexagram.噬嗑, 21)
    .put(Hexagram.賁, 22)
    
    .put(Hexagram.剝, 23)
    .put(Hexagram.復, 24)
    .put(Hexagram.無妄, 25)
    .put(Hexagram.大畜, 26)
    .put(Hexagram.頤, 27)
    
    .put(Hexagram.大過, 28)
    .put(Hexagram.坎, 29)
    .put(Hexagram.離, 30)
    
    .put(Hexagram.咸, 31)
    .put(Hexagram.恆, 32)
    .put(Hexagram.遯, 33)
    .put(Hexagram.大壯, 34)
    
    .put(Hexagram.晉, 35)
    .put(Hexagram.明夷, 36)
    .put(Hexagram.家人, 37)
    .put(Hexagram.睽, 38)
    
    .put(Hexagram.蹇, 39)
    .put(Hexagram.解, 40)
    .put(Hexagram.損, 41)
    .put(Hexagram.益, 42)
    .put(Hexagram.夬, 43)
    .put(Hexagram.姤, 44)
    .put(Hexagram.萃, 45)
    
    .put(Hexagram.升, 46)
    .put(Hexagram.困, 47)
    .put(Hexagram.井, 48)
    .put(Hexagram.革, 49)
    .put(Hexagram.鼎, 50)
    .put(Hexagram.震, 51)
    
    .put(Hexagram.艮, 52)
    .put(Hexagram.漸, 53)
    .put(Hexagram.歸妹, 54)
    .put(Hexagram.豐, 55)
    .put(Hexagram.旅, 56)
    .put(Hexagram.巽, 57)
    
    .put(Hexagram.兌, 58)
    .put(Hexagram.渙, 59)
    .put(Hexagram.節, 60)
    .put(Hexagram.中孚, 61)
    
    .put(Hexagram.小過, 62)
    .put(Hexagram.既濟, 63)
    .put(Hexagram.未濟, 64).build();


  private final static Map<Hexagram , Integer> mapOld = Collections.synchronizedMap(new HashMap<Hexagram , Integer>());
  static
  {
    mapOld.put(Hexagram.乾, 1);
    mapOld.put(Hexagram.坤, 2);
    mapOld.put(Hexagram.屯, 3);
    mapOld.put(Hexagram.蒙, 4);
    mapOld.put(Hexagram.需, 5);
    mapOld.put(Hexagram.訟, 6);
    mapOld.put(Hexagram.師, 7);
    
    mapOld.put(Hexagram.比, 8);
    mapOld.put(Hexagram.小畜, 9);
    mapOld.put(Hexagram.履, 10);
    mapOld.put(Hexagram.泰, 11);
    mapOld.put(Hexagram.否, 12);
    
    mapOld.put(Hexagram.同人, 13);
    mapOld.put(Hexagram.大有, 14);
    mapOld.put(Hexagram.謙, 15);
    mapOld.put(Hexagram.豫, 16);
    mapOld.put(Hexagram.隨, 17);
    
    mapOld.put(Hexagram.蠱, 18);
    mapOld.put(Hexagram.臨, 19);
    mapOld.put(Hexagram.觀, 20);
    mapOld.put(Hexagram.噬嗑, 21);
    mapOld.put(Hexagram.賁, 22);
    
    mapOld.put(Hexagram.剝, 23);
    mapOld.put(Hexagram.復, 24);
    mapOld.put(Hexagram.無妄, 25);
    mapOld.put(Hexagram.大畜, 26);
    mapOld.put(Hexagram.頤, 27);
    
    mapOld.put(Hexagram.大過, 28);
    mapOld.put(Hexagram.坎, 29);
    mapOld.put(Hexagram.離, 30);
    
    mapOld.put(Hexagram.咸, 31);
    mapOld.put(Hexagram.恆, 32);
    mapOld.put(Hexagram.遯, 33);
    mapOld.put(Hexagram.大壯, 34);
    
    mapOld.put(Hexagram.晉, 35);
    mapOld.put(Hexagram.明夷, 36);
    mapOld.put(Hexagram.家人, 37);
    mapOld.put(Hexagram.睽, 38);
    
    mapOld.put(Hexagram.蹇, 39);
    mapOld.put(Hexagram.解, 40);
    mapOld.put(Hexagram.損, 41);
    mapOld.put(Hexagram.益, 42);
    mapOld.put(Hexagram.夬, 43);
    mapOld.put(Hexagram.姤, 44);
    mapOld.put(Hexagram.萃, 45);
    
    mapOld.put(Hexagram.升, 46);
    mapOld.put(Hexagram.困, 47);
    mapOld.put(Hexagram.井, 48);
    mapOld.put(Hexagram.革, 49);
    mapOld.put(Hexagram.鼎, 50);
    mapOld.put(Hexagram.震, 51);
    
    mapOld.put(Hexagram.艮, 52);
    mapOld.put(Hexagram.漸, 53);
    mapOld.put(Hexagram.歸妹, 54);
    mapOld.put(Hexagram.豐, 55);
    mapOld.put(Hexagram.旅, 56);
    mapOld.put(Hexagram.巽, 57);
    
    mapOld.put(Hexagram.兌, 58);
    mapOld.put(Hexagram.渙, 59);
    mapOld.put(Hexagram.節, 60);
    mapOld.put(Hexagram.中孚, 61);
    
    mapOld.put(Hexagram.小過, 62);
    mapOld.put(Hexagram.既濟, 63);
    mapOld.put(Hexagram.未濟, 64);
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

  /** 從卦序傳回卦 */
  @Override
  public Hexagram getHexagram(int index)
  {
    if (index > 64)
      index = index % 64;
    
    if (index <=0 )
      index = 64-(0-index) % 64;
    
    return map.inverse().get(index);
    /*
    Iterator<Entry<Hexagram , Integer>> it = mapOld.entrySet().iterator();
    while(it.hasNext())
    {
      Entry<Hexagram , Integer> entry = it.next();
      if (entry.getValue().intValue() == index)
        return entry.getKey();
    }
    return null;
    */
  }
  
  @Override
  public int compare(HexagramIF hexagram1, HexagramIF hexagram2)
  {
    return getIndex(hexagram1) - getIndex(hexagram2);
  }
}
