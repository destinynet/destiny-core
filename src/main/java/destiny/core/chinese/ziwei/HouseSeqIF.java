/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;

/** 12宮的順序 */
public interface HouseSeqIF extends Descriptive {

  /** 取得 下 n 個 宮位 是什麼。 若 n = 0 , 則傳回自己 */
  House next(House from , int n);

  default House prev(House from , int n) {
    return next(from , (0-n));
  }
}
