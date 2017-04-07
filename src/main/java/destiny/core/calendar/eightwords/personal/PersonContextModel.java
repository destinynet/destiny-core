/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.Time;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.EightWordsContext;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.chrono.IsoEra;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoField.YEAR_OF_ERA;

/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），方便未來 View 端直接存取。不用在 View 端計算。
 */
public class PersonContextModel implements Serializable {

  private final PersonContext personContext;

  /** 農曆 */
  private final ChineseDate chineseDate;

  private String locationName = "";

  private final int gmtMinuteOffset;

  /** 日光節約 */
  private final boolean dst;

  /** 輸出大運的模式 */
  public enum FortuneOutputFormat {西元 , 民國 , 實歲 , 虛歲}

  /** 總共要輸出的大運 */
  private final List<FortuneData> fortuneDatas = new ArrayList<>();

  /** 上一個「節」 */
  private final SolarTerms prevMajorSolarTerms;

  /** 下一個「節」 */
  private final SolarTerms nextMajorSolarTerms;

  /** 命宮 */
  private final StemBranch risingStemBranch;

  public PersonContextModel(PersonContext context, int fortunes, FortuneOutputFormat fortuneOutputFormat, String locationName) {
    this.personContext = context;

    this.chineseDate = context.getChineseDate(context.getLmt() , context.getLocation());
    this.locationName = locationName;
    this.dst = Time.getDstSecondOffset(context.getLmt(), context.getLocation()).v1();

    gmtMinuteOffset = (Time.getDstSecondOffset(context.getLmt(), context.getLocation()).v2() / 60);

    // 首先取得到下/上個節氣的秒數
    double fortuneMonthSpan = personContext.getFortuneMonthSpan();

    // forward : 大運是否順行
    boolean isForward = personContext.isFortuneDirectionForward();

    EightWords eightWords = personContext.getEightWords();


    // 命宮干支
    risingStemBranch = context.getRisingStemBranch(context.getLmt(), context.getLocation());

    //下個大運的干支
    StemBranch nextStemBranch = isForward ? eightWords.getMonth().getNext() : eightWords.getMonth().getPrevious();

    // 前一個大運，開始的歲數
    int prevStart = 0;
    // 前一個大運，結束的歲數
    int prevEnd = 0;

    // 計算九柱大運的相關資訊
    for (int i=1 ; i<=fortunes ; i++) {
      // 西元/民國/實歲/虛歲之值
      int startFortune;
      int endFortune;
      double startFortuneSeconds = personContext.getTargetMajorSolarTermsSeconds(  i  * (isForward ? 1 : -1));
      double   endFortuneSeconds = personContext.getTargetMajorSolarTermsSeconds((i+1)* (isForward ? 1 : -1));

      Tuple2<Long , Long> pair1 = Time.splitSecond(Math.abs(startFortuneSeconds) * fortuneMonthSpan);
      LocalDateTime startFortuneLmt = LocalDateTime.from(personContext.getLmt()).plusSeconds(pair1.v1()).plusNanos(pair1.v2());
      Tuple2<Long , Long> pair2 = Time.splitSecond(Math.abs(endFortuneSeconds)   * fortuneMonthSpan);
      LocalDateTime endFortuneLmt  = LocalDateTime.from(personContext.getLmt()).plusSeconds(pair2.v1()).plusNanos(pair2.v2());

      switch(fortuneOutputFormat)
      {
        case 西元 : {
          startFortune = startFortuneLmt.get(YEAR_OF_ERA);
          if (startFortuneLmt.toLocalDate().getEra() == IsoEra.BCE) // 西元前
            startFortune= 0-startFortune;
          endFortune = endFortuneLmt.get(YEAR_OF_ERA);
          if (endFortuneLmt.toLocalDate().getEra() == IsoEra.BCE) // 西元前
            endFortune = 0-endFortune;
          break;
        }
        case 民國 : {
          int year; //normalized 的 年份 , 有零 , 有負數
          year = startFortuneLmt.get(YEAR_OF_ERA);
          if (startFortuneLmt.toLocalDate().getEra() == IsoEra.BCE) //西元前
            year = -(year-1);
          startFortune = year-1911;
          year = endFortuneLmt.get(YEAR_OF_ERA);
          if (endFortuneLmt.toLocalDate().getEra() == IsoEra.BCE) //西元前
            year = -(year-1);
          endFortune = year-1911;
          break;
        }
        case 實歲 : {
          startFortune = (int) (Math.abs(startFortuneSeconds) * fortuneMonthSpan / (365.2563*24*60*60)) ;
          endFortune   = (int) (Math.abs(endFortuneSeconds)   * fortuneMonthSpan / (365.2563*24*60*60)) ;
          break;
        }
        default : {
          //虛歲
          // 取得 起運/終運 時的八字
          EightWordsContext eightWordsContext = new EightWordsContext(context.getChineseDateImpl() , personContext.getYearMonthImpl() ,
              personContext.getDayImpl() , personContext.getHourImpl() ,
              personContext.getMidnightImpl() , personContext.isChangeDayAfterZi(), context.getRisingSignImpl());

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
        }
      }

      FortuneData fortuneData = new FortuneData(nextStemBranch , startFortuneLmt , endFortuneLmt, startFortune , endFortune);
      fortuneDatas.add(fortuneData);

      nextStemBranch = isForward ? nextStemBranch.getNext() : nextStemBranch.getPrevious();
    } // for 1 ~ fortunes)

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

  } // constructor


  public PersonContext getPersonContext() {
    return personContext;
  }

  public ChineseDate getChineseDate() {
    return chineseDate;
  }

  public int getGmtMinuteOffset() {
    return gmtMinuteOffset;
  }

  public List<FortuneData> getFortuneDatas() {
    return Collections.unmodifiableList(fortuneDatas);
  }

  public SolarTerms getPrevMajorSolarTerms() {
    return prevMajorSolarTerms;
  }

  public SolarTerms getNextMajorSolarTerms() {
    return nextMajorSolarTerms;
  }

  public String getLocationName() {
    return locationName;
  }

  /** 取得命宮 */
  public StemBranch getRisingStemBranch() {
    return risingStemBranch;
  }


  public boolean isDst() {
    return dst;
  }
}
