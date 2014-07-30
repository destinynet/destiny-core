/**
 * @author smallufo
 * Created on 2010/2/4 at 下午6:26:56
 */
package destiny.utils.image;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class ProcessorIconWatermark implements Processor , Serializable
{
  /** 不透明度 , 1>= op >=0 ，數值越高就越清楚  */
  float opacity = 0.5f;
  
  public ProcessorIconWatermark()
  {
  }
  
  public ProcessorIconWatermark(float op)
  {
    this.opacity = op;
  }
  
  @Override
  public void process(@NotNull BufferedImage img)
  {
    int imgW = img.getWidth();
    int imgH = img.getHeight();

    InputStream logoIs= getClass().getResourceAsStream("logo1.png");
    BufferedImage logoImage;
    try
    {
      logoImage = ImageIO.read(logoIs);
      int logoW = logoImage.getWidth()+5;
      int logoH = logoImage.getHeight()+5;

      if (logoW < imgW  && logoH < imgH)
      {
        //logo 比較小，可以貼到原圖上
        int newX = imgW - logoW;
        int newY = imgH - logoH;
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.drawImage(logoImage , null , newX , newY );
        g.dispose();
      }
    }
    catch (IOException ignored)
    {
    }
    finally
    {
      try
      {
        logoIs.close();
      }
      catch (IOException ignored)
      {
      }
    }
  }

}
