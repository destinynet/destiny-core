/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:51:44
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import destiny.astrology.classical.AverageDailyMotionMap;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

import static java.util.Optional.empty;

/** Swift in motion (faster than average). */
public final class Swift extends Rule {

  public Swift() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return AverageDailyMotionMap.get(planet).flatMap(dailyDeg ->
      h.getPositionOpt(planet).map(Position::getSpeedLng).flatMap(speedLng -> {
        if (speedLng > dailyDeg) {
          logger.debug("{} 每日移動速度比平均值還快" , planet);
          return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
        }
        return empty();
      })
    );

  }

}
