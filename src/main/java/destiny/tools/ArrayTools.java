/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.tools;

public class ArrayTools {

  public static <T> T get(T[] ARRAY, int index) {
    int length = ARRAY.length;
    if (index < 0)
      return get(ARRAY , index + length);
    else if (index >= length)
      return get(ARRAY , index % length);
    else
      return ARRAY[index];
  }
}
