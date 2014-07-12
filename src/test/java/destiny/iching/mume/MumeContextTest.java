package destiny.iching.mume;

import destiny.iching.Hexagram;
import destiny.iching.Symbol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MumeContextTest {

  @Test
  public void testGetTargetHexagram() throws Exception {
    assertEquals(Hexagram.姤  , new MumeContext(Hexagram.getHexagram(Symbol.乾 , Symbol.乾) , 1).getTargetHexagram());
    assertEquals(Hexagram.同人, new MumeContext(Hexagram.getHexagram(Symbol.乾 , Symbol.乾) , 2).getTargetHexagram());
    assertEquals(Hexagram.履  , new MumeContext(Hexagram.getHexagram(Symbol.乾 , Symbol.乾) , 3).getTargetHexagram());
    assertEquals(Hexagram.小畜, new MumeContext(Hexagram.getHexagram(Symbol.乾 , Symbol.乾) , 4).getTargetHexagram());
    assertEquals(Hexagram.大有, new MumeContext(Hexagram.getHexagram(Symbol.乾 , Symbol.乾) , 5).getTargetHexagram());
    assertEquals(Hexagram.夬  , new MumeContext(Hexagram.getHexagram(Symbol.乾 , Symbol.乾) , 6).getTargetHexagram());

    assertEquals(Hexagram.復  , new MumeContext(Hexagram.getHexagram(Symbol.坤 , Symbol.坤) , 1).getTargetHexagram());
    assertEquals(Hexagram.師  , new MumeContext(Hexagram.getHexagram(Symbol.坤 , Symbol.坤) , 2).getTargetHexagram());
    assertEquals(Hexagram.謙  , new MumeContext(Hexagram.getHexagram(Symbol.坤 , Symbol.坤) , 3).getTargetHexagram());
    assertEquals(Hexagram.豫  , new MumeContext(Hexagram.getHexagram(Symbol.坤 , Symbol.坤) , 4).getTargetHexagram());
    assertEquals(Hexagram.比  , new MumeContext(Hexagram.getHexagram(Symbol.坤 , Symbol.坤) , 5).getTargetHexagram());
    assertEquals(Hexagram.剝  , new MumeContext(Hexagram.getHexagram(Symbol.坤 , Symbol.坤) , 6).getTargetHexagram());

  }
}