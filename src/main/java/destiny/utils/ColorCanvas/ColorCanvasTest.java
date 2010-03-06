package destiny.utils.ColorCanvas;

import junit.framework.TestCase;

public class ColorCanvasTest extends TestCase
{
  public void testColorCanvas()
  {
    ColorCanvas cc = new ColorCanvas(1 , 10 , "　");
    cc.setText("一二三四五", 1, 1);
    //System.out.println(cc.toString());
    System.out.println(cc.getHtmlOutput());
  }

}
