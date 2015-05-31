/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;

import java.util.Arrays;

import static destiny.core.chinese.FiveElement.*;

/**
 * 十二天將
 */
public enum General {

  貴人('貴', true , 土),
  螣蛇('螣', false, 火),
  朱雀('朱', false, 火),
  六合('合', true , 木),
  勾陳('勾', false, 土),
  青龍('青', true , 木),
  天空('空', false, 土),
  白虎('白', false, 金),
  太常('常', true , 土),
  玄武('玄', false, 水),
  太陰('陰', true , 金),
  天后('后', true , 水);

  private final char shortName;

  private final boolean positive; // 吉(true) 凶(false)

  private final FiveElement fiveElement;

  General(char shortName, boolean positive, FiveElement fiveElement) {
    this.shortName = shortName;
    this.positive = positive;
    this.fiveElement = fiveElement;
  }

  public static General get(Branch branch , GeneralStemBranchIF stemBranchConfig) {
    return Arrays.asList(values()).stream().filter(g -> stemBranchConfig.getStemBranch(g).getBranch() == branch)
      .findFirst().orElseThrow(() -> new AssertionError(branch));
  }

  public char getShortName() {
    return shortName;
  }

  public StemBranch getStemBranch(GeneralStemBranchIF stemBranchConfig) {
    return stemBranchConfig.getStemBranch(this);
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
