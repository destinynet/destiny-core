/**
 * Created by smallufo on 2014-06-06.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.Gender;
import destiny.core.calendar.eightwords.personal.EightWordsPersonContext;

import java.awt.*;

/**
 * 一個「人」的簡易八字盤（正方形），沒有流年，沒有日期
 */
public class EightWordsPersonSquareChart extends EightWordsWithDescChart{

  public EightWordsPersonSquareChart(int width, EightWordsPersonContext context, EightWordsChart.Direction direction) {
    super(width, Color.WHITE, Color.BLACK,
      context.getGender() == Gender.男 ? Color.BLUE : Color.RED ,
      context.getEightWords(), direction);
  }
}
