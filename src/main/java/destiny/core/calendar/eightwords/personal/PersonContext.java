/**
 * Created by smallufo on 2015-04-21.
 */
package destiny.core.calendar.eightwords.personal;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import destiny.astrology.*;
import destiny.core.Gender;
import destiny.core.IntAge;
import destiny.core.IntAgeNote;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.TimeTools;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.*;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

  /** 歲數實作 */
  @NotNull
  private final IntAge intAgeImpl;

  /** 出生時刻 */
  private final ChronoLocalDateTime lmt;

  /** 出生地點 */
  private final Location location;

  private final String locationName;

  /** 性別 */
  @NotNull
  private final Gender gender;


  /** 現在（LMT）的節/氣 */
  private final SolarTerms currentSolarTerms;

  /** 運 :「月」的 span 倍數，內定 120，即：一個月干支 擴展(乘以)120 倍，變成十年 */
  private double fortuneMonthSpan = 120;

  /** 運：「日」的 span 倍數，內定 365，即：一日走一年 */
  private final double fortuneDaySpan = 365;

  /** 運 :「時辰」的 span 倍數，內定 365x12，即：一時辰走一年 */
  private final double fortuneHourSpan = 365 * 12;

  /** 大運輸出格式 */
  //private final FortuneOutput fortuneOutput;

  private final Cache<PersonContext, Map<Integer, Double>> cache =
    CacheBuilder.newBuilder()
      .maximumSize(100)
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .build();

  /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法 */
  private final FortuneDirectionIF fortuneDirectionImpl;

  /** 歲數註解實作 */
  private final List<IntAgeNote> ageNoteImpls;

  /** constructor */
  public PersonContext(EightWordsIF eightWordsImpl, ChineseDateIF chineseDateImpl, YearMonthIF yearMonthImpl,
                       DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi,
                       @NotNull SolarTermsIF solarTermsImpl, @NotNull StarTransitIF starTransitImpl,
                       @NotNull IntAge intAgeImpl, ChronoLocalDateTime lmt, Location location, String locationName,
                       @NotNull Gender gender, double fortuneMonthSpan, FortuneDirectionIF fortuneDirectionImpl,
                       RisingSignIF risingSignImpl, StarPositionIF starPositionImpl, List<IntAgeNote> ageNoteImpls) {
    super(lmt , location , eightWordsImpl , yearMonthImpl, chineseDateImpl , dayImpl , hourImpl , midnightImpl , changeDayAfterZi , risingSignImpl , starPositionImpl);
    this.solarTermsImpl = solarTermsImpl;
    this.starTransitImpl = starTransitImpl;
    this.intAgeImpl = intAgeImpl;
    this.locationName = locationName;
    this.fortuneMonthSpan = fortuneMonthSpan;
    this.fortuneDirectionImpl = fortuneDirectionImpl;

    // LMT 的八字
    this.lmt = lmt;
    this.location = location;
    this.gender = gender;
    this.ageNoteImpls = ageNoteImpls;
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(lmt , location);
    this.currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
  }


  public PersonContextModel getModel() {
    return new PersonContextModel(gender , eightWords , lmt , location , locationName ,
      getChineseDate() , isDst() ,
      getGmtMinuteOffset() ,
      getFortuneDatas(9) ,
      getRisingStemBranch() ,
      getBranchOf(Planet.SUN) ,
      getBranchOf(Planet.MOON) ,
      getPrevNextMajorSolarTerms(), getAgeMap(90));
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

  public Map<Integer , Tuple2<Double , Double>> getAgeMap(int toAge) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , location);
    return intAgeImpl.getRangesMap(gender , gmtJulDay , location , 1 , toAge);
  }

  /** 八字大運是否順行 */
  public boolean isFortuneDirectionForward() {
    return this.fortuneDirectionImpl.isForward(this);
  }


  /**
   * 計算此時刻，距離上一個「節」有幾秒，距離下一個「節」又有幾秒
   */
  public Tuple2<Tuple2<SolarTerms , Double> , Tuple2<SolarTerms , Double>> getMajorSolarTermsBetween() {
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(lmt , location);
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , location);

    // 現在（亦即：上一個節）的「節」
    SolarTerms prevMajorSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
    if (!prevMajorSolarTerms.isMajor()) // 如果是「中氣」的話
      prevMajorSolarTerms = prevMajorSolarTerms.previous(); // 再往前取一個 , 即可得到「節」

    ChronoLocalDateTime prevGmt = starTransitImpl.getNextTransitGmtDateTime(SUN , prevMajorSolarTerms.getZodiacDegree() , ECLIPTIC , gmtJulDay , false);
    Duration dur1 = Duration.between(gmt, prevGmt).abs();
    double d1 = dur1.getSeconds()+ dur1.getNano() / 1_000_000_000.0;


    // 下一個「節」
    SolarTerms nextMajorSolarTerms = this.getNextMajorSolarTerms(prevMajorSolarTerms, false);
    ChronoLocalDateTime nextGmt = starTransitImpl.getNextTransitGmtDateTime(SUN , nextMajorSolarTerms.getZodiacDegree(), ECLIPTIC, gmtJulDay , true);
    Duration dur2 = Duration.between(gmt, nextGmt).abs();
    double d2 = dur2.getSeconds() + dur2.getNano() / 1_000_000_000.0;

    logger.debug(" prevGmt = {}" , prevGmt);
    logger.debug("(now)Gmt = {}" , gmt);
    logger.debug(" nextGmt = {}" , nextGmt);
    return Tuple.tuple(Tuple.tuple(prevMajorSolarTerms, d1) , Tuple.tuple(nextMajorSolarTerms , d2));
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

    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(lmt , location);
    //double stepGmt = TimeTools.getGmtJulDay(gmt);
    double stepGmtJulDay = TimeTools.getGmtJulDay(lmt , location);
    //現在的 節氣
    SolarTerms currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
    SolarTerms stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, reverse);

    int i;
    if (!reverse)
      i = 1;
    else
      i = -1;

    Map<Integer , Double> hashMap = cache.getIfPresent(this);


    if (hashMap == null) {
      hashMap = new LinkedHashMap<>();
      cache.put(this , hashMap);
    }

    Double targetGmtJulDay = null ;
    if (hashMap.containsKey(index))
      targetGmtJulDay = hashMap.get(index);

    if (targetGmtJulDay == null) {
      if (!reverse) {
        //順推
        if (hashMap.get(index - 1) != null)
          i = index - 1;

        while (i <= index) {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推
          if (hashMap.get(i) == null) {
            logger.debug("順推 cache.get({}) miss" , i);
            //沒有計算過
            targetGmtJulDay = this.starTransitImpl.getNextTransitGmt(SUN, stepMajorSolarTerms.getZodiacDegree(), ECLIPTIC, stepGmtJulDay, true);
            //以隔天計算現在節氣
            stepGmtJulDay = targetGmtJulDay + 1;  //LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);


            hashMap.put(i , targetGmtJulDay);
            cache.put(this , hashMap);
          }
          else {
            //之前計算過
            logger.debug("順推 cache.get({}) hit" , i);
            targetGmtJulDay = hashMap.get(i);
            stepGmtJulDay = targetGmtJulDay + 1;// LocalDateTime.from(targetGmt).plusSeconds(24 * 60 * 60);
          }

          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay);
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

            targetGmtJulDay = this.starTransitImpl.getNextTransitGmt(SUN, stepMajorSolarTerms.getZodiacDegree(), ECLIPTIC, stepGmtJulDay, false);
            //以前一天計算現在節氣
            stepGmtJulDay = targetGmtJulDay - 1; // LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
            hashMap.put(i , targetGmtJulDay);
            cache.put(this , hashMap);
          }
          else {
            //之前計算過
            targetGmtJulDay = hashMap.get(i);
            stepGmtJulDay = targetGmtJulDay - 1; //LocalDateTime.from(targetGmt).minusSeconds(24 * 60 * 60);
          }

          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmtJulDay);
          stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms, true);
          i--;
        } //while (i >= index)
      } //逆推
    }

    assert targetGmtJulDay != null;

    // 同義於 Duration.between(gmt , targetGmtJulDay)
    double durDays = targetGmtJulDay - TimeTools.getGmtJulDay(gmt);
    logger.trace("durDays = {} " , durDays);
    return durDays * 86400;

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
   * @return 在此 gmtJulDay 時刻，座落於歲數的哪一歲當中
   * 可能歲數超出範圍之後，或是根本在出生之前，就會傳回 empty
   */
  private Optional<Integer> getAge(double gmtJulDay , Map<Integer , Tuple2<Double , Double>> ageMap) {
    return ageMap.entrySet().stream().filter(entry -> {
      Tuple2<Double , Double> t2 = entry.getValue();
      return gmtJulDay > t2.v1() && t2.v2() > gmtJulDay;
    }).map(Map.Entry::getKey).findFirst();
  }

  public List<IntAgeNote> getAgeNoteImpls() {
    return ageNoteImpls;
  }

  /**
   * @param count 計算 n柱 大運的資料
   */
  public List<FortuneData> getFortuneDatas(int count) {
    // forward : 大運是否順行
    boolean isForward = isFortuneDirectionForward();

    //下個大運的干支
    StemBranch nextStemBranch = isForward ? eightWords.getMonth().getNext() : eightWords.getMonth().getPrevious();

    List<FortuneData> fortuneDatas = new ArrayList<>(count);

    double gmtJulDay = getGmtJulDay();

    Map<Integer , Tuple2<Double , Double>> ageMap = getAgeMap(120);

    // 計算九柱大運的相關資訊
    for (int i=1 ; i<=count ; i++) {
      // 西元/民國/實歲/虛歲之值
      int startFortune;
      int endFortune;
      double startFortuneSeconds = getTargetMajorSolarTermsSeconds(  i  * (isForward ? 1 : -1));
      double   endFortuneSeconds = getTargetMajorSolarTermsSeconds((i+1)* (isForward ? 1 : -1));

      double startFortuneGmtJulDay = gmtJulDay + Math.abs(startFortuneSeconds)*fortuneMonthSpan/86400.0;
      double   endFortuneGmtJulDay = gmtJulDay +   Math.abs(endFortuneSeconds)*fortuneMonthSpan/86400.0;

      startFortune = getAge(startFortuneGmtJulDay , ageMap).orElse(0);
      endFortune   = getAge(  endFortuneGmtJulDay , ageMap).orElse(0);

      List<String> startFortuneAgeNotes = ageNoteImpls.stream().map(impl -> impl.getAgeNote(ageMap.get(startFortune))).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
      List<String>   endFortuneAgeNotes = ageNoteImpls.stream().map(impl -> impl.getAgeNote(ageMap.get(  endFortune))).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

      FortuneData fortuneData = new FortuneData(nextStemBranch , startFortuneGmtJulDay, endFortuneGmtJulDay, startFortune , endFortune, startFortuneAgeNotes, endFortuneAgeNotes);
      fortuneDatas.add(fortuneData);

      nextStemBranch = isForward ? nextStemBranch.getNext() : nextStemBranch.getPrevious();
    } // for 1 ~ fortunes)
    return fortuneDatas;
  }


  public Branch getBranchOf(Star star) {
    Position pos = starPositionImpl.getPosition(star , lmt , location , Centric.GEO ,Coordinate.ECLIPTIC);
    return ZodiacSign.getZodiacSign(pos.getLng()).getBranch();
  }

  /** 是否有日光節約時間 */
  public boolean isDst() {
    return TimeTools.getDstSecondOffset(lmt, location).v1();
  }

  /** 與 GMT 的時差 (分) */
  public int getGmtMinuteOffset() {
    return TimeTools.getDstSecondOffset(lmt, location).v2() / 60;
  }

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
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(lmt, location);
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
