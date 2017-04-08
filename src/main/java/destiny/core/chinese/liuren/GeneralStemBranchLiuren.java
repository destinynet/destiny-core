/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.liuren;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

import java.io.Serializable;
import java.util.Locale;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.liuren.General.*;

/**
 * 大六壬：壬子天后 癸亥玄武
 */
public class GeneralStemBranchLiuren implements GeneralStemBranchIF , Serializable {

  private final static BiMap<General , StemBranch> map = new ImmutableBiMap.Builder<General, StemBranch>()
    .put(貴人 , StemBranch.get(己, 丑))
    .put(螣蛇 , StemBranch.get(丁, 巳))
    .put(朱雀 , StemBranch.get(丙, 午))
    .put(六合 , StemBranch.get(乙, 卯))
    .put(勾陳 , StemBranch.get(戊, 辰))
    .put(青龍 , StemBranch.get(甲, 寅))
    .put(天空 , StemBranch.get(戊, 戌))
    .put(白虎 , StemBranch.get(庚, 申))
    .put(太常 , StemBranch.get(己, 未))
    .put(玄武 , StemBranch.get(癸, 亥))
    .put(太陰 , StemBranch.get(辛, 酉))
    .put(天后 , StemBranch.get(壬, 子))
    .build();

  @Override
  public StemBranch getStemBranch(General general) {
    return map.get(general);
  }

  /**
   * 從地支，找尋「天將」
   * 因為 map value 為「干支」，因此需要 filter
   * */
  @Override
  public General get(Branch branch) {
    return map.values().stream()
      .filter(stemBranch -> stemBranch.getBranch() == branch)
      .findAny()
      .map(stemBranch -> map.inverse().get(stemBranch))
      .orElseThrow(RuntimeException::new);
  }

  @Override
  public String getTitle(Locale locale) {
    return "《大六壬》";
  }

  @Override
  public String getDescription(Locale locale) {
    return "壬子天后 癸亥玄武";
  }
}
