/**
 * @author smallufo 
 * Created on 2006/5/5 at 下午 05:11:12
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.*;
import destiny.core.calendar.eightwords.personal.HiddenStemsIF;
import destiny.core.calendar.eightwords.personal.HiddenStemsStandardImpl;
import destiny.core.calendar.eightwords.personal.Reactions;
import destiny.core.calendar.eightwords.personal.ReactionsUtil;
import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranch;
import destiny.utils.ColorCanvas.AlignUtil;
import destiny.utils.ColorCanvas.ColorCanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 純粹繪製『八字盤』，不包含『人』的因素（大運流年等）
 */
public class ContextColorCanvasWrapper
{
  /** 八字 Context */
  private EightWordsContext context;
  /** 地支藏干的實作，內定採用標準設定 */
  private HiddenStemsIF     hiddenStemsImpl  = new HiddenStemsStandardImpl();
  /** 當地時間 */
  private Time              lmt              = new Time();
  /** 地點 */
  private Location          location         = new Location();
  /** 地點的名稱 */
  private String locationName = "";
  
  /** TODO : IoC Google Maps URL Builder */
  @NotNull
  private LocationUrlBuilder urlBuilder = new GoogleMapsUrlBuilder();
  
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
  
  public ContextColorCanvasWrapper(EightWordsContext context, Time lmt, Location location, String locationName, HiddenStemsIF hiddenStemsImpl, String linkUrl, Direction direction)
  {
    this.context = context;
    this.lmt = lmt;
    this.location = location;
    this.locationName = locationName;
    this.hiddenStemsImpl = hiddenStemsImpl;
    this.linkUrl = linkUrl;
    // this.fourWordsImpl = fourWordsImpl;
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
    ColorCanvas cc = new ColorCanvas(8,52,"　");
    
    ColorCanvas 西元資訊 = new ColorCanvas(1,36, "　");
    StringBuffer timeData = new StringBuffer();
    timeData.append("西元：");
    if(lmt.isBeforeChrist())
      timeData.append("前");
    else
      timeData.append("　");
      
    timeData.append(AlignUtil.alignRight(lmt.getYear(),4));
    timeData.append("年");
    timeData.append(AlignUtil.alignRight(lmt.getMonth(),2));
    timeData.append("月");
    timeData.append(AlignUtil.alignRight(lmt.getDay(),2));
    timeData.append("日");
    timeData.append(AlignUtil.alignRight(lmt.getHour(),2));
    timeData.append("時");
    timeData.append(AlignUtil.alignRight(lmt.getMinute(),2));
    timeData.append("分");
    timeData.append(AlignUtil.alignRight(lmt.getSecond(),4));
    timeData.append("秒");
    西元資訊.setText(timeData.toString(), 1, 1);
    cc.add(西元資訊 , 1 , 1 );
    
    URL url = urlBuilder.getUrl(location);
    
    ColorCanvas 地點名稱 = new ColorCanvas(1,44, "　");
    地點名稱.setText("地點：", 1 , 1);
    //地點名稱.setText(locationName , 1 , 7);
    地點名稱.setText(locationName , 1 , 7 , Optional.empty() , Optional.empty() , Optional.empty() , Optional.of(url) , Optional.empty() , false);
    int minuteOffset = (int) (DstUtils.getDstSecondOffset(lmt, location).getSecond() / 60);
    地點名稱.setText(" GMT時差："+AlignUtil.alignRight(minuteOffset,6)+"分鐘", 1, 25 , "999999");
    cc.add(地點名稱 , 2 , 1);
    
    
    /*
    ColorCanvas 農曆資訊 = new ColorCanvas(1,34, "　");
    農曆資訊.setText("農曆：" , 1 , 1);
    cc.add(農曆資訊 , 2 , 1);
    */
    
    ColorCanvas 經度 = new ColorCanvas(1, 24 , "　");
    StringBuffer lonText = new StringBuffer();
    lonText.append(location.getEastWest() == Location.EastWest.EAST ? "東" : "西");
    lonText.append("經：");
    lonText.append(AlignUtil.alignRight(location.getLongitudeDegree(),4));
    lonText.append("度");
    lonText.append(AlignUtil.alignRight(location.getLongitudeMinute(),2));
    lonText.append("分");
    lonText.append(AlignUtil.alignRight(location.getLongitudeSecond(),4));
    lonText.append("秒");
    經度.setText(lonText.toString(), 1, 1 , Optional.empty() , Optional.empty() , Optional.empty() , Optional.of(url) , Optional.empty() , false);
    cc.add(經度 , 3 , 1);
    
    ColorCanvas 緯度 = new ColorCanvas(1, 20 , "　");
    StringBuffer latText = new StringBuffer();
    latText.append(location.getNorthSouth() == Location.NorthSouth.NORTH ? "北" : "南");
    latText.append("緯：");
    latText.append(AlignUtil.alignRight(location.getLatitudeDegree(),2));
    latText.append("度");
    latText.append(AlignUtil.alignRight(location.getLatitudeMinute(),2));
    latText.append("分");
    latText.append(AlignUtil.alignRight(location.getLatitudeSecond(),4));
    latText.append("秒");
    緯度.setText(latText.toString(), 1, 1 , Optional.empty() , Optional.empty() , Optional.empty() , Optional.of(url) , Optional.empty() , false);
    cc.add(緯度 , 3 , 25);
    
    cc.setText("換日："+ (context.isChangeDayAfterZi() ? "子初換日" : "子正換日"), 4, 1 , "999999" );
    //如果是南半球，則添加南半球月支是否對沖
    if (location.getNorthSouth() == Location.NorthSouth.SOUTH)
    {
      YearMonthIF yearMonthImpl = context.getYearMonthImpl();
      if (yearMonthImpl instanceof YearMonthSolarTermsStarPositionImpl)
      {
        YearMonthSolarTermsStarPositionImpl impl = (YearMonthSolarTermsStarPositionImpl) yearMonthImpl;
        cc.setText("南半球" , 4 , 35 , "FF0000");
        cc.setText(      "月令："+ (impl.isSouthernHemisphereOpposition() ? "對沖" : "不對沖"), 4, 41 , "999999");
      }
    }
    
    cc.setText("日光節約：" , 4 , 19 , "999999");
    boolean isDst = DstUtils.getDstSecondOffset(lmt, location).getFirst();
    String dstString= isDst ? "有" : "無";
    cc.setText(dstString , 4 , 29 , (isDst ? "FF0000" : "999999") , "" , null);
      
    cc.setText("子正是："+ context.getMidnightImpl().getTitle(Locale.TRADITIONAL_CHINESE) , 5 , 1 , "999999" , null , context.getMidnightImpl().getDescription(Locale.TRADITIONAL_CHINESE));
    cc.setText("時辰劃分：" + context.getHourImpl().getTitle(Locale.TRADITIONAL_CHINESE)  , 6 , 1 , "999999" , null , context.getHourImpl().getDescription(Locale.TRADITIONAL_CHINESE));
    if (linkUrl != null)
    {
      cc.setText("命盤連結  ", 7, 1 , "999999");
      try
      {
        //網址長度可能是奇數
        if (linkUrl.length() % 2 == 1)
          linkUrl = linkUrl + ' ';
        cc.setText(linkUrl, 7, 11 , Optional.of("999999") , Optional.empty() , Optional.empty() , Optional.of(new URL(linkUrl)) , Optional.empty() , false);
      }
      catch (MalformedURLException e)
      {
        e.printStackTrace();
      }  
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
  @Nullable
  protected ColorCanvas getEightWordsColorCanvas()
  {
    EightWords eightWords = context.getEightWords(lmt, location);
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
  private ColorCanvas getOnePillar(StemBranch stemBranch , String pillarName , HeavenlyStems dayStem) {
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
    ColorCanvas cc = new ColorCanvas(19,52,"　");
    cc.add(getMetaDataColorCanvas() , 1 , 1);
    cc.add(getEightWordsColorCanvas() , 10 , 1);
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
  protected ColorCanvas 地支藏干(EarthlyBranches 地支 , @NotNull HeavenlyStems 天干)
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
