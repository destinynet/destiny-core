/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese;

import static destiny.core.chinese.FiveElement.*;

public class BranchTools {

  /** 地支三合 */
  public static FiveElement trilogy(BranchIF branch) {
    switch (branch.getBranch()) {
      case 申: case 子: case 辰: return 水;
      case 巳: case 酉: case 丑: return 金;
      case 亥: case 卯: case 未: return 木;
      case 寅: case 午: case 戌: return 火;
      default: throw new AssertionError(branch);
    }
  }

  /** 地支三會 */
  public static FiveElement direction(BranchIF branch) {
    switch (branch.getBranch()) {
      case 亥: case 子: case 丑: return 水;
      case 寅: case 卯: case 辰: return 木;
      case 巳: case 午: case 未: return 火;
      case 申: case 酉: case 戌: return 金;
      default: throw new AssertionError(branch);
    }
  }
}
