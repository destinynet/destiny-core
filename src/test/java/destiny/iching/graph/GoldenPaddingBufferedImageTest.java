/**
 * Created by smallufo on 2015-04-23.
 */
package destiny.iching.graph;

import destiny.core.chart.Constants;
import destiny.iching.Hexagram;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class GoldenPaddingBufferedImageTest {

  @Test
  public void testChart() throws Exception{
    long t0 = System.currentTimeMillis();
    Hexagram hex = Hexagram.未濟;

    GoldenPaddingBufferedImage image = new GoldenPaddingBufferedImage(hex , Constants.WIDTH_HEIGHT.WIDTH , 370 , Color.GRAY , Color.BLACK);

    File pngFile = new File("/Users/smallufo/temp/GoldenPaddingBufferedImage.png");
    ImageIO.write(image, "png", pngFile);
    long t1 = System.currentTimeMillis();

    System.out.println("takes " + (t1 - t0) + " millis");
  }

}