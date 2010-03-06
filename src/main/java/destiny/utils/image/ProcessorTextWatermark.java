/**
 * @author smallufo
 * Created on 2010/2/4 at 下午6:21:41
 */
package destiny.utils.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.text.AttributedString;

public class ProcessorTextWatermark implements Processor , Serializable
{

  @Override
  public void process(BufferedImage img)
  {
    Graphics2D g = img.createGraphics();
    
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    String funpText = "funP推推王";
    AttributedString as = new AttributedString(funpText);
    Font erasFont = new Font("Eras Demi ITC", Font.PLAIN , 90);
    
    as.addAttribute(TextAttribute.FONT, erasFont, 0, 3);
    as.addAttribute(TextAttribute.FOREGROUND, Color.BLUE , 0 , 3);
    as.addAttribute(TextAttribute.FONT, erasFont, 3, 4);
    as.addAttribute(TextAttribute.FOREGROUND, Color.RED , 3 , 4);
    
    Font kaiFont = new Font("標楷體", Font.PLAIN , 90); 
    as.addAttribute(TextAttribute.FONT, kaiFont, 4, 7); 
    g.drawString(as.getIterator() , 300 , 600 );
    
  }

}
