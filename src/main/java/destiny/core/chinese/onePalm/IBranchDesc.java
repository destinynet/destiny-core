/**
 * Created by smallufo on 2015-05-22.
 */
package destiny.core.chinese.onePalm;

import destiny.core.chinese.Branch;

/** 由地支，取得解釋 */
public interface IBranchDesc {

  /** 取得某宮位的簡介 */
  String getHouseIntro(Branch house);

  /** 取得某柱（座落於某宮）的解釋 */
  String getContent(Palm.Pillar pillar , Branch house);

  /** 取得「時柱」的詩詞 */
  String getPoem(Branch hourBranch);

  /** 取得「時柱」的解釋 */
  String getContent(Branch hourBranch);
}
