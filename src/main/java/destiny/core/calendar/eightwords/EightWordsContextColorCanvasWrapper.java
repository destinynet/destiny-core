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
import destiny.utils.ColorCanvas.AlignUtil;
import destiny.utils.ColorCanvas.ColorCanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * 純粹繪製『八字盤』，不包含『人』的因素（大運流年等）
 */
public class EightWordsContextColorCanvasWrapper
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
  
  public EightWordsContextColorCanvasWrapper(EightWordsContext context, Time lmt, Location location , String locationName , 
      HiddenStemsIF hiddenStemsImpl , String linkUrl)
  {
    this.context = context;
    this.lmt = lmt;
    this.location = location;
    this.locationName = locationName;
    this.hiddenStemsImpl = hiddenStemsImpl;
    this.linkUrl = linkUrl;
    // this.fourWordsImpl = fourWordsImpl;
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
    ColorCanvas cc = new ColorCanvas(8,44,"　");
    
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
    地點名稱.setText(locationName , 1 , 7 , null , null , null , url , null , false);
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
    經度.setText(lonText.toString(), 1, 1 , null , null , null , url , null , false);
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
    緯度.setText(latText.toString(), 1, 1 , null , null , null , url , null , false);
    cc.add(緯度 , 3 , 25);
    
    cc.setText("換日："+ (context.isChangeDayAfterZi() ? "子初換日" : "子正換日"), 4, 1 , "999999" );
    //如果是南半球，則添加南半球月支是否對沖
    if (location.getNorthSouth() == Location.NorthSouth.SOUTH)
    {
      YearMonthIF yearMonthImpl = context.getYearMonthImpl();
      if (yearMonthImpl instanceof YearMonthSolarTermsStarPositionImpl)
      {
        YearMonthSolarTermsStarPositionImpl impl = (YearMonthSolarTermsStarPositionImpl) yearMonthImpl;
        cc.setText("南半球月令："+ (impl.isSouthernHemisphereOpposition() ? "對沖" : "不對沖"), 4, 25 , "999999");
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
        cc.setText(linkUrl, 7, 11 , "999999" , null , null , new URL(linkUrl) , null , false);
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
   */
  @Nullable
  protected ColorCanvas getEightWordsColorCanvas()
  {
    EightWords eightWords = context.getEightWords(lmt, location);
    ColorCanvas 八字 = new ColorCanvas(10, 36, "　", null, null);

    八字.setText("時", 1, 3);
    八字.setText("柱", 2, 3);
    八字.setText("：", 3, 3);
    八字.setText("日", 1, 13);
    八字.setText("柱", 2, 13);
    八字.setText("：", 3, 13);
    八字.setText("月", 1, 23);
    八字.setText("柱", 2, 23);
    八字.setText("：", 3, 23);
    八字.setText("年", 1, 33);
    八字.setText("柱", 2, 33);
    八字.setText("：", 3, 33);

    ReactionsUtil reactionsUtil = new ReactionsUtil(this.hiddenStemsImpl);

    八字.setText(eightWords.getHour().getStem().toString(), 6, 3, "red", null, eightWords.getHour().getStem().toString() + "時");
    八字.setText(eightWords.getHour().getBranch().toString(), 7, 3, "red", null, eightWords.getHour().toString() + "時");

    String 時干對日主 = reactionsUtil.getReaction(eightWords.getHour().getStem(), eightWords.getDay().getStem()).toString();
    八字.setText(時干對日主.substring(0, 1), 4, 3, "gray");
    八字.setText(時干對日主.substring(1, 2), 5, 3, "gray");

    // 日干支
    八字.setText(eightWords.getDay().getStem().toString(), 6, 13, "red", null, eightWords.getDay().getStem().toString() + "日");
    八字.setText(eightWords.getDay().getBranch().toString(), 7, 13, "red", null, eightWords.getDay().toString() + "日");

    // 月干支
    八字.setText(eightWords.getMonth().getStem().toString(), 6, 23, "red", null, eightWords.getMonth().getStem().toString() + "月");
    八字.setText(eightWords.getMonth().getBranch().toString(), 7, 23, "red", null, eightWords.getMonth().toString() + "月");

    String 月干對日主 = reactionsUtil.getReaction(eightWords.getMonth().getStem(), eightWords.getDay().getStem()).toString();
    八字.setText(月干對日主.substring(0, 1), 4, 23, "gray");
    八字.setText(月干對日主.substring(1, 2), 5, 23, "gray");

    // 年干支
    八字.setText(eightWords.getYear().getStem().toString(), 6, 33, "red", null, eightWords.getYear().getStem().toString() + "年");
    八字.setText(eightWords.getYear().getBranch().toString(), 7, 33, "red", null, eightWords.getYear().toString() + "年");

    String 年干對日主 = reactionsUtil.getReaction(eightWords.getYear().getStem(), eightWords.getDay().getStem()).toString();
    八字.setText(年干對日主.substring(0, 1), 4, 33, "gray");
    八字.setText(年干對日主.substring(1, 2), 5, 33, "gray");

    八字.add(地支藏干(eightWords.getHour().getBranch(), eightWords.getDay().getStem()), 8, 1);
    八字.add(地支藏干(eightWords.getDay().getBranch(), eightWords.getDay().getStem()), 8, 11);
    八字.add(地支藏干(eightWords.getMonth().getBranch(), eightWords.getDay().getStem()), 8, 21);
    八字.add(地支藏干(eightWords.getYear().getBranch(), eightWords.getDay().getStem()), 8, 31);

    return 八字;    
  }

  /**
   * 傳回八字命盤
   */
  @NotNull
  @Override
  public String toString()
  {
    ColorCanvas cc = new ColorCanvas(19,44,"　");
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
