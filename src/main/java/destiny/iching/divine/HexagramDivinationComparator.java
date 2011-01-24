/**
 * @author smallufo
 * @date 2002/8/19 , @date 2009/6/23 改寫
 */
package destiny.iching.divine;

import java.util.Comparator;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import destiny.iching.Hexagram;
import destiny.iching.HexagramIF;
import destiny.iching.HexagramSequenceIF;

/**
 * 京房卦序：乾為天,天風姤,天山遯,天地否...
 */
public class HexagramDivinationComparator implements Comparator<HexagramIF> , HexagramSequenceIF
{
  private final static BiMap<Hexagram,Integer> map = new ImmutableBiMap.Builder<Hexagram , Integer>()
    .put(Hexagram.乾   ,  1)
    .put(Hexagram.姤   ,  2)
    .put(Hexagram.遯   ,  3)
    .put(Hexagram.否   ,  4)
    .put(Hexagram.觀   ,  5)
    .put(Hexagram.剝   ,  6)
    .put(Hexagram.晉   ,  7)
    .put(Hexagram.大有 ,  8)
    
    .put(Hexagram.震   ,  9)
    .put(Hexagram.豫   , 10)
    .put(Hexagram.解   , 11)
    .put(Hexagram.恆   , 12)
    .put(Hexagram.升   , 13)
    .put(Hexagram.井   , 14)
    .put(Hexagram.大過 , 15)
    .put(Hexagram.隨   , 16)
    
    .put(Hexagram.坎   , 17)
    .put(Hexagram.節   , 18)
    .put(Hexagram.屯   , 19)
    .put(Hexagram.既濟 , 20)
    .put(Hexagram.革   , 21)
    .put(Hexagram.豐   , 22)
    .put(Hexagram.明夷 , 23)
    .put(Hexagram.師   , 24)
    
    .put(Hexagram.艮   , 25)
    .put(Hexagram.賁   , 26)
    .put(Hexagram.大畜 , 27)
    .put(Hexagram.損   , 28)
    .put(Hexagram.睽   , 29)
    .put(Hexagram.履   , 30)
    .put(Hexagram.中孚 , 31)
    .put(Hexagram.漸   , 32)
    
    .put(Hexagram.坤   , 33)
    .put(Hexagram.復   , 34)
    .put(Hexagram.臨   , 35)
    .put(Hexagram.泰   , 36)
    .put(Hexagram.大壯 , 37)
    .put(Hexagram.夬   , 38)
    .put(Hexagram.需   , 39)
    .put(Hexagram.比   , 40)
    
    .put(Hexagram.巽   , 41)
    .put(Hexagram.小畜 , 42)
    .put(Hexagram.家人 , 43)
    .put(Hexagram.益   , 44)
    .put(Hexagram.無妄 , 45)
    .put(Hexagram.噬嗑 , 46)
    .put(Hexagram.頤   , 47)
    .put(Hexagram.蠱   , 48)
    
    .put(Hexagram.離   , 49)
    .put(Hexagram.旅   , 50)
    .put(Hexagram.鼎   , 51)
    .put(Hexagram.未濟 , 52)
    .put(Hexagram.蒙   , 53)
    .put(Hexagram.渙   , 54)
    .put(Hexagram.訟   , 55)
    .put(Hexagram.同人 , 56)
    
    .put(Hexagram.兌   , 57)
    .put(Hexagram.困   , 58)
    .put(Hexagram.萃   , 59)
    .put(Hexagram.咸   , 60)
    .put(Hexagram.蹇   , 61)
    .put(Hexagram.謙   , 62)
    .put(Hexagram.小過 , 63)
    .put(Hexagram.歸妹 , 64)
    .build();

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
  public Hexagram getHexagram(int index)
  {
    if (index > 64)
      index = index % 64;
    
    if (index <=0 )
      index = 64-(0-index) % 64;

    return map.inverse().get(index);
  }

  @Override
  public int compare(HexagramIF h1, HexagramIF h2)
  {
    return getIndex(h1)-getIndex(h2);
  }
}
