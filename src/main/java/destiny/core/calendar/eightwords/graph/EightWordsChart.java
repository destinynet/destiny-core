/**
 * Created by smallufo on 2014-06-04.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.EightWordsNullable;
import destiny.core.chinese.StemBranchNullable;
import destiny.font.FontRepository;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static destiny.core.chart.Constants.GOLDEN_RATIO;
import static destiny.core.chart.Constants.WIDTH_HEIGHT;

/**
 * 純粹八個字（干支可能為空）的圖 , 黃金比例
 *
 *   +-------------------------+
 *   |  HHH   DDD   MMM   YYY  |
 *   |                         |
 *   |  HHH   DDD   MMM   YYY  |
 *   +-------------------------+
 */
public class EightWordsChart extends BufferedImage implements Serializable {

  /**
   * @param which 寬度還是高度
   * @param value 寬度或高度的值
   * @param bg 背景顏色
   * @param fore 前景顏色
   * @param dayStemColor 日主的顏色
   * @param eightWordsNullable 八個字
   * @param direction 排列方向：右到左，還是左到右
   */
  public EightWordsChart(WIDTH_HEIGHT which, int value, Color bg, Color fore, Color dayStemColor, @NotNull EightWordsNullable eightWordsNullable, Direction direction) {
    super(
        (which == WIDTH_HEIGHT.WIDTH  ? value : (int)(value * GOLDEN_RATIO))
      , (which == WIDTH_HEIGHT.HEIGHT ? value : (int)(value / GOLDEN_RATIO))
      , BufferedImage.TYPE_INT_ARGB);

    Graphics2D g = this.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(bg);
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setColor(fore);

    float cellWidth = getCellWidth();
    float cellHeight = getCellHeight();

    int fontSize = getFontSize();

    g.setFont(getFont());

    List<StemBranchNullable> stemBranchList = eightWordsNullable.getStemBranches();
    if (direction == Direction.R2L)
      Collections.reverse(stemBranchList);

    for(int i=0 ; i<4 ; i++) {
      StemBranchNullable sb = stemBranchList.get(i);

      for (int j = 1 ; j <= 2 ; j++) {
        float textY = (float) (cellHeight*(j-0.2));
        float textX = cellWidth*i + (cellWidth-fontSize)/2;

        g.setColor(fore);
        if (j % 2 == 1) {
          if ((direction == Direction.R2L && i == 1) || (direction == Direction.L2R && i == 2))
            g.setColor(dayStemColor); // 日主
          g.drawString(String.valueOf(sb.getStem()), textX, textY);
        }
        else {
          g.drawString(String.valueOf(sb.getBranch()), textX, textY);
        }
      } // 干支
    } // 四柱
    g.dispose();
  } // constructor

  @NotNull
  public Font getFont() {
    return new Font(FontRepository.getFontLiHei() , Font.PLAIN, getFontSize());
  }

  public float getCellWidth() {
    return (float) (getWidth() / 4.0);
  }

  public float getCellHeight() {
    return (float) (getHeight() / 2.0);
  }

  public int getFontSize() {
    return (int) (getHeight() * GOLDEN_RATIO / 4.0 * 0.9);
  }
}
