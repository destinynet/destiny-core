/**
 * @author smallufo
 * Created on 2013/5/9 at 下午7:20:13
 */
package destiny.iching.graph;

import destiny.font.FontRepository;
import destiny.iching.HexagramIF;
import destiny.iching.Symbol;
import destiny.utils.image.Processor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 將 HexagramChart 的旁邊（左或右）寫上 Symbol 的字（天 澤 火 雷...）
 */
public class ProcessorSideSymbol implements Processor , Serializable
{
  private HexagramIF hexagram;
  
  // 八卦的符號文字（天 澤 火 雷...），要在左邊還是右邊
  public enum Side {LEFT , RIGHT};
  
  private Side side;
  
  private BaseHexagramChart chart;
  
  public ProcessorSideSymbol(HexagramIF hexagram , Side side , BaseHexagramChart chart)
  {
    this.hexagram = hexagram;
    this.side = side;
    this.chart = chart;
  }

  @Override
  public void process(BufferedImage img)
  {
    int width  = img.getWidth();
    double rowHigh = chart.getRowHeight();
    
    double fontX = 0;
    switch (side)
    {
      case LEFT  : fontX = chart.paddingLeft/2 - rowHigh/2; break;
      case RIGHT : fontX = width - chart.paddingLeft/2 - rowHigh/2 ; break; 
    }
    Graphics2D g = img.createGraphics();
    
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setColor(chart.fore);
    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, (int) rowHigh));
    
    // 上卦
    g.drawString( getString(hexagram.getUpperSymbol()) , (int) fontX , (int) (chart.paddingTop + rowHigh*3) );
    
    // 下卦
    g.drawString( getString(hexagram.getLowerSymbol()) , (int) fontX , (int) (chart.paddingTop + rowHigh*9) );
    g.dispose();
  }

  private String getString(Symbol symbol)
  {
    switch (symbol)
    {
      case 乾 : return "天";
      case 兌 : return "澤";
      case 離 : return "火";
      case 震 : return "雷";
      case 巽 : return "風";
      case 坎 : return "水";
      case 艮 : return "山";
      case 坤 : return "地";
    }
    return "";
  }
  
}
