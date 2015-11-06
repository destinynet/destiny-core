/**
 * @author smallufo
 * Created on 2011/4/10 at 下午11:21:13
 */
package destiny.tools.random;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface RandomService {

  /**
   * @param count 要傳回的整數數量
   * @param min   最小的整數值 (包含)
   * @param max   最大的整數值 (包含)
   */
  int[] getIntegers(int count, int min, int max);

  default List<Integer> getIntegerList(int count, int min, int max) {
    return IntStream.of(getIntegers(count, min, max)).boxed().collect(Collectors.toList());
  }

  default <T extends Enum<?>> T randomEnum(Class<T> clazz) {
    int length = clazz.getEnumConstants().length;
    int r = getIntegers(length, 0, length - 1)[0];
    return clazz.getEnumConstants()[r];
  }

  boolean[] getYinYangs(int count);
}
