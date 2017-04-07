/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical;

import destiny.astrology.Aspect;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple3;

/**
 * <pre>
 * Refranation 定義：返回、臨陣脫逃
 * 這是六種 Denials of Perfection 之一 ，定義於此：
 * http://www.skyscript.co.uk/tobyn2.html#ref
 * 當兩顆星正在 apply 某交角，在形成 Perfect 之前，其中一顆轉為逆行
 *
 * 此程式必須能正確判斷以下兩種情形
 * 1. 本星即將 apply 他星，而在 perfect 前，本星逆行，代表自我退縮。
 * 2. 本星即將 apply 他星，而在 perfect 前，他星逆行，代表對方退縮。
 *
 * TODO : 應該加上演算法：如果星體順轉逆（或逆轉順），並且逃離了 aspect 的有效範圍，才是真的「臨陣脫逃」
 * </pre>
 */
public interface RefranationIF {

  @NotNull
  Tuple3<Boolean , Point, Aspect> resultOf(HoroscopeContext context , Planet planet , Point otherPoint);
}
