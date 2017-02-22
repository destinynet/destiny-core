/**
 * @author smallufo
 * Created on 2008/1/3 at 上午 8:55:07
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.BesiegedIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.core.calendar.Time;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 夾輔 : 被金星木星包夾 , 是很幸運的情形<br/>
 * 角度考量 0/60/90/120/180 <br/>
 * 中間不能與其他行星形成角度
 */
public class Besieged_Jupiter_Venus extends Rule {

  /** 計算兩星夾角的工具箱 */
  private final BesiegedIF besiegedImpl;

  public Besieged_Jupiter_Venus(BesiegedIF besiegedImpl) {
    this.besiegedImpl = besiegedImpl;
  }


  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext) {
    if (planet == Planet.SUN || planet == Planet.MOON || planet == Planet.MERCURY || planet == Planet.MARS || planet == Planet.SATURN) {
      if (besiegedImpl.isBesieged(planet, Planet.VENUS, Planet.JUPITER, Time.getGmtFromLmt(horoscopeContext.getLmt(), horoscopeContext.getLocation()), true, false)) {
        //planet + " 被 " + Planet.VENUS + " 以及 " + Planet.JUPITER + " 夾輔 (善意 Besieged)"
        return Optional.of(ImmutablePair.of("comment", new Object[]{planet, Planet.VENUS, Planet.JUPITER}));
      }
    }
    return Optional.empty();
  }

}
