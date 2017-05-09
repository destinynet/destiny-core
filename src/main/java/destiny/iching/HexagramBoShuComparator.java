/**
 * @author smallufo
 * Created on 2011/1/24 at 下午4:00:08
 */
package destiny.iching;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * 帛書卦序
 */
public class HexagramBoShuComparator extends AbstractHexagramSequence implements Comparator<HexagramIF> {

  private static final BiMap<Hexagram,Integer> map = new ImmutableBiMap.Builder<Hexagram , Integer>()
    .put(Hexagram.乾, 1)
    .put(Hexagram.否, 2)
    .put(Hexagram.遯, 3)
    .put(Hexagram.履, 4)
    .put(Hexagram.訟, 5)
    .put(Hexagram.同人, 6)
    .put(Hexagram.無妄, 7)
    
    .put(Hexagram.姤, 8)
    .put(Hexagram.艮, 9)
    .put(Hexagram.大畜, 10)
    .put(Hexagram.剝, 11)
    .put(Hexagram.損, 12)
    
    .put(Hexagram.蒙, 13)
    .put(Hexagram.賁, 14)
    .put(Hexagram.頤, 15)
    .put(Hexagram.蠱, 16)
    .put(Hexagram.坎, 17)
    
    .put(Hexagram.需, 18)
    .put(Hexagram.比, 19)
    .put(Hexagram.蹇, 20)
    .put(Hexagram.節, 21)
    .put(Hexagram.既濟, 22)
    
    .put(Hexagram.屯, 23)
    .put(Hexagram.井, 24)
    .put(Hexagram.震, 25)
    .put(Hexagram.大壯, 26)
    .put(Hexagram.豫, 27)
    
    .put(Hexagram.小過, 28)
    .put(Hexagram.歸妹, 29)
    .put(Hexagram.解, 30)
    
    .put(Hexagram.豐, 31)
    .put(Hexagram.恆, 32)
    .put(Hexagram.坤, 33)
    .put(Hexagram.泰, 34)
    
    .put(Hexagram.謙, 35)
    .put(Hexagram.臨, 36)
    .put(Hexagram.師, 37)
    .put(Hexagram.明夷, 38)
    
    .put(Hexagram.復, 39)
    .put(Hexagram.升, 40)
    .put(Hexagram.兌, 41)
    .put(Hexagram.夬, 42)
    .put(Hexagram.萃, 43)
    .put(Hexagram.咸, 44)
    .put(Hexagram.困, 45)
    
    .put(Hexagram.革, 46)
    .put(Hexagram.隨, 47)
    .put(Hexagram.大過, 48)
    .put(Hexagram.離, 49)
    .put(Hexagram.大有, 50)
    .put(Hexagram.晉, 51)
    
    .put(Hexagram.旅, 52)
    .put(Hexagram.睽, 53)
    .put(Hexagram.未濟, 54)
    .put(Hexagram.噬嗑, 55)
    .put(Hexagram.鼎, 56)
    .put(Hexagram.巽, 57)
    
    .put(Hexagram.小畜, 58)
    .put(Hexagram.觀, 59)
    .put(Hexagram.漸, 60)
    .put(Hexagram.中孚, 61)
    
    .put(Hexagram.渙, 62)
    .put(Hexagram.家人, 63)
    .put(Hexagram.益, 64).build();

  @Override
  protected BiMap<Hexagram, Integer> getMap() {
    return map;
  }

  @Override
  public int compare(@NotNull HexagramIF h1, @NotNull HexagramIF h2)
  {
    return getIndex(h1)-getIndex(h2);
  }

}
