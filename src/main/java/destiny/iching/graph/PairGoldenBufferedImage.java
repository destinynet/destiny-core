/**
 * @author smallufo
 * Created on 2013/5/11 at 下午11:52:29
 */
package destiny.iching.graph;

import destiny.iching.HexagramIF;
import destiny.iching.graph.ProcessorSideSymbol.Side;
import destiny.utils.image.Processor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static destiny.core.chart.Constants.GOLDEN_RATIO;
import static destiny.core.chart.Constants.WIDTH_HEIGHT;

/** 黃金比例的矩形雙卦象 */
public class PairGoldenBufferedImage extends AbstractPairBufferImage
{
  public PairGoldenBufferedImage(@NotNull HexagramIF src, String srcName, @NotNull HexagramIF dst, String dstName, WIDTH_HEIGHT which, int value, Color bg, Color fore)
  {
    super(src, srcName, dst, dstName, Type.GOLDEN, which, value , bg , fore);
  }

  @Override
  protected double getOxX()
  {
    // 單一卦象的寬度 減去 paddingX 的一半
    return getSingleHexagramWidth() - getPaddingX()/2.0;
  }

  @Override
  protected double getArrowX(double radius)
  {
    double paddingX = getPaddingX();
    return w - h / GOLDEN_RATIO + paddingX - radius*2;
  }
  
  /** 繪製側邊文字 , 可能是八卦類像文字（天澤火雷...） */
  @Override
  protected void drawSide()
  {
    Processor pSymbol;
    pSymbol = new ProcessorSideSymbol(src, Side.LEFT, getSrcChart());
    pSymbol.process(getSrcChart());
    pSymbol = new ProcessorSideSymbol(dst, Side.RIGHT, getSrcChart());
    pSymbol.process(getDstChart());
  }
  
  /** 單一卦象的寬度 */
  private double getSingleHexagramWidth()
  {
    return h / GOLDEN_RATIO;
  }
  
  /** 單一卦象的 paddingX */
  private double getPaddingX()
  {
    return getSrcChart().paddingX;
  }

}
