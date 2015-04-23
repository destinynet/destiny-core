/**
 * @author smallufo 
 * Created on 2009/3/24 at 上午 10:23:54
 */ 
package destiny.core;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

public class BirthDataWithLocNameEmail extends BirthDataWithLocName
{
  private String name;
  private String email;
  
  public BirthDataWithLocNameEmail(String name, Gender gender, Time time, Location location)
  {
    super(name, gender, time, location);
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void setName(String name)
  {
    this.name = name;
  }

  
  
  
}
