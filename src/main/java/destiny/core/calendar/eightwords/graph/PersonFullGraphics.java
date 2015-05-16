/**
 * Created by smallufo on 2015-04-29.
 */
package destiny.core.calendar.eightwords.graph;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeDecoratorChinese;
import destiny.core.calendar.eightwords.*;
import destiny.core.calendar.eightwords.personal.*;
import destiny.core.chart.Constants;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranch;
import destiny.font.FontRepository;

import java.awt.*;
import java.util.Locale;
import java.util.Optional;

/**
 * 直式八字命盤，下方九條大運
 * 寬度若為1 , 高度為 1.618
 * +-------------------------+
 * | 西元 xxx      性別 :男性  |  meta
 * | 地點 xxx      GMT :     |
 * | 東經 xxx 北緯 xxx        |
 * | 換日 / DST / link / ... |
 * +-------------------------+
 * |   x     x     x     x   | squareChart
 * |   x     x     x     x   |
 * |                         |
 * |  HHH   DDD   MMM   YYY  |
 * |                         |
 * |  HHH   DDD   MMM   YYY  |
 * |                         |
 * |   y     y     y     y   |
 * |  xxx   xxx   xxx   xxx  |
 * |  xxx   xxx   xxx   xxx  |
 * +-------------------------+
 * |   y     y     y     y   |
 * | xxx xxx xxx xxx xxx xxx |
 * | xxx xxx xxx xxx xxx xxx | 九條流年大運
 * +-------------------------+
 */
public class PersonFullGraphics {

  private static ReactionsUtil reactionsUtil = new ReactionsUtil(new HiddenStemsStandardImpl());

  public static void render(Graphics2D g, PersonContextModel model, int width, Color bg, Color fore,
                            Direction direction) {
    int height = getHeight(width);

    PersonContext context = model.getPersonContext();

    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bg);
    g.fillRect(0, 0, width, height);
    g.setColor(fore);

    // 上方的 meta data graphics
    int metaHeight = getMetaHeight(width);
    Graphics2D metaGraphics = (Graphics2D) g.create(0, 0, width, metaHeight);
    renderMeta(metaGraphics, width, context, model);

    // 中間的squareGraphics
    Graphics2D personSquareGraphics = (Graphics2D) g.create(0 , metaHeight , width , width); // square , width = height
    EightWordsWithDescGraphics.render(personSquareGraphics, width, bg , fore , context.getEightWords(), Optional.of(context.getGender()), direction);


    // 九個大運，每個大運最多三柱 ( cylinder )，
    float threeCylindersWidth = width/9;
    // 每個大運之間自我保留左右各 5% padding
    float padding = (float) (threeCylindersWidth * 0.05);
    // 字體寬度，就是剩餘寬度除以三
    float fontWidth = (threeCylindersWidth - (padding * 2)) / 3;

