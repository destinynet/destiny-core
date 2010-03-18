/**
 * @author smallufo
 * @date 2005/4/4
 * @time 下午 03:05:36
 */
package destiny.utils.ColorCanvas;

import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;


public class ColorByteTest extends TestCase
{
  public void testColorByte() throws MalformedURLException
  {
    ColorByte cb;

    //不變
    cb = new ColorByte((byte)1 , "#FFFFFF" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null);
    assertEquals(cb.getForeColor() , "#FFFFFF");
    
    //不變
    cb = new ColorByte((byte)1 , "#FFF" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertEquals(cb.getForeColor() , "#FFF");
    
    //小寫轉大寫
    cb = new ColorByte((byte)1 , "#ffffff" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertEquals(cb.getForeColor() , "#FFFFFF");
    
    //小寫轉成大寫，並在前加上 '#'
    cb = new ColorByte((byte)1 , "ffffff" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertEquals(cb.getForeColor() , "#FFFFFF");
    
    //小寫轉成大寫
    cb = new ColorByte((byte)1 , "#fff" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertEquals(cb.getForeColor() , "#FFF");
    
    //小寫轉成大寫，並在前加上 '#'
    cb = new ColorByte((byte)1 , "fff" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertEquals(cb.getForeColor() , "#FFF");

    //錯誤的色碼
    cb = new ColorByte((byte)1 , "##ffffff" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertEquals(cb.getForeColor() , "##ffffff");
    
    //錯誤的色碼
    cb = new ColorByte((byte)1 , "##fffff" , "red" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertEquals(cb.getForeColor() , "##fffff");

  }

  public void testIsSameProperties() throws MalformedURLException
  {
    ColorByte cb1 , cb2;
    //應該相同
    cb1 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(cb1.isSameProperties(cb2));
    
    //應該相同
    cb1 = new ColorByte((byte)1 , "FFFFFF" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "ffffff" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(cb1.isSameProperties(cb2));
    
    //應該相同
    cb1 = new ColorByte((byte)1 , "abc" , "DEF" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "ABC" , "def" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(cb1.isSameProperties(cb2));
    
    //前景不同
    cb1 = new ColorByte((byte)1 , "red"  ,"blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "green","blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //背景不同
    cb1 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "red" , "black", new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //字體family不同
    cb1 = new ColorByte((byte)1 , "red" , "blue" , new Font("標楷體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //字體型態不同
    cb1 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.ITALIC, 16) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //字體大小不同
    cb1 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 14) , new URL("http://xfiles.to") , null );
    cb2 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to") , null );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //網址不同 , 但是網址不納入比對 (耗時)，因此會視為相同
    cb1 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to" ) , null );
    cb2 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to/") , null );
    assertTrue(!cb1.isSameProperties(cb2));
    
    //title 不同
    cb1 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to/") , "AAA" );
    cb2 = new ColorByte((byte)1 , "red" , "blue" , new Font("細明體" , Font.PLAIN , 16) , new URL("http://xfiles.to/") , "BBB" );
    assertTrue(!cb1.isSameProperties(cb2));
  }

}
