/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.iching;

import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/** 以 BiMap 實作 64卦的卦序 */
public abstract class AbstractHexagramSequence implements HexagramSequenceIF, Serializable {

  protected abstract BiMap<Hexagram, Integer> getMap();

  @Override
  public int getIndex(@NotNull HexagramIF hexagram) {
    Hexagram h = Hexagram.getHexagram(hexagram.getUpperSymbol(), hexagram.getLowerSymbol());
    return getMap().get(h);
  }


  @Override
  public Hexagram getHexagram(int index) {
    if (index > 64)
      index = index % 64;

    if (index <= 0)
      index = 64 - (0 - index) % 64;

    return getMap().inverse().get(index);
  }

}
