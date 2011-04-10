/**
 * @author smallufo 
 * Created on 2008/5/2 at 上午 1:22:15
 */ 
package destiny.utils.random;

import java.io.IOException;

import destiny.core.chinese.YinYangIF;

public interface RandomIF
{
  /**
   * @param count 要傳回的整數數量
   * @param min 最小的整數值 (包含)
   * @param max 最大的整數值 (包含)
   * @return count 數量個整數
   * @throws IOException 
   */
  public int[] getIntegers(int count , int min , int max) throws IOException;
  
  /** 取得 count 個陰陽 
   * @throws IOException */
  public YinYangIF[] getYinYangs(int count) throws IOException;
}
