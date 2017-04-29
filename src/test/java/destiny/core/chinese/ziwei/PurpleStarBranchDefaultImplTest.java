/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static destiny.core.chinese.Branch.午;
import static destiny.core.chinese.Branch.未;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class PurpleStarBranchDefaultImplTest {

  private Logger logger = LoggerFactory.getLogger(getClass());


  IPurpleStarBranch impl = new PurpleStarBranchDefaultImpl();

  @Test
  public void testTitle() {
    assertNotNull(impl.getTitle(Locale.TAIWAN));
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE));
    logger.info("tw = {} , cn = {}" , impl.getTitle(Locale.TAIWAN) , impl.getTitle(Locale.CHINA));
  }

  /**
   * 計算紫微星所在宮位 , 驗證資料見此教學頁面 http://bit.ly/2oo2hZz
   */
  @Test
  public void getBranchOfPurpleStar() throws Exception {
    // 假設某個人出生日是23日，五行局為金四局 ==> 紫微在午
    assertSame(午, impl.getBranchOfPurpleStar(4, 23));

    // 假設有一個人是24日生，金四局 ==> 紫微在未
    assertSame(未, impl.getBranchOfPurpleStar(4, 24));
  }

}