/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

/** 民國紀年 */
public class IntAgeNoteMinguoYearImpl implements IntAgeNote , Serializable {

  private final static Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  @Override
  public Optional<String> getAgeNote(double gmtJulDay) {
    ChronoLocalDateTime start = revJulDayFunc.apply(gmtJulDay);
    int westYear = start.get(ChronoField.YEAR_OF_ERA);
    if (westYear >= 1912) {
      return Optional.of("民"+(westYear-1911));
    }
    else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> getAgeNote(Tuple2<Double, Double> startAndEnd) {
    return getAgeNote(startAndEnd.v1());
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
