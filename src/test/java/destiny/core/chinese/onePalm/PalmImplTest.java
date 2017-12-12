/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.chinese.onePalm;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static destiny.core.chinese.Branch.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * 測試比對資料
 * http://blog.yam.com/eightwords/article/23760469
 */
public class PalmImplTest {

  PalmIF impl = new PalmImpl();

  PositiveIF defaultImpl = new PositiveGenderImpl();


  /**
   * 如西元2001年2月4日12點（當天為立春）出生之男，換算為陰曆則為辛巳年正月十二日午時，
   * 先將大拇指點在巳（天文宮）上，巳上起正月，正月還是在巳（天文宮）上，
   * 然後在巳起初一，順數十二，最後落在辰（天奸宮）上，
   * 再在辰上起子時，出生的午時落在戌（天藝宮）上，「戌宮」即胎宮。
   * 那麼此人的四宮就出來了。如下面：
   * <p>
   * 年：巳—天文宮　月：巳—天文宮　日：辰—天奸宮　時：戌—天藝宮
   */
  @Test
  public void testGetPalm_Male() {
    Palm palm = impl.getPalm(Gender.男, 巳, false, 1, 12, 午, defaultImpl, true);

    assertSame(巳, palm.getYear());
    assertSame(巳, palm.getMonth());
    assertSame(辰, palm.getDay());
    assertSame(戌, palm.getHour());

    assertSame(巳 , palm.getBranch(Palm.Pillar.年));
    assertSame(巳 , palm.getBranch(Palm.Pillar.月));
    assertSame(辰 , palm.getBranch(Palm.Pillar.日));
    assertSame(戌 , palm.getBranch(Palm.Pillar.時));

    Map<Branch, Collection<Palm.Pillar>> map = palm.getNonEmptyPillars();
    assertSame(1 , map.get(戌).size());
    assertSame(1 , map.get(辰).size());
    assertSame(2 , map.get(巳).size());
  }

  /**
   * 如西元1970年10月23日20點出生之女，換算為陰曆則為庚戌年9月二十四日戌時，
   * 先將大拇指點在戌（天藝宮）上，在戌上起一月，逆數，二月在亥上，到九月在寅（天權宮）上，
   * 然後在寅上起初一，逆數到二十四，最後落在卯（天破宮）上，
   * 再在卯上起子時，出生的戌時落在巳（天文宮）上，「巳宮」即胎宮。那麼此女的四宮就出來了。
   * <p>
   * 年：戌—天藝宮　月：寅—天權宮　日：卯—天破宮　時：巳—天文宮
   */
  @Test
  public void testGetPalm_Female() {
    PalmIF impl = new PalmImpl();
    Palm palm = impl.getPalm(Gender.女, 戌, false, 9, 24, 戌, defaultImpl, true);

    assertSame(戌, palm.getYear());
    assertSame(寅, palm.getMonth());
    assertSame(卯, palm.getDay());
    assertSame(巳, palm.getHour());
  }

  /**
   * 例如：子年三月初八辰時生人，男命即從子（天貴宮）上起正月，三月則落在寅（天權宮）上，寅為初運（1－10歲），卯（天破宮）為二運（11－20歲）也，
   * 辰（天奸宮）為三運（21－30歲），餘此類推。
   */
  @Test
  public void testGetMajorFortunes_Male() {
    Map<Integer, Branch> map = impl.getPalm(Gender.男, 子, false, 3, 8, 辰, defaultImpl, true).getMajorFortunes(10);
    assertEquals(寅, map.get(1));
    assertEquals(卯, map.get(11));
    assertEquals(辰, map.get(21));
  }

  /**
   * 子年三月初八辰時生人
   * 女則逆行，也從子（天貴宮）上起正月，三月則落在戌（天藝宮）上，戌為初運（1－10歲），酉（天刃宮）為二運（11－20歲）也，餘此類推。
   */
  @Test
  public void testGetMajorFortunes_Female() {
    Map<Integer, Branch> map = impl.getPalm(Gender.女, 子, false, 3, 8, 辰, defaultImpl, true).getMajorFortunes(10);
    assertEquals(戌, map.get(1));
    assertEquals(酉, map.get(11));
    assertEquals(申, map.get(21));
  }

  /**
   * 子年三月初八辰時生人，
   * 男命即從子（天貴宮）上起正月，三月則落在寅（天權宮）上，寅上起到初八，最後日落在酉上。
   * 酉（天刃宮）為一歲，二歲則為戌（天藝宮），亥（天壽宮）為三歲，餘此類推。
   */
  @Test
  public void testGetMinorFortunes_Male() {
    assertSame(酉, impl.getPalm(Gender.男, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(1));
    assertSame(戌, impl.getPalm(Gender.男, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(2));
    assertSame(亥, impl.getPalm(Gender.男, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(3));
    assertSame(子, impl.getPalm(Gender.男, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(4));
  }

  /**
   * 子年三月初八辰時生人，
   * 女則逆行，即從子（天貴宮）上起正月，三月則落在戌（天藝宮）上，從戌起到初八，日落在卯上，
   * 卯（天破宮）為一歲，二歲則為寅（天權宮），丑（天厄宮）為三歲，餘此類推。
   */
  @Test
  public void testGetMinorFortunes_Female() {
    assertSame(卯, impl.getPalm(Gender.女, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(1));
    assertSame(寅, impl.getPalm(Gender.女, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(2));
    assertSame(丑, impl.getPalm(Gender.女, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(3));
    assertSame(子, impl.getPalm(Gender.女, 子, false, 3, 8, 辰, defaultImpl, true).getMinorFortunes(4));
  }


  /**
   * 命宮
   * 如上例男命農曆辛巳年正月十二日午時生，生時為午，落在戌宮，從戌（天藝宮）上起午，順數到卯便至未上，命宮即為未（天驛宮）
   */
  @Test
  public void testRising_Male() {
    Palm palm = impl.getPalm(Gender.男, 巳, false, 1, 12, 午, defaultImpl, true);
    assertSame(未, palm.getBranch(Palm.House.命));

  }

  /**
   * 命宮
   * 女命農曆子年三月初八辰時生，生時為辰時，落在亥宮，從亥上（天壽宮）起辰，逆數到卯便至子（天貴宮）上，命宮即為子。
   */
  @Test
  public void testRising_Female() {
    Palm palm = impl.getPalm(Gender.女, 子, false, 3, 8, 辰, defaultImpl, true);
    assertSame(子, palm.getBranch(Palm.House.命));
  }


  @Test
  public void testRising() {
    Palm palm = impl.getPalm(Gender.男, 未, false, 4, 1, 子, defaultImpl, true);
    assertSame(丑, palm.getBranch(Palm.House.命));
  }

  /**
   * 陽男陰女順
   * 陰男陽女逆
   * 測試資料
   * http://curtisyen74.pixnet.net/blog/post/19456721-達摩掌金排盤
   */
  @Test
  public void testAlternativePositive_1() {
    System.out.println("palm1 = " + impl.getPalm(Gender.女, 申, false, 8, 20, 戌, defaultImpl, true));
    System.out.println("palm2 = " + impl.getPalm(Gender.女, 申, false, 8, 20, 戌, new PositiveGenderYinYangImpl(), true));
  }
}