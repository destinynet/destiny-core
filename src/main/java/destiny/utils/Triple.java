/** 2009/12/7 下午8:23:21 by smallufo */
package destiny.utils;

import org.jetbrains.annotations.NotNull;

public class Triple<A, B, C> extends Tuple<A, B>
{
  private final C c;

  protected Triple(A a, B b, C c) {
    super(a, b);
    this.c = c;
  }

  @NotNull
  public static <A,B,C> Triple<A,B,C> of(A a , B b , C c) {
    return new Triple<>(a,b,c);
  }

  public C getThird()
  {
    return c;
  }

  @NotNull
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

