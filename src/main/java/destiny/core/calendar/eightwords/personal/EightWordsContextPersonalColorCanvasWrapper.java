/**
 * @author smallufo
 * @date 2005/4/4
 * @time 下午 02:58:28
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.Time;
import destiny.core.calendar.TimeDecoratorChinese;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.EightWordsContext;
import destiny.core.calendar.eightwords.EightWordsContextColorCanvasWrapper;
import destiny.core.calendar.eightwords.fourwords.FourWordsIF;
import destiny.core.calendar.eightwords.fourwords.FourWordsImpl;
import destiny.core.chinese.StemBranch;
import destiny.utils.Decorator;
import destiny.utils.ColorCanvas.AlignUtil;
import destiny.utils.ColorCanvas.ColorCanvas;


/**
 * 繪製個人八字彩色命盤
 */
public class EightWordsContextPersonalColorCanvasWrapper extends EightWordsContextColorCanvasWrapper
{
  /** 八字 Context */
  private EightWordsPersonContext personContext;
  
  /** 地支藏干的實作，內定採用標準設定 */
  private HiddenStemsIF hiddenStemsImpl;
  
  private ColorCanvas cc;

  public enum OutputMode {HTML , TEXT};
  private OutputMode outputMode = OutputMode.HTML;
  
  /** 輸出大運的模式 */
  public enum FortuneOutputFormat {西元 , 民國 , 實歲 , 虛歲};
  private FortuneOutputFormat fortuneOutputFormat = FortuneOutputFormat.虛歲;
  
  private final Decorator<Time> timeDecorator = new TimeDecoratorChinese();
  
  /** 四字斷終生 */
  private static FourWordsIF  fourWordsImpl = new FourWordsImpl();

  public EightWordsContextPersonalColorCanvasWrapper(EightWordsPersonContext personContext , String locationName , HiddenStemsIF hiddenStemsImpl , String linkUrl)
  {
    super(personContext , personContext.getLmt() , personContext.getLocation() , locationName , hiddenStemsImpl , linkUrl , fourWordsImpl);
    this.personContext = personContext;
    this.hiddenStemsImpl = hiddenStemsImpl;
    //this.timeDecorator = new TimeDecoratorChinese();
  }

  public void setOutputMode(OutputMode mode)
  {
    this.outputMode = mode;
  }

