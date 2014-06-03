/**
 * @author smallufo
 * Created on 2013/5/12 at 上午10:22:25
 */
package destiny.iching.graph;

import destiny.iching.Hexagram;
import junit.framework.TestCase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestBaseHexagramChart extends TestCase
{
  public void testChart() throws IOException
  {
    long t0 = System.currentTimeMillis();
    Hexagram h;
    h = Hexagram.未濟;

    // 原始圖案，沒有任何文字
    BaseHexagramChart c = new BaseHexagramChart(h, 370, 600, Color.WHITE, Color.BLACK
        , 0 ,0 ,0,0);
    System.out.println("width = " + c.getWidth() + " , height = " + c.getHeight());
    
    File pngFile = new File("/Users/smallufo/temp/chart.png");
    ImageIO.write (c, "png", pngFile);
    long t1 = System.currentTimeMillis();
    
    System.out.println("takes " + (t1-t0) + " millis");
    
  }
}
