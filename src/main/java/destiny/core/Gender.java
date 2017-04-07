package destiny.core;

import java.io.Serializable;
import java.util.Locale;

/** 性別 */
public enum Gender implements Serializable
{
  男(true),
  女(false);
  
  private final boolean male;
  
  Gender(boolean value)
  {
    this.male = value;
  }

  @Override
  public String toString()
  {
    return GenderDecorator.getOutputString(this , Locale.getDefault());
  }
  
  public String toString(Locale locale)
  {
    return GenderDecorator.getOutputString(this , locale);
  }

  public boolean isMale()
  {
    return male;
  }
  
}
