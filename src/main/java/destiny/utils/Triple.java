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

}

