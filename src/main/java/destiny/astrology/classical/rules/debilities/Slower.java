/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:08:31
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import destiny.astrology.classical.AverageDailyMotionMap;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

import static java.util.Optional.empty;

public final class Slower extends Rule {

  public Slower() {
  }


  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return AverageDailyMotionMap.get(planet).flatMap(dailyDeg ->
      h.getPositionOptional(planet).map(Position::getSpeedLng).flatMap(speedLng -> {
        if (speedLng < dailyDeg) {
          logger.debug("{} 每日移動速度比平均值還慢" , planet);
          return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
        }
        return empty();
      })
    );

  }

}
