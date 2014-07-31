/**
 * @author smallufo 
 * Created on 2008/12/10 at 上午 1:08:46
 */
package destiny.astrology.chart;

import org.jetbrains.annotations.NotNull;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;

/**
 * A class that simplifies a few common image operations, in particular creating
 * a BufferedImage from an image file, and using MediaTracker to wait until an
 * image or several images are done loading. 1998 Marty Hall,
 * http://www.apl.jhu.edu/~hall/java/
 */

public class ImageUtilities
{

  /**
   * Create Image from a file, then turn that into a BufferedImage.
   */

  @NotNull
  public static BufferedImage getBufferedImage(String imageFile, @NotNull Component c)
  {
    Image image = c.getToolkit().getImage(imageFile);
    waitForImage(image, c);
    BufferedImage bufferedImage = new BufferedImage(image.getWidth(c), image.getHeight(c), BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.drawImage(image, 0, 0, c);
    g2d.dispose();
    return (bufferedImage);
  }

  /**
   * Take an Image associated with a file, and wait until it is done loading.
   * Just a simple application of MediaTracker. If you are loading multiple
   * images, don't use this consecutive times; instead use the version that
   * takes an array of images.
   */

  public static boolean waitForImage(Image image, Component c)
  {
    MediaTracker tracker = new MediaTracker(c);
    tracker.addImage(image, 0);
    try
    {
      tracker.waitForAll();
    }
    catch (InterruptedException ignored)
    {
    }
    return (!tracker.isErrorAny());
  }

  /**
   * Take some Images associated with files, and wait until they are done
   * loading. Just a simple application of MediaTracker.
   */

  public static boolean waitForImages(@NotNull Image[] images, Component c)
  {
    MediaTracker tracker = new MediaTracker(c);
    for (Image image : images) {
      tracker.addImage(image, 0);
    }
    try
    {
      tracker.waitForAll();
    }
    catch (InterruptedException ignored)
    {
    }
    return (!tracker.isErrorAny());
  }
}