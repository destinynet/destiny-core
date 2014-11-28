/**
 * Created by smallufo on 2014-11-28.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.personal.EightWordsPersonContext;
import destiny.core.calendar.eightwords.personal.HiddenStemsStandardImpl;
import destiny.core.calendar.eightwords.personal.Reactions;
import destiny.core.calendar.eightwords.personal.ReactionsUtil;
import destiny.core.chart.Constants;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranch;
import destiny.font.FontRepository;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 直式八字命盤，下方九條大運
 * 寬度若為1 , 高度為 1.618
 *
 *   +-------------------------+
 *   |   x     x     x     x   | squareChart
 *   |   x     x     x     x   |
 *   |                         |
 *   |  HHH   DDD   MMM   YYY  |
 *   |                         |
 *   |  HHH   DDD   MMM   YYY  |
 *   |                         |
 *   |   y     y     y     y   |
 *   |  xxx   xxx   xxx   xxx  |
 *   |  xxx   xxx   xxx   xxx  |
 *   +-------------------------+
 *   |   y     y     y     y   |
 *   | xxx xxx xxx xxx xxx xxx |
 *   | xxx xxx xxx xxx xxx xxx | 九條流年大運
 *   +-------------------------+
 *
 */
public class EightWordsPersonalFullChart extends BufferedImage implements Serializable {

  private Color bg;
  private ReactionsUtil reactionsUtil = new ReactionsUtil(new HiddenStemsStandardImpl());

  public EightWordsPersonalFullChart(int width , Color bg, Color fore, @NotNull EightWordsPersonContext context, EightWordsChart.Direction direction) {
    super(width, (int) (width * Constants.GOLDEN_RATIO),  BufferedImage.TYPE_INT_ARGB);

    this.bg = bg;

    Graphics2D g = this.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bg);
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(fore);

    // 上方的squareChart
    EightWordsPersonSquareChart squareChart = new EightWordsPersonSquareChart(width , context , direction);
    g.drawImage(squareChart , 0 , 0 , null);

    // 九個大運，每個大運最多三柱 ( cylinder )，
    float threeCylindersWidth = width/9;
    // 每個大運之間自我保留左右各 5% padding
    float padding = (float) (threeCylindersWidth * 0.05);
    // 字體寬度，就是剩餘寬度除以三
    float fontWidth = (threeCylindersWidth - (padding * 2)) / 3;

    float y = squareChart.getHeight() + fontWidth*2;

    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, (int) fontWidth));



    //forward : 大運是否順行
    boolean isForward = context.isFortuneDirectionForward();
    EightWords eightWords = context.getEightWords();

    //下個大運的干支
    StemBranch nextStemBranch;
    if (isForward)
      nextStemBranch = eightWords.getMonth().getNext();
    else
      nextStemBranch = eightWords.getMonth().getPrevious();

    for (int i=1 ; i<=9 ; i++) {
      int x;
      if (direction == EightWordsChart.Direction.L2R)
        x = (int) (threeCylindersWidth * (i-1));
      else
        x = (int) (width - threeCylindersWidth * i);

      g.drawImage(getThreeCylinders(threeCylindersWidth , nextStemBranch , eightWords) , x , (int) y , null);


      if (isForward)
        nextStemBranch = nextStemBranch.getNext();
      else
        nextStemBranch = nextStemBranch.getPrevious();
    } // 1 to 9
  } // constructor

  /** 取得大運（內含最多三個藏干） */
  private BufferedImage getThreeCylinders(float width , StemBranch nextStemBranch , EightWords eightWords) {

    BufferedImage img = new BufferedImage( (int) width , (int) width / 3 * 8 , BufferedImage.TYPE_INT_ARGB);

    Graphics2D g = img.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bg);
    g.fillRect(0, 0, img.getWidth(), img.getHeight());

    float fontSize = (float) (width * 0.85 / 3);
    float padding = (float) (width * 0.05);

    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, (int) fontSize));
    g.setColor(Color.LIGHT_GRAY);

    HeavenlyStems dayStem = eightWords.getDay().getStem();

    //g.drawString("????" , padding , fontSize);
    Reactions reaction = reactionsUtil.getReaction(nextStemBranch.getStem() , dayStem);
    g.drawString(reaction.toString().substring(0,1) , padding + fontSize   , fontSize*2);
    g.drawString(reaction.toString().substring(1,2) , padding + fontSize   , fontSize*3);

    // 大運天干地支
    g.setColor(Color.BLACK);
    g.drawString(nextStemBranch.getStem()  .toString() , padding + fontSize , fontSize*4);
    g.drawString(nextStemBranch.getBranch().toString() , padding + fontSize , fontSize*5);

    // 地支藏干
    g.setColor(Color.LIGHT_GRAY);
    java.util.List<Reactions> reactions = reactionsUtil.getReactions(nextStemBranch.getBranch() , dayStem);
    if (reactions.size() >= 1 ) {
      Reactions eachReaction = reactions.get(0);
      HeavenlyStems hiddenStem = ReactionsUtil.getHeavenlyStems(dayStem,eachReaction); // 地支藏干
      g.drawString(hiddenStem.toString() , padding + fontSize   , fontSize*6);
      g.drawString(eachReaction.toString().substring(0 , 1) , padding + fontSize , fontSize * 7);
      g.drawString(eachReaction.toString().substring(1 , 2) , padding + fontSize , fontSize * 8);
    }
    if (reactions.size() >= 2 ) {
      Reactions eachReaction = reactions.get(1);
      HeavenlyStems hiddenStem = ReactionsUtil.getHeavenlyStems(dayStem,eachReaction); // 地支藏干
      g.drawString(hiddenStem.toString() , padding + fontSize*2   , fontSize*6);
      g.drawString(eachReaction.toString().substring(0 , 1) , padding + fontSize*2 , fontSize * 7);
      g.drawString(eachReaction.toString().substring(1 , 2) , padding + fontSize*2 , fontSize * 8);
    }
    if (reactions.size() == 3 ) {
      Reactions eachReaction = reactions.get(2);
      HeavenlyStems hiddenStem = ReactionsUtil.getHeavenlyStems(dayStem, eachReaction); // 地支藏干
      g.drawString(hiddenStem.toString() , padding    , fontSize*6);
      g.drawString(eachReaction.toString().substring(0, 1) , padding , fontSize * 7);
      g.drawString(eachReaction.toString().substring(1 , 2) , padding , fontSize * 8);
    }
    return img;
  }

}
