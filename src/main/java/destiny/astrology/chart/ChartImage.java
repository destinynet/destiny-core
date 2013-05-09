/**
 * @author smallufo 
 * Created on 2008/12/10 at 上午 2:23:19
 */
package destiny.astrology.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

import destiny.astrology.Horoscope;

/**
 * 繪製一張命盤 , 嘗試打浮水印
 * 測試中 , 勿用
 * @deprecated
 */
public class ChartImage extends BufferedImage
{
  Color bg   = Color.BLACK;
  Color fore = Color.WHITE;
  int   center;

  public ChartImage(Horoscope horoscope , int width)
  {
    super(width, width, BufferedImage.TYPE_INT_RGB);
    
    
    center = width / 2;
    Graphics2D g = this.createGraphics();
    //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //填滿背景
    g.setColor(bg);
    g.fillRect(0, 0, width, width);

    //設定前景色
    g.setColor(fore);
    
    //畫出水平線
    g.drawLine(0,width/2 , width , width/2);
    
    Ellipse2D.Double 最外圓環 = new Ellipse2D.Double(0, 0, width, width);
    g.draw(最外圓環);
    
    //取得西落點是黃道幾度
    double degDesc = horoscope.getCuspDegree(7);
    
    
    System.out.println("start = " + degDesc);

    //圓的半徑
    int radius = width / 2;
    //星座圈
    double rignSign = 0.90;
    Stroke strokeBold = new BasicStroke(2.0f);
    Stroke strokeNormal = new BasicStroke(1.0f);
    for (int 黃道度數 = 0; 黃道度數 < 360 ; 黃道度數++)
    {
      int 第一象限度數 = (int) (黃道度數- degDesc);
      if (第一象限度數 >=360)
        第一象限度數 -= 360;
      if (第一象限度數 >=360)
        第一象限度數 += 360;
      
      if (黃道度數 % 30 == 0)
      {
        g.setStroke(strokeBold);
        g.setColor(fore);

        int x1 = (int) (center + radius * rignSign * Math.cos(Math.toRadians(第一象限度數)));
        int y1 = (int) (center - radius * rignSign * Math.sin(Math.toRadians(第一象限度數)));
        int x2 = (int) (center + radius * Math.cos(Math.toRadians(第一象限度數)));
        int y2 = (int) (center - radius * Math.sin(Math.toRadians(第一象限度數)));

        g.drawLine(x1, y1, x2, y2);
      }
    }
    
    g.setStroke(strokeNormal);

    //符水印
    drawSignature(g);
    
    g.setColor(fore);
    
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    
    Ellipse2D.Double 星座環 = new Ellipse2D.Double(center - center * rignSign, center - center * rignSign, width * rignSign, width * rignSign);
    g.draw(星座環);
    
    
    //360度數環
    double ring360 = 0.85;
    
    for (int 黃道度數 = 0; 黃道度數 < 360 ; 黃道度數++)
    {
      int 第一象限度數 = (int) (黃道度數- degDesc);
      if (第一象限度數 >=360)
        第一象限度數 -= 360;
      if (第一象限度數 >=360)
        第一象限度數 += 360;
      
      if (黃道度數 % 30 == 0)
      {
        g.setStroke(strokeBold);
        g.setColor(fore);
      }
      else if (黃道度數 %5 == 0)
      {
        g.setStroke(strokeNormal);
        g.setColor(fore);
      }
      else
      {
        g.setStroke(strokeNormal);
        g.setColor(Color.GRAY);
      }

      int x1 = (int) (center + radius * ring360 * Math.cos(Math.toRadians(第一象限度數)));
      int y1 = (int) (center - radius * ring360 * Math.sin(Math.toRadians(第一象限度數)));
      int x2 = (int) (center + radius * rignSign * Math.cos(Math.toRadians(第一象限度數)));
      int y2 = (int) (center - radius * rignSign * Math.sin(Math.toRadians(第一象限度數)));

      g.drawLine(x1, y1, x2, y2);
    }

    g.setStroke(strokeNormal);
    g.setColor(fore);
    Ellipse2D.Double 三六十度數環 = new Ellipse2D.Double(center - center * ring360, center - center * ring360, width * ring360, width * ring360);
    g.draw(三六十度數環);
    
    //地盤環
    double ringLand = 0.75;
    for(int house = 1 ; house <=12 ; house++)
    {
      double degHouse = horoscope.getCuspDegree(house);
      int 第一象限度數 = (int) (degHouse- degDesc);
      if (第一象限度數 >=360)
        第一象限度數 -= 360;
      if (第一象限度數 >=360)
        第一象限度數 += 360;
      
      int x1 = (int) (center + radius * ringLand * Math.cos(Math.toRadians(第一象限度數)));
      int y1 = (int) (center - radius * ringLand * Math.sin(Math.toRadians(第一象限度數)));
      int x2 = (int) (center + radius * ring360 * Math.cos(Math.toRadians(第一象限度數)));
      int y2 = (int) (center - radius * ring360 * Math.sin(Math.toRadians(第一象限度數)));

      g.setStroke(strokeNormal);
      if (house % 3 == 1)
        g.setColor(fore);
      else
        g.setColor(Color.GRAY);
      
      g.drawLine(x1, y1, x2, y2);
      
      //從中心點輻射出去
      x2 = x1;
      y2 = y1;
      x1 = center;
      y1 = center;
      
      g.drawLine(x1, y1, x2, y2);
    }
    
    g.setColor(fore);
    Ellipse2D.Double 地盤環 = new Ellipse2D.Double(center - center * ringLand, center - center * ringLand, width * ringLand, width * ringLand);
    g.draw(地盤環);
  }
  
  /**
   * 打符水印
   */
  private void drawSignature(Graphics2D g)
  {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    String funpText = "funP推推王";
    AttributedString as = new AttributedString(funpText);
    Font erasFont = new Font("Eras Demi ITC", Font.PLAIN , 90);
    
    as.addAttribute(TextAttribute.FONT, erasFont, 0, 3);
    as.addAttribute(TextAttribute.FOREGROUND, Color.BLUE , 0 , 3);
    as.addAttribute(TextAttribute.FONT, erasFont, 3, 4);
    as.addAttribute(TextAttribute.FOREGROUND, Color.RED , 3 , 4);
    
    Font kaiFont = new Font("標楷體", Font.PLAIN , 90); 
    as.addAttribute(TextAttribute.FONT, kaiFont, 4, 7); 
    g.drawString(as.getIterator() , 300 , 600 );
  }

}
