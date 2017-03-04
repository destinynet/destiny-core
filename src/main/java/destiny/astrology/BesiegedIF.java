/**
 * Created by smallufo on 2017-02-16.
 */
package destiny.astrology;

import destiny.core.calendar.Time;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <pre>
 * 計算此顆星被哪兩顆星包夾
 * 這裡的「包夾」，是中性字眼。代表此星，先前與A星形成交角，後與B星形成交角
 * 也就是說，任何星在任何時候都處於「被包夾」狀態
 * 至於被包夾的好壞，要看星性以及更進一步的交角 Apply / Separate 決定
 * </pre>
 */
public interface BesiegedIF {

  Logger logger = LoggerFactory.getLogger(BesiegedIF.class);

  /**
   * 最 Generalized 的介面
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmtJulDay GMT 時間
   * @param otherPlanets 其他行星
   * @param angles 欲計算的交角
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者
   * TODO : 目前的交角都只考慮「perfect」準確交角（一般行星三分容許度，日月17分），並未考慮容許度（即 applying），未來要改進
   */
  Triple<List<Planet> , Optional<Aspect> , Optional<Aspect>> getBesiegingPlanets(Planet planet, double gmtJulDay,
                                                                                 @NotNull Collection<Planet> otherPlanets,
                                                                                 @NotNull double[] angles);


  /**
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmtJulDay GMT 時間
   * @param otherPlanets 其他行星
   * @param searchingAspects 欲計算的交角
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  @NotNull
  default Triple<List<Planet> , Optional<Aspect> , Optional<Aspect>> getBesiegingPlanets(Planet planet, double gmtJulDay,
                                                                                         @NotNull Collection<Planet> otherPlanets,
                                                                                         @NotNull Collection<Aspect> searchingAspects) {
    double[] angles = new double[searchingAspects.size()];
    Iterator<Aspect> it = searchingAspects.iterator();
    int i=0;
    while (it.hasNext()) {
      angles[i] = it.next().getDegree();
      i++;
    }
    return getBesiegingPlanets(planet, gmtJulDay, otherPlanets, angles);
  }

  /**
   * 角度皆為 0/60/90/120/180
   * @param planet 計算此顆星 被哪兩顆行星 包夾
   * @param gmtJulDay GMT 時間
   * @param isClassical 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @return 第一顆星是「之前」形成交角的星 ; 第二顆星是「之後」會形成交角的星
   */
  default List<Planet> getBesiegingPlanets(Planet planet, double gmtJulDay, boolean isClassical) {
    List<Planet> otherPlanets = new ArrayList<>();
    for (Planet each : Planet.values) {
      if (each != planet)
        otherPlanets.add(each);
    }
    if (isClassical) {
      otherPlanets.remove(Planet.URANUS);
      otherPlanets.remove(Planet.NEPTUNE);
      otherPlanets.remove(Planet.PLUTO);
    }

    Collection<Aspect> majorAspects = Aspect.getAngles(Aspect.Importance.HIGH);
    Collection<Aspect> mediumAspects = Aspect.getAngles(Aspect.Importance.MEDIUM);
    Collection<Aspect> searchingAspects = new ArrayList<>();

    searchingAspects.addAll(majorAspects);
    if (!isClassical) {
      searchingAspects.addAll(mediumAspects);
    }
    return getBesiegingPlanets(planet, gmtJulDay, otherPlanets, searchingAspects).getLeft();
  }

  default List<Planet> getBesiegingPlanets(Planet planet , LocalDateTime gmt , boolean isClassical) {
    return getBesiegingPlanets(planet , Time.getGmtJulDay(gmt) , isClassical);
  }


  @NotNull
  default Triple<List<Planet> , Optional<Aspect> , Optional<Aspect>> getBesiegingPlanets(Planet planet, LocalDateTime gmt,
                                                                                         @NotNull Collection<Planet> otherPlanets,
                                                                                         @NotNull Collection<Aspect> searchingAspects) {
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getBesiegingPlanets(planet , gmtJulDay , otherPlanets , searchingAspects);
  }


  default List<Planet> getBesiegingPlanets(Planet planet , LocalDateTime gmt , boolean onlyClassicalPlanets , @NotNull Collection<Aspect> aspects) {
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
    return getBesiegingPlanets(planet , gmt , otherPlanets , aspects).getLeft();
  }

  default List<Planet>  getBesiegingPlanets(Planet planet , LocalDateTime gmt ,
                                            boolean onlyClassicalPlanets , Aspect[] aspects) {
    return getBesiegingPlanets(planet , gmt , onlyClassicalPlanets , Arrays.asList(aspects));
  }





  /**
   * @param planet 此 planet 是否被 p1 , p2 所包夾
   * @param isClassical 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @param isOnlyHardAspects 是否只計算 「艱難交角」 : 0/90/180 ; 如果「false」的話，連 60/120 也算進去
   * @return 是否被包夾
   */
  default boolean isBesieged(Planet planet , Planet p1 , Planet p2 , LocalDateTime gmt , boolean isClassical , boolean isOnlyHardAspects) {
    List<Planet> otherPlanets = new ArrayList<>();
    for (Planet each : Planet.values) {
      if (each != planet)
        otherPlanets.add(each);
    }
    if (isClassical) {
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
    if (isOnlyHardAspects) {
      //只考量「硬」角度，所以 移除和諧的角度 (60/120)
      constrainingAspects.remove(Aspect.SEXTILE);
      constrainingAspects.remove(Aspect.TRINE);
    }


    Triple<List<Planet> , Optional<Aspect> , Optional<Aspect>>  triple = getBesiegingPlanets(planet , gmt , otherPlanets , searchingAspects);
    List<Planet> besiegingPlanets = triple.getLeft();

    Optional<Aspect> aspectPrior = triple.getMiddle();
    Optional<Aspect> aspectAfter = triple.getRight();

    logger.debug("包夾 {} 的是 {}({}) 以及 {}({})", planet , besiegingPlanets.get(0) , aspectPrior , besiegingPlanets.get(1) , aspectAfter);
    if (besiegingPlanets.contains(p1)
      && besiegingPlanets.contains(p2)
      && aspectPrior.isPresent()
      && aspectAfter.isPresent())
    {
      if(constrainingAspects.contains(aspectPrior.get()) && constrainingAspects.contains(aspectAfter.get()))
        return true;
    }
    return false;
  }

}
