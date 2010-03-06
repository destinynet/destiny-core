/**
 * @author smallufo 
 * Created on 2008/12/11 at 上午 3:13:41
 */ 
package destiny.astrology.chart;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;


public interface Ring
{
  public double getInnerFrom();
  
  public double getOuterTo();
  
  /** 要繪製輻射線的度數，以第一象限計算 */
  public EmitLineProperties[] getEmitLineProperties();
  
  /** 第幾度要放置什麼影像 */
  public Map<Double , BufferedImage> getBfferedImages();
  
  /** 繪製線條 (交角線條) */
  public Set<PointConnection> getPointConnections();
  
  /** 取得繪製內環的樣式 , 如果傳回 null , 代表不繪製內環 */
  public Style getInnerRingStyle();
}
