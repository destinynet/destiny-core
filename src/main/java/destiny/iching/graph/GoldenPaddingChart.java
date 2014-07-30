/**
 * @author smallufo
 * Created on 2013/5/12 at 上午10:36:33
 */
package destiny.iching.graph;

import destiny.iching.HexagramIF;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static destiny.core.chart.Constants.GOLDEN_RATIO;
import static destiny.core.chart.Constants.WIDTH_HEIGHT;

/**
 * 一個「本卦」的圖形，設計成黃金比例的卡片
 * 若寬為 1 , 高為 1.618
 */
public class GoldenPaddingChart extends BaseHexagramChart 
{
  protected double paddingX;
  protected double paddingY;
  
  public GoldenPaddingChart(@NotNull HexagramIF hex, WIDTH_HEIGHT which , int value, Color bg, Color fore)
  {
    super(hex
        , (which == WIDTH_HEIGHT.WIDTH  ? value : (int)(value / GOLDEN_RATIO))
        , (which == WIDTH_HEIGHT.HEIGHT ? value : (int)(value * GOLDEN_RATIO))
        , bg, fore
        , (which == WIDTH_HEIGHT.WIDTH ? (value * GOLDEN_RATIO - value)/2 : (value - value/ GOLDEN_RATIO)/2 )
        , (which == WIDTH_HEIGHT.WIDTH ? value * (2- GOLDEN_RATIO)/2 : (value / GOLDEN_RATIO) * (2- GOLDEN_RATIO) /2 )
        , (which == WIDTH_HEIGHT.WIDTH ? (value * GOLDEN_RATIO - value)/2 : (value - value/ GOLDEN_RATIO)/2 )
        , (which == WIDTH_HEIGHT.WIDTH ? value * (2- GOLDEN_RATIO)/2 : (value / GOLDEN_RATIO) * (2- GOLDEN_RATIO) /2 )
        );
    
    this.paddingX = getWidth()  * (2- GOLDEN_RATIO) / 2;
    this.paddingY = getHeight() * (2- GOLDEN_RATIO) / 2;
  }

}
