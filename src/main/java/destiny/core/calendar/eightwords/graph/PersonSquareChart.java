/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.Gender;
import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.EightWords;

import java.awt.*;
import java.util.Optional;

/**
 * 接收 PersonContext
 *
 * 一個「人」的簡易八字盤（正方形），沒有流年，沒有日期
 */
public class PersonSquareChart extends EightWordsWithDescChart {

  public PersonSquareChart(int width, EightWords eightWords , Optional<Gender> genderOptional , Direction direction) {
    super(width, Color.WHITE, Color.BLACK, genderOptional , eightWords, direction);
  }
}
