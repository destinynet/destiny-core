/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese;

import org.junit.Test;

public class TaiXuanTest {

  @Test
  public void testGetStem() throws Exception {
    for (HeavenlyStems stem : HeavenlyStems.values()) {
      System.out.println(stem + " -> " + TaiXuan.get(stem));
    }
  }

  @Test
  public void testGetBranch() throws Exception {
    for (EarthlyBranches branch : EarthlyBranches.values())
      System.out.println(branch + " -> " + TaiXuan.get(branch));
  }
}