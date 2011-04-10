/**
 * @author smallufo
 * Created on 2011/4/10 at 下午11:21:13
 */
package destiny.utils.random;

import destiny.core.chinese.YinYangIF;

public interface RandomService
{
  public int[] getIntegers(int count , int min , int max) ;
  
  public YinYangIF[] getYinYangs(int count) ;
}
