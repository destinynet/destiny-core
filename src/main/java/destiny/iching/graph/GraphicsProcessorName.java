/**
 * Created by smallufo on 2015-04-24.
 */
package destiny.iching.graph;

import destiny.font.FontRepository;

import java.awt.*;
import java.io.Serializable;

import static destiny.core.chart.Constants.GOLDEN_RATIO;

/** 在卦的下方寫上短卦名 */
public class GraphicsProcessorName implements GraphicsProcessor , Serializable {

  private final Side side;
  private final String name;

  public GraphicsProcessorName(Side side, String name) {
    this.side = side;
    this.name = name;
  }

  @Override
  public void process(PairGraphics pairGraphics) {
    // 下方卦名，例如「乾」「坤」「屯」..

    int bottomFontSize = (int) (pairGraphics.singlePaddingY / GOLDEN_RATIO);

    int width = pairGraphics.singleW;
    int height = pairGraphics.singleH;
    double paddingY = pairGraphics.paddingB;

    Graphics g = null;
    switch (side) {
      case L :
        g = pairGraphics.srcGraph;

        break;
      case R :
        g = pairGraphics.dstGraph;
        break;
    }

    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, bottomFontSize));
    g.drawString(name
        , width/2 - name.length()*bottomFontSize/2
        , (int) (height - paddingY/2+bottomFontSize/2));
  }
}
