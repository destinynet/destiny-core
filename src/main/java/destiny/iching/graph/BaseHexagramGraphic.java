/**
 * Created by smallufo on 2015-04-23.
 */
package destiny.iching.graph;

import destiny.iching.HexagramIF;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import static destiny.core.chart.Constants.GOLDEN_RATIO;

/**
 * 可任意指定寬高的單一卦象圖 ，純粹六條槓，沒有文字，沒有寬高比例綁定，可自定 top / right / bottom / left padding
┌────────┐
│ ██████ │
│        │
│ ██  ██ │
│        │
│ ██████ │
│        │
│ ██  ██ │
│        │
│ ██████ │
│        │
│ ██  ██ │
└────────┘
 * */
public class BaseHexagramGraphic implements Serializable {

  /** 可任意指定寬高的單一卦象圖 , 以及指定四邊的 padding */
  public static void render(@NotNull Graphics2D g , @NotNull HexagramIF hex, int width, int height, Color bg, Color fore,
                           double paddingTop, double paddingRight, double paddingBottom, double paddingLeft) {
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(bg);
    g.fillRect(0, 0, width, height);

    double rowHigh = getRowHeight(height , paddingTop , paddingBottom);

    g.setColor(fore);

    // 計算[半個]陰爻的寬度
    double half_width = (width - (paddingLeft+paddingRight))/ GOLDEN_RATIO/2;

    // 從上（六爻）畫到下（初爻），貼齊 upper padding
    for (int i=6 ; i >= 1 ; i--)
    {
      if (hex.getLine(i))
      {
        // 陽爻，畫出橫槓
        double x = paddingLeft ;
        double y = paddingTop + rowHigh*(12-2*i);
        g.fill(new Rectangle2D.Double(x , y , width-(paddingLeft+paddingRight) , rowHigh));
      }
      else
      {
        // 陰爻，畫出兩條橫槓
        // 半條陰爻的寬度
        double leftX  = paddingLeft;
        double leftY  = paddingTop + rowHigh*(12-2*i);
        double rightX = width - paddingRight - half_width;
        double rightY = paddingTop + rowHigh*(12-2*i);
        g.fill(new Rectangle2D.Double(leftX  , leftY  , half_width , rowHigh));
        g.fill(new Rectangle2D.Double(rightX , rightY , half_width , rowHigh));
      }
    }
  }

  protected static double getRowHeight(int height , double paddingTop , double paddingBottom) {
    return (height - paddingTop - paddingBottom)/11.0;
  }



}
