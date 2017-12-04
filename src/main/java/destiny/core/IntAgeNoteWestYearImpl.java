/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import kotlin.Pair;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

public class IntAgeNoteWestYearImpl implements IntAgeNote , Serializable {

  private final static Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  @Override
  public Optional<String> getAgeNote(double gmtJulDay) {
    ChronoLocalDateTime start = revJulDayFunc.apply(gmtJulDay);
    return Optional.of(String.valueOf(start.get(ChronoField.YEAR_OF_ERA)));
  }

  @Override
  public Optional<String> getAgeNote(Pair<Double, Double> startAndEnd) {
    return getAgeNote(startAndEnd.getFirst());
  }

  @Override
  public String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(getClass().getName() , locale).getString("name");
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }


  @Override
  public String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
