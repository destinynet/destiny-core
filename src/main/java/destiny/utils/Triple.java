/** 2009/12/7 下午8:23:21 by smallufo */
package destiny.utils;

public class Triple<A, B, C> extends Tuple<A, B>
{
  private final C c;

  public Triple(A a, B b , C c)
  {
    super(a, b);
    this.c = c;
  }
  
  public C getThird()
  {
    return c;
  }

  @Override
  public String toString()
  {
    return "Triple [a="+getFirst()+", b="+getSecond()+", c="+c+"]";
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((c == null) ? 0 : c.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (!(obj instanceof Triple))
      return false;
    @SuppressWarnings("rawtypes")
    Triple other = (Triple) obj;
    if (c == null)
    {
      if (other.c != null)
        return false;
    }
    else if (!c.equals(other.c))
      return false;
    return true;
  }

}

