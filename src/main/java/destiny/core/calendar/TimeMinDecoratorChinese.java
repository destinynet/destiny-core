/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.calendar;

import destiny.tools.ColorCanvas.AlignUtil;
import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;

import static java.time.chrono.IsoEra.BCE;
import static java.time.temporal.ChronoField.*;

/**
 * 簡單的中文輸出 , 只到「分」<BR/>
 * <pre>
 *西元　2000年01月01日　00時00分 00.00秒
 *西元前2000年12月31日　23時59分 59.99秒
 * </pre>
 */
public class TimeMinDecoratorChinese implements Decorator<ChronoLocalDateTime>, Serializable {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @NotNull
  @Override
  public String getOutputString(ChronoLocalDateTime time) {
    StringBuilder sb = new StringBuilder();

    logger.debug("time = {} , era = {}" , time , time.toLocalDate().getEra());

    sb.append("西元");
    if (time.toLocalDate().getEra() == BCE) {
      sb.append("前" );
    }
    else
      sb.append("　");
    sb.append(alignRight(time.get(YEAR_OF_ERA), 4)).append("年");
    sb.append(time.get(MONTH_OF_YEAR) < 10 ? "0" : "").append(time.get(MONTH_OF_YEAR)).append("月");
    sb.append(time.get(DAY_OF_MONTH) < 10 ? "0" : "").append(time.get(DAY_OF_MONTH)).append("日");
    sb.append("　");
    sb.append(time.get(HOUR_OF_DAY) < 10 ? "0" : "").append(time.get(HOUR_OF_DAY)).append("時");
    sb.append(time.get(MINUTE_OF_HOUR) < 10 ? "0" : "").append(time.get(MINUTE_OF_HOUR)).append("分");

    return sb.toString();
  }

  public static String alignRight(int value , int width) {
    StringBuffer sb = new StringBuffer(String.valueOf(value));
    int valueLength = sb.length();

    return AlignUtil.outputStringBuffer(valueLength , width , sb);
  }
}
