/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology;

/** 計算日食、月食的介面 */
public interface IEclipse {


  /** 從此時之後，全球各地的日食資料 */
  Eclipse nextSolarEclipse(double fromGmtJulDay , boolean forward);
}