    float y = metaHeight + width + fontWidth*1;

    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, (int) fontWidth));

    //forward : 大運是否順行
    boolean isForward = context.isFortuneDirectionForward();
    EightWords eightWords = context.getEightWords();

    for (int i=1 ; i <= model.getFortuneDatas().size() ; i++) {
      int x;
      if (direction == Direction.L2R)
        x = (int) (threeCylindersWidth * (i-1));
      else
        x = (int) (width - threeCylindersWidth * i);

      Graphics2D threeCylindersGraph = (Graphics2D) g.create(x , (int)y , (int)threeCylindersWidth , (int) getThreeCylindersHeight(threeCylindersWidth));
      renderThreeCylinders(threeCylindersGraph , threeCylindersWidth , model.getFortuneDatas().get(i-1) , eightWords , bg);
    }
  }

  public static int getHeight(int width) {
    return (int) (width * Constants.GOLDEN_RATIO);
  }

  /**
   *            w
   * +-------------------------+
   * | 西元 xxx      性別 :男性  |  meta
   * | 地點 xxx      GMT :     |
   * | 東經 xxx 北緯 xxx        |  w/1.618/2
   * | 換日 / DST / link / ... |
   * +-------------------------+
   */
  private static void renderMeta(Graphics2D g, float width, PersonContext context , PersonContextModel model) {
    int height = getMetaHeight((int) width);

    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // meta data 標準色
    Color generalMetaColor = Color.BLACK;

    float lineHeight = (float) (height / 7 * 0.9);
    float fontSize = (float) (lineHeight*0.8);
    float x = lineHeight/2;
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, (int) fontSize));
    g.setColor(generalMetaColor);
    TimeDecoratorChinese timeDecorator = new TimeDecoratorChinese();

    g.drawString(timeDecorator.getOutputString(context.getLmt())+"　　　　　性別：" , x , lineHeight );
    switch (context.getGender()) {
      case 男: g.setColor(Color.BLUE);break;
      case 女: g.setColor(Color.RED);break;
      default: g.setColor(generalMetaColor);
    }
    g.drawString(context.getGender().name()+"性" , fontSize*29 , lineHeight);
    g.setColor(generalMetaColor);
    g.drawString("地點："+ model.getLocationName() , x , lineHeight*2);
    g.drawString("GMT時差："+ model.getGmtMinuteOffset()+"分鐘" , x + fontSize* 21 , lineHeight*2);
    Location location = context.getLocation();
    StringBuffer sb = new StringBuffer();
    sb.append(location.getEastWest() == Location.EastWest.EAST ? "東經" : "西經").append(" ");
    sb.append(location.getLongitudeDegree()).append("度 ");
    sb.append(location.getLongitudeMinute()).append("分 ");
    sb.append(location.getLongitudeSecond()).append("秒 , ");

    sb.append(location.getNorthSouth() == Location.NorthSouth.NORTH ? "北緯" : "南緯").append(" ");
    sb.append(location.getLatitudeDegree()).append("度 ");
    sb.append(location.getLatitudeMinute()).append("分 ");
    sb.append(location.getLatitudeSecond()).append("秒");

    g.drawString(sb.toString() , x , lineHeight*3);

    g.drawString("換日：" , x , lineHeight*4);
    String changeDay;
    if (!context.isChangeDayAfterZi()) {
      // 子正換日
      g.setColor(Color.RED);
      changeDay = "子正換日";
    } else
      changeDay = "子初換日";

    g.drawString(changeDay , x + fontSize*3 , lineHeight*4);

    g.setColor(generalMetaColor);

    g.drawString("日光節約：" , x + fontSize*10 , lineHeight*4);
    String hasDst;
    if (model.isDst()) {
      g.setColor(Color.RED);
      hasDst = "有";
    } else
      hasDst = "無";
    g.drawString(hasDst , x + fontSize*15 , lineHeight*4);

    if (location.getNorthSouth() == Location.NorthSouth.SOUTH ) {
      YearMonthIF yearMonthImpl = context.getYearMonthImpl();
      if (yearMonthImpl instanceof YearMonthSolarTermsStarPositionImpl) {
        YearMonthSolarTermsStarPositionImpl impl = (YearMonthSolarTermsStarPositionImpl) yearMonthImpl;
        g.setColor(Color.RED);
        g.drawString("南半球" , x + fontSize * 20 , lineHeight*4);
        g.setColor(generalMetaColor);
        g.drawString("月令：" + (impl.isSouthernHemisphereOpposition() ? "對沖" : "不對沖") , x + fontSize * 23 , lineHeight*4);
      }
    }

    g.setColor(generalMetaColor);

    g.drawString("子正是：" + context.getMidnightImpl().getTitle(Locale.TRADITIONAL_CHINESE) , x , lineHeight*5);

    g.drawString("時辰劃分：" , x , lineHeight * 6);
    if (context.getHourImpl() instanceof HourLmtImpl) {
      g.setColor(Color.RED);
    }
    g.drawString(context.getHourImpl().getTitle(Locale.TRADITIONAL_CHINESE) , x + fontSize * 5 , lineHeight*6);
  }

  private static int getMetaHeight(int width) {
    return (int) (width / Constants.GOLDEN_RATIO / 2);
  }

  private static void renderThreeCylinders(Graphics2D g , float width , FortuneData fortuneData , EightWords eightWords , Color bg) {
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bg);

    int height = (int) getThreeCylindersHeight(width);
    g.fillRect(0, 0, (int) width, height);

    float fontSize = (float) (width * 0.85 / 3);
    float padding = (float) (width * 0.05);

    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, (int) fontSize));

    // 流年上方的字（歲數/西元/民國）
    g.setColor(Color.PINK);
    g.drawString(String.valueOf(fortuneData.getStartFortune()), padding, fontSize);

    // 天干十神
    g.setColor(Color.LIGHT_GRAY);
    HeavenlyStems dayStem = eightWords.getDay().getStem();

    StemBranch stemBranch = fortuneData.getStemBranch();

    Reactions reaction = reactionsUtil.getReaction(stemBranch.getStem() , dayStem);
    g.drawString(reaction.toString().substring(0,1) , padding + fontSize   , fontSize*2);
    g.drawString(reaction.toString().substring(1,2) , padding + fontSize   , fontSize*3);

    // 大運天干地支
    g.setColor(Color.BLACK);
    g.drawString(stemBranch.getStem()  .toString() , padding + fontSize , fontSize*4);
    g.drawString(stemBranch.getBranch().toString() , padding + fontSize , fontSize*5);

    // 地支藏干
    g.setColor(Color.LIGHT_GRAY);
    java.util.List<Reactions> reactions = reactionsUtil.getReactions(stemBranch.getBranch() , dayStem);
    if (reactions.size() >= 1 ) {
      Reactions eachReaction = reactions.get(0);
      HeavenlyStems hiddenStem = ReactionsUtil.getHeavenlyStems(dayStem, eachReaction); // 地支藏干
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
  }

  private static float getThreeCylindersHeight(float threeCylindersWidth) {
    return threeCylindersWidth / 3 * 8;
  }

}
