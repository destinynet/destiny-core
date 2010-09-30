/**
 * @author smallufo 
 * Created on 2006/5/27 at 上午 06:30:21
 */ 
package destiny.IChing.divine;

import java.io.Serializable;
import java.util.Locale;

import destiny.core.Descriptive;

public abstract class AbstractSettingsOfStemBranch implements SettingsOfStemBranchIF, Descriptive , Serializable
{
  public String getTitle(Locale locale)
  {
    return "";
  }

  public String getDescription(Locale locale)
  {
    return "";
  }

}
