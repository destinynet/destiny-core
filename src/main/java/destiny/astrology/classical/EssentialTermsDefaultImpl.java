/**
 * @author smallufo 
 * Created on 2007/11/28 at 下午 3:06:17
 */ 
package destiny.astrology.classical;

import com.google.common.collect.ImmutableList;
import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Essential Terms , 內定實作 , 參考 Ptolemy's Table , 以五分法
 */
public class EssentialTermsDefaultImpl implements IEssentialTerms, Serializable {

  private final static ImmutableList<PointDegree> degList = new ImmutableList.Builder<PointDegree>()
    //戌
    .add(new PointDegree(Planet.JUPITER , 6))
    .add(new PointDegree(Planet.VENUS , 14))
    .add(new PointDegree(Planet.MERCURY , 21))
    .add(new PointDegree(Planet.MARS , 26))
    .add(new PointDegree(Planet.SATURN , 30))
    //酉
    .add(new PointDegree(Planet.VENUS , 38))
    .add(new PointDegree(Planet.MERCURY , 45))
    .add(new PointDegree(Planet.JUPITER , 52))
    .add(new PointDegree(Planet.SATURN , 56))
    .add(new PointDegree(Planet.MARS , 60))
    //申
    .add(new PointDegree(Planet.MERCURY , 67))
    .add(new PointDegree(Planet.JUPITER , 74))
    .add(new PointDegree(Planet.VENUS , 81))
    .add(new PointDegree(Planet.SATURN , 85))
    .add(new PointDegree(Planet.MARS , 90))
    //未
    .add(new PointDegree(Planet.MARS , 96))
    .add(new PointDegree(Planet.JUPITER , 103))
    .add(new PointDegree(Planet.MERCURY , 110))
    .add(new PointDegree(Planet.VENUS , 117))
    .add(new PointDegree(Planet.SATURN , 120))
    //午
    .add(new PointDegree(Planet.SATURN , 126))
    .add(new PointDegree(Planet.MERCURY , 133))
    .add(new PointDegree(Planet.VENUS , 139))
    .add(new PointDegree(Planet.JUPITER , 145))
    .add(new PointDegree(Planet.MARS , 150))
    //巳
    .add(new PointDegree(Planet.MERCURY , 157))
    .add(new PointDegree(Planet.VENUS , 163))
    .add(new PointDegree(Planet.JUPITER , 168))
    .add(new PointDegree(Planet.SATURN , 174))
    .add(new PointDegree(Planet.MARS , 180))
    //辰
    .add(new PointDegree(Planet.SATURN , 186))
    .add(new PointDegree(Planet.VENUS , 191))
    .add(new PointDegree(Planet.JUPITER , 199))
    .add(new PointDegree(Planet.MERCURY , 204))
    .add(new PointDegree(Planet.MARS , 210))
    //卯
    .add(new PointDegree(Planet.MARS , 216))
    .add(new PointDegree(Planet.JUPITER , 224))
    .add(new PointDegree(Planet.VENUS , 231))
    .add(new PointDegree(Planet.MERCURY , 237))
    .add(new PointDegree(Planet.SATURN , 240))
    //寅
    .add(new PointDegree(Planet.JUPITER , 248))
    .add(new PointDegree(Planet.VENUS , 254))
    .add(new PointDegree(Planet.MERCURY , 259))
    .add(new PointDegree(Planet.SATURN , 265))
    .add(new PointDegree(Planet.MARS , 270))
    //丑
    .add(new PointDegree(Planet.VENUS , 276))
    .add(new PointDegree(Planet.MERCURY , 282))
    .add(new PointDegree(Planet.JUPITER , 289))
    .add(new PointDegree(Planet.MARS , 295))
    .add(new PointDegree(Planet.SATURN , 300))
    //子
    .add(new PointDegree(Planet.SATURN , 306))
    .add(new PointDegree(Planet.MERCURY , 312))
    .add(new PointDegree(Planet.VENUS , 320))
    .add(new PointDegree(Planet.JUPITER , 325))
    .add(new PointDegree(Planet.MARS , 330))
    //亥
    .add(new PointDegree(Planet.VENUS , 338))
    .add(new PointDegree(Planet.JUPITER , 344))
    .add(new PointDegree(Planet.MERCURY , 350))
    .add(new PointDegree(Planet.MARS , 356))
    .add(new PointDegree(Planet.SATURN , 359.999999999999)) //如果改成 360 , 會被 normalize 成 0 度
    .build();

  @NotNull
  @Override
  public Point getTermsStar(double degree) {
    double normalizedDegree = Utils.getNormalizeDegree(degree);
    int signIndex = (int) normalizedDegree / 30;
    for (int i=0 ; i<5 ; i++) {
      PointDegree pointDegree = degList.get(signIndex*5+i);
      if (normalizedDegree < pointDegree.getDegree())
        return pointDegree.getPoint();
    }
    throw new RuntimeException("Cannot find Essential Terms at degree " + degree + " , signIndex = " + signIndex);
  }

  @Override
  @NotNull
  public Point getTermsStar(@NotNull ZodiacSign sign, double degree)
  {
    return getTermsStar(sign.getDegree() + degree);
  }
}

