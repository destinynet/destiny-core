/**
 * @author smallufo 
 * Created on 2007/11/26 at 下午 3:50:48
 */ 
package destiny.astrology.classical;

import com.google.common.collect.ImmutableMap;
import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 取得星座 ( ZodiacSign ) 的 : 旺 Rulership , 廟 Exaltation , 陷 Detriment , 落 Fail <br/>
 * 內定實作為 托勒密表格
 */
public class EssentialRedfDefaultImpl implements IEssentialRedf, Serializable
{
  /** 存放星體在黃道帶上幾度得到 Exaltation (廟 , +4) 的度數 */
  private final static ImmutableMap<Point,Double> starExaltationMap = new ImmutableMap.Builder<Point, Double>()
    .put(Planet.SUN    ,  19.0) // 太陽在戌宮 19度 exalted.
    .put(Planet.MOON   ,  33.0) // 月亮在酉宮 03度 exalted.
    .put(Planet.MERCURY, 165.0) // 水星在巳工 15度 exalted.
    .put(Planet.VENUS  , 357.0) // 金星在亥宮 27度 exalted.
    .put(Planet.MARS   , 298.0) // 火星在丑宮 28度 exalted.
    .put(Planet.JUPITER, 105.0) // 木星在未宮 15度 exalted.
    .put(Planet.SATURN , 201.0) // 土星在辰宮 21度 exalted.
    .put(LunarNode.NORTH_TRUE ,  63.0) //北交點在 申宮 03度 exalted.
    .put(LunarNode.NORTH_MEAN ,  63.0) //北交點在 申宮 03度 exalted.
    .put(LunarNode.SOUTH_TRUE , 243.0) //南交點在 寅宮 03度 exalted.
    .put(LunarNode.SOUTH_MEAN , 243.0) //南交點在 寅宮 03度 exalted.
    .build();

  /*
  private static Map<Point , Double> starExaltationMap = Collections.synchronizedMap(new HashMap<Point , Double>());
  static
  {
    starExaltationMap.put(Planet.SUN    ,  19.0); // 太陽在戌宮 19度 exalted.
    starExaltationMap.put(Planet.MOON   ,  33.0); // 月亮在酉宮 03度 exalted.
    starExaltationMap.put(Planet.MERCURY, 165.0); // 水星在巳工 15度 exalted.
    starExaltationMap.put(Planet.VENUS  , 357.0); // 金星在亥宮 27度 exalted.
    starExaltationMap.put(Planet.MARS   , 298.0); // 火星在丑宮 28度 exalted.
    starExaltationMap.put(Planet.JUPITER, 105.0); // 木星在未宮 15度 exalted.
    starExaltationMap.put(Planet.SATURN , 201.0); // 土星在辰宮 21度 exalted.
    starExaltationMap.put(LunarNode.NORTH_TRUE ,  63.0); //北交點在 申宮 03度 exalted.
    starExaltationMap.put(LunarNode.SOUTH_TRUE , 243.0); //南交點在 寅宮 03度 exalted.
  }
  */
  
  /** 
   * 放星體在黃道帶上幾度得到 Fall (落 , -4) 的度數
   * 前面 starExaltationMap 中，每個星體的度數 +180度即為「落」 
   * */ 
  @NotNull
  private static final Map<Point , Double> starFallMap = new HashMap<>();
  static
  {
    for(Point eachPoint : starExaltationMap.keySet())
      starFallMap.put(eachPoint , Utils.getNormalizeDegree(starExaltationMap.get(eachPoint)+180));
  }
  
  /** 星座 + 強弱度 的組合 key , 中間以減號 (-) 串接 */
  @NotNull
  private static String getCompositeKey(@NotNull ZodiacSign sign , @NotNull Dignity dignity)
  {
    return sign.toString()+'-'+dignity.toString();
  }
  
  /** key 為 Sign-Dignity , 中間以 '-' 串接 */
  private static final Map<String , Point> essentialDignitiesMap = Collections.synchronizedMap(new HashMap<>());
  static 
  {
    /** 設定 Rulership (旺 , +5) */
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.ARIES       , Dignity.RULER), Planet.MARS);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.TAURUS      , Dignity.RULER), Planet.VENUS);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.GEMINI      , Dignity.RULER), Planet.MERCURY);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.CANCER      , Dignity.RULER), Planet.MOON);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.LEO         , Dignity.RULER), Planet.SUN);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.VIRGO       , Dignity.RULER), Planet.MERCURY);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.LIBRA       , Dignity.RULER), Planet.VENUS);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.SCORPIO     , Dignity.RULER), Planet.MARS);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.SAGITTARIUS , Dignity.RULER), Planet.JUPITER);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.CAPRICORN   , Dignity.RULER), Planet.SATURN);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.AQUARIUS    , Dignity.RULER), Planet.SATURN);
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.PISCES      , Dignity.RULER), Planet.JUPITER);

    /**  設定 Detriment (陷 , -5) , 其值為對沖星座之 Ruler */
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.ARIES       , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.ARIES       .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.TAURUS      , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.TAURUS      .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.GEMINI      , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.GEMINI      .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.CANCER      , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.CANCER      .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.LEO         , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.LEO         .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.VIRGO       , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.VIRGO       .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.LIBRA       , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.LIBRA       .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.SCORPIO     , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.SCORPIO     .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.SAGITTARIUS , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.SAGITTARIUS .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.CAPRICORN   , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.CAPRICORN   .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.AQUARIUS    , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.AQUARIUS    .getOppositeSign() , Dignity.RULER)));
    essentialDignitiesMap.put(getCompositeKey(ZodiacSign.PISCES      , Dignity.DETRIMENT), essentialDignitiesMap.get(getCompositeKey(ZodiacSign.PISCES      .getOppositeSign() , Dignity.RULER)));
  }
  
  public EssentialRedfDefaultImpl()
  {
  }

  /**
   * @param dignity {@link Dignity#RULER} 與 {@link Dignity#DETRIMENT} 不會傳回 empty() ,
   *                                    但 {@link Dignity#EXALTATION} 與 {@link Dignity#FALL} 就有可能為 empty()
   */
  @Override
  public Optional<Point> getPoint(@NotNull ZodiacSign sign, @NotNull Dignity dignity) {
    switch (dignity) {
      /** 廟 , +4 */
      case EXALTATION : return findPoint(sign , starExaltationMap); // maybe empty
      /** 落 , -4 */
      case FALL : return findPoint(sign , starFallMap); // maybe empty
      default : return Optional.of(essentialDignitiesMap.get(getCompositeKey(sign, dignity))); // not null
    }
  }
  
  private Optional<Point> findPoint(ZodiacSign sign, @NotNull Map<Point, Double> map) {
    for (Map.Entry<Point, Double> mapEntry : map.entrySet()) {
      if (sign == ZodiacSign.getZodiacSign(mapEntry.getValue()))
        return Optional.of(mapEntry.getKey());
    }
    return Optional.empty();
  }
  
  /**
   * 取得在此星座得到「Exaltation , 廟 +4」的星體及度數
   */
  public Optional<PointDegree> getExaltationStarDegree(ZodiacSign sign) {
    return findPoint(sign, starExaltationMap).map(point -> new PointDegree(point, starExaltationMap.get(point)));
  }
  
  /**
   * 取得在此星座得到「Fall , 落 -4」的星體及度數
   */
  public Optional<PointDegree> getFallStarDegree(ZodiacSign sign) {
    return findPoint(sign, starFallMap).map(point -> new PointDegree(point , starFallMap.get(point)));
  }

}
