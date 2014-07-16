/**
 * Created by smallufo on 2014-06-06.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.calendar.eightwords.personal.EightWordsPersonContext;

import java.awt.*;
import java.util.Optional;

/**
 * 一個「人」的簡易八字盤（正方形），沒有流年，沒有日期
 */
public class EightWordsPersonSquareChart extends EightWordsWithDescChart{

  public EightWordsPersonSquareChart(int width, EightWordsPersonContext context, EightWordsChart.Direction direction) {
    super(width, Color.WHITE, Color.BLACK, Optional.of(context.getGender()) , context.getEightWords(), direction);
  }
}
