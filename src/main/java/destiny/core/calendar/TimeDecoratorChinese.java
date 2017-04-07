/**
 * Created by smallufo on 2017-02-23.
 */
package destiny.core.calendar;

import destiny.tools.ColorCanvas.AlignUtil;
import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

import static java.time.chrono.IsoEra.BCE;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;

/**
 * 簡單的中文輸出 , 總共輸出 38位元 <BR/>
 * <pre>
 *西元　2000年01月01日　00時00分 00.00秒
 *西元前2000年12月31日　23時59分 59.99秒
 * </pre>
 */
public class TimeDecoratorChinese implements Decorator<LocalDateTime>, Serializable {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();

    logger.debug("time = {} , era = {}" , time , time.toLocalDate().getEra());

    sb.append("西元");
    if (time.toLocalDate().getEra() == BCE) {
      sb.append("前" );
    }
    else
      sb.append("　");
    sb.append(alignRight(time.get(YEAR_OF_ERA), 4)).append("年");
    sb.append(time.getMonthValue() < 10 ? "0" : "").append(time.getMonthValue()).append("月");
    sb.append(time.getDayOfMonth() < 10 ? "0" : "").append(time.getDayOfMonth()).append("日");
    sb.append("　");
    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour()).append("時");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute()).append("分");

    sb.append(' ');
    if (time.getSecond() < 10) {
      sb.append("0");
    }
    sb.append(time.getSecond());

    if (time.getNano() == 0) {
      sb.append(".00");
    } else {
      sb.append(".");
      sb.append(String.valueOf(time.getNano()).substring(0,2));
    }
    sb.append("秒");
    return sb.toString();
  }

  public static String alignRight(int value , int width) {
    StringBuffer sb = new StringBuffer(String.valueOf(value));
    int valueLength = sb.length();

    return AlignUtil.outputStringBuffer(valueLength , width , sb);
  }
}
