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
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPositionOptional(planet).map(Position::getLng).flatMap(planetDegree -> {
      LunarNode south = LunarNode.of(LunarNode.NorthSouth.SOUTH, nodeType);
      return h.getPositionOptional(south).map(Position::getLng).flatMap(southDeg -> {
        if (Horoscope.getAngle(planetDegree, southDeg) <= 1) {
          logger.debug("{} 與 {} 形成 {}", planet, south, Aspect.CONJUNCTION);
          return Optional.of(Tuple.tuple("comment", new Object[]{planet, south, Aspect.CONJUNCTION}));
        }
        return Optional.empty();
      });
    });
  }

}
