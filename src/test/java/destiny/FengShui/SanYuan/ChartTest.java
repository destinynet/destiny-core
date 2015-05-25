package destiny.FengShui.SanYuan;

import destiny.iching.Symbol;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 11:22:16
 */
public class ChartTest
{
  private Chart chart;

  @Nullable
  private ChartBlock cb;

  /**
   * 七運，午山子向
   * 
   * 14  68  86
   * 六  二  四
   * 
   * 95  23  41
   * 五  七  九
   * 
   * 59  77  32
   * 一  三  八
   */
  @Test
  public void testChart1()
  {
    chart = new Chart(7 , Mountain.午 , Symbol.坎);
    
    cb = chart.getChartBlock(Symbol.坎);
    assertSame(7 , cb.getMountain());
    assertSame(7 , cb.getDirection());
    assertSame(3 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.艮);
    assertSame(5 , cb.getMountain());
    assertSame(9 , cb.getDirection());
    assertSame(1 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.震);
    assertSame(9 , cb.getMountain());
    assertSame(5 , cb.getDirection());
    assertSame(5 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.巽);
    assertSame(1 , cb.getMountain());
    assertSame(4 , cb.getDirection());
    assertSame(6 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.離);
    assertSame(6 , cb.getMountain());
    assertSame(8 , cb.getDirection());
    assertSame(2 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.坤);
    assertSame(8 , cb.getMountain());
    assertSame(6 , cb.getDirection());
    assertSame(4 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.兌);
    assertSame(4 , cb.getMountain());
    assertSame(1 , cb.getDirection());
    assertSame(9 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.乾);
    assertSame(3 , cb.getMountain());
    assertSame(2 , cb.getDirection());
    assertSame(8 , cb.getPeriod());
  }
  
  /**
   * 七運，午山子向 , 與前者一樣，只是換了觀點，不影響結果
   * 
   * 14  68  86
   * 六  二  四
   * 
   * 95  23  41
   * 五  七  九
   * 
   * 59  77  32
   * 一  三  八
   */
  @Test
  public void testChart2()
  {
    chart = new Chart(7 , Mountain.午 , Symbol.乾);
    
    cb = chart.getChartBlock(Symbol.坎);
    assertSame(7 , cb.getMountain());
    assertSame(7 , cb.getDirection());
    assertSame(3 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.艮);
    assertSame(5 , cb.getMountain());
    assertSame(9 , cb.getDirection());
    assertSame(1 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.震);
    assertSame(9 , cb.getMountain());
    assertSame(5 , cb.getDirection());
    assertSame(5 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.巽);
    assertSame(1 , cb.getMountain());
    assertSame(4 , cb.getDirection());
    assertSame(6 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.離);
    assertSame(6 , cb.getMountain());
    assertSame(8 , cb.getDirection());
    assertSame(2 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.坤);
    assertSame(8 , cb.getMountain());
    assertSame(6 , cb.getDirection());
    assertSame(4 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.兌);
    assertSame(4 , cb.getMountain());
    assertSame(1 , cb.getDirection());
    assertSame(9 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.乾);
    assertSame(3 , cb.getMountain());
    assertSame(2 , cb.getDirection());
    assertSame(8 , cb.getPeriod());
  }

  
  /**
   * 一運丙山 (五入中)
   * 
   * 47  92  29
   * 九  五  七
   * 
   * 38  56  74
   * 八  一  三
   * 
   * 83  11  65
   * 四  六  二
   * 
   */
  @Test
  public void testChart3()
  {
    chart = new Chart(1 , Mountain.丙 , Symbol.乾);
    
    cb = chart.getChartBlock(Symbol.坎);
    assertSame(1 , cb.getMountain());
    assertSame(1 , cb.getDirection());
    assertSame(6 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.艮);
    assertSame(8 , cb.getMountain());
    assertSame(3 , cb.getDirection());
    assertSame(4 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.震);
    assertSame(3 , cb.getMountain());
    assertSame(8 , cb.getDirection());
    assertSame(8 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.巽);
    assertSame(4 , cb.getMountain());
    assertSame(7 , cb.getDirection());
    assertSame(9 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.離);
    assertSame(9 , cb.getMountain());
    assertSame(2 , cb.getDirection());
    assertSame(5 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.坤);
    assertSame(2 , cb.getMountain());
    assertSame(9 , cb.getDirection());
    assertSame(7 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.兌);
    assertSame(7 , cb.getMountain());
    assertSame(4 , cb.getDirection());
    assertSame(3 , cb.getPeriod());
    
    cb = chart.getChartBlock(Symbol.乾);
    assertSame(6 , cb.getMountain());
    assertSame(5 , cb.getDirection());
    assertSame(2 , cb.getPeriod());
  }
}
