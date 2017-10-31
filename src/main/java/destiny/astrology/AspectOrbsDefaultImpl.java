/**
 * @author smallufo 
 * Created on 2007/11/24 at 下午 11:51:07
 */ 
package destiny.astrology;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;

/** 
 * 「現代占星術」的交角容許度，內定實作
 * 參考資料 : http://www.myastrologybook.com/aspects-and-orbs.htm 
 * */
public class AspectOrbsDefaultImpl implements IAspectOrbs, Serializable{

  private final static ImmutableMap<Aspect , Double> aspectOrbsMap = new ImmutableMap.Builder<Aspect , Double>()
    .put(Aspect.CONJUNCTION, 11.0) //0
    .put(Aspect.SEMISEXTILE , 1.5) //30
    .put(Aspect.DECILE , 1.0) //36
    .put(Aspect.NOVILE , 1.0) //40
    .put(Aspect.SEMISQUARE , 2.0) //45
    .put(Aspect.SEPTILE , 1.5) // 360x1/7
    .put(Aspect.SEXTILE , 4.5) //60
    .put(Aspect.QUINTILE , 2.0) //72
    .put(Aspect.BINOVILE , 1.0) //80
    .put(Aspect.SQUARE , 7.5) //90
    .put(Aspect.BISEPTILE , 1.5 ) // 360x2/7
    .put(Aspect.SESQUIQUINTLE , 1.5) //108
    .put(Aspect.TRINE , 7.5 ) //120
    .put(Aspect.SESQUIQUADRATE , 2.0) //135
    .put(Aspect.BIQUINTILE , 2.0) //144
    .put(Aspect.QUINCUNX , 2.0) //150
    .put(Aspect.TRISEPTILE , 1.5) //360x3/7
    .put(Aspect.QUATRONOVILE , 1.0 ) //160
    .put(Aspect.OPPOSITION , 11.0) //180
    .build();

  @Override
  public double getAspectOrb(Aspect aspect)
  {
    return aspectOrbsMap.get(aspect);
  }

}
