/**
 * @author smallufo 
 * Created on 2008/5/2 at 上午 2:10:16
 */ 
package destiny.utils.random;

import java.io.Serializable;
import java.util.Random;

/**
 * 利用 Java 內部的 Random 來實作 Integer Generator
 */
public class RandomImpl implements RandomIF , Serializable
{
  public RandomImpl()
  {
  }

  @Override
  public int[] getIntegers(int count, int min, int max)
  {
    int[] results = new int[count];
    Random r = new Random();
    for(int i=0 ; i<count ; i++)
      results[i] = min + r.nextInt( (max-min+1));
    return results;
  }
}
