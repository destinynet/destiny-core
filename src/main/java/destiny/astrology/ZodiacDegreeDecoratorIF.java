/**
 * @author smallufo 
 * Created on 2008/1/14 at 下午 11:08:27
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

/** 黃道帶上的字串輸出 */
public interface ZodiacDegreeDecoratorIF
{
  @NotNull
  public String getOutputString(double degree);
  
  @NotNull
  public String getSimpOutString(double degree);
}
