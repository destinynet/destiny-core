/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import java.io.Serializable;

public abstract class AbstractPositionImpl<T extends Point> implements IPosition<T> , Serializable {

  private final T t;

  AbstractPositionImpl(T t) {
    this.t = t;
  }

  @Override
  public T getPoint() {
    return t;
  }

}
