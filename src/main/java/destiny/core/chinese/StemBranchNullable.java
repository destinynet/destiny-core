/**
 * Created by smallufo on 2014-08-03.
 */
package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Optional;

/**
 * stem or branch may be empty (or both empty)
 */
public class StemBranchNullable implements Serializable {

  @Nullable
  protected final HeavenlyStems stem;   //天干

  @Nullable
  protected final EarthlyBranches branch; //地支

  // 0[甲子] ~ 59[癸亥]
  @NotNull
  protected transient static StemBranchNullable[] stemBranchArray = new StemBranchNullable[60];
  static
  {
    int n=0;
    do
    {
      stemBranchArray[n]= new StemBranchNullable (
        HeavenlyStems  .getHeavenlyStems  ( n % 10 ) ,
        EarthlyBranches.getEarthlyBranches( n % 12 )
      );
      n++;
    }
    while(n<60);
  }

  public StemBranchNullable() {
    stem = null;
    branch = null;
  }


  public StemBranchNullable(@Nullable HeavenlyStems stem, @Nullable EarthlyBranches branch) {
    this.stem = stem;
    this.branch = branch;
    if (stem != null && branch != null)
      if ((HeavenlyStems.getIndex(stem) % 2) != (EarthlyBranches.getIndex(branch) % 2))
        throw new RuntimeException("Stem/Branch combination illegal ! " + stem + " cannot be combined with " + branch);
  }

  /**
   * 0[甲子] ~ 59[癸亥]
   * @param index
   */
  public static StemBranchNullable get(int index)
  {
    return stemBranchArray[normalize(index)];
  }

  public static StemBranchNullable get(@Nullable HeavenlyStems 天干 , @Nullable EarthlyBranches 地支)
  {
    if (天干 != null && 地支 != null) {
      if ((HeavenlyStems.getIndex(天干) % 2) != (EarthlyBranches.getIndex(地支) % 2))
        throw new RuntimeException("Stem/Branch combination illegal ! " + 天干 + " cannot be combined with " + 地支);

      int hIndex = HeavenlyStems.getIndex(天干);
      int eIndex = EarthlyBranches.getIndex(地支);
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
          throw new RuntimeException("Invalid 天干/地支 Combination!");
      }
    } // (天干 != null && 地支 != null)
    else {
      return new StemBranchNullable(天干 , 地支);
    }
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

  public static StemBranchNullable empty() {
    return new StemBranchNullable(null , null);
  }

  @Nullable
  public HeavenlyStems getStem() {
    return stem;
  }

  public Optional<HeavenlyStems> getStemOptional() {
    return Optional.ofNullable(stem);
  }

  @Nullable
  public EarthlyBranches getBranch() {
    return branch;
  }

  public Optional<EarthlyBranches> getBranchOptional() {
    return Optional.ofNullable(branch);
  }


  public static StemBranchNullable get(char heavenlyStems , char earthlyBranches)
  {
    return get(HeavenlyStems.getHeavenlyStems(heavenlyStems).get() , EarthlyBranches.getEarthlyBranches(earthlyBranches).get());
  }

  public static StemBranchNullable get(@NotNull String stemBranch)
  {
    if (stemBranch.length() != 2)
      throw new RuntimeException("The length of " + stemBranch + " must equal to 2 !");
    else
      return get(stemBranch.charAt(0) , stemBranch.charAt(1));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StemBranchNullable)) {
      return false;
    }

    StemBranchNullable that = (StemBranchNullable) o;

    if (!branch.equals(that.branch)) {
      return false;
    }
    if (!stem.equals(that.stem)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = stem.hashCode();
    result = 31 * result + branch.hashCode();
    return result;
  }
}
