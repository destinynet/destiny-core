package destiny.astrology;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeConverter
{
  public TimeConverter()
  {
  }
  
  /**
  * 將當地時間轉成 GMT 時間
  * 例如 :
  * Calendar LMT = new GregorianCalendar(1976,6-1,4,10 ,22,0);
  * TimeZone TZ = TimeZone.getTimeZone("Asia/Taipei");
  * Calendar result = TimeConverter.toGMT(LMT , TZ);
  */
  public static Calendar toGMT(Calendar LMT , TimeZone TZ)
  {
    Calendar result = Calendar.getInstance();
    result.setTimeInMillis(LMT.getTimeInMillis() - TZ.getOffset(LMT.getTimeInMillis()));
    return result;
  }
  
}