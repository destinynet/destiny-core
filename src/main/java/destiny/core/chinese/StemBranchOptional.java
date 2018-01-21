/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese;

import destiny.tools.ArrayTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class StemBranchOptional implements Serializable {

  @Nullable
  final Stem stem;

  @Nullable
  final Branch branch;

  // 0[甲子] ~ 59[癸亥]
  private transient static StemBranchOptional[] ARRAY = new StemBranchOptional[60];
  static {
    int n = 0;
    do {
      ARRAY[n] = new StemBranchOptional(
        Stem.Companion.get(n % 10),
        Branch.Companion.get(n % 12)
      );
      n++;
    } while (n < 60);
  }


  StemBranchOptional(@Nullable Stem stemOpt, @Nullable Branch branchOpt) {
    check(stemOpt , branchOpt);
    this.stem = stemOpt;
    this.branch = branchOpt;
  }

  public static StemBranchOptional empty() {
    return new StemBranchOptional(null , null);
  }


  /**
   * 0[甲子] ~ 59[癸亥]
   */
  private static StemBranchOptional get(int index) {
    return ArrayTools.INSTANCE.get(ARRAY , index);
  }

  public static StemBranchOptional get(@Nullable Stem stemOpt, @Nullable Branch branchOpt) {
    check(stemOpt , branchOpt);

    if (stemOpt != null && branchOpt != null) {
      int hIndex = Stem.Companion.getIndex(stemOpt);
      int eIndex = Branch.Companion.getIndex(branchOpt);
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

  public static StemBranchOptional get(char stemChar, char branchChar) {
    return get(Stem.Companion.get(stemChar) , Branch.Companion.get(branchChar));
  }

  public static StemBranchOptional get(@NotNull String stemBranch) {
    if (stemBranch.length() != 2)
      throw new RuntimeException("The length of " + stemBranch + " must equal to 2 !");
    else
      return get(stemBranch.charAt(0), stemBranch.charAt(1));
  }

  public static StemBranchOptional get(@NotNull StemBranch sb) {
    return get(sb.stem , sb.branch);
  }

  private static Optional<Integer> getIndex(StemBranchOptional sb) {
    if (sb.stem != null && sb.branch != null) {
      for(int i=0 ; i < ARRAY.length ; i++) {
        if (sb.equals(ARRAY[i])) {
          return Optional.of(i);
        }
      }
    }
    return Optional.empty();
  }

  public Optional<Integer> getIndexOpt() {
    return StemBranchOptional.getIndex(this);
  }

  public Optional<? extends StemBranchOptional> nextOpt(int n) {
    return getIndex(this).map(i -> get(i+n));
  }

  private static void check(@Nullable Stem stem, @Nullable Branch branch) {
    if (stem!= null && branch != null) {
      if (stem.getBooleanValue() != SimpleBranch.get(branch).getBooleanValue()) {
        throw new RuntimeException("Stem/Branch combination illegal ! " + stem + " cannot be combined with " + branch);
      }
    }
  }

  @Nullable
  public Stem getStem() {
    return stem;
  }

  public Optional<Stem> getStemOptional() {
    return Optional.ofNullable(stem);
  }

  @Nullable
  public Branch getBranch() {
    return branch;
  }

  public Optional<Branch> getBranchOptional() {
    return Optional.ofNullable(branch);
  }

  @Override
  public String toString() {
    return "[" + stem + ' ' + branch + ']';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof StemBranchOptional))
      return false;
    StemBranchOptional that = (StemBranchOptional) o;
    return Objects.equals(stem, that.stem) && Objects.equals(branch, that.branch);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stem, branch);
  }


}
