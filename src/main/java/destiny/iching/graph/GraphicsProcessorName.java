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
  public void process(PairGraphBuilder builder) {
    // 下方卦名，例如「乾」「坤」「屯」..

    int bottomFontSize = (int) (builder.singlePaddingY / GOLDEN_RATIO);

    int width = builder.singleW;
    int height = builder.singleH;
    double paddingY = builder.paddingB;

    Graphics g = null;
    switch (side) {
      case L :
        g = builder.srcGraph;

        break;
      case R :
        g = builder.dstGraph;
        break;
    }

    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, bottomFontSize));
    g.drawString(name
        , width/2 - name.length()*bottomFontSize/2
        , (int) (height - paddingY/2+bottomFontSize/2));
  }
}
