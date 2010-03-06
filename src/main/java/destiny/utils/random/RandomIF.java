/**
 * @author smallufo 
 * Created on 2008/5/2 at 上午 1:22:15
 */ 
package destiny.utils.random;

public interface RandomIF
{
  /**
   * @param count 要傳回的整數數量
   * @param min 最小的整數值 (包含)
   * @param max 最大的整數值 (包含)
   * @return count 數量個整數
   */
  public int[] getIntegers(int count , int min , int max);
}
