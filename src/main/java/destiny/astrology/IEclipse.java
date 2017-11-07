/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology;

import java.util.Optional;

/** 計算日食、月食的介面 */
public interface IEclipse {


  /** 從此時之後，全球各地的日食資料 */
  AbstractEclipse nextSolarEclipse(double fromGmtJulDay , boolean forward);

  /** 此時此刻，哪裡有發生日食，其特徵為何 */
  Optional<EclipseInfo> getEclipseInfo(double gmtJulDay);
}
