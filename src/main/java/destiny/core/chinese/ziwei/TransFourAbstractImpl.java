/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Map;

import static org.jooq.lambda.tuple.Tuple.tuple;

public abstract class TransFourAbstractImpl implements TransFour {

  protected abstract Map<Tuple2<Stem, Type> , ZStar> getTransMap();

  @Override
  public ZStar getStarOf(Stem year, Type type) {
    return getTransMap().get(tuple(year , type));
  }
}
