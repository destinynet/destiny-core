/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:30:05
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile conjunction with Dragon's Tail (Moon's South Node). */
public final class Partile_Conj_South_Node extends Rule {

  /** 內定採用 NodeType.MEAN */
  private NodeType nodeType = NodeType.MEAN;


  public Partile_Conj_South_Node() {
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public void setNodeType(NodeType nodeType) {
    this.nodeType = nodeType;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext) {
    double planetDegree = horoscopeContext.getPosition(planet).getLng();
    double southDeg;
    if (nodeType == NodeType.TRUE)
      southDeg = horoscopeContext.getPosition(LunarNode.SOUTH_TRUE).getLng();
    else
      southDeg = horoscopeContext.getPosition(LunarNode.SOUTH_MEAN).getLng();

    if (Horoscope2.getAngle(planetDegree, southDeg) <= 1) {
      if (nodeType == NodeType.TRUE) {
        //addComment(Locale.TAIWAN , planet + " 與 " + LunarNode.SOUTH_TRUE + " 形成 " + Aspect.CONJUNCTION);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, LunarNode.SOUTH_TRUE, Aspect.CONJUNCTION}));
      }
      else {
        //addComment(Locale.TAIWAN , planet + " 與 " + LunarNode.SOUTH_MEAN + " 形成 " + Aspect.CONJUNCTION);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, LunarNode.SOUTH_MEAN, Aspect.CONJUNCTION}));
      }
    }
    return Optional.empty();
  }

}
