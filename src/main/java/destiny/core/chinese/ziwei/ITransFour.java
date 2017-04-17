/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 四化
 * 參考實作 http://destiny66.com/xuetang/tzyy_wei/question/question.htm
 *
 * 四化
 *
 * 　庚年及壬年的安法不同。《全書》庚年在卷二的安法是「日武同陰」 {@link TransFourSouthImpl}
 *   ，但是在卷四古例安法中則為「日武陰同」。 {@link TransFourDefaultImpl}
 *
 *   壬年的安法為「梁紫府武」。 {@link TransFourDefaultImpl}
 *
 * 　　《全集》庚年安法為「日武同相」， {@link TransFourNorthImpl}
 *           壬年安法為「梁紫左武」。 {@link TransFourNorthImpl}
 *
 * 　　由此可知，某些重四化的流派，其四化的安法是以《全集》為主。至於尚有其它安法，則非是。
 * */
public interface ITransFour extends Descriptive {

  enum Value {祿 , 權 , 科 , 忌}

  /** 取得「某天干（可能是本命年、大限、或是流年、流月、流日、流時）的某四化」是哪顆星 */
  ZStar getStarOf(Stem stem , Value value);

  /** 類似前者，但逆算：計算此星於此干，是否有四化，若有的話，其為何者 */
  default Optional<Value> getValueOf(ZStar star , Stem stem) {
    // 先把本年四化的四顆星都找出來
    Map<ZStar , Value> map =
      Arrays.stream(Value.values())
      .map(type -> Tuple.tuple(getStarOf(stem , type) , type))
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    return Optional.ofNullable(map.getOrDefault(star , null));
  }
}
