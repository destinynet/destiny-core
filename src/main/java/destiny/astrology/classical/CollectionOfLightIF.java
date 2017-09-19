/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.astrology.classical;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.List;
import java.util.Optional;

public interface CollectionOfLightIF {

  /** 收集光線的形式 */
  enum CollectType {DIGNITIES , DEBILITIES , ALL}

  /**
   * 指定某種「光線蒐集模式」
   * @param planet
   * @param horoscopeContext
   * @param collectType @NotNull 詢問是否符合某種 「光線蒐集模式 : CollectType 」
   * @return
   */
  Tuple2<Boolean, List<Planet>> getResult(Planet planet, HoroscopeContextIF horoscopeContext, CollectType collectType);

  /**
   * 不指定「光線蒐集模式」，若呈現任何一種，就傳回來
   * @param planet
   * @param horoscopeContext
   * @return
   */
  //public Triple<Boolean , List<Planet> , Optional<CollectType>> getResult(Planet planet, HoroscopeContext horoscopeContext);

  Tuple3<Boolean , List<Planet> , Optional<CollectType>> getResult(Planet planet, HoroscopeContextIF horoscopeContext , Optional<CollectType> collectTypeOptional);
}
