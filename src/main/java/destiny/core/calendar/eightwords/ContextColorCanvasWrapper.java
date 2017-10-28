/**
 * @author smallufo 
 * Created on 2006/5/5 at 下午 05:11:12
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.GoogleMapsUrlBuilder;
import destiny.core.calendar.Location;
import destiny.core.calendar.LocationUrlBuilder;
import destiny.core.calendar.TimeTools;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.eightwords.personal.HiddenStemsIF;
import destiny.core.calendar.eightwords.personal.HiddenStemsStandardImpl;
import destiny.core.calendar.eightwords.personal.ReactionsUtil;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.tools.ColorCanvas.AlignUtil;
import destiny.tools.ColorCanvas.ColorCanvas;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

import static java.time.temporal.ChronoField.*;

/**
 * 純粹繪製『八字盤』，不包含『人』的因素（大運流年等）
 */
public class ContextColorCanvasWrapper {
  private Logger logger = LoggerFactory.getLogger(getClass());

  /** 八字 Context */
  protected final EightWordsContext context;

  //protected final EightWordsContextModel model;

  /** 地支藏干的實作，內定採用標準設定 */
  private HiddenStemsIF     hiddenStemsImpl  = new HiddenStemsStandardImpl();
  /** 當地時間 */
  private ChronoLocalDateTime lmt              = LocalDateTime.now();
  /** 地點 */
  private Location          location         = Location.of(Locale.TAIWAN);
  /** 地點的名稱 */
  private String locationName = "";
  
  /** TODO : IoC Google Maps URL Builder */
  @NotNull
  private final LocationUrlBuilder urlBuilder = new GoogleMapsUrlBuilder();
  
  /** 網址連結 */
  private String linkUrl;
  
  /** 四字斷終生 */
  // private FourWordsIF fourWordsImpl;
  
  /** 輸出模式 */
  public enum OutputMode {HTML , TEXT}

  private OutputMode outputMode = OutputMode.HTML;

  /** 輸出方向，由左至右，還是由右至左 */
  private final Direction direction;

  private final ReactionsUtil reactionsUtil;
  
  public ContextColorCanvasWrapper(EightWordsContext context, ChronoLocalDateTime lmt, Location location, String locationName, HiddenStemsIF hiddenStemsImpl, String linkUrl, Direction direction) {
    this.context = context;
    this.lmt = lmt;
    this.location = location;
    this.locationName = locationName;
    this.hiddenStemsImpl = hiddenStemsImpl;
    this.linkUrl = linkUrl;
    this.direction = direction;

    reactionsUtil = new ReactionsUtil(this.hiddenStemsImpl);
  }

  /** 設定地支藏干的實作 */
  public void setHiddenStemsImpl(HiddenStemsIF impl)
  {
    this.hiddenStemsImpl = impl;
  }
  
