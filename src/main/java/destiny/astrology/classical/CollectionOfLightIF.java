/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.astrology.classical;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple2;

import java.util.List;
import java.util.Optional;

public interface CollectionOfLightIF {

  /** 收集光線的形式 */
  enum CollectType {
    DIGNITIES, DEBILITIES, ALL
  }

  /**
   * 指定某種「光線蒐集模式」
   *
   * @param collectType @NotNull 詢問是否符合某種 「光線蒐集模式」 : {@link CollectType}
   */
  Optional<List<Planet>> getResult(@NotNull Planet planet, @NotNull Horoscope h, @NotNull CollectType collectType);

  /**
   * 不指定「光線蒐集模式」，若呈現任何一種，就傳回來
   *
   * @param collectTypeOptional 若為 empty , 則傳回的 CollectType 會計算出，是哪種 {@link CollectType}
   */
  Optional<Tuple2<List<Planet> , CollectType>> getResultOptional(Planet planet, Horoscope h, Optional<CollectType> collectTypeOptional);
}
