/**
 * @author smallufo 
 * Created on 2008/12/10 at 上午 12:34:27
 */ 
package destiny.astrology.chart.gifLibs;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import destiny.astrology.chart.BufferedImageTools;

public class RunGif
{
  public static void main(String[] args) throws IOException
  {
    //File file = new File("c:/temp/gif-90x80.gif");
    File file = new File("c:/temp/google.gif");
    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);

    FileOutputStream fos = new FileOutputStream(new File("c:/temp/out.gif"));

    GifDecoder gifDecoder = new GifDecoder();
    AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();

    gifDecoder.read(bis);

    int frameCount = gifDecoder.getFrameCount();
    int loopCount = gifDecoder.getLoopCount();
    animatedGifEncoder.setRepeat(loopCount);
    animatedGifEncoder.start(fos);

    for (int frameNumber = 0; frameNumber < frameCount; frameNumber++)
    {
      BufferedImage frame = gifDecoder.getFrame(frameNumber); // frame i
      int delay = gifDecoder.getDelay(frameNumber); // display duration of frame in milliseconds
      animatedGifEncoder.setDelay(delay); // frame delay per sec
      animatedGifEncoder.addFrame(BufferedImageTools.getScaledImage(frame, 0.5));
    }

    animatedGifEncoder.finish();

    fos.flush();
    fos.close();
  }
}