  /**
   * 取得 MetaData (國曆 農曆 經度 緯度 等資料)
   */
  @NotNull
  protected ColorCanvas getMetaDataColorCanvas()
  {
    ColorCanvas cc = new ColorCanvas(9,52,"　");
    
    ColorCanvas 西元資訊 = new ColorCanvas(1,36, "　");
    StringBuilder timeData = new StringBuilder();
    timeData.append("西元：");

    if(lmt.toLocalDate().get(ChronoField.YEAR) <= 0)
      timeData.append("前");
    else
      timeData.append("　");
      
    timeData.append(AlignUtil.alignRight(lmt.get(YEAR_OF_ERA),4));
    timeData.append("年");
    timeData.append(AlignUtil.alignRight(lmt.get(MONTH_OF_YEAR),2));
    timeData.append("月");
    timeData.append(AlignUtil.alignRight(lmt.get(DAY_OF_MONTH),2));
    timeData.append("日");
    timeData.append(AlignUtil.alignRight(lmt.get(HOUR_OF_DAY),2));
    timeData.append("時");
    timeData.append(AlignUtil.alignRight(lmt.get(MINUTE_OF_HOUR),2));
    timeData.append("分");
    timeData.append(AlignUtil.alignRight(lmt.get(SECOND_OF_MINUTE),4));
    timeData.append("秒");
    西元資訊.setText(timeData.toString(), 1, 1);
    cc.add(西元資訊 , 1 , 1 );

    ChineseDate chineseDate = context.getChineseDate();
    cc.setText("農曆：("+chineseDate.getCycle() + "循環)" + chineseDate , 2 , 1);
    
    String url = urlBuilder.getUrl(location);
    
    ColorCanvas 地點名稱 = new ColorCanvas(1,44, "　");
    地點名稱.setText("地點：", 1 , 1);
    //地點名稱.setText(locationName , 1 , 7);
    地點名稱.setText(locationName , 1 , 7 , Optional.empty() , Optional.empty() , Optional.empty() , url , Optional.empty() , false);
    int minuteOffset = TimeTools.getDstSecondOffset(lmt, location).v2() / 60;
    地點名稱.setText(" GMT時差："+AlignUtil.alignRight(minuteOffset,6)+"分鐘", 1, 25 , "999999");
    cc.add(地點名稱 , 3 , 1);
    

    
    ColorCanvas 經度 = new ColorCanvas(1, 24 , "　");
    StringBuilder lonText = new StringBuilder();
    lonText.append(location.getEastWest() == Location.EastWest.EAST ? "東" : "西");
    lonText.append("經：");
    lonText.append(AlignUtil.alignRight(location.getLngDeg(),4));
    lonText.append("度");
    lonText.append(AlignUtil.alignRight(location.getLngMin(),2));
    lonText.append("分");
    lonText.append(AlignUtil.alignRight(location.getLngSec(),4));
    lonText.append("秒");
    經度.setText(lonText.toString(), 1, 1 , Optional.empty() , Optional.empty() , Optional.empty() , url , Optional.empty() , false);
    cc.add(經度 , 4 , 1);
    
    ColorCanvas 緯度 = new ColorCanvas(1, 20 , "　");
    StringBuilder latText = new StringBuilder();
    latText.append(location.getNorthSouth() == Location.NorthSouth.NORTH ? "北" : "南");
    latText.append("緯：");
    latText.append(AlignUtil.alignRight(location.getLatDeg(),2));
    latText.append("度");
    latText.append(AlignUtil.alignRight(location.getLatMin(),2));
    latText.append("分");
    latText.append(AlignUtil.alignRight(location.getLatSec(),4));
    latText.append("秒");
    緯度.setText(latText.toString(), 1, 1 , Optional.empty() , Optional.empty() , Optional.empty() , url , Optional.empty() , false);
    cc.add(緯度 , 4 , 25);
    
    cc.setText("換日："+ (context.isChangeDayAfterZi() ? "子初換日" : "子正換日"), 5, 1 , "999999" );
    //如果是南半球，則添加南半球月支是否對沖
    if (location.getNorthSouth() == Location.NorthSouth.SOUTH)
    {
      YearMonthIF yearMonthImpl = context.getYearMonthImpl();
      if (yearMonthImpl instanceof YearMonthSolarTermsStarPositionImpl)
      {
        YearMonthSolarTermsStarPositionImpl impl = (YearMonthSolarTermsStarPositionImpl) yearMonthImpl;
        cc.setText("南半球" , 5 , 35 , "FF0000");
        cc.setText(      "月令："+ (impl.isSouthernHemisphereOpposition() ? "對沖" : "不對沖"), 5, 41 , "999999");
      }
    }
    
    cc.setText("日光節約：" , 5 , 19 , "999999");
    boolean isDst = TimeTools.getDstSecondOffset(lmt, location).v1();
    String dstString= isDst ? "有" : "無";
    cc.setText(dstString , 5 , 29 , (isDst ? "FF0000" : "999999") , "" , null);
      
    cc.setText("子正是："+ context.getMidnightImpl().getTitle(Locale.TRADITIONAL_CHINESE) , 6 , 1 , "999999" , null , context.getMidnightImpl().getDescription(Locale.TRADITIONAL_CHINESE));
    cc.setText("時辰劃分：" + context.getHourImpl().getTitle(Locale.TRADITIONAL_CHINESE), 7, 1, "999999", null, context.getHourImpl().getDescription(Locale.TRADITIONAL_CHINESE));



    // 命宮
    int risingLine = 8;
    StemBranch 命宮 = context.getRisingStemBranch();
    cc.setText("命宮：", risingLine , 1 , "999999" , null , "命宮");
    cc.setText(命宮.toString() , risingLine , 7 , "FF0000" , null , 命宮.toString());
    cc.setText("（"+context.getRisingSignImpl().getTitle(Locale.TAIWAN)+"）" , risingLine , 11 , "999999");

    int linkLine = 9;
    if (linkUrl != null)
    {
      cc.setText("命盤連結  ", linkLine, 1 , "999999");
      //網址長度可能是奇數
      if (linkUrl.length() % 2 == 1)
        linkUrl = linkUrl + ' ';
      cc.setText(linkUrl, linkLine, 11 , Optional.of("999999") , Optional.empty() , Optional.empty() , linkUrl, Optional.empty() , false);
    }
    //EightWords eightWords = context.getEightWords(lmt , location);
    //cc.setText("四字斷終生：" + fourWordsImpl.getResult(eightWords), 8, 1 , "#0000FF" , "#FFFF00" , fourWordsImpl.getResult(eightWords));
    return cc;
  }
  
