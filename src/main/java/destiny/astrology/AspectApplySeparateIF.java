/**
 * Created by smallufo at 2008/11/6 下午 2:41:54
 */
package destiny.astrology;

import java.util.Collection;
import java.util.Optional;

/** 一個星盤當中，兩個星體，是否形成交角。以及即將形成 (APPLYING , 入相位)，還是離開該交角 (SEPARATING , 出相位) */
public interface AspectApplySeparateIF {

  enum AspectType {APPLYING, SEPARATING}

  /** 如果不是形成 aspect 交角，會傳回 empty */
  Optional<AspectType> getAspectType(HoroscopeContextIF horoscopeContext, Point p1, Point p2, Aspect aspect);

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null */
  Optional<AspectType> getAspectType(HoroscopeContextIF horoscopeContext, Point p1, Point p2, Collection<Aspect> aspects);
}