  /** 取得八字命盤 */
  @Override
  public String toString()
  {
    cc = new ColorCanvas(30,70,"　");

    ColorCanvas metaDataColorCanvas = getMetaDataColorCanvas();

    cc.add(metaDataColorCanvas , 1 , 1); // 國曆 農曆 經度 緯度 短網址 等 MetaData

    cc.setText("性別：" , 1,59);
    cc.setText(personContext.getGender().toString() , 1, 65); // '男' or '女'
    cc.setText("性" , 1 , 67);

    cc.setText("八字：" , 9 , 1);

    EightWords eightWords = personContext.getEightWords();

    ReactionsUtil reactionsUtil = new ReactionsUtil(this.hiddenStemsImpl);

    cc.add(getEightWordsColorCanvas() , 9 , 9); // 純粹八字盤


    ColorCanvas 大運直 = new ColorCanvas(9,24,"　" );
    ColorCanvas 大運橫 = new ColorCanvas(8,70,"　" , null , null);

    //forward : 大運是否順行
    boolean isForward = personContext.isFortuneDirectionForward() ? true : false;

    //首先取得到下/上個節氣的秒數
    //double toMajorSolarTermsSeconds = context.getTargetMajorSolarTermsSeconds( isForward == true ? 1 : -1 );
    //接著取得月的 span 倍數
    double fortuneMonthSpan = personContext.getFortuneMonthSpan();


    //下個大運的干支
    StemBranch nextStemBranch;
    if (isForward)
      nextStemBranch = eightWords.getMonth().getNext();
    else
      nextStemBranch = eightWords.getMonth().getPrevious();

    // 前一個大運，開始的歲數
    int prevStart = 0;
    // 前一個大運，結束的歲數
    int prevEnd = 0;
    
    for (int i=1 ; i <= 9 ; i++)
    {
      int startFortune;
      int endFortune;
      double startFortuneSeconds = personContext.getTargetMajorSolarTermsSeconds(  i  * (isForward ? 1 : -1));
      double   endFortuneSeconds = personContext.getTargetMajorSolarTermsSeconds((i+1)* (isForward ? 1 : -1));
      Time startFortuneLmt = new Time(personContext.getLmt() , Math.abs(startFortuneSeconds) * fortuneMonthSpan);
      Time   endFortuneLmt = new Time(personContext.getLmt() , Math.abs(endFortuneSeconds)   * fortuneMonthSpan);
      
      switch(fortuneOutputFormat)
      {
        case 西元 :
        {
          startFortune = startFortuneLmt.getYear();
          if (!startFortuneLmt.isAd())
            startFortune= 0-startFortune;
          endFortune = endFortuneLmt.getYear();
          if (!endFortuneLmt.isAd())
            endFortune = 0-endFortune;
          break;
        }
        case 民國 :
        {
          int year; //normalized 的 年份 , 有零 , 有負數
          year = startFortuneLmt.getYear();
          if (!startFortuneLmt.isAd()) //西元前
            year = -(year-1);
          startFortune = year-1911;
          year = endFortuneLmt.getYear();
          if (!endFortuneLmt.isAd()) //西元前
            year = -(year-1);
          endFortune = year-1911;
          break;
        }
        case 實歲 :
        {
          startFortune = (int) (Math.abs(startFortuneSeconds) * fortuneMonthSpan / (365.2563*24*60*60)) ;
          endFortune   = (int) (Math.abs(endFortuneSeconds)   * fortuneMonthSpan / (365.2563*24*60*60)) ;
          break;
        }
        default : //虛歲
        {
          // 取得 起運/終運 時的八字
          EightWordsContext eightWordsContext = new EightWordsContext(personContext.getYearMonthImpl() , 
              personContext.getDayImpl() , personContext.getHourImpl() , 
              personContext.getMidnightImpl() , personContext.isChangeDayAfterZi());
          
          EightWords startFortune8w = eightWordsContext.getEightWords(startFortuneLmt, personContext.getLocation());
          EightWords endFortune8w   = eightWordsContext.getEightWords(endFortuneLmt, personContext.getLocation());

          // 計算年干與本命年干的距離
          startFortune = startFortune8w.getYear().differs(eightWords.getYear())+1;
          //System.out.println("differs result , startFortune = " + startFortune + " , prevStart = " + prevStart);
          while (startFortune < prevStart)
            startFortune +=60;
          prevStart = startFortune;
          //System.out.println(startFortune8w.getYear()+"["+startFortune8w.getYear().getIndex()+"] to " + eightWords.getYear()+"["+eightWords.getYear().getIndex()+"] is "+ startFortune);
          
          endFortune = endFortune8w.getYear().differs(eightWords.getYear())+1;
          while (endFortune < prevEnd)
            endFortune += 60;
          prevEnd = endFortune;

          //startFortune = (int) (Math.abs(startFortuneSeconds) * fortuneMonthSpan / (365.2563*24*60*60)) +1;
          //endFortune   = (int) (Math.abs(endFortuneSeconds)   * fortuneMonthSpan / (365.2563*24*60*60)) +1;
        }
      }

      大運直.setText(AlignUtil.alignRight(startFortune , 6 ) , i , 1 , "green" , null , "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt));
      大運直.setText("→" , i , 9 , "green" );
      大運直.setText(AlignUtil.alignRight(endFortune , 6) , i , 13 , "green" , null , "終運時刻：" + timeDecorator.getOutputString(endFortuneLmt));
      大運直.setText(nextStemBranch.toString() , i , 21 , "green");

      大運橫.setText(AlignUtil.alignCenter(startFortune , 6) , 1 , 73-8*i , "green" , null , "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt));
      Reactions reaction = reactionsUtil.getReaction(nextStemBranch.getStem() , eightWords.getDay().getStem());
      大運橫.setText(reaction.toString().substring(0,1) , 2 , 75-8*i , "gray");
      大運橫.setText(reaction.toString().substring(1,2) , 3 , 75-8*i , "gray");
      大運橫.setText(nextStemBranch.getStem()  .toString() , 4 , 75-8*i , "red");
      大運橫.setText(nextStemBranch.getBranch().toString() , 5 , 75-8*i , "red");
      大運橫.add(地支藏干(nextStemBranch.getBranch() , eightWords.getDay().getStem()) , 6 , 73-8*i);

      if (isForward)
        nextStemBranch = nextStemBranch.getNext();
      else
        nextStemBranch = nextStemBranch.getPrevious();
    }


    cc.setText("大運（"+fortuneOutputFormat +"）", 8, 55);
    cc.add(大運直 , 9, 47);
    cc.add(大運橫 , 20 , 1);

    ColorCanvas 節氣 = new ColorCanvas(2 , cc.getWidth() ,  "　");
    SolarTerms prevMajorSolarTerms ;
    SolarTerms nextMajorSolarTerms ;
    SolarTerms currentSolarTerms = personContext.getCurrentSolarTerms();
    int currentSolarTermsIndex = SolarTerms.getIndex(currentSolarTerms);
    if (currentSolarTermsIndex % 2 == 0)  //立春 , 驚蟄 , 清明 ...
    {
      prevMajorSolarTerms = currentSolarTerms;
      nextMajorSolarTerms = currentSolarTerms.next().next();
    }
    else
    {
      prevMajorSolarTerms = currentSolarTerms.previous();
      nextMajorSolarTerms = currentSolarTerms.next();
    }

    Time prevMajorSolarTermsTime = new Time(personContext.getLmt() , personContext.getTargetMajorSolarTermsSeconds(-1) );
    節氣.setText(prevMajorSolarTerms.toString() , 1 , 1);
    節氣.setText("：" , 1, 5);
    節氣.setText(this.timeDecorator.getOutputString(prevMajorSolarTermsTime) , 1,7);

    Time nextMajorSolarTermsTime = new Time(personContext.getLmt() , personContext.getTargetMajorSolarTermsSeconds(1) );
    節氣.setText(nextMajorSolarTerms.toString() , 2 , 1);
    節氣.setText("：" , 2, 5);
    節氣.setText(this.timeDecorator.getOutputString(nextMajorSolarTermsTime) , 2,7);

    cc.add(節氣 , 29 , 1);

    switch(this.outputMode)
    {
      case TEXT:
        return cc.toString();
      case HTML:
        return cc.getHtmlOutput();
      default:
        return cc.getHtmlOutput();
    }
  } // toString()
  

  

  /**
   * 設定大運輸出的格式
   */
  public void setFortuneOutputFormat(FortuneOutputFormat fortuneOutputFormat)
  {
    this.fortuneOutputFormat = fortuneOutputFormat;
  }


}
