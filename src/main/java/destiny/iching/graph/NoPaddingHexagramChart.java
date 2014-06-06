/**
 * @author smallufo
 * Created on 2013/5/9 at 下午11:36:07
 */
package destiny.iching.graph;

import destiny.iching.HexagramIF;

import java.awt.*;

import static destiny.core.chart.Constants.GOLDEN_RATIO;
import static destiny.core.chart.Constants.WIDTH_HEIGHT;

/**
 * 無外框的單一易卦圖
 */
public class NoPaddingHexagramChart extends BaseHexagramChart
{
  
  /**
   * 無外框的單一易卦圖，寬高自定
   */
  public NoPaddingHexagramChart(HexagramIF hex , int width , int height , Color bg , Color fore)
  {
    super(hex , width , height , bg , fore , 0 , 0 , 0 , 0);
  }
  
  /**
   * 無外框的單一易卦圖，以黃金比例呈現
   */
  public NoPaddingHexagramChart(HexagramIF hex , WIDTH_HEIGHT which , int value , Color bg , Color fore)
  {
    super(hex
        , (which == WIDTH_HEIGHT.WIDTH  ? value : (int)(value / GOLDEN_RATIO))
        , (which == WIDTH_HEIGHT.HEIGHT ? value : (int)(value * GOLDEN_RATIO))
        , bg, fore, 0, 0, 0, 0);
  } // constructor
  
  
}
