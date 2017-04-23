/**
 * Created by smallufo on 2017-04-20.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.ziwei.StarLucky.天魁;
import static destiny.core.chinese.ziwei.StarMain.*;
import static destiny.core.chinese.ziwei.StarMinor.三台;
import static destiny.core.chinese.ziwei.StarMinor.天巫;
import static destiny.core.chinese.ziwei.StarUnlucky.地劫;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class StrengthMiddleImplTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  IStrength impl = new StrengthMiddleImpl();

  @Test
  public void testTitle() {
    logger.info("title tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.CHINA));
  }

  @Test
  public void testListStarByType() {
    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(StarMain.values));
    starList.addAll(Arrays.asList(StarLucky.values));
    starList.addAll(Arrays.asList(StarUnlucky.values));
    starList.addAll(Arrays.asList(StarMinor.values));
    starList.addAll(Arrays.asList(StarDoctor.values));
    starList.addAll(Arrays.asList(StarLongevity.values));

    Map<Type, Set<ZStar>> map = starList.stream()
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
  public void getMap() {
    impl.getMapOf(紫微).forEach((k,v) -> {
      logger.info("{} -> {}" , k , v);
    });

    impl.getMapOf(地劫).forEach((k,v) -> {
      logger.info("{} : {} -> {}" , 地劫 , k , v);
    });
  }

  @Test
  public void getStrength() {


    assertSame(5, impl.getStrengthOf(紫微, 子).orElse(0));
    assertSame(5, impl.getStrengthOf(破軍, Branch.亥).orElse(0));

    assertSame(2, impl.getStrengthOf(天魁, 子).orElse(0));
    assertSame(5, impl.getStrengthOf(三台, 子).orElse(0));
    assertFalse(impl.getStrengthOf(天巫 , 子).isPresent());
  }
}