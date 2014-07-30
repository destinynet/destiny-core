/** 2009/11/26 上午10:39:35 by smallufo */
package destiny.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  @NotNull
  public static <A,B> Tuple<A,B> of(A a , B b) {
    return new Tuple<>(a,b);
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

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((a == null) ? 0 : a.hashCode());
    result = prime * result + ((b == null) ? 0 : b.hashCode());
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Tuple))
      return false;
    @SuppressWarnings("rawtypes")
    Tuple other = (Tuple) obj;
    if (a == null)
    {
      if (other.a != null)
        return false;
    }
    else if (!a.equals(other.a))
      return false;
    if (b == null)
    {
      if (other.b != null)
        return false;
    }
    else if (!b.equals(other.b))
      return false;
    return true;
  }
}

