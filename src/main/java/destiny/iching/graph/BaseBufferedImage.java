/**
 * @author smallufo
 * Created on 2013/5/12 at 上午10:04:59
 */
package destiny.iching.graph;

import destiny.iching.HexagramIF;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

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
public class BaseBufferedImage extends BufferedImage implements Serializable
{
  protected Color              bg           = Color.WHITE;
  protected Color              fore         = Color.BLACK;
  
  protected HexagramIF hexagram;
  
  protected int width;
  protected int height;
  
  protected double paddingTop;
  private double paddingRight;
  double paddingBottom;
  double paddingLeft;
  
  // 每條 row 的高度
  protected double rowHigh;
  
  protected Logger logger = LoggerFactory.getLogger(getClass());
  
  /** 可任意指定寬高的單一卦象圖 , 以及指定四邊的 padding */
  public BaseBufferedImage(@NotNull HexagramIF hex, int width, int height, Color bg, Color fore, double paddingTop, double paddingRight, double paddingBottom, double paddingLeft)
  {
    super(width , height , BufferedImage.TYPE_INT_ARGB);


    this.hexagram = hex;
    this.width = width;
    this.height = height;
    this.bg = bg;
    this.fore = fore;
    
    this.paddingTop = paddingTop;
    this.paddingRight = paddingRight;
    this.paddingBottom = paddingBottom;
    this.paddingLeft = paddingLeft;

    Graphics2D g = this.createGraphics();

    BaseHexagramGraphic.render(g, hex, width, height, bg, fore, paddingTop, paddingRight, paddingBottom, paddingLeft);

    g.dispose();
  } // constructor

  public double getRowHeight()
  {
    return (height - paddingTop - paddingBottom)/11.0; 
  }
}
