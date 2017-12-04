/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.YinYangIF;
import kotlin.Pair;
import org.jooq.lambda.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/** 起大限 */
public interface IBigRange extends Descriptive {

  Logger logger = LoggerFactory.getLogger(IBigRange.class);

  /** 取得此 house 的大限起訖時刻 , 傳回「虛歲」 (vAge) */
  Pair<Integer , Integer> getVageRange(House house, int set, YinYangIF yinYang, Gender gender, IHouseSeq houseSeqImpl);

  /** 計算每個地支 的 大限 起訖 虛歲 */
  default Map<Branch , Pair<Integer , Integer>> getFlowBigVageMap(Map<Branch, House> branchHouseMap, int set, StemBranch birthYear, Gender gender, IHouseSeq houseSeq) {
    return Arrays.stream(Branch.values())
      .map(branch -> {

        Pair<Integer , Integer> t2 = getVageRange(branchHouseMap.get(branch), set, birthYear.getStem(), gender, houseSeq);
        return Tuple.tuple(branch , t2);
      }).collect(Collectors.toMap(t -> t.v1, t -> t.v2));
  }

  /** 承上 , 計算每個地支的 大限 起訖 「虛歲」時刻，並且按照先後順序排列 (年齡 小 -> 大) */
  default Map<StemBranch , Pair<Integer , Integer>> getSortedFlowBigVageMap(Map<Branch , House> branchHouseMap , int set , StemBranch birthYear , Gender gender , IHouseSeq houseSeq) {
    Map<Branch , Pair<Integer , Integer>> map = getFlowBigVageMap(branchHouseMap , set , birthYear , gender , houseSeq);

    Stem stemOf寅 = IZiwei.getStemOf寅(birthYear.getStem());

    return map.entrySet().stream()
      .map(e -> {
        StemBranch sb = IZiwei.getStemBranchOf(e.getKey() , stemOf寅);
        return new AbstractMap.SimpleEntry<>(sb, e.getValue());
      })
      .sorted() // FIXME : Pair 沒有實作 Comparable , 但是否真有必要排序呢？ 好像沒必要?
      //.sorted(Map.Entry.comparingByValue())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }


  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IBigRange.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}
