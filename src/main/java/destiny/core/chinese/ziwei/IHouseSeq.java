/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;

/** 12宮的順序 */
public interface IHouseSeq extends Descriptive {

  House[] getHouses();

  /** 取得 下 n 個 宮位 是什麼。 若 n = 0 , 則傳回自己 */
  House next(House from , int n);

  default House prev(House from , int n) {
    return next(from , (0-n));
  }

  /**
   * 此 宮位 , 「領先」另一個宮位，幾步
   * 此值一定為正值
   * 如果某些情況（例如 「全書派」要找不存在的「相貌宮」） ，就會傳回 -1
   */
  int getAheadOf(House h1 , House h2);
}
