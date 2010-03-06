/**
 * @author smallufo 
 * Created on 2008/1/14 at 下午 11:08:27
 */ 
package destiny.astrology;

/** 黃道帶上的字串輸出 */
public interface ZodiacDegreeDecoratorIF
{
  public String getOutputString(double degree);
  
  public String getSimpOutString(double degree);
}
