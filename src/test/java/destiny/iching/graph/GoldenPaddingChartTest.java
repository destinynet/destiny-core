/**
 * @author smallufo
 * Created on 2013/5/12 at 上午10:55:31
 */
package destiny.iching.graph;

import destiny.core.chart.Constants;
import destiny.iching.Hexagram;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GoldenPaddingChartTest
{
  @Test
  public void testChart() throws IOException
  {
    long t0 = System.currentTimeMillis();
    
    Hexagram hex = Hexagram.未濟;

    int width = 548;

    GoldenPaddingBufferedImage c = new GoldenPaddingBufferedImage(hex, Constants.WIDTH_HEIGHT.HEIGHT, 1000, Color.WHITE, Color.BLACK);
    
    System.out.println("w = " + c.getWidth() + " , h = " + c.getHeight());
    
    File pngFile = new File("/Users/smallufo/temp/chart.png");
    ImageIO.write (c, "png", pngFile);
    long t1 = System.currentTimeMillis();
    
    System.out.println("takes " + (t1-t0) + " millis");
  }
}
