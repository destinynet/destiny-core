/**
 * Created by smallufo on 2015-04-24.
 */
package destiny.iching.graph;

import destiny.font.FontRepository;
import destiny.iching.HexagramIF;
import destiny.iching.Symbol;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class GraphicsProcessorSideSymbol implements GraphicsProcessor {

  private final HexagramIF hexagram;

  // 八卦的符號文字（天 澤 火 雷...），要在左邊還是右邊
  private final Side side;

  public GraphicsProcessorSideSymbol(Side side, HexagramIF hexagram) {
    this.hexagram = hexagram;
    this.side = side;

  }

  @Override
  public void process(PairGraphBuilder builder) {
    double fontX = 0;

    double rowHigh = builder.getRowHigh();

    Graphics g = null;

    switch (side)
    {
      case L:
        g = builder.srcGraph;
        fontX = builder.singlePaddingX / 2 - rowHigh / 2;
        break;
      case R:
        g = builder.dstGraph;
        fontX = builder.singleW - builder.singlePaddingX / 2 - rowHigh / 2;
        break;
    }

    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, (int) rowHigh));

    // 上卦
    g.drawString( getString(hexagram.getUpperSymbol()) , (int) fontX , (int) (builder.singlePaddingY + rowHigh*3) );

    // 下卦
    g.drawString(getString(hexagram.getLowerSymbol()), (int) fontX, (int) (builder.singlePaddingY + rowHigh * 9));
  }


  @NotNull
  private String getString(@NotNull Symbol symbol)
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
