/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

public interface GeneralSeqIF {

  /** 取得下 n 個天將 , n 若等於 0  , 則傳回自己 */
  General next(General from , int n);

  default General prev(General from , int n) {
    return next(from , (0-n));
  }
}
