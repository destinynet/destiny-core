/**
 * @author smallufo
 * Created on 2013/5/12 at 上午10:55:31
 */
package destiny.iching.graph;

import destiny.iching.Hexagram;
import destiny.iching.graph.BaseHexagramChart.WIDTH_HEIGHT;
import junit.framework.TestCase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestGoldenPaddingChart extends TestCase
{
  public void testChart() throws IOException
  {
    long t0 = System.currentTimeMillis();
    
    Hexagram hex = Hexagram.未濟;
    GoldenPaddingChart c = new GoldenPaddingChart(hex, WIDTH_HEIGHT.HEIGHT, 1000, Color.WHITE, Color.BLACK);
    
    System.out.println("width = " + c.getWidth() + " , height = " + c.getHeight());
    
    File pngFile = new File("/Users/smallufo/temp/chart.png");
    ImageIO.write (c, "png", pngFile);
    long t1 = System.currentTimeMillis();
    
    System.out.println("takes " + (t1-t0) + " millis");
  }
}
