/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ZStarTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 在 {@link Builder#Builder} 當中 ,
   * 先利用 starBranchMap 建立出
   * Map<Branch , Set<ZStar>> branchStarMap
   * 再建立出一個
   * Map<Branch , Set<ZStar>> branchStarMap2
   * 這是沒有效率的做法
   *
   * 嘗試改進此法
   */
  @Test
  public void testMapGroupBy() {
    // 嘗試先建立一個簡單的 starBranchMap
    Map<ZStar, Branch> starBranchMap = new HashMap<>();
    starBranchMap.put(StarMain.紫微.INSTANCE, Branch.子);
    starBranchMap.put(StarMain.天同.INSTANCE, Branch.子);
    starBranchMap.put(StarMain.七殺.INSTANCE, Branch.丑);

    // 哪個地支 裡面 有哪些星體
    Map<Branch , Set<ZStar>> map1 = starBranchMap.entrySet().stream()
      .collect(
        Collectors.groupingBy(
          entry -> entry.getValue().getBranch(),
          TreeMap::new,   // 保留地支順序
          Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
        )
      );
    logger.info("map1 = {}" , map1);


    Map<Branch , Set<ZStar>> map2 = Arrays.stream(Branch.values()).map(branch -> {
      Set<ZStar> stars = map1.getOrDefault(branch , new HashSet<>());
      return Tuple.tuple(branch , stars);
    }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
    logger.info("map2 = {}" , map2);




  }

  @Test
  public void testListStarByType() {
    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(StarMain.Companion.getValues()));
    starList.addAll(Arrays.asList(StarLucky.Companion.getValues()));
    starList.addAll(Arrays.asList(StarUnlucky.values));
    starList.addAll(Arrays.asList(StarMinor.values));
    starList.addAll(Arrays.asList(StarDoctor.values));
    starList.addAll(Arrays.asList(StarLongevity.values));

    Map<ZStar.Type, Set<ZStar>> map = starList.stream()
      .collect(
        Collectors.groupingBy(
          ZStar::getType,
          TreeMap::new ,
          Collectors.mapping(t -> t , Collectors.toSet())
        )
      );
    map.forEach((type , stars) -> {
      logger.info("{}" , type);
      stars.forEach(star -> logger.info("\t{}", star));
    });
  }

}