/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.core.chinese.liuren;

import org.junit.Test;

import static destiny.core.chinese.liuren.General.*;
import static destiny.core.chinese.liuren.General.貴人;
import static destiny.core.chinese.liuren.General.青龍;
import static org.junit.Assert.assertSame;

public class GeneralZhaoSeqImplTest {

  private GeneralSeqIF seqZhao = new GeneralZhaoSeqImpl();

  @Test
  public void next() throws Exception {
    assertSame(青龍 , 青龍.next(-24, seqZhao));
    assertSame(青龍 , 青龍.next(-12, seqZhao));
    assertSame(六合 , 青龍.next(-11, seqZhao));
    assertSame(天后 , 青龍.next(-2, seqZhao));
    assertSame(貴人 , 青龍.next(-1, seqZhao));
    assertSame(青龍 , 青龍.next(0 , seqZhao));
    assertSame(六合 , 青龍.next(1 , seqZhao));
    assertSame(勾陳 , 青龍.next(2 , seqZhao));
    assertSame(貴人 , 青龍.next(11, seqZhao));
    assertSame(青龍 , 青龍.next(12, seqZhao));
    assertSame(青龍 , 青龍.next(24, seqZhao));
  }

  @Test
  public void prev() {
    assertSame(青龍 , 青龍.prev(24 , seqZhao));
    assertSame(青龍 , 青龍.prev(12 , seqZhao));
    assertSame(六合 , 青龍.prev(11 , seqZhao));
    assertSame(天后 , 青龍.prev(2 , seqZhao));
    assertSame(貴人 , 青龍.prev(1 , seqZhao));
    assertSame(青龍 , 青龍.prev(0 , seqZhao));
    assertSame(六合 , 青龍.prev(-1, seqZhao));
    assertSame(勾陳 , 青龍.prev(-2, seqZhao));
    assertSame(貴人 , 青龍.prev(-11, seqZhao));
    assertSame(青龍 , 青龍.prev(-12, seqZhao));
    assertSame(青龍 , 青龍.prev(-24, seqZhao));
  }
}