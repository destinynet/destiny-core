/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import java.io.Serializable;

public abstract class AbstractPositionImpl<T> implements IPosition<T> , Serializable {

  private final Point point;

  protected AbstractPositionImpl(Point point) {this.point = point;}

  @Override
  public Point getPoint() {
    return point;
  }


}
