/**
 * @author smallufo 
 * Created on 2008/12/10 at 上午 12:35:33
 */ 
package destiny.utils.image;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class BufferedImageTools implements Serializable
{
  /**
   * 直接設定長寬為多少
   */
  @NotNull
  public static BufferedImage getImage(@NotNull BufferedImage bufferedImage , int newWidth, int newHeight)
  {
    /** png 的 type 為 0 , 將無法 resize , 必須將輸出 type 設成 TYPE_INT_RGB */
    int type = (bufferedImage.getType() == 0) ? BufferedImage.TYPE_INT_RGB : bufferedImage.getType();
    BufferedImage resizedBufferedImage = new BufferedImage(newWidth, newHeight, type);
    resizedBufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
    return resizedBufferedImage;
  }
  
  /**
   * 按照比例縮放原圖
   */
  @Nullable
  public static BufferedImage getScaledImage(@NotNull BufferedImage bufferedImage , double scale)
  {
    /** png 的 type 為 0 , 將無法 resize , 必須將輸出 type 設成 TYPE_INT_RGB */
    int type = (bufferedImage.getType() == 0) ? BufferedImage.TYPE_INT_RGB : bufferedImage.getType();
    BufferedImage resizedBufferedImage = null;
    
    int newWidth = (int) (bufferedImage.getWidth() * scale);
    int newHeight = (int) (bufferedImage.getHeight() * scale);
    
    if (type == BufferedImage.TYPE_CUSTOM)
    { // handmade
      ColorModel cm = bufferedImage.getColorModel();
      WritableRaster raster = cm.createCompatibleWritableRaster(newWidth, newHeight);
      boolean alphaPremultiplied = cm.isAlphaPremultiplied();
      resizedBufferedImage = new BufferedImage(cm, raster, alphaPremultiplied, null);
    }
    else
      resizedBufferedImage = new BufferedImage(newWidth, newHeight, type);
    
    Graphics2D g = resizedBufferedImage.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawRenderedImage(bufferedImage, AffineTransform.getScaleInstance(scale, scale));
    g.dispose();
    
    return resizedBufferedImage;
  }
  
  /**
   * 設定最大長度或寬度 , 取得等比例縮放的圖片 
   */
  @NotNull
  public static BufferedImage getFittingImage(@NotNull BufferedImage bufferedImage , int maxLength)
  {
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    
    int newWidth , newHeight;
    double scale;
    if (width > height)
    {
      newWidth = maxLength;
      scale = maxLength / (double) width;
      newHeight = (int) (height * scale);
    }
    else
    {
      newHeight = maxLength;
      scale = maxLength / (double) height;
      newWidth = (int) (width * scale) ;
    }
    return getImage(bufferedImage, newWidth, newHeight);
  }
  
  /**
   * 取得正方形的圖片
   */
  @NotNull
  public static BufferedImage getSquareImage(@NotNull BufferedImage bufferedImage , int length)
  {
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    
    BufferedImage subImage;
    if (width > height)
    { //比較寬
      int widthStart = width/2 - height/2;
      subImage = bufferedImage.getSubimage(widthStart, 0, height, height);
    }
    else
    {
      //比較高
      int heightStart = height/2 - width/2;
      subImage = bufferedImage.getSubimage(0, heightStart, width, width);
    }
    return getImage(subImage , length , length);
  }
  
  /**
   * 將 BufferedImage 轉成 byte array
   * @throws IOException 
   */
  public static byte[] getArray(@NotNull BufferedImage bufferedImage , String type)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageOutputStream ios = null;
    try {
      ios = ImageIO.createImageOutputStream(baos);
      ImageIO.write(bufferedImage, type , ios);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      bufferedImage.getGraphics().dispose();
      if (ios != null) {
        try {
          ios.flush();
          ios.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return baos.toByteArray();
  }
}
