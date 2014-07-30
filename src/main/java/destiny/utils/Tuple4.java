/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.utils;

import org.jetbrains.annotations.NotNull;

public class Tuple4<A , B , C , D> extends Triple<A , B , C>  {
  private final D d;

  public Tuple4(A a, B b, C c, D d) {
    super(a, b, c);
    this.d = d;
  }

  @NotNull
  public static <A,B,C,D> Tuple4<A,B,C,D> of(A a , B b , C c , D d) {
    return new Tuple4(a , b , c , d);
  }

  public D getFourth() {
    return d;
  }

  @NotNull
  @Override
  public String toString() {
    return "Tuple4 [a = " + getFirst() + " , b = " + getSecond() + " , c = " + getThird() + " , d = " + getFourth() + " ]";
  }
}
