/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:28:37
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 取得星體的位置。支援 Planet , Asteroid , Hamburger <br/> 
 * Swiss Ephemeris 的實作是 StarPositionImpl <br/>
 * 原廠還支援 月亮的 Apsis , 但我使用 ApsisIF 來實作
 */
public interface StarPositionIF
{
  /** 設定觀測地點，對於 Centric.TOPO 有用 */
  public void setLocation(Location location);
  
  /** 設定座標系統 : 黃道/赤道/恆星 */
  public void setCoordinate(Coordinate coordinate);
  
  /** 設定中心系統 : 地心/日心/地表/質心 */
  public void setCentric(Centric centric);
  
  /** 取得星體的位置 , 時間是 GMT*/
  public Position getPosition(Star star , Time gmt);
  
  /** 取得星體的位置 , 包含當地時間以及座標 */
  public Position getPosition(Star star , Time lmt , Location location);
}
