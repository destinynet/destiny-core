/**
 * @author smallufo
 * Created on 2008/5/2 at 上午 1:22:15
 */
package destiny.utils.random;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface RandomIF {

  /**
   * @param count 要傳回的整數數量
   * @param min   最小的整數值 (包含)
   * @param max   最大的整數值 (包含)
   * @return count 數量個整數
   * @throws IOException
   */
  int[] getIntegers(int count, int min, int max) throws IOException;

  default List<Integer> getIntegerList(int count, int min, int max) throws IOException {
    return IntStream.of(getIntegers(count , min , max)).boxed().collect(Collectors.toList());
  }

  default <T extends Enum<?>> T randomEnum(Class<T> clazz) throws IOException{
    int length = clazz.getEnumConstants().length;
    int r = getIntegers(length , 0 , length-1)[0];
    return clazz.getEnumConstants()[r];
  }

  /**
   * 取得 count 個陰陽
   *
   * @throws IOException
   */
  boolean[] getYinYangs(int count) throws IOException;
}
