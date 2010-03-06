package destiny.astrology.prediction;

import destiny.core.calendar.Time;

/** 
 * 繼承自 Mappable , 只是此推運法是「線性收斂」的 : 只要時間不同，所收斂到的時間，也必定不相同<br/>
 * 可以反推(從近推到遠)：從推運日期逆推到真實日期 。 <br/>
 * ProgressionSecondary , ProgressionTertiary , ProgressionMinor 皆屬此類 , Transits 更不用提，收斂係數等於 1:1<br/>
 * 但 SolarReturn , LunarReturn 不屬於此類
 * 
 */
interface LinearIF extends Mappable
{
  /**
   * 取得反推（發散 divergent）的時間
   * @param natalTime 起始時間，通常是出生時間
   * @param nowTime   某時刻
   * @return nowTime 相對於 natalTime 輻射放大出去， 是此人的何時
   */
  public Time getDivergentTime(Time natalTime , Time nowTime);
}
