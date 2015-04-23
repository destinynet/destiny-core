/**
 * Created by smallufo on 2015-04-23.
 */
package destiny.iching.graph;

import destiny.iching.Hexagram;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class NoPaddingHexagramBufferedImageTest {

  @Test
  public void testChart() throws IOException {
    long t0 = System.currentTimeMillis();
    Hexagram h = Hexagram.未濟;

    NoPaddingBufferedImage image = new NoPaddingBufferedImage(h , 370 , 600 , Color.WHITE ,  Color.BLACK);
    File pngFile = new File("/Users/smallufo/temp/NoPaddingHexagramBufferedImage.png");
    ImageIO.write(image, "png", pngFile);

    long t1 = System.currentTimeMillis();
    System.out.println("takes " + (t1 - t0) + " millis");
  }

}