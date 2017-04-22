/**
 * Created by smallufo on 2015-04-21.
 */
package destiny.core.calendar.eightwords.personal;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import destiny.astrology.StarPositionIF;
import destiny.astrology.StarTransitIF;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.Time;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.*;
import destiny.core.chinese.StemBranch;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static destiny.astrology.Coordinate.ECLIPTIC;
import static destiny.astrology.Planet.SUN;

public class PersonContext extends EightWordsContext {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /** 實作計算節氣的介面 */
  @NotNull
  private final SolarTermsIF solarTermsImpl;

  /** 星體運行到某點的介面 */
  @NotNull
  private final StarTransitIF starTransitImpl;

  /** 出生時刻 */
  private final LocalDateTime lmt;

  /** 出生地點 */
  private final Location location;

  /** 性別 */
  @NotNull
  private final Gender gender;


  /** 此人的八字 */
  @NotNull
  private final EightWords eightWords;

  /** 現在（LMT）的節/氣 */
  private final SolarTerms currentSolarTerms;

  /** 運 :「月」的 span 倍數，內定 120，即：一個月干支 擴展(乘以)120 倍，變成十年 */
  private double fortuneMonthSpan = 120;

  /** 運：「日」的 span 倍數，內定 365，即：一日走一年 */
  private final double fortuneDaySpan = 365;

  /** 運 :「時辰」的 span 倍數，內定 365x12，即：一時辰走一年 */
  private final double fortuneHourSpan = 365 * 12;

  private final Cache<PersonContext, Map<Integer, LocalDateTime>> cache =
    CacheBuilder.newBuilder()
      .maximumSize(100)
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .build();

  /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法 */
  private FortuneDirectionIF fortuneDirectionImpl = new FortuneDirectionDefaultImpl();



  /** constructor */
  public PersonContext(ChineseDateIF chineseDateImpl, YearMonthIF yearMonth, DayIF dayImpl, HourIF hourImpl, MidnightIF midnight, boolean changeDayAfterZi, @NotNull SolarTermsIF solarTermsImpl, @NotNull StarTransitIF starTransitImpl, LocalDateTime lmt, Location location, @NotNull Gender gender, double fortuneMonthSpan, FortuneDirectionIF fortuneDirectionImpl, RisingSignIF risingSignImpl, StarPositionIF starPositionImpl) {
    super(chineseDateImpl, yearMonth, dayImpl, hourImpl, midnight, changeDayAfterZi, risingSignImpl, starPositionImpl);
    this.solarTermsImpl = solarTermsImpl;
    this.starTransitImpl = starTransitImpl;
    this.fortuneMonthSpan = fortuneMonthSpan;
    this.fortuneDirectionImpl = fortuneDirectionImpl;

    // LMT 的八字
    this.lmt = lmt;
    this.location = location;
    this.gender = gender;
    this.eightWords = this.getEightWords(lmt, location);
    //Time gmt = Time.getGMTfromLMT(lmt, location);
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    this.currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
  }


  /** 取得出生時刻 */
  @NotNull
  public LocalDateTime getLmt() {
    return lmt;
  }

  /** 取得出生地點 */
  @NotNull
  public Location getLocation() {
    return location;
  }

  /** 取得出生時刻的八字 */
  @NotNull
  public EightWords getEightWords() {
    return this.eightWords;
  }

  /** 性別 */
  @NotNull
  public Gender getGender() {
    return gender;
  }

  /** 運 : 日干支的倍數 */
  public double getFortuneDaySpan() {
    return fortuneDaySpan;
  }


  /** 運 : 時干支的倍數 */
  public double getFortuneHourSpan() {
    return fortuneHourSpan;
  }

  /** 大運 : 月干支的倍數 , 內定為 120 */
  public double getFortuneMonthSpan() {
    return fortuneMonthSpan;
  }

  /** 八字大運是否順行 */
  public boolean isFortuneDirectionForward() {
    return this.fortuneDirectionImpl.isForward(this);
  }


  /** 現在的節氣 */
  public SolarTerms getCurrentSolarTerms() {
    return currentSolarTerms;
  }


  /**
   * 計算此時刻，距離上一個「節」有幾秒，距離下一個「節」又有幾秒
   */
  public Pair<Pair<SolarTerms , Double> , Pair<SolarTerms , Double>> getMajorSolarTermsBetween() {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);

    // 現在（亦即：上一個節）的「節」
    SolarTerms prevMajorSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
    if (!prevMajorSolarTerms.isMajor()) // 如果是「中氣」的話
      prevMajorSolarTerms = prevMajorSolarTerms.previous(); // 再往前取一個 , 即可得到「節」

    LocalDateTime prevGmt = starTransitImpl.getNextTransitGmt(SUN , prevMajorSolarTerms.getZodiacDegree() , ECLIPTIC , gmt , false);
    Duration dur1 = Duration.between(gmt, prevGmt).abs();
    double d1 = dur1.getSeconds()+ dur1.getNano() / 1_000_000_000.0;


    // 下一個「節」
    SolarTerms nextMajorSolarTerms = this.getNextMajorSolarTerms(prevMajorSolarTerms, false);
    LocalDateTime nextGmt = starTransitImpl.getNextTransitGmt(SUN , nextMajorSolarTerms.getZodiacDegree(), ECLIPTIC, gmt , true);
    Duration dur2 = Duration.between(gmt, nextGmt).abs();
    double d2 = dur2.getSeconds() + dur2.getNano() / 1_000_000_000.0;

    logger.debug(" prevGmt = {}" , prevGmt);
    logger.debug("(now)Gmt = {}" , gmt);
    logger.debug(" nextGmt = {}" , nextGmt);
    return Pair.of(Pair.of(prevMajorSolarTerms , d1) , Pair.of(nextMajorSolarTerms , d2));
  }

  /**
   * 距離下 N 個「節」有幾秒 , 如果 index 為負，代表計算之前的「節」。 index 不能等於 0
   *
   * @return <b>如果 index 為正，則傳回正值; 如果 index 為負，則傳回負值</b>
   */
  public double getTargetMajorSolarTermsSeconds(int index) {
    if (index == 0)
      throw new RuntimeException("index cannot be 0 !");

    boolean reverse = false;
    if (index < 0)
      reverse = true;

    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    LocalDateTime stepGmt = LocalDateTime.from(gmt).plusSeconds(0);
    //現在的 節氣
    SolarTerms currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
    SolarTerms stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, reverse);

    int i;
    if (!reverse)
      i = 1;
    else
      i = -1;

    Map<Integer , LocalDateTime> hashMap = cache.getIfPresent(this);

    if (hashMap == null) {
      hashMap = new LinkedHashMap<>();
      cache.put(this , hashMap);
    }

    LocalDateTime targetGmt = null ;
    if (hashMap.containsKey(index))
      targetGmt = hashMap.get(index);

    if (targetGmt == null) {
      if (!reverse) {
        //順推
        if (hashMap.get(index - 1) != null)
          i = index - 1;

        while (i <= index) {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推
          if (hashMap.get(i) == null) {
            logger.debug("順推 cache.get({}) miss" , i);
            //沒有計算過
            targetGmt = this.starTransitImpl.getNextTransitGmt(SUN, stepMajorSolarTerms.getZodiacDegree(), ECLIPTIC, stepGmt, true);
            //以隔天計算現在節氣
            stepGmt = LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);

            hashMap.put(i , targetGmt);
            cache.put(this , hashMap);
          }
          else {
            //之前計算過
            logger.debug("順推 cache.get({}) hit" , i);
            targetGmt = hashMap.get(i);
            stepGmt = LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);
          }

          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmt);
          stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, false);
          i++;
        } // while (i <= index)
      } //順推
      else {
        //逆推
        if (hashMap.get(index + 1) != null)
          i = index + 1;

        while (i >= index) {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推

          if (hashMap.get(i) == null) {
            //沒有計算過

            targetGmt = this.starTransitImpl.getNextTransitGmt(SUN, stepMajorSolarTerms.getZodiacDegree(), ECLIPTIC, stepGmt, false);
            //以前一天計算現在節氣
            stepGmt = LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
            hashMap.put(i , targetGmt);
            cache.put(this , hashMap);
          }
          else {
            //之前計算過
            targetGmt = hashMap.get(i);
            stepGmt = LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
          }

          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmt);
          stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, true);
          i--;
        } //while (i >= index)
      } //逆推
    }

    Duration dur = Duration.between(gmt , targetGmt);
    long diffSecs = dur.getSeconds();
    long diffNano = dur.getNano();
    return diffSecs + diffNano / 1_000_000_000.0;
  } // getTargetMajorSolarTermsSeconds(int)


  /**
   * 取得下一個「節」
   *
   * @param currentSolarTerms 現在的「節」
   * @param reverse           是否逆推
   * @return 下一個「節」（如果 reverse == true，則傳回上一個「節」）
   */
  @NotNull
  private SolarTerms getNextMajorSolarTerms(@NotNull SolarTerms currentSolarTerms, boolean reverse) {
    int currentSolarTermsIndex = SolarTerms.getIndex(currentSolarTerms);
    if (currentSolarTermsIndex % 2 == 0)  //立春 , 驚蟄 , 清明 ...
    {
      if (!reverse) //順推
        return currentSolarTerms.next().next();
      else
        return currentSolarTerms;
    }
    else //雨水 , 春分 , 穀雨 ...
    {
      if (!reverse) //順推
        return currentSolarTerms.next();
      else
        return currentSolarTerms.previous();
    }
  } // getNextMajorSolarTerms()


  /**
   * 由 GMT 反推月大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 月大運干支
   */
  public StemBranch getStemBranchOfFortuneMonth(LocalDateTime targetGmt) {
    return this.getStemBranchOfFortune(targetGmt, this.fortuneMonthSpan);
  }

  /**
   * 由 GMT 反推日大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 日大運干支
   */
  public StemBranch getStemBranchOfFortuneDay(LocalDateTime targetGmt) {
    return this.getStemBranchOfFortune(targetGmt, this.fortuneDaySpan);
  }

  /**
   * 由 GMT 反推時大運
   *
   * @param targetGmt 目標時刻 (in GMT)
   * @return 時大運干支
   */
  public StemBranch getStemBranchOfFortuneHour(LocalDateTime targetGmt) {
    return this.getStemBranchOfFortune(targetGmt, this.fortuneHourSpan);
  }

  /**
   * 由 GMT 反推月大運，private method
   *
   * @param targetGmt 目標時刻（必須在初生時刻之後）
   * @param span      放大倍數
   * @return 干支
   */
  private StemBranch getStemBranchOfFortune(LocalDateTime targetGmt, double span) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt, location);
    StemBranch resultStemBranch = this.getEightWords().getMonth();

    Duration dur = Duration.between(targetGmt , gmt).abs();
    double diffSeconds = dur.getSeconds() + dur.getNano() / 1_000_000_000.0;

    if (targetGmt.isAfter(gmt) && isFortuneDirectionForward()) {
      logger.debug("大運順行");
      int index = 1;
      while (getTargetMajorSolarTermsSeconds(index) * span < diffSeconds) {
        resultStemBranch = resultStemBranch.getNext();
        index++;
      }
      return resultStemBranch;
    }
    if (targetGmt.isAfter(gmt) && !isFortuneDirectionForward()) {
      logger.debug("大運逆行");
      int index = -1;
      while (Math.abs(getTargetMajorSolarTermsSeconds(index) * span) < diffSeconds) {
        resultStemBranch = resultStemBranch.getPrevious();
        index--;
      }
      return resultStemBranch;
    }
    else
      throw new RuntimeException("Error while getStemBranchOfFortune(" + targetGmt + ")");
  }


  @NotNull
  @Override
  public String toString() {
    return "EightWordsPersonContext [gender=" + gender + ", lmt=" + lmt + ", location=" + location + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PersonContext))
      return false;

    PersonContext that = (PersonContext) o;

    if (Double.compare(that.fortuneMonthSpan, fortuneMonthSpan) != 0)
      return false;
    if (Double.compare(that.fortuneDaySpan, fortuneDaySpan) != 0)
      return false;
    if (Double.compare(that.fortuneHourSpan, fortuneHourSpan) != 0)
      return false;
    if (!solarTermsImpl.equals(that.solarTermsImpl))
      return false;
    if (!starTransitImpl.equals(that.starTransitImpl))
      return false;
    if (!lmt.equals(that.lmt))
      return false;
    if (!location.equals(that.location))
      return false;
    if (gender != that.gender)
      return false;
    if (!eightWords.equals(that.eightWords))
      return false;
    if (currentSolarTerms != that.currentSolarTerms)
      return false;
    return !(fortuneDirectionImpl != null ? !fortuneDirectionImpl.equals(that.fortuneDirectionImpl) : that.fortuneDirectionImpl != null);

  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = solarTermsImpl.hashCode();
    result = 31 * result + starTransitImpl.hashCode();
    result = 31 * result + lmt.hashCode();
    result = 31 * result + location.hashCode();
    result = 31 * result + gender.hashCode();
    result = 31 * result + (eightWords.hashCode());
    result = 31 * result + (currentSolarTerms != null ? currentSolarTerms.hashCode() : 0);
    temp = Double.doubleToLongBits(fortuneMonthSpan);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(fortuneDaySpan);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(fortuneHourSpan);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (fortuneDirectionImpl != null ? fortuneDirectionImpl.hashCode() : 0);
    return result;
  }
}
