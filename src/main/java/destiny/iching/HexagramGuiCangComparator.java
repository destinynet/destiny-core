/**
 * @author smallufo
 * Created on 2011/1/24 at 下午3:50:21
 */
package destiny.iching;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/** 歸藏卦序 */
public class HexagramGuiCangComparator extends AbstractHexagramSequence implements Comparator<HexagramIF> {

  private static final BiMap<Hexagram,Integer> map = new ImmutableBiMap.Builder<Hexagram , Integer>()
    .put(Hexagram.坤, 1)
    .put(Hexagram.乾, 2)
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
    .put(Hexagram.艮, 15)
    .put(Hexagram.震, 16)
    .put(Hexagram.大過, 17)
    
    .put(Hexagram.頤 , 18)
    .put(Hexagram.困, 19)
    .put(Hexagram.井, 20)
    .put(Hexagram.革, 21)
    .put(Hexagram.鼎, 22)
    
    .put(Hexagram.旅, 23)
    .put(Hexagram.豐, 24)
    .put(Hexagram.中孚, 25)
    .put(Hexagram.小過, 26)
    .put(Hexagram.臨, 27)
    
    .put(Hexagram.觀, 28)
    .put(Hexagram.萃, 29)
    .put(Hexagram.升, 30)
    
    .put(Hexagram.剝, 31)
    .put(Hexagram.復, 32)
    .put(Hexagram.大畜, 33)
    .put(Hexagram.無妄, 34)
    
    .put(Hexagram.睽, 35)
    .put(Hexagram.家人, 36)
    .put(Hexagram.節, 37)
    .put(Hexagram.渙, 38)
    
    .put(Hexagram.蹇, 39)
    .put(Hexagram.解, 40)
    .put(Hexagram.損, 41)
    .put(Hexagram.益, 42)
    .put(Hexagram.咸, 43)
    .put(Hexagram.恆, 44)
    .put(Hexagram.夬, 45)
    
    .put(Hexagram.姤, 46)
    .put(Hexagram.巽, 47)
    .put(Hexagram.兌, 48)
    .put(Hexagram.離, 49)
    .put(Hexagram.坎, 50)
    .put(Hexagram.謙, 51)
    
    .put(Hexagram.豫, 52)
    .put(Hexagram.歸妹, 53)
    .put(Hexagram.漸, 54)
    .put(Hexagram.晉, 55)
    .put(Hexagram.明夷, 56)
    .put(Hexagram.賁, 57)
    
    .put(Hexagram.噬嗑, 58)
    .put(Hexagram.既濟, 59)
    .put(Hexagram.未濟, 60)
    .put(Hexagram.遯, 61)
    
    .put(Hexagram.大壯, 62)
    .put(Hexagram.蠱, 63)
    .put(Hexagram.隨, 64).build();

  @Override
  protected BiMap<Hexagram, Integer> getMap() {
    return map;
  }

  @Override
  public int compare(@NotNull HexagramIF h1, @NotNull HexagramIF h2) {
    return getIndex(h1) - getIndex(h2);
  }

}
