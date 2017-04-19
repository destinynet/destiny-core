/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static destiny.core.chinese.ziwei.StarMinor.天官;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class StrengthTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testListStarByType() {
    IStrength impl = new StrengthImpl();

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
      stars.forEach(star -> logger.info("\t{} : {}", star , impl.getMapOf(star)));
    });
  }


  @Test
  public void getStarMap() throws Exception {
    IStrength impl = new StrengthImpl();

    assertSame(2 , impl.getStrengthOf(天官 , Branch.卯).orElse(0));
    assertFalse(impl.getStrengthOf(天官 , Branch.子).isPresent());
  }

}