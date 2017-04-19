/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ZStarTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testListStarByType() {
    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(StarMain.values));
    starList.addAll(Arrays.asList(StarLucky.values));
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