/**
 * @author smallufo 
 * Created on 2008/12/14 at 下午 3:30:42
 */ 
package destiny.astrology.chart.astrolog;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;

import destiny.astrology.Utils;

/** 計算一個星體 icon , 佔據該星盤的弧角，從幾度，到幾度 */
public class RingPointIconRangeUtil implements Serializable
{
  private double center;
  private double radius;
  private double innerFrom;
  private double outerTo;
  private BufferedImage planetImg;
  
  /** 該 icon 「未重新排列前」，佔據的度數，比較小的值 */
  private double rangeFrom;
  /** 該 icon 「未重新排列前」，佔據的度數，比較大的值 */
  private double rangeTo;
  
  /** 這個 icon 的中心點 , 原始置於「從第一象限算起」幾度 (尚未調整位置) */
  private double angle;
  
  public RingPointIconRangeUtil(double center , double radius  , double innerFrom , double outerTo , BufferedImage planetImg , double angle)
  {
    this.center = center;
    this.radius = radius;
    this.innerFrom = innerFrom;
    this.outerTo = outerTo;
    this.planetImg = planetImg;
    this.angle = angle;
    this.calculateRange();
  }
  
  private void calculateRange()
  {
    //icon 寬度
    double imgW = planetImg.getWidth();
    //icon 高度
    double imgH = planetImg.getHeight();
    
    //System.out.println("圖寬 : " + imgW + " , 圖高 : " + imgH);
    
    double x = center + radius * (innerFrom+outerTo)/2 * Math.cos(Math.toRadians(angle));
    double y = center - radius * (innerFrom+outerTo)/2 * Math.sin(Math.toRadians(angle));
    
    
    /** 依序儲存左上、右上、左下、右下的度數 */
    double[] rectangleDegs = new double[4];
    
    double 左上角X = x - imgW/2;
    double 左上角Y = y - imgH/2;
    rectangleDegs[0] = getAngle(左上角X, 左上角Y , center);
    
    double 右上角X = x + imgW/2;
    double 右上角Y = y - imgH/2;
    rectangleDegs[1] = getAngle(右上角X, 右上角Y , center);
    
    double 左下角X = x - imgW/2;
    double 左下角Y = y + imgH/2;
    rectangleDegs[2] = getAngle(左下角X, 左下角Y , center);
    
    double 右下角X = x + imgW/2;
    double 右下角Y = y + imgH/2;
    rectangleDegs[3] = getAngle(右下角X, 右下角Y , center);

    //System.out.println("原始 center : ("+x+","+y+") , 角度 "+ getAngle(x, y , center) + "\n左上 = (" + 左上角X+","+左上角Y + ") , \n右上 = (" + 右上角X+","+右上角Y+") , \n左下 = ("+左下角X+","+左下角Y+") , \n右下 = (" + 右下角X+","+右下角Y+")");
    
    Arrays.sort(rectangleDegs);
    //System.out.print("排序後變成：\t");
    //for(double d : rectangleDegs)
      //System.out.print(d+"\t");
    //System.out.println("佔據 : " + (rectangleDegs[3] - rectangleDegs[0]) + " 度\n");
    if (rectangleDegs[3] - rectangleDegs[0] < 180)
    {
      rangeFrom = rectangleDegs[0];
      rangeTo = rectangleDegs[3];
    }
    else
    {
      //icon 佔據跨過七宮宮首，一定是 358~2 度之間
      rangeFrom = rectangleDegs[3];
      rangeTo = rectangleDegs[0];
    }
  }
  
  /** 該 icon 「未重新排列前」，佔據的度數，比較小的值(可能是 358,359)  */
  public double getRangeFrom()
  {
    return this.rangeFrom;
  }
  
  /** 該 icon 「未重新排列前」，佔據的度數，比較大的值(可能只是 0.x 或 1.x)  */
  public double getRangeTo()
  {
    return this.rangeTo;
  }
  
  /** 傳入螢幕的 x , y 座標，以及圓心座標 , 傳回度數 */
  public static double getAngle(double x , double y , double center)
  {
    double differX = x - center;
    double differY = center - y;
    double tan = differY / differX;
    double angle = Utils.getNormalizeDegree(Math.toDegrees(Math.atan(tan)));
    if (differX < 0)
      return Utils.getNormalizeDegree(angle + 180);
    else
      return Utils.getNormalizeDegree(Math.toDegrees(Math.atan(tan)));
  }
}
