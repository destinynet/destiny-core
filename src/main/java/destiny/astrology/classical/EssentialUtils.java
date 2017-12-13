/**
 * @author smallufo 
 * Created on 2008/1/2 at 下午 4:08:25
 */ 
package destiny.astrology.classical;

import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.Horoscope;
import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** EssentialIF 的工具箱 */
public class EssentialUtils
{
  private EssentialIF essentialImpl = new EssentialDefaultImpl();
  
  /** 內定採用 Swiss Ephemeris 的 DayNightDifferentiatorImpl */
  private DayNightDifferentiator dayNightDifferentiatorImpl;// = new DayNightDifferentiatorImpl(new RiseTransImpl());

  private Logger logger = LoggerFactory.getLogger(getClass());
  
  public EssentialUtils(DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }
  
  /** 
   * receiver 是否 接納 receivee by Essential Dignities (Ruler/Exaltation/Triplicity/Term/Face) <br/>
   * 老闆是 receiver , 客人是 receivee , 如果客人進入了老闆的地盤 ( 旺 / 廟 / 三分 / Terms / Faces ) , 則「老闆接納外人」 
   * */
  public boolean isReceivingFromDignities(Point receiver , Point receivee , @NotNull Horoscope h) {

    return h.getZodiacSignOpt(receivee).map(receiveeSign -> {
      //比對 Ruler
      if (essentialImpl.getPoint(receiveeSign, Dignity.RULER).orElse(null) == receiver) {
        logger.debug("{} 透過 {} 接納 {}" , receiver , Dignity.RULER  , receivee);
        return true;
      }

      //比對 Exaltation
      if (essentialImpl.getPoint(receiveeSign, Dignity.EXALTATION).orElse(null) == receiver) {
        logger.debug("{} 透過 {} 接納 {}" , receiver , Dignity.EXALTATION , receivee);
        return true;
      }

      //比對 Triplicity
      if (essentialImpl.getTriplicityPoint(receiveeSign, dayNightDifferentiatorImpl.getDayNight(h.getLmt(), h.getLocation())) == receiver) {
        logger.debug("{} 透過 Triplicity 接納 {}" , receiver , receivee);
        return true;
      }

      return h.getPositionOpt(receivee).map(pos -> {
        double lngDegree = pos.getLng();
        //比對 Terms
        if (essentialImpl.getTermsPoint(lngDegree) == receiver) {
          logger.debug("{} 透過 TERMS 接納 {}" , receiver , receivee);
          return true;
        }

        //比對 Face
        if (essentialImpl.getFacePoint(lngDegree) == receiver) {
          logger.debug("{} 透過 FACE 接納 {}" , receiver , receivee);
          return true;
        }

        return false;
      }).orElse(false);

    }).orElse(false);

  }
  
  /** receiver 是否 接納 receivee by Essential Debilities (Detriment/Fall) */
  public boolean isReceivingFromDebilities(Point receiver , Point receivee , @NotNull Horoscope h) {

    return h.getZodiacSignOpt(receivee).map(receiveeSign -> {
      if (essentialImpl.getPoint(receiveeSign , Dignity.DETRIMENT).orElse(null) == receiver)
        return true;
      return essentialImpl.getPoint(receiveeSign, Dignity.FALL).orElse(null) == receiver;
    }).orElse(false);

//    ZodiacSign receiveeSign = h.getZodiacSign(receivee);
//    if (essentialImpl.getPoint(receiveeSign , Dignity.DETRIMENT) == receiver)
//      return true;
//    return essentialImpl.getPoint(receiveeSign, Dignity.FALL) == receiver;
  }
  
  /** Ruler 互訪 , 還沒確認是 優質互容 */
  public boolean isBothRulerVisit(Point planet , @NotNull Horoscope h) {

    return h.getZodiacSignOpt(planet).flatMap(sign1 ->
      essentialImpl.getPoint(sign1, Dignity.RULER).map(signRuler ->
        h.getZodiacSignOpt(signRuler)
          .filter(sign2 -> planet == essentialImpl.getPoint(sign2, Dignity.RULER).orElse(null))
          .isPresent()
      )
    ).orElse(false);

//    //取得此 Planet 在什麼星座
//    ZodiacSign sign1 = h.getZodiacSign(planet);
//
//    Point signRuler = essentialImpl.getPoint(sign1, Dignity.RULER);
//    ZodiacSign sign2 = h.getZodiacSign(signRuler);
//    Point planet2 = essentialImpl.getPoint(sign2, Dignity.RULER);
//    return planet == planet2;
  }
  
  /** 如果其中一顆星處於 {@link Dignity#DETRIMENT} 或是 {@link Dignity#FALL} , 則為 true */
  public boolean isOneInBadSituation(Point p1 , ZodiacSign sign1 , Point p2 , ZodiacSign sign2)
  {
    return (
        p1 == essentialImpl.getPoint(sign1, Dignity.DETRIMENT).orElse(null)
     || p1 == essentialImpl.getPoint(sign1, Dignity.FALL).orElse(null)
    )
      || (
        p2 == essentialImpl.getPoint(sign2, Dignity.DETRIMENT).orElse(null)
     || p2 == essentialImpl.getPoint(sign2, Dignity.FALL).orElse(null)
    );
  }
  
  /** 如果 兩顆星都處於 {@link Dignity#DETRIMENT} 或是  {@link Dignity#FALL} , 則為 true */
  public boolean isBothInBadSituation(Point p1 , ZodiacSign sign1 , Point p2 , ZodiacSign sign2) {
    return (
      p1 == essentialImpl.getPoint(sign1, Dignity.DETRIMENT).orElse(null)
   || p1 == essentialImpl.getPoint(sign1, Dignity.FALL).orElse(null)
    ) && (
      p2 == essentialImpl.getPoint(sign2, Dignity.DETRIMENT).orElse(null)
   || p2 == essentialImpl.getPoint(sign2, Dignity.FALL).orElse(null)
    );
  }

  public void setEssentialImpl(EssentialIF essentialImpl)
  {
    this.essentialImpl = essentialImpl;
  }

  public void setDayNightDifferentiatorImpl(DayNightDifferentiator dayNightDifferentiatorImpl) {
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }
}
