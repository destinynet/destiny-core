/**
 * @author smallufo
 * Created on 2013/5/12 at 下午2:18:54
 */
package destiny.iching.graph;

import destiny.core.chart.Constants.WIDTH_HEIGHT;
import destiny.font.FontRepository;
import destiny.iching.HexagramIF;
import destiny.iching.contentProviders.ExpressionIF;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Locale;

public class PairGoldenChartWithText extends PairGoldenChart
{
  private Logger logger = LoggerFactory.getLogger(getClass());
  
  public PairGoldenChartWithText()
  {
    super(null, null, null, null, WIDTH_HEIGHT.WIDTH , TYPE_INT_ARGB, null, null);
  }
  
  /**
   * [經文] 黃金比例的雙卦象 , 顯示爻辭，動爻加深
   */
  public PairGoldenChartWithText(
      @NotNull ExpressionIF expImpl ,
      @NotNull HexagramIF src, String srcName,
      @NotNull HexagramIF dst, String dstName,
      WIDTH_HEIGHT which, int value, Color bg, Color fore)
  {
    super(src, srcName, dst, dstName, which, value, bg, fore);
    
    Graphics2D g = this.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    GoldenPaddingChart srcChart = getSrcChart();
    GoldenPaddingChart dstChart = getDstChart();
    
    int fontSize = (int) (srcChart.getRowHeight()*0.7);
    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, fontSize));
    
    float srcTextX = (float) srcChart.paddingLeft;
    float dstTextX = (float) (width - dstChart.width + dstChart.paddingLeft);
    
    double rowHeight = (float) srcChart.getRowHeight();
    FontMetrics fontMetrics = g.getFontMetrics(new Font(FontRepository.getFontLiHei() , Font.PLAIN, fontSize));
    
    for(int i=6 ; i>=1 ; i--)
    {
      float textY = (float) (srcChart.paddingTop + rowHeight*(13.7-2*i));
      
      if (src.getLine(i) != dst.getLine(i))
      {
        // 有變爻 , 文字顏色較深
        g.setColor(Color.decode("#666666"));
      }
      else
      {
        // 沒有變爻，文字顏色較淺
        g.setColor(Color.decode("#aaaaaa"));
      }
      
      String srcExp = expImpl.getLineExpression(src, i, Locale.TAIWAN);
      
      // 計算是否超過右邊邊界
      int expWidth = fontMetrics.stringWidth(srcExp);
      double rightMargin = width - dstChart.width + dstChart.paddingLeft*0.8;
      if (srcTextX + expWidth > rightMargin)
      {
        while (srcTextX + expWidth > rightMargin)
        {
          logger.debug("右邊 [{}] 超過 {} : {} " , new Object[] {srcTextX + expWidth , rightMargin , srcExp} );
          srcExp = srcExp.substring(0,srcExp.length()-1);
          expWidth = fontMetrics.stringWidth(srcExp);
        }
        srcExp = srcExp + "…";
      }
      logger.debug("[{}] px :  第 {} 爻 : {}" , new Object[] {expWidth , i , srcExp } );

      // 印出本卦爻辭
      g.drawString(srcExp , srcTextX , textY);
      
      // 變卦爻辭
      String dstExp = expImpl.getLineExpression(dst, i, Locale.TAIWAN);
      g.drawString(dstExp , dstTextX , textY);
    } // line 6 to 1
    
    // 底端印出浮水印
    g.setColor(Color.decode("#aaaaaa"));
    String watermark = "destiny.pro 命理網";
    //FontMetrics fontMetrics = g.getFontMetrics(new Font(BaseHexagramChart.FONT_LIHEI , Font.PLAIN, fontSize));
    int watermarkWidth = fontMetrics.stringWidth(watermark);
    float watermarkX = width/2 - watermarkWidth/2;
    g.drawString(watermark , watermarkX , height-fontSize);
    g.dispose();
  } // constructor

}
