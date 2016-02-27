/**
 * @author smallufo 
 * Created on 2008/1/2 at 下午 10:54:53
 */ 
package destiny.astrology.beans;

import destiny.astrology.Aspect;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.core.calendar.Time;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * <pre>
 * 計算此顆星被哪兩顆星包夾
 * 這裡的「包夾」，是中性字眼。代表此星，先前與A星形成交角，後與B星形成交角
 * 也就是說，任何星在任何時候都處於「被包夾」狀態
 * 至於被包夾的好壞，要看星性以及更進一步的交角 Apply / Separate 決定
 * </pre>
 */
public class BesiegedBean implements Serializable
{
  private Logger logger = LoggerFactory.getLogger(getClass());

  private final RelativeTransitIF relativeTransitImpl;

  /** 之前形成的交角 */
  private Optional<Aspect> aspectPrior;
  
  /** 之後形成的交角 */
  private Optional<Aspect> aspectAfter;

  public BesiegedBean(RelativeTransitIF relativeTransitImpl) {
    this.relativeTransitImpl = relativeTransitImpl;
  }

  /**
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmt GMT 時間
   * @param onlyClassicalPlanets 是否只計算古典占星的星，如果「是」的話，則排除三王星
   * @param aspects 欲計算的交角 array
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  @NotNull
  public List<Planet>  getBesiegingPlanets(Planet planet , Time gmt , boolean onlyClassicalPlanets , Aspect[] aspects)
  {
    return getBesiegingPlanets(planet , gmt , onlyClassicalPlanets , Arrays.asList(aspects));
  }
  
  /**
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmt GMT 時間
   * @param onlyClassicalPlanets 是否只計算古典占星的星，如果「是」的話，則排除三王星
   * @param aspects 欲計算的交角 Collection
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  @NotNull
  public List<Planet>  getBesiegingPlanets(Planet planet , Time gmt , boolean onlyClassicalPlanets , @NotNull Collection<Aspect> aspects)
  {
    List<Planet> otherPlanets = new ArrayList<>();
    for(Planet each : Planet.values)
    {
      if (each != planet)
        otherPlanets.add(each);
    }
    if (onlyClassicalPlanets)
    {
      otherPlanets.remove(Planet.URANUS);
      otherPlanets.remove(Planet.NEPTUNE);
      otherPlanets.remove(Planet.PLUTO);
    }
    return getBesiegingPlanets(planet , gmt , otherPlanets , aspects);
  }
  
  /**
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmt GMT 時間
   * @param otherPlanets 其他行星
   * @param aspects 欲計算的交角 array
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  @NotNull
  public List<Planet> getBesiegingPlanets(Planet planet , Time gmt , @NotNull Collection<Planet> otherPlanets , @NotNull Aspect[] aspects)
  {
    double[] angles = new double[aspects.length];
    for(int i=0 ; i<angles.length ; i++)
      angles[i] = aspects[i].getDegree();
    return getBesiegingPlanets(planet, gmt, otherPlanets, angles);
  }

  
  /**
   * @param planet 此 planet 是否被 p1 , p2 所包夾
   * @param isClassical 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @param isOnlyHardAspects 是否只計算 「艱難交角」 : 0/90/180 ; 如果「false」的話，連 60/120 也算進去
   * @return 是否被包夾
   */
  public boolean isBesieged(Planet planet , Planet p1 , Planet p2 , Time gmt , boolean isClassical , boolean isOnlyHardAspects)
  {
    List<Planet> otherPlanets = new ArrayList<>();
    for(Planet each : Planet.values)
    {
      if (each != planet)
        otherPlanets.add(each);
    }
    if (isClassical)
    {
      otherPlanets.remove(Planet.URANUS);
      otherPlanets.remove(Planet.NEPTUNE);
      otherPlanets.remove(Planet.PLUTO);
    }
    
    Collection<Aspect> searchingAspects = Collections.synchronizedList(new ArrayList<>());
    searchingAspects.add(Aspect.CONJUNCTION);// 0
    searchingAspects.add(Aspect.SQUARE);     // 90
    searchingAspects.add(Aspect.OPPOSITION); // 180
    searchingAspects.add(Aspect.SEXTILE);  // 60
    searchingAspects.add(Aspect.TRINE);    // 120
    
    Collection<Aspect> constrainingAspects = Collections.synchronizedList(new ArrayList<Aspect>());
    constrainingAspects.addAll(searchingAspects);
    if (isOnlyHardAspects) 
    { 
      //只考量「硬」角度，所以 移除和諧的角度 (60/120)
      constrainingAspects.remove(Aspect.SEXTILE);
      constrainingAspects.remove(Aspect.TRINE);
    }
    
    
    List<Planet> besiegingPlanets = getBesiegingPlanets(planet , gmt , otherPlanets , searchingAspects);
    logger.debug("包夾 {} 的是 {}({}) 以及 {}({})"
      , planet , besiegingPlanets.get(0) , aspectPrior.get() , besiegingPlanets.get(1) , aspectAfter.get());
    if (besiegingPlanets.contains(p1) && besiegingPlanets.contains(p2))
    {
      if(constrainingAspects.contains(aspectPrior.get()) && constrainingAspects.contains(aspectAfter.get()))
        return true;
    }
    return false;
  }

  
  /**
   * 角度皆為 0/60/90/120/180
   * @param planet 計算此顆星 被哪兩顆行星 包夾
   * @param gmt GMT 時間
   * @param isClassical 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @return 第一顆星是「之前」形成交角的星 ; 第二顆星是「之後」會形成交角的星
   */
  @NotNull
  public List<Planet> getBesiegingPlanets(Planet planet , Time gmt , boolean isClassical)
  {
    List<Planet> otherPlanets = new ArrayList<>();
    for(Planet each : Planet.values)
    {
      if (each != planet)
        otherPlanets.add(each);
    }
    if (isClassical)
    {
      otherPlanets.remove(Planet.URANUS);
      otherPlanets.remove(Planet.NEPTUNE);
      otherPlanets.remove(Planet.PLUTO);
    }
    
    Collection<Aspect> majorAspects = Aspect.getAngles(Aspect.Importance.HIGH);
    Collection<Aspect> mediumAspects = Aspect.getAngles(Aspect.Importance.MEDIUM);
    Collection<Aspect> searchingAspects = new ArrayList<>();
    if(isClassical)
      searchingAspects.addAll(majorAspects);
    else
    {
      searchingAspects.addAll(majorAspects);
      searchingAspects.addAll(mediumAspects);
    }
    return getBesiegingPlanets(planet, gmt, otherPlanets, searchingAspects);
  }
  
  
  /**
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmt GMT 時間
   * @param otherPlanets 其他行星
   * @param searchingAspects 欲計算的交角
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  @NotNull
  List<Planet> getBesiegingPlanets(Planet planet, Time gmt, @NotNull Collection<Planet> otherPlanets, @NotNull Collection<Aspect> searchingAspects)
  {
    double[] angles = new double[searchingAspects.size()];
    Iterator<Aspect> it = searchingAspects.iterator();
    int i=0;
    while(it.hasNext())
    {
      angles[i] = it.next().getDegree();
      i++;
    }
    return getBesiegingPlanets(planet, gmt, otherPlanets, angles);
  }

  
  /**
   * 最 Generalized 的介面
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmt GMT 時間
   * @param otherPlanets 其他行星
   * @param angles 欲計算的交角
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者
   * TODO : 目前的交角都只考慮「perfect」準確交角（一般行星三分容許度，日月17分），並未考慮容許度（即 applying），未來要改進
   */
  @NotNull
  List<Planet> getBesiegingPlanets(Planet planet, Time gmt, @NotNull Collection<Planet> otherPlanets, @NotNull double[] angles)
  {
    if (otherPlanets.contains(planet))
      otherPlanets.remove(planet);
    
    //先逆推，計算此 planet 「之前」與其他行星呈現 交角的最近時間及行星
    Time otherPlanetsNearestTimeBackward = null;
    //之前形成交角的行星
    Planet priorPlanet = null;
    for (Planet eachOther : otherPlanets)
    {
      Pair<Time , Double> tuple = relativeTransitImpl.getNearestRelativeTransitTime(planet, eachOther, gmt, false, angles);

      Time resultTime = tuple.getLeft();
      if (resultTime != null) // result 有可能為 null , 例如計算 太陽/水星 [90,180,270] 的度數，將不會有結果
      {
        if (otherPlanetsNearestTimeBackward == null)
        {
          otherPlanetsNearestTimeBackward = resultTime;
          priorPlanet = eachOther;
          aspectPrior = Aspect.getAspect(tuple.getRight());
        }
        else
        {
          if (resultTime.isAfter(otherPlanetsNearestTimeBackward))
          {
            otherPlanetsNearestTimeBackward = resultTime;
            priorPlanet = eachOther;
            aspectPrior = Aspect.getAspect(tuple.getRight());
          }
        }        
      }
    }
    //System.out.println(planet + " : 之前 , 與 " + priorPlanet + " 形成交角，於 " + otherPlanetsNearestTimeBackward);
    
    //順推，計算此 planet 「之後」與其他行星呈現交角的時間及行星
    //將之前形成交角的行星，移除之後搜尋的範圍中（只有月亮有此情形）
    if (priorPlanet != null)
      otherPlanets.remove(priorPlanet);
    
    Time otherPlanetsNearestTimeForward = null;
    //之後形成交角的行星
    Planet afterPlanet = null; 
    for (Planet eachOther : otherPlanets)
    {
      Pair<Time , Double> tuple = relativeTransitImpl.getNearestRelativeTransitTime(planet , eachOther , gmt , true , angles);
      Time resultTime = tuple.getLeft();
      if (resultTime != null) // result 有可能為 null , 例如計算 太陽/水星 [90,180,270] 的度數，將不會有結果
      {
        if (otherPlanetsNearestTimeForward == null)
        {
          otherPlanetsNearestTimeForward = resultTime;
          afterPlanet = eachOther;
          aspectAfter = Aspect.getAspect(tuple.getRight());
        }
        else
        {
          if (resultTime.isBefore(otherPlanetsNearestTimeForward))
          {
            otherPlanetsNearestTimeForward = resultTime;
            afterPlanet = eachOther;
            aspectAfter = Aspect.getAspect(tuple.getRight());
          }
        }
      }
    }
    //System.out.println(planet + " : 之後 , 與 " + afterPlanet + " 形成交角，於 " + otherPlanetsNearestTimeForward);
    
    List<Planet> resultList = new ArrayList<>();
    resultList.add(priorPlanet);
    resultList.add(afterPlanet);
    
    return resultList;    
  }

}
