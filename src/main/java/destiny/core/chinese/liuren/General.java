/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;

import java.util.Arrays;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.FiveElement.*;
import static destiny.core.chinese.Stem.*;

/**
 * 十二天將
 */
public enum General {

  貴人('貴', StemBranch.get(己, 丑), true , 土),
  螣蛇('螣', StemBranch.get(丁, 巳), false, 火),
  朱雀('朱', StemBranch.get(丙, 午), false, 火),
  六合('合', StemBranch.get(乙, 卯), true , 木),
  勾陳('勾', StemBranch.get(戊, 辰), false, 土),
  青龍('青', StemBranch.get(甲, 寅), true , 木),
  天空('空', StemBranch.get(戊, 戌), false, 土),
  白虎('白', StemBranch.get(庚, 申), false, 金),
  太常('常', StemBranch.get(己, 未), true , 土),
  玄武('玄', StemBranch.get(癸, 亥), false, 水),
  太陰('陰', StemBranch.get(辛, 酉), true , 金),
  天后('后', StemBranch.get(壬, 子), true , 水);

  private final char shortName;

  private final StemBranch stemBranch;

  private final boolean positive; // 吉(true) 凶(false)

  private final FiveElement fiveElement;

  General(char shortName, StemBranch stemBranch, boolean positive, FiveElement fiveElement) {
    this.shortName = shortName;
    this.stemBranch = stemBranch;
    this.positive = positive;
    this.fiveElement = fiveElement;
  }

  public static General get(Branch branch) {
    return Arrays.asList(values()).stream().filter(g -> g.getStemBranch().getBranch() == branch)
      .findFirst().orElseThrow(() -> new AssertionError(branch));
  }

  public char getShortName() {
    return shortName;
  }

  public StemBranch getStemBranch() {
    return stemBranch;
  }

  public boolean isPositive() {
    return positive;
  }

  public FiveElement getFiveElement() {
    return fiveElement;
  }

  public General next(int n, GeneralSeqIF seq) {
    return seq.next(this , n);
  }

  public General prev(int n , GeneralSeqIF seq) {
    return seq.prev(this , n);
  }
}
