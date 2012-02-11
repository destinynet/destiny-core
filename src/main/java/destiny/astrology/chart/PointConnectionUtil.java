/**
 * @author smallufo 
 * Created on 2008/12/20 at 下午 3:03:03
 */
package destiny.astrology.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import destiny.astrology.Utils;

/** 傳入兩點座標，傳回兩點連線的圖形 */
public class PointConnectionUtil implements Serializable
{
  /** 從這點 */
  private double        x1, y1;

  /** 連到這點 */
  private double        x2, y2;

  private Stroke        stroke;

  private transient BufferedImage resultImage;

  public PointConnectionUtil(double x1, double y1, double x2, double y2, Stroke stroke)
  {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;

    this.stroke = stroke;
    this.calculate();
  }

  private void calculate()
  {
    int width = (int) Math.ceil(Math.abs(x1 - x2));
    int height = (int) Math.ceil(Math.abs(y1 - y2));

    //BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = resultImage.createGraphics();

    // 設定黑色為透明色
    /*
     * g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
     * RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
     * g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER ,
     * 1.0f)); g2d.setPaint(Color.BLACK); //對黑色透明 Rectangle2D.Double rect = new
     * Rectangle2D.Double(0,0,width,height); g2d.fill(rect);
     */

    //星體「點」，到「重新排列過的星體 icon 中點」的 線條角度
    double lineAngle;
    double differX = x2 - x1;
    double differY = y1 - y2;
    //線條的 tan
    double tan = differY / differX;
    double angle = Utils.getNormalizeDegree(Math.toDegrees(Math.atan(tan)));
    if (differX < 0)
      lineAngle = Utils.getNormalizeDegree(angle + 180);
    else
      lineAngle = Utils.getNormalizeDegree(Math.toDegrees(Math.atan(tan)));

    Point2D.Float p2dFrom = new Point2D.Float();
    Point2D.Float p2dTo   = new Point2D.Float();

    if (lineAngle >= 0 && lineAngle < 90)
    {
      //線條往右上發射
      p2dFrom.setLocation(0, y1 - y2);
      p2dTo.setLocation(x2 - x1, 0);
    }
    else if (lineAngle >= 90 && lineAngle < 180)
    {
      //線條往左上發射
      p2dFrom.setLocation(x1 - x2, y1 - y2);
      p2dTo.setLocation(0, 0);
    }
    else if (lineAngle >= 180 && lineAngle < 270)
    {
      //線條往左下發射
      p2dFrom.setLocation(x1 - x2, 0);
      p2dTo.setLocation(0, y2 - y1);
    }
    else
    {
      //線條往右下發射
      p2dFrom.setLocation(0, 0);
      p2dTo.setLocation(x2 - x1, y2 - y1);
    }

    //由內圈畫到外圈
    Line2D l2d = new Line2D.Double(p2dFrom, p2dTo);

    g2d.setColor(Color.WHITE);
    g2d.setStroke(stroke);
    //繪製這種斜線，要加上「反鋸齒」，否則線條可能會破掉！
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.draw(l2d);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
  }

  public BufferedImage getResultImage()
  {
    return resultImage;
  }
}
