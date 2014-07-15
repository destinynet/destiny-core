/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.utils;

public class Tuple4<A , B , C , D> extends Triple<A , B , C>  {
  private final D d;

  public Tuple4(A a, B b, C c, D d) {
    super(a, b, c);
    this.d = d;
  }

  public static <A,B,C,D> Tuple4<A,B,C,D> of(A a , B b , C c , D d) {
    return new Tuple4(a , b , c , d);
  }

  public D getFourth() {
    return d;
  }

  @Override
  public String toString() {
    return "Tuple4 [a = " + getFirst() + " , b = " + getSecond() + " , c = " + getThird() + " , d = " + getFourth() + " ]";
  }
}
