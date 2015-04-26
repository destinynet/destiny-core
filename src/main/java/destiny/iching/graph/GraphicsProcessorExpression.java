/**
 * Created by smallufo on 2015-04-24.
 */
package destiny.iching.graph;

import destiny.font.FontRepository;
import destiny.iching.contentProviders.ExpressionIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Locale;

/**
 * 畫出爻辭 (Line Expression)
 */
public class GraphicsProcessorExpression implements GraphicsProcessor {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final ExpressionIF expImpl;

  public GraphicsProcessorExpression(ExpressionIF expImpl) {this.expImpl = expImpl;}


  @Override
  public void process(PairGraphics pairGraphics) {

    Graphics2D g = pairGraphics.fullG;

    int fontSize = (int) (pairGraphics.getRowHeight()*0.7);
    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, fontSize));


    float srcTextX = (float) pairGraphics.paddingL;
    float dstTextX = (float) (pairGraphics.width - pairGraphics.singleW + pairGraphics.singlePaddingX);

    double rowHeight = pairGraphics.getRowHeight();
    FontMetrics fontMetrics = g.getFontMetrics(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));

    for(int i=6 ; i>=1 ; i--)
    {
      float textY = (float) (pairGraphics.paddingT + rowHeight*(13.7-2*i));

      if (pairGraphics.src.getLine(i) != pairGraphics.dst.getLine(i))
      {
        // 有變爻 , 文字顏色較深
        g.setColor(Color.decode("#666666"));
      }
      else
      {
        // 沒有變爻，文字顏色較淺
        g.setColor(Color.decode("#aaaaaa"));
      }

      String srcExp = expImpl.getLineExpression(pairGraphics.src, i, Locale.TAIWAN);

      // 計算是否超過右邊邊界
      int expWidth = fontMetrics.stringWidth(srcExp);
      double rightMargin = pairGraphics.width - pairGraphics.singleW + pairGraphics.singlePaddingX*0.8;
      if (srcTextX + expWidth > rightMargin)
      {
        while (srcTextX + expWidth > rightMargin)
        {
          logger.debug("右邊 [{}] 超過 {} : {} " , srcTextX + expWidth, rightMargin, srcExp);
          srcExp = srcExp.substring(0,srcExp.length()-1);
          expWidth = fontMetrics.stringWidth(srcExp);
        }
        srcExp = srcExp + "…";
      }
      logger.debug("[{}] px :  第 {} 爻 : {}" , expWidth, i, srcExp);

      // 印出本卦爻辭
      g.drawString(srcExp , srcTextX , textY);

      // 印出變卦爻辭
      String dstExp = expImpl.getLineExpression(pairGraphics.dst, i, Locale.TAIWAN);
      g.drawString(dstExp , dstTextX , textY);
    } // line 6 to 1

  }
}
