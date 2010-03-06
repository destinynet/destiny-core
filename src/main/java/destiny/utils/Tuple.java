/** 2009/11/26 上午10:39:35 by smallufo */
package destiny.utils;

import java.io.Serializable;

public class Tuple<A, B> implements Serializable
{
  private final A a;
  private final B b;
  
  public Tuple(A a , B b)
  {
    this.a = a;
    this.b = b;
  }
  
  public A getFirst()
  {
    return a;
  }
  
  public B getSecond()
  {
    return b;
  }

  @Override
  public String toString()
  {
    return "Tuple [a=" + a + ", b=" + b + "]";
  }
}

