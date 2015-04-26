/**
 * Created by smallufo on 2015-04-24.
 */
package destiny.iching.graph;

public interface GraphicsProcessor {

  // 左邊還是右邊
  enum Side {
    L, R
  }

  void process(PairGraphics pairGraphics);
}
