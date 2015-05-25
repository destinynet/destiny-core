/** 2009/12/15 上午3:51:40 by smallufo */
package destiny.FengShui.SanYuan;

import destiny.iching.Symbol;
import org.junit.Test;

public class ChartColorCanvasWrapperTest
{
  private Chart chart;
  private ChartColorCanvasWrapper wrapper;

  @Test
  public void testGetColorCanvas()
  {
    chart = new Chart(7 , Mountain.午 , Symbol.兌);
    wrapper = new ChartColorCanvasWrapper(chart);
    System.out.println(wrapper.getColorCanvas().toString());
  }

}

