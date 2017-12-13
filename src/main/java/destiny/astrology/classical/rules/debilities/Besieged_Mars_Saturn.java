/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:33:43
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.IBesieged;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.core.calendar.TimeTools;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Optional;

/**
 * Besieged between Mars and Saturn. 
 * 被火土夾制，只有日月水金，這四星有可能發生
 * 前一個角度與火土之一形成 0/90/180 , 後一個角度又與火土另一顆形成 0/90/180
 * 中間不能與其他行星形成角度
 */
public final class Besieged_Mars_Saturn extends Rule {

  /** 計算兩星夾角的工具箱 */
  private final IBesieged besiegedImpl;

  public Besieged_Mars_Saturn(IBesieged besiegedImpl) {
    this.besiegedImpl = besiegedImpl;
  }


  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, @NotNull Horoscope h) {
    if (planet == Planet.SUN || planet == Planet.MOON || planet == Planet.MERCURY || planet == Planet.VENUS) {
      //火土夾制，只考量「硬」角度 , 所以最後一個參數設成 true
      ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(h.getLmt() , h.getLocation());
      if (besiegedImpl.isBesieged(planet, Planet.MARS, Planet.SATURN, gmt, true, true)) {
        //addComment(Locale.TAIWAN , planet + " 被 " + Planet.MARS + " 以及 " + Planet.SATURN +" 夾制 (Besieged)");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, Planet.MARS, Planet.SATURN}));
      }
    }
    return Optional.empty();
  }

}
