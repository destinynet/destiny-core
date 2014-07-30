/**
 * @author smallufo
 * Created on 2013/5/9 at 下午7:39:58
 */
package destiny.iching.graph;

import destiny.font.FontRepository;
import destiny.utils.image.Processor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import static destiny.core.chart.Constants.GOLDEN_RATIO;

/** 在卦的下方寫上短卦名 */
public class ProcessorName implements Processor , Serializable
{
  private String name;
  private BaseHexagramChart chart;
  
  public ProcessorName(String name , BaseHexagramChart chart)
  {
    this.name = name;
    this.chart = chart;
  }

  @Override
  public void process(@NotNull BufferedImage img)
  {
    int width = img.getWidth();
    int height = img.getHeight();
    
    double paddingY = chart.paddingBottom; 
    
    Graphics2D g = img.createGraphics();
    
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setColor(chart.fore);
    
    // 下方卦名，例如「乾」「坤」「屯」..
    int bottomFontSize = (int) (paddingY / GOLDEN_RATIO);
    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, bottomFontSize));
    g.drawString(name 
        , width/2 - name.length()*bottomFontSize/2
        , (int) (height - paddingY/2+bottomFontSize/2) );
    g.dispose();
  }

}
