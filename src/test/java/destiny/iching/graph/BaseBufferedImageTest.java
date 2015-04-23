/**
 * @author smallufo
 * Created on 2013/5/12 at 上午10:22:25
 */
package destiny.iching.graph;

import destiny.iching.Hexagram;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BaseBufferedImageTest {

  @Test
  public void testChart() throws IOException {
    long t0 = System.currentTimeMillis();
    Hexagram h = Hexagram.未濟;

    // 原始圖案，沒有任何文字
    BaseBufferedImage image = new BaseBufferedImage(h, 370, 600, Color.WHITE, Color.BLACK, 20, 10, 20, 10);

    File pngFile = new File("/Users/smallufo/temp/BaseHexagramBufferedImage.png");
    ImageIO.write(image, "png", pngFile);
    long t1 = System.currentTimeMillis();

    System.out.println("takes " + (t1 - t0) + " millis");

  }
}
