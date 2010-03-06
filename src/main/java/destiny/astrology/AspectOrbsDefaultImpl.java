/**
 * @author smallufo 
 * Created on 2007/11/24 at 下午 11:51:07
 */ 
package destiny.astrology;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** 
 * 「現代占星術」的交角容許度，內定實作
 * 參考資料 : http://www.myastrologybook.com/aspects-and-orbs.htm 
 * */
public class AspectOrbsDefaultImpl implements AspectOrbsIF , Serializable
{
  private static Map<Aspect , Double> aspectOrbsMap = Collections.synchronizedMap(new HashMap<Aspect , Double>());
  static
  {
    aspectOrbsMap.put(Aspect.CONJUNCTION, 11.0); //0
    aspectOrbsMap.put(Aspect.SEMISEXTILE , 1.5); //30
    aspectOrbsMap.put(Aspect.DECILE , 1.0); //36
    aspectOrbsMap.put(Aspect.NOVILE , 1.0); //40
    aspectOrbsMap.put(Aspect.SEMISQUARE , 2.0); //45
    aspectOrbsMap.put(Aspect.SEPTILE , 1.5); // 360x1/7
    aspectOrbsMap.put(Aspect.SEXTILE , 4.5); //60
    aspectOrbsMap.put(Aspect.QUINTILE , 2.0); //72
    aspectOrbsMap.put(Aspect.BINOVILE , 1.0); //80
    aspectOrbsMap.put(Aspect.SQUARE , 7.5); //90
    aspectOrbsMap.put(Aspect.BISEPTILE , 1.5 ); // 360x2/7
    aspectOrbsMap.put(Aspect.SESQUIQUINTLE , 1.5); //108
    aspectOrbsMap.put(Aspect.TRINE , 7.5 ); //120
    aspectOrbsMap.put(Aspect.SESQUIQUADRATE , 2.0); //135
    aspectOrbsMap.put(Aspect.BIQUINTILE , 2.0); //144
    aspectOrbsMap.put(Aspect.QUINCUNX , 2.0); //150
    aspectOrbsMap.put(Aspect.TRISEPTILE , 1.5); //360x3/7
    aspectOrbsMap.put(Aspect.QUATRONOVILE , 1.0 ); //160
    aspectOrbsMap.put(Aspect.OPPOSITION , 11.0); //180
  }
  

  @Override
  public double getAspectOrb(Aspect aspect)
  {
    return aspectOrbsMap.get(aspect).doubleValue();
  }

}
