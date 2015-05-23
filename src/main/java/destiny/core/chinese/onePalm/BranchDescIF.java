/**
 * Created by smallufo on 2015-05-22.
 */
package destiny.core.chinese.onePalm;

import destiny.core.chinese.EarthlyBranches;

/** 由地支，取得解釋 */
public interface BranchDescIF {

  String getPoem(EarthlyBranches branch);

  String getContent(EarthlyBranches branches);
}
