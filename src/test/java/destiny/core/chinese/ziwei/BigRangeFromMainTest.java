/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.YinYangIF;
import org.jooq.lambda.tuple.Tuple;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class BigRangeFromMainTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  IBigRange impl = new BigRangeFromMain();
  IHouseSeq seq = new HouseSeqDefaultImpl();

  @Test
  public void getRange() throws Exception {
    // 陽男順行
    assertEquals(Tuple.tuple(2.0, 11.0), impl.getRange(House.命宮, 2, YinYangIF.陽, Gender.男, FortuneOutput.虛歲, seq));
    assertEquals(Tuple.tuple(12.0, 21.0), impl.getRange(House.兄弟, 2, YinYangIF.陽, Gender.男, FortuneOutput.虛歲, seq));

    // 陰女順行
    assertEquals(Tuple.tuple(2.0, 11.0), impl.getRange(House.命宮, 2, YinYangIF.陰, Gender.女, FortuneOutput.虛歲, seq));
    assertEquals(Tuple.tuple(12.0, 21.0), impl.getRange(House.兄弟, 2, YinYangIF.陰, Gender.女, FortuneOutput.虛歲, seq));

    // 陰男逆行
    assertEquals(Tuple.tuple(2.0, 11.0), impl.getRange(House.命宮, 2, YinYangIF.陰, Gender.男, FortuneOutput.虛歲, seq));
    assertEquals(Tuple.tuple(12.0, 21.0), impl.getRange(House.父母, 2, YinYangIF.陰, Gender.男, FortuneOutput.虛歲, seq));

    // 陽女逆行
    assertEquals(Tuple.tuple(2.0, 11.0), impl.getRange(House.命宮, 2, YinYangIF.陽, Gender.女, FortuneOutput.虛歲, seq));
    assertEquals(Tuple.tuple(12.0, 21.0), impl.getRange(House.父母, 2, YinYangIF.陽, Gender.女, FortuneOutput.虛歲, seq));
  }

}