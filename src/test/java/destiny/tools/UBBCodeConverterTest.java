/**
 * Created by smallufo on 2017-03-23.
 */
package destiny.tools;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UBBCodeConverterTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void reQuote() throws Exception {
    UBBCodeConverter codeConverter = new UBBCodeConverter();
    String s1 = "123 [quote] 內文 [/quote] 456";
    String s2 = "123 \n" + "<br>[quote] 內文 [/quote] \n" + "<br>456";
    String s3 = "123 \n" + "<br>[quote] 內文<br>哈囉 [/quote] \n" + "<br>456";
    String s4 = "123 \n" + "<br>[quote] 內文\n哈囉 [/quote] \n" + "<br>456";
    logger.info("s1 : {}", codeConverter.getAll(s1));
    logger.info("s2 : {}", codeConverter.getAll(s2));
    logger.info("s3 : {}", codeConverter.getAll(s3));
    logger.info("s4 : {}", codeConverter.getAll(s4));
  }

}