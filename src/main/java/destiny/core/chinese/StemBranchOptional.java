/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class StemBranchOptional {

  @NotNull
  protected final Optional<Stem> stemOpt;

  @NotNull
  protected final Optional<Branch> branchOpt;

  private transient static StemBranchOptional[] ARRAY = new StemBranchOptional[60];
  static {
    int n = 0;
    do {
      ARRAY[n] = new StemBranchOptional(
        Optional.of(Stem.getHeavenlyStems(n % 10)),
        Optional.of(Branch.getEarthlyBranches(n % 12))
      );
      n++;
    } while (n < 60);
  }


  private StemBranchOptional(@NotNull Optional<Stem> stemOpt, @NotNull Optional<Branch> branchOpt) {
    check(stemOpt , branchOpt);
    this.stemOpt = stemOpt;
    this.branchOpt = branchOpt;
  }

  /**
   * 0[甲子] ~ 59[癸亥]
   */
  private static StemBranchOptional get(int index) {
    return ARRAY[normalize(index)];
  }

  private static int normalize(int index)
  {
    if (index >= 60)
      return (normalize(index-60));
    else if (index < 0)
      return (normalize(index+60));
    else
      return index;
  }

  public static StemBranchOptional get(Optional<Stem> stemOpt, Optional<Branch> branchOpt) {
    check(stemOpt , branchOpt);

    if (stemOpt.isPresent() && branchOpt.isPresent()) {
      int hIndex = Stem.getIndex(stemOpt.get());
      int eIndex = Branch.getIndex(branchOpt.get());
      switch (hIndex - eIndex) {
        case 0:
        case -10:
          return get(eIndex);
        case 2:
        case -8:
          return get(eIndex + 12);
        case 4:
        case -6:
          return get(eIndex + 24);
        case 6:
        case -4:
          return get(eIndex + 36);
        case 8:
        case -2:
          return get(eIndex + 48);
        default:
          throw new AssertionError("Invalid Stem/Branch Combination!");
      }
    } else {
      return new StemBranchOptional(stemOpt , branchOpt);
    }
  }

  public static StemBranchOptional get(char heavenlyStems, char earthlyBranches) {
    return get(Optional.of(Stem.getHeavenlyStems(heavenlyStems).get()), Optional.of(Branch.getEarthlyBranches(earthlyBranches).get()));
  }

  public static StemBranchOptional get(@NotNull String stemBranch) {
    if (stemBranch.length() != 2)
      throw new RuntimeException("The length of " + stemBranch + " must equal to 2 !");
    else
      return get(stemBranch.charAt(0), stemBranch.charAt(1));
  }


  private static void check(@NotNull Optional<Stem> stemOpt, @NotNull Optional<Branch> branchOpt) {
    if (stemOpt.isPresent() && branchOpt.isPresent()) {
      if (stemOpt.get().getBooleanValue() !=  SimpleBranch.get(branchOpt.get()).getBooleanValue())
        throw new RuntimeException("Stem/Branch combination illegal ! " + stemOpt.get() + " cannot be combined with " + branchOpt.get());
    }
  }


  @Override
  public String toString() {
    return "[" + stemOpt + ' ' + branchOpt + ']';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof StemBranchOptional))
      return false;
    StemBranchOptional that = (StemBranchOptional) o;
    return Objects.equals(stemOpt, that.stemOpt) && Objects.equals(branchOpt, that.branchOpt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stemOpt, branchOpt);
  }
}
