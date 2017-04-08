/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.core.chinese.liuren;

import org.junit.Test;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.liuren.General.*;
import static org.junit.Assert.assertSame;

public class GeneralStemBranchLiurenTest {

  @Test
  public void get() throws Exception {
    GeneralStemBranchIF impl = new GeneralStemBranchLiuren();
    assertSame(貴人 , impl.get(丑));
    assertSame(螣蛇 , impl.get(巳));
    assertSame(朱雀 , impl.get(午));
    assertSame(六合 , impl.get(卯));
    assertSame(勾陳 , impl.get(辰));
    assertSame(青龍 , impl.get(寅));
    assertSame(天空 , impl.get(戌));
    assertSame(白虎 , impl.get(申));
    assertSame(太常 , impl.get(未));
    assertSame(玄武 , impl.get(亥));
    assertSame(太陰 , impl.get(酉));
    assertSame(天后 , impl.get(子));
  }

}