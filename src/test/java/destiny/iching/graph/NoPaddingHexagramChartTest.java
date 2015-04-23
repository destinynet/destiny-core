/**
 * @author smallufo
 * Created on 2013/5/9 at 下午11:51:07
 */
package destiny.iching.graph;

import destiny.iching.Hexagram;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class NoPaddingHexagramChartTest
{
  @Test
  public void testChart() throws Exception
  {
    long t0 = System.currentTimeMillis();
    
    Hexagram h = Hexagram.未濟;
    
//    NoPaddingHexagramChart c = new NoPaddingHexagramChart(h, WIDTH_HEIGHT.HEIGHT, 100
//        , Color.decode("#FF9999") , Color.BLACK);
    NoPaddingBufferedImage c = new NoPaddingBufferedImage(h, 100 , 272 , Color.WHITE , Color.BLACK);
    System.out.println("width = " + c.getWidth() + " , height = " + c.getHeight());
    
    File pngFile = new File("/Users/smallufo/temp/chart.png");
    ImageIO.write (c, "png", pngFile);
    long t1 = System.currentTimeMillis();
    
    System.out.println("takes " + (t1-t0) + " millis");
  }
}
