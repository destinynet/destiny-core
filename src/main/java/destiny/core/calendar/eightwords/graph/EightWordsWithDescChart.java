/**
 * Created by smallufo on 2014-07-31.
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
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranchNullable;
import destiny.font.FontRepository;
import destiny.utils.Triple;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 八字盤 with 十神文字 , 性別為 optional .
 * 若傳入性別，男生日主為藍色，女生日主為紅色
 *
 * 正方形圖片
 *
 *   +-------------------------+
 *   |   x     x     x     x   |
 *   |   x     x     x     x   |
 *   |                         |
 *   |  HHH   DDD   MMM   YYY  |
 *   |                         |
 *   |  HHH   DDD   MMM   YYY  |
 *   |                         |
 *   |   y     y     y     y   | \
 *   |  xxx   xxx   xxx   xxx  |  } Desc
 *   |  xxx   xxx   xxx   xxx  | /
 *   +-------------------------+
 */
public class EightWordsWithDescChart extends BufferedImage implements Serializable {

  /** 地支藏干的實作，內定採用標準設定 */
  @NotNull
  private HiddenStemsIF hiddenStemsImpl  = new HiddenStemsStandardImpl();

  public EightWordsWithDescChart(int width , Color bg, Color fore, @NotNull Optional<Gender> genderOptional
    , @NotNull EightWordsNullable eightWordsNullable, Direction direction) {
    super(width, width, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g = this.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bg);
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(fore);

    Color dayStemColor = genderOptional.isPresent()? (genderOptional.get().isMale() ? Color.BLUE : Color.RED) : fore;

    // 主要的八個字
    EightWordsChart mainChart = new EightWordsChart(Constants.WIDTH_HEIGHT.WIDTH , width , bg , fore
      , dayStemColor , eightWordsNullable , direction);

    // 字體大小，為「八字圖片」中，每個字體寬度的三分之一
    int fontSize = mainChart.getFontSize()/3;
    // 主要的「八字圖片」，高度離頂端為兩個字體 size
    float mainChartY = fontSize*2;
    g.drawImage(mainChart , 0 , (int)mainChartY , null);

    // 地支藏干
    ReactionsUtil reactionsUtil = new ReactionsUtil(this.hiddenStemsImpl);

    HeavenlyStems dayStem = eightWordsNullable.getDayStem();

    // Tuple < 干支 , 天干(相對於日干)的十神 , 地支藏干(相對於日干)的十神 >
    List<Triple<StemBranchNullable, Reactions , List<Reactions>>> list = new ArrayList<>();

    eightWordsNullable.getStemBranches().forEach(sb -> {
      List<Reactions> reactions = reactionsUtil.getReactions(sb.getBranch() , dayStem);
      list.add(Triple.of(sb, reactionsUtil.getReaction(sb.getStem(), dayStem), reactions));
    });

    if (direction == Direction.R2L)
      Collections.reverse(list);


    float hiddenStemY = mainChartY + mainChart.getHeight() + fontSize;
    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, fontSize));
    for(int i=0 ; i<list.size() ; i++) {

      g.setColor(Color.GRAY);

      Reactions hsReactions = list.get(i).getSecond();
      List<Reactions> ebReactions = list.get(i).getThird();
      //System.out.println("sb = " + sb + " , hsReactions = " + hsReactions + " , ebReactions = " + ebReactions);

      if (ebReactions.size() >= 1) {
        float fontX = i*mainChart.getCellWidth() + (mainChart.getCellWidth()-mainChart.getFontSize())/2 + fontSize;

        if ((direction == Direction.R2L && i != 1) ||
            (direction == Direction.L2R && i != 2)
          ) {
          // 非日主上方，就要寫上十神
          g.drawString(hsReactions.toString().substring(0,1) , fontX , mainChartY-fontSize);
          g.drawString(hsReactions.toString().substring(1,2) , fontX , mainChartY);
        } else {
          // 日主上方，寫上男女
          if (genderOptional.isPresent())
            g.drawString(genderOptional.get().toString() , fontX , mainChartY);
        }
        //第一個藏干，放中間
        g.drawString(ReactionsUtil.getHeavenlyStems(dayStem,ebReactions.get(0)).toString() , fontX , hiddenStemY);
        // 十神
        g.drawString(ebReactions.get(0).toString().substring(0,1) , fontX , hiddenStemY+fontSize);
        g.drawString(ebReactions.get(0).toString().substring(1,2) , fontX , hiddenStemY+fontSize*2);
      }
      if (ebReactions.size() >= 2) {
        float fontX = i*mainChart.getCellWidth() + (mainChart.getCellWidth()-mainChart.getFontSize())/2 + fontSize*2;
        //第二個藏干，放右邊
        g.drawString(ReactionsUtil.getHeavenlyStems(dayStem,ebReactions.get(1)).toString() , fontX , hiddenStemY);
        // 十神
        g.drawString(ebReactions.get(1).toString().substring(0,1) , fontX , hiddenStemY+fontSize);
        g.drawString(ebReactions.get(1).toString().substring(1,2) , fontX , hiddenStemY+fontSize*2);
      }
      if (ebReactions.size() == 3) {
        float fontX = i*mainChart.getCellWidth() + (mainChart.getCellWidth()-mainChart.getFontSize())/2;
        //第三個藏干，放左邊
        g.drawString(ReactionsUtil.getHeavenlyStems(dayStem,ebReactions.get(2)).toString() , fontX , hiddenStemY);
        // 十神
        g.drawString(ebReactions.get(2).toString().substring(0,1) , fontX , hiddenStemY+fontSize);
        g.drawString(ebReactions.get(2).toString().substring(1,2) , fontX , hiddenStemY+fontSize*2);
      }
    }

    // 在圖片上方以及下方各畫出兩條水平線，標出圖片高度範圍。經比對，在各種解析度下，高度均約為寬度的 0.98倍 (ratio)，因此可以將此 Chart 定為正方形
//    g.setColor(Color.RED);
//    int top = (int)(mainChartY-fontSize*2);
//    g.drawLine(0 , top , getWidth() , top);
//    int bottom = (int)(mainChartY + mainChart.getHeight() + fontSize*3);
//    g.drawLine(0 , bottom , getWidth() , bottom);
//    int trueHeight = (bottom - top);
//    System.out.println("w = " + getWidth() + " , h = " + trueHeight + " , ratio = " + ((double)trueHeight/getWidth()));

    g.dispose();
  }
}
