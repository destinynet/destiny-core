/**
 * Created by smallufo on 2017-11-08.
 */
package destiny.astrology;

import java.io.Serializable;

/**
 * 在一個地點觀測到的日、月食資料，開始、結束、各個接觸點 (contact) 各為何時
 */
public class EclipseSpan implements Serializable {

  /** 經度 */
  private final double lng;

  /** 緯度 */
  private final double lat;

  /** 高度 (米) */
  private final double alt;

  /** 哪一種食，其 起訖 資訊為何 */
  private final AbstractEclipse eclipse;


  public EclipseSpan(double lng, double lat, double alt, AbstractEclipse eclipse) {
    this.lng = lng;
    this.lat = lat;
    this.alt = alt;
    this.eclipse = eclipse;
  }

  public double getLng() {
    return lng;
  }

  public double getLat() {
    return lat;
  }

  public double getAlt() {
    return alt;
  }

  public AbstractEclipse getEclipse() {
    return eclipse;
  }

  @Override
  public String toString() {
    return "[EclipseSpan " + "lng=" + lng + ", lat=" + lat + ", alt=" + alt + ", eclipse=" + eclipse + ']';
  }
}
