/**
 * Created by smallufo on 2015-04-28.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.EightWordsNullable;
import destiny.core.chart.Constants;
import destiny.core.chinese.StemBranchOptional;
import destiny.font.FontRepository;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;

import static destiny.core.chart.Constants.GOLDEN_RATIO;

public class EightWordsGraphics {

  /**
   * 純粹八個字（干支可能為空）的圖 , 黃金比例
   * +-------------------------+
   * |  HHH   DDD   MMM   YYY  |
   * |                         |
   * |  HHH   DDD   MMM   YYY  |
   * +-------------------------+
   */
  public static void render(Graphics2D g, Constants.WIDTH_HEIGHT which, int value, Color bg, Color fore,
                            Color dayStemColor, @NotNull EightWordsNullable eightWordsNullable,
                            Direction direction) {
    int w = getWidth(which , value);
    int h = getHeight(which , value);

    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(bg);
    g.fillRect(0, 0, w, h);

    g.setColor(fore);

    float cellWidth = getCellWidth(w);
    float cellHeight = getCellHeight(h);

    int fontSize = getFontSize(h);

    g.setFont(getFont(h));

    java.util.List<StemBranchOptional> stemBranchList = eightWordsNullable.getStemBranches();
    if (direction == Direction.R2L)
      Collections.reverse(stemBranchList);

    for (int i = 0; i < 4; i++) {
      StemBranchOptional sb = stemBranchList.get(i);

      for (int j = 1; j <= 2; j++) {
        float textY = (float) (cellHeight * (j - 0.2));
        float textX = cellWidth * i + (cellWidth - fontSize) / 2;

        g.setColor(fore);
        if (j % 2 == 1) {
          if ((direction == Direction.R2L && i == 1) || (direction == Direction.L2R && i == 2))
            g.setColor(dayStemColor); // 日主
          g.drawString(String.valueOf(sb.getStemOptional().get()), textX, textY);
        }
        else {
          g.drawString(String.valueOf(sb.getBranchOptional().get()), textX, textY);
        }
      } // 干支
    } // 四柱
  }

  public static float getCellWidth(int width) {
    return (float) (width / 4.0);
  }

  public static float getCellHeight(int height) {
    return (float) (height / 2.0);
  }

  public static int getWidth(Constants.WIDTH_HEIGHT which, int value) {
    return (which == Constants.WIDTH_HEIGHT.WIDTH ? value : (int) (value * GOLDEN_RATIO));
  }

  public static int getHeight(Constants.WIDTH_HEIGHT which, int value) {
    return (which == Constants.WIDTH_HEIGHT.HEIGHT ? value : (int) (value / GOLDEN_RATIO));
  }

  public static int getFontSize(int height) {
    return (int) (height * GOLDEN_RATIO / 4.0 * 0.9);
  }

  private static Font getFont(int height) {
    return new Font(FontRepository.getFontLiHei() , Font.PLAIN, getFontSize(height));
  }
}
