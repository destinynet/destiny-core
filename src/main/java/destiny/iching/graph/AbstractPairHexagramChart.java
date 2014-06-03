/**
 * @author smallufo
 * Created on 2013/5/11 at 下午10:55:06
 */
package destiny.iching.graph;

import destiny.font.FontRepository;
import destiny.iching.HexagramIF;
import destiny.iching.graph.BaseHexagramChart.WIDTH_HEIGHT;
import destiny.utils.image.Processor;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 橫向兩個卦
 */
public abstract class AbstractPairHexagramChart extends BufferedImage implements Serializable
{
  // 是否繪製輔助線條
  public boolean drawRulers = false;
  
  private GoldenPaddingChart srcChart;
  private GoldenPaddingChart dstChart;
  
  protected HexagramIF src;
  protected HexagramIF dst;
  
  protected int width;
  protected int height;
  
  public enum Type {MERGED , GOLDEN}
  
  protected Color bg;
  protected Color fore = Color.BLACK;
  
  /**
   * @param src     本卦
   * @param srcName
   * @param dst     變卦
   * @param dstName
   * @param type    MERGED : 左右兩卦直接拼接 , GOLDEN : 呈現寬體黃金比例矩形，左右各放本卦及變卦
   * @param which   傳入寬度還是高度
   * @param value   (寬或高) 其值為多少
   * @param bg
   */
  public AbstractPairHexagramChart(
      HexagramIF src , String srcName ,
      HexagramIF dst , String dstName , 
      Type type , WIDTH_HEIGHT which, int value , Color bg , Color fore)
  {
    super(
          (which == WIDTH_HEIGHT.WIDTH  ? value :
             (type == Type.GOLDEN) ? (int)(value * BaseHexagramChart.GOLDEN_RATIO) :
                                     (int)(value / BaseHexagramChart.GOLDEN_RATIO)*2 ) 
        , (which == WIDTH_HEIGHT.HEIGHT ? value :
             (type == Type.GOLDEN) ? (int)(value / BaseHexagramChart.GOLDEN_RATIO) :
                                     (int)(value / 2 * BaseHexagramChart.GOLDEN_RATIO))
        , BufferedImage.TYPE_INT_ARGB);
    
    this.src = src;
    this.dst = dst;
    this.width = getWidth();
    this.height = getHeight();
    this.bg = bg;
    this.fore = fore;
    
    this.srcChart = new GoldenPaddingChart(src , WIDTH_HEIGHT.HEIGHT  
        , height
        , bg , fore);
    
    this.dstChart = new GoldenPaddingChart(dst , WIDTH_HEIGHT.HEIGHT
        , height
        , bg , fore);
    
    
    
    // 加上下方卦名
    Processor pName;
    pName = new ProcessorName(srcName , srcChart);
    pName.process(srcChart);
    pName = new ProcessorName(dstName , dstChart);
    pName.process(dstChart);
    
    // 加上側邊八卦代表文字
    drawSide();

    
    Graphics2D g = this.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bg);
    g.fillRect(0, 0, width, height);
    
    // 左半邊為本卦
    g.drawImage(srcChart, 0 , 0 , null);
    
    // 右半邊為變卦
    g.drawImage(dstChart, width - dstChart.width , 0 , null);
    
    if (drawRulers)
    {
      // 繪製輔助線條
      g.setColor(Color.decode("#999999"));
      // 本卦右邊的直線
      g.draw(new Line2D.Double(srcChart.width , 0 , srcChart.width , getHeight()));
      // 變卦左邊的直線
      g.draw(new Line2D.Double(width - dstChart.width , 0 , width - dstChart.width , getHeight()));
    }
    
    double rowHigh = srcChart.getRowHeight();// HexagramChart.getRowHigh(height);
    
    //double paddingY = HexagramChart.getPaddingY(height);

    double oxX = getOxX();
    
    for(int i=6 ; i >= 1 ; i--)
    {
      if (src.getLine(i).getYinYang().getBooleanValue() != dst.getLine(i).getYinYang().getBooleanValue())
      {
        // 有變爻
        // 圈叉的 Y 座標
        //double oxY = paddingY + rowHigh*(12.5-2*i);
        double oxY = srcChart.paddingY + rowHigh*(12.5-2*i);
        
        g.setColor(Color.RED);
        g.setFont(new Font(FontRepository.FONT_LIHEI , Font.PLAIN, (int) rowHigh));
        g.setStroke(new BasicStroke((float) (rowHigh / 8.0)));
        
        double radius = (rowHigh / BaseHexagramChart.GOLDEN_RATIO) / 2;
        if (src.getLine(i).getYinYang().getBooleanValue())
        {
          // 陽變陰 , 畫圓
          // 從左上角，寬高分別是 radius * 2
          Ellipse2D.Double circle = new Ellipse2D.Double(oxX - radius , oxY - radius , radius*2 , radius*2);
          g.draw(circle);
        }
        else
        {
          // 陰變陽 , 畫一個叉叉
          g.draw(new Line2D.Double(oxX - radius , oxY - radius , oxX + radius , oxY + radius));
          g.draw(new Line2D.Double(oxX - radius , oxY + radius , oxX + radius , oxY - radius));
        }
        
        // 畫向右的箭頭
        double arrowX = getArrowX(radius);
        double arrowY = oxY;
        
        g.draw(new Line2D.Double(oxX + radius*2 , oxY , arrowX , arrowY));
        // 往左上角畫去
        g.draw(new Line2D.Double(arrowX , arrowY , arrowX-radius , arrowY-radius));
        // 往左下角畫去
        g.draw(new Line2D.Double(arrowX , arrowY , arrowX-radius , arrowY+radius));
      } // 有變爻
    } // 6 to 1
  } // constructor
  
  /** OX 的 X 座標 */
  protected abstract double getOxX();
  
  /** 取得箭頭頂點的 X 座標 */
  protected abstract double getArrowX(double radius);

  /** 繪製側邊文字 , 可能是八卦類像文字（天澤火雷...） */
  protected abstract void drawSide();
  
  public GoldenPaddingChart getSrcChart()
  {
    return srcChart;
  }

  public GoldenPaddingChart getDstChart()
  {
    return dstChart;
  }
  
}