  /**
   * 取得八字彩色盤 (不含「人」的資料)
<pre>
　時　　　　日　　　　月　　　　年　　　　　　　　　
　柱　　　　柱　　　　柱　　　　柱　　　　　　　　　
　：　　　　：　　　　：　　　　：　　　　　　　　　
　比　　　　　　　　　食　　　　傷　　　　　　　　　
　肩　　　　　　　　　神　　　　官　　　　　　　　　
　癸　　　　癸　　　　乙　　　　甲　　　　　　　　　
　亥　　　　卯　　　　亥　　　　午　　　　　　　　　
　甲壬　　　　乙　　　甲壬　　　己丁　　　　　　　　
　傷劫　　　　食　　　傷劫　　　七偏　　　　　　　　
　官財　　　　神　　　官財　　　殺財　　　　　　　
</pre>
   */
  protected ColorCanvas getEightWordsColorCanvas() {
    EightWords eightWords = context.getEightWords();
    ColorCanvas 八字 = new ColorCanvas(10, 36, "　", Optional.empty(), Optional.empty());

    List<ColorCanvas> pillars = new ArrayList<>();
    pillars.add(getOnePillar(eightWords.getYear()  , "年" , eightWords.getDayStem()));
    pillars.add(getOnePillar(eightWords.getMonth() , "月" , eightWords.getDayStem()));
    pillars.add(getOnePillar(eightWords.getDay()   , "日" , eightWords.getDayStem()));
    pillars.add(getOnePillar(eightWords.getHour()  , "時" , eightWords.getDayStem()));

    if (direction == Direction.R2L)
      Collections.reverse(pillars);

    for (int i=1 ; i<=4 ; i++) {
      八字.add(pillars.get(i-1) , 1 ,  (i-1)*10+1 );
    }

    return 八字;    
  }

  /** 取得「一柱」的 ColorCanvas , 10 x 6
<pre>
　時　
　柱　
　：　
　比　
　肩　
　癸　
　亥　
　甲壬
　傷劫
　官財
</pre>
   * @param stemBranch
   * @param pillarName "年" or "月" or "日" or "時"
   */
  private ColorCanvas getOnePillar(StemBranch stemBranch , String pillarName , Stem dayStem) {
    ColorCanvas pillar = new ColorCanvas(10, 6, "　", Optional.empty(), Optional.empty());
    pillar.setText(pillarName , 1 , 3);
    pillar.setText("柱", 2, 3);
    pillar.setText("：", 3, 3);

    pillar.setText(stemBranch.getStem()  .toString(), 6, 3, "red", null, stemBranch.getStem().toString() + pillarName);
    pillar.setText(stemBranch.getBranch().toString(), 7, 3, "red", null, stemBranch.toString() + pillarName);

    if (!"日".equals(pillarName)) {
      String 干對日主 = reactionsUtil.getReaction(stemBranch.getStem(), dayStem).toString();
      pillar.setText(干對日主.substring(0, 1), 4, 3, "gray");
      pillar.setText(干對日主.substring(1, 2), 5, 3, "gray");
    }
    pillar.add(地支藏干(stemBranch.getBranch(), dayStem), 8, 1);
    return pillar;
  }

  /**
   * 傳回八字命盤
   */
  @NotNull
  @Override
  public String toString()
  {
    ColorCanvas cc = new ColorCanvas(20,52,"　");
    cc.add(getMetaDataColorCanvas() , 1 , 1);
    cc.add(getEightWordsColorCanvas() , 11 , 1);
    switch(this.outputMode)
    {
      case TEXT:
        return cc.toString();
      case HTML:
        return cc.getHtmlOutput();
      default :
        return cc.getHtmlOutput();
    }
  } //toString()
  
  
  @NotNull
  protected ColorCanvas 地支藏干(Branch 地支 , @NotNull Stem 天干)
  {
    ReactionsUtil reactionsUtil = new ReactionsUtil(this.hiddenStemsImpl);
    ColorCanvas resultCanvas = new ColorCanvas(3, 6 , "　");
    List<Reactions> reactions = reactionsUtil.getReactions(地支 , 天干);
    for (int i=0 ; i<reactions.size() ; i++)
    {
      Reactions eachReaction = reactions.get(i);
      resultCanvas.setText(ReactionsUtil.getHeavenlyStems(天干,eachReaction).toString() , 1 , 5-2*i , "gray"); // 天干
      resultCanvas.setText(eachReaction.toString().substring(0 , 1) , 2 , 5-2*i , "gray");
      resultCanvas.setText(eachReaction.toString().substring(1 , 2) , 3 , 5-2*i , "gray");

    }
    return resultCanvas;
  }

  public OutputMode getOutputMode()
  {
    return outputMode;
  }

  public void setOutputMode(OutputMode outputMode)
  {
    this.outputMode = outputMode;
  }


}
