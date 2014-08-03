/**
 * @author smallufo
 * @date 2005/4/4
 * @time 下午 03:05:36
 */
package destiny.utils.ColorCanvas;

import org.junit.Test;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColorByteTest
{
  @Test
  public void testColorByte() throws MalformedURLException
  {
    ColorByte cb;

    //不變
    cb = new ColorByte((byte)1 , Optional.of("#FFFFFF") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty());
    assertEquals(cb.getForeColor() , Optional.of("#FFFFFF"));
    
    //不變
    cb = new ColorByte((byte)1 , Optional.of("#FFF") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertEquals(cb.getForeColor() , Optional.of("#FFF"));
    
    //小寫轉大寫
    cb = new ColorByte((byte)1 , Optional.of("#FFFFFF") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertEquals(cb.getForeColor() , Optional.of("#FFFFFF"));
    
    //小寫轉成大寫，並在前加上 '#'
    cb = new ColorByte((byte)1 , Optional.of("ffffff") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertEquals(cb.getForeColor() , Optional.of("#FFFFFF"));
    
    //小寫轉成大寫
    cb = new ColorByte((byte)1 , Optional.of("#FFF") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertEquals(cb.getForeColor() , Optional.of("#FFF"));
    
    //小寫轉成大寫，並在前加上 '#'
    cb = new ColorByte((byte)1 , Optional.of("fff") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertEquals(cb.getForeColor() , Optional.of("#FFF"));

    //錯誤的色碼
    cb = new ColorByte((byte)1 , Optional.of("##ffffff") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertEquals(cb.getForeColor() , Optional.of("##ffffff"));
    
    //錯誤的色碼
    cb = new ColorByte((byte)1 , Optional.of("##fffff") , Optional.of("red") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertEquals(cb.getForeColor() , Optional.of("##fffff"));

  }

  @Test
  public void testIsSameProperties() throws MalformedURLException
  {
    ColorByte cb1 , cb2;
    //應該相同
    cb1 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(cb1.isSameProperties(cb2));
    
    //應該相同
    cb1 = new ColorByte((byte)1 , Optional.of("FFFFFF") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("ffffff") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(cb1.isSameProperties(cb2));
    
    //應該相同
    cb1 = new ColorByte((byte)1 , Optional.of("abc") , Optional.of("DEF") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("ABC") , Optional.of("def") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(cb1.isSameProperties(cb2));
    
    //前景不同
    cb1 = new ColorByte((byte)1 , Optional.of("red")  ,Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("green"),Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //背景不同
    cb1 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("black"), Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //字體family不同
    cb1 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("標楷體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //字體型態不同
    cb1 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.ITALIC, 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //字體大小不同
    cb1 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 14)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to")) , Optional.empty() );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //網址不同 , 但是網址不納入比對 (耗時)，因此會視為相同
    cb1 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to" )) , Optional.empty() );
    cb2 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to/")) , Optional.empty() );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //title 不同
    cb1 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to/")) , Optional.of("AAA") );
    cb2 = new ColorByte((byte)1 , Optional.of("red") , Optional.of("blue") , Optional.of(new Font("細明體" , Font.PLAIN , 16)) , Optional.of(new URL("http://xfiles.to/")) , Optional.of("BBB") );
    assertTrue(!cb1.isSameProperties(cb2));
  }

}
