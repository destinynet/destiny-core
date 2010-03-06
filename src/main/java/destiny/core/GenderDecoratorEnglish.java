/**
 * @author smallufo 
 * Created on 2008/4/2 at 上午 1:28:52
 */ 
package destiny.core;

import destiny.utils.Decorator;

public class GenderDecoratorEnglish implements Decorator<Gender>
{
  @Override
  public String getOutputString(Gender gender)
  {
    return (gender == Gender.男) ? "Male" : "Female";
  }
}
