/**
 * Created by smallufo on 2015-04-25.
 */
package destiny.iching.graph;

import destiny.font.FontRepository;

import java.awt.*;

/**
 * 底端印出浮水印
 */
public class GraphicsProcessorLogo implements GraphicsProcessor {

  @Override
  public void process(PairGraphics pairGraphics) {
    Graphics2D g = pairGraphics.fullG;

    g.setColor(Color.decode("#aaaaaa"));
    String watermark = "DestinyNet 命理網";

    int fontSize = (int) (pairGraphics.getRowHeight() * 0.7);
    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, fontSize));

    FontMetrics fontMetrics = g.getFontMetrics(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));

    int watermarkWidth = fontMetrics.stringWidth(watermark);
    float watermarkX = pairGraphics.width / 2 - watermarkWidth / 2;

    g.drawString(watermark, watermarkX, pairGraphics.height - fontSize);
  }
}
