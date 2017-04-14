/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei;

import java.io.Serializable;

public abstract class IHouseAbstractImpl<T> implements IHouse<T> , Serializable {

  private final ZStar star;

  protected IHouseAbstractImpl(ZStar star) {
    this.star = star;
  }

  @Override
  public ZStar getStar() {
    return star;
  }
}
