/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import org.junit.Test;

import static destiny.core.chinese.Branch.午;
import static destiny.core.chinese.Branch.未;
import static org.junit.Assert.assertSame;

public class PurpleStarBranchDefaultImplTest {

  /**
   * 計算紫微星所在宮位 , 驗證資料見此教學頁面 http://bit.ly/2oo2hZz
   */
  @Test
  public void getBranchOfPurpleStar() throws Exception {
    IPurpleStarBranch impl = new PurpleStarBranchDefaultImpl();
    // 假設某個人出生日是23日，五行局為金四局 ==> 紫微在午
    assertSame(午, impl.getBranchOfPurpleStar(4, 23));

    // 假設有一個人是24日生，金四局 ==> 紫微在未
    assertSame(未, impl.getBranchOfPurpleStar(4, 24));
  }

}