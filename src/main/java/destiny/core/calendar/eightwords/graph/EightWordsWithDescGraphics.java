/**
 * Created by smallufo on 2015-04-29.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.Gender;
import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.EightWordsNullable;
import destiny.core.calendar.eightwords.personal.HiddenStemsIF;
import destiny.core.calendar.eightwords.personal.HiddenStemsStandardImpl;
import destiny.core.calendar.eightwords.personal.Reactions;
import destiny.core.calendar.eightwords.personal.ReactionsUtil;
import destiny.core.chart.Constants;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranchOptional;
import destiny.font.FontRepository;
import destiny.utils.Triple;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class EightWordsWithDescGraphics {

  private static HiddenStemsIF hiddenStemsImpl = new HiddenStemsStandardImpl();

  /**
   * 八字盤 with 十神文字 , 性別為 optional .
   * 若傳入性別，男生日主為藍色，女生日主為紅色
   * <p>
   * 正方形圖片
   * <p>
   * +-------------------------+
   * |   x     x     x     x   |
   * |   x     x     x     x   |
   * |                         |
   * |  HHH   DDD   MMM   YYY  |\
   * |                         | } mainChart
   * |  HHH   DDD   MMM   YYY  |/
   * |                         |
   * |   y     y     y     y   | \
   * |  xxx   xxx   xxx   xxx  |  } Desc
   * |  xxx   xxx   xxx   xxx  | /
   * +-------------------------+
   */
  public static void render(Graphics2D g, int width, Color bg, Color fore, @NotNull EightWordsNullable eightWordsNullable, @NotNull Optional<Gender> genderOptional, Direction direction) {
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bg);
    g.fillRect(0, 0, width, width);
    g.setColor(fore);

    Color dayStemColor = genderOptional.isPresent() ? (genderOptional.get().isMale() ? Color.BLUE : Color.RED) : fore;

    // 字體大小，為「八字圖片」中，每個字體寬度的三分之一
    int mainChartWidth = EightWordsGraphics.getWidth(Constants.WIDTH_HEIGHT.WIDTH, width);
    int mainChartHeight = EightWordsGraphics.getHeight(Constants.WIDTH_HEIGHT.WIDTH, width);
    float mainChartCellWidth = EightWordsGraphics.getCellWidth(mainChartWidth);
    int mainChartFontSize = EightWordsGraphics.getFontSize(mainChartHeight);
    int fontSize = mainChartFontSize / 3;

    // 繪製 mainChart
    // 主要的「八字圖片」，高度離頂端為兩個字體 size
    float mainChartY = fontSize * 2;
    Graphics2D mainChart = (Graphics2D) g.create(0, (int) mainChartY, width, EightWordsGraphics.getHeight(Constants.WIDTH_HEIGHT.WIDTH, width));
    EightWordsGraphics.render(mainChart, Constants.WIDTH_HEIGHT.WIDTH, width, bg, fore, dayStemColor, eightWordsNullable, direction);

    // 地支藏干
    ReactionsUtil reactionsUtil = new ReactionsUtil(hiddenStemsImpl);
    Stem dayStem = eightWordsNullable.getDayStem();

    // Tuple < 干支 , 天干(相對於日干)的十神 , 地支藏干(相對於日干)的十神 >
    java.util.List<Triple<StemBranchOptional, Reactions, java.util.List<Reactions>>> list = new ArrayList<>();

    eightWordsNullable.getStemBranches().forEach(sb -> {
      java.util.List<Reactions> reactions = reactionsUtil.getReactions(sb.getBranchOptional().get(), dayStem);
      list.add(Triple.of(sb, reactionsUtil.getReaction(sb.getStemOptional().get(), dayStem), reactions));
    });

    if (direction == Direction.R2L)
      Collections.reverse(list);

    float hiddenStemY = mainChartY + mainChartHeight + fontSize;
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));


    for (int i = 0; i < list.size(); i++) {

      g.setColor(Color.GRAY);

      Reactions hsReactions = list.get(i).getSecond();
      java.util.List<Reactions> ebReactions = list.get(i).getThird();

      if (ebReactions.size() >= 1) {
        float fontX = i * mainChartCellWidth + (mainChartCellWidth - mainChartFontSize) / 2 + fontSize;

        if ((direction == Direction.R2L && i != 1) || (direction == Direction.L2R && i != 2)) {
          // 非日主上方，就要寫上十神
          g.drawString(hsReactions.toString().substring(0, 1), fontX, mainChartY - fontSize);
          g.drawString(hsReactions.toString().substring(1, 2), fontX, mainChartY);
        }
        else {
          // 日主上方，寫上男女
          if (genderOptional.isPresent())
            g.drawString(genderOptional.get().toString(), fontX, mainChartY);
        }
        //第一個藏干，放中間
        g.drawString(ReactionsUtil.getHeavenlyStems(dayStem, ebReactions.get(0)).toString(), fontX, hiddenStemY);
        // 十神
        g.drawString(ebReactions.get(0).toString().substring(0, 1), fontX, hiddenStemY + fontSize);
        g.drawString(ebReactions.get(0).toString().substring(1, 2), fontX, hiddenStemY + fontSize * 2);
      }
      if (ebReactions.size() >= 2) {
        float fontX = i * mainChartCellWidth + (mainChartCellWidth - mainChartFontSize) / 2 + fontSize * 2;
        //第二個藏干，放右邊
        g.drawString(ReactionsUtil.getHeavenlyStems(dayStem, ebReactions.get(1)).toString(), fontX, hiddenStemY);
        // 十神
        g.drawString(ebReactions.get(1).toString().substring(0, 1), fontX, hiddenStemY + fontSize);
        g.drawString(ebReactions.get(1).toString().substring(1, 2), fontX, hiddenStemY + fontSize * 2);
      }
      if (ebReactions.size() == 3) {
        float fontX = i * mainChartCellWidth + (mainChartCellWidth - mainChartFontSize) / 2;
        //第三個藏干，放左邊
        g.drawString(ReactionsUtil.getHeavenlyStems(dayStem, ebReactions.get(2)).toString(), fontX, hiddenStemY);
        // 十神
        g.drawString(ebReactions.get(2).toString().substring(0, 1), fontX, hiddenStemY + fontSize);
        g.drawString(ebReactions.get(2).toString().substring(1, 2), fontX, hiddenStemY + fontSize * 2);
      }
    }

    // 在圖片上方以及下方各畫出兩條水平線，標出圖片高度範圍。經比對，在各種解析度下，高度均約為寬度的 0.98倍 (ratio)，因此可以將此 Chart 定為正方形
//    g.setColor(Color.RED);
//    int top = (int)(mainChartY-fontSize*2);
//    g.drawLine(0 , top , width , top);
//    int bottom = (int)(mainChartY + mainChartHeight + fontSize*3);
//    g.drawLine(0 , bottom , width , bottom);
//    int trueHeight = (bottom - top);
//    System.out.println("w = " + width + " , h = " + trueHeight + " , ratio = " + ((double)trueHeight/width));


  } // render()
}
