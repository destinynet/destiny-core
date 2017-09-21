/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:45:23
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile conjunction with Dragon's Head (Moon's North Node). */
public final class Partile_Conj_North_Node extends Rule {

  /** 內定採用 NodeType.MEAN */
  private NodeType nodeType = NodeType.MEAN;

  public Partile_Conj_North_Node() {
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public void setNodeType(NodeType nodeType) {
    this.nodeType = nodeType;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    return h.getPosition(planet).map(Position::getLng).flatMap(planetDegree -> {
      LunarNode north = LunarNode.of(LunarNode.NorthSouth.NORTH, nodeType);
      return h.getPosition(north).map(Position::getLng).flatMap(northDeg -> {
        if (Horoscope.getAngle(planetDegree, northDeg) <= 1) {
          logger.debug("{} 與 {} 形成 {}", planet, north, Aspect.CONJUNCTION);
          return Optional.of(Tuple.tuple("comment", new Object[]{planet, north, Aspect.CONJUNCTION}));
        }
        return Optional.empty();
      });
    });
  }

}
