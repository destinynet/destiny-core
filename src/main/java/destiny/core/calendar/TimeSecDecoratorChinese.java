/**
 * Created by smallufo on 2017-02-23.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 簡單的中文輸出 , 總共輸出 38位元 <BR/>
 * <pre>
 *西元　2000年01月01日　00時00分 00.00秒
 *西元前2000年12月31日　23時59分 59.99秒
 * </pre>
 */
public class TimeSecDecoratorChinese implements Decorator<LocalDateTime>, Serializable {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();

    logger.debug("time = {} , era = {}" , time , time.toLocalDate().getEra());

    TimeMinDecoratorChinese min = new TimeMinDecoratorChinese();
    sb.append(min.getOutputString(time));

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

}
