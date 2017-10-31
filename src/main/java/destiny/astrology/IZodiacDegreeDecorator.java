/**
 * @author smallufo
 * Created on 2008/1/14 at 下午 11:08:27
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

/** 黃道帶上的字串輸出 */
public interface IZodiacDegreeDecorator {

  @NotNull
  String getOutputString(double degree);

  @NotNull
  String getSimpOutString(double degree);
}
