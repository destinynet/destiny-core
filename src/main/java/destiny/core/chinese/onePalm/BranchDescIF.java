/**
 * Created by smallufo on 2015-05-22.
 */
package destiny.core.chinese.onePalm;

import destiny.core.chinese.EarthlyBranches;

/** 由地支，取得解釋 */
public interface BranchDescIF {

  /** 取得某宮位的簡介 */
  String getHouseIntro(EarthlyBranches house);

  /** 取得某柱（座落於某宮）的解釋 */
  String getContent(Palm.Pillar pillar , EarthlyBranches house);

  /** 取得「時柱」的詩詞 */
  String getPoem(EarthlyBranches hourBranch);

  /** 取得「時柱」的解釋 */
  String getContent(EarthlyBranches hourBranch);
}
