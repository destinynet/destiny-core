/*
 * @author smallufo
 * @date 2005/1/21
 * @time 上午 11:02:28
 */
package destiny.iching.divine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.Test;

import destiny.iching.Hexagram;
import destiny.iching.HexagramSequenceIF;

public class HexagramDivinationComparatorTest
{
  @Test
  public void testHexagramDivinationComparator()
  {
    Hexagram[] hexagrams = new Hexagram[] 
      { Hexagram.乾 , Hexagram.坤 , Hexagram.屯  , Hexagram.蒙 , Hexagram.需 , Hexagram.訟 , Hexagram.師 , 
        Hexagram.比 , Hexagram.小畜 , Hexagram.履 , Hexagram.泰 , Hexagram.否 , 
        Hexagram.同人 , Hexagram.大有 , Hexagram.謙 , Hexagram.豫 , Hexagram.隨 ,
        Hexagram.蠱 , Hexagram.臨 , Hexagram.觀 , Hexagram.噬嗑 , Hexagram.賁 ,
        Hexagram.剝 , Hexagram.復 , Hexagram.無妄 , Hexagram.大畜 , Hexagram.頤 ,
        Hexagram.大過 , Hexagram.坎 , Hexagram.離 ,
        Hexagram.咸 , Hexagram.恆 , Hexagram.遯 , Hexagram.大壯 ,
        Hexagram.晉 , Hexagram.明夷 , Hexagram.家人 , Hexagram.睽 ,
        Hexagram.蹇 , Hexagram.解 , Hexagram.損 , Hexagram.益 , Hexagram.夬 , Hexagram.姤 , Hexagram.萃 ,
        Hexagram.升 , Hexagram.困 , Hexagram.井 , Hexagram.革 , Hexagram.鼎 , Hexagram.震 ,
        Hexagram.艮 , Hexagram.漸 , Hexagram.歸妹 , Hexagram.豐 , Hexagram.旅 , Hexagram.巽 ,
        Hexagram.兌 , Hexagram.渙 , Hexagram.節 , Hexagram.中孚 ,
        Hexagram.小過 , Hexagram.既濟 , Hexagram.未濟
      };
    
    Hexagram[] expected  = new Hexagram[] 
      { Hexagram.乾 , Hexagram.姤   , Hexagram.遯   , Hexagram.否   , Hexagram.觀   , Hexagram.剝   , Hexagram.晉   , Hexagram.大有 ,
        Hexagram.震 , Hexagram.豫   , Hexagram.解   , Hexagram.恆   , Hexagram.升   , Hexagram.井   , Hexagram.大過 , Hexagram.隨   ,
        Hexagram.坎 , Hexagram.節   , Hexagram.屯   , Hexagram.既濟 , Hexagram.革   , Hexagram.豐   , Hexagram.明夷 , Hexagram.師   ,
        Hexagram.艮 , Hexagram.賁   , Hexagram.大畜 , Hexagram.損   , Hexagram.睽   , Hexagram.履   , Hexagram.中孚 , Hexagram.漸   ,
        Hexagram.坤 , Hexagram.復   , Hexagram.臨   , Hexagram.泰   , Hexagram.大壯 , Hexagram.夬   , Hexagram.需   , Hexagram.比   ,
        Hexagram.巽 , Hexagram.小畜 , Hexagram.家人 , Hexagram.益   , Hexagram.無妄 , Hexagram.噬嗑 , Hexagram.頤   , Hexagram.蠱   ,
        Hexagram.離 , Hexagram.旅   , Hexagram.鼎   , Hexagram.未濟 , Hexagram.蒙   , Hexagram.渙   , Hexagram.訟   , Hexagram.同人 ,
        Hexagram.兌 , Hexagram.困   , Hexagram.萃   , Hexagram.咸   , Hexagram.蹇   , Hexagram.謙   , Hexagram.小過 , Hexagram.歸妹 
      };
    
    Arrays.sort(hexagrams,new HexagramDivinationComparator());
    for (int i=0; i < hexagrams.length ; i++)
    {
      assertSame(expected[i] , hexagrams[i]);
    }
  }
  
  @Test
  public void testHexagramSequence()
  {
    HexagramSequenceIF sequence = new HexagramDivinationComparator();
    
    assertEquals(Hexagram.乾   , Hexagram.getHexagram(1 , sequence) );
    assertEquals(Hexagram.姤   , Hexagram.getHexagram(2 , sequence) );
    assertEquals(Hexagram.遯   , Hexagram.getHexagram(3 , sequence) );
    assertEquals(Hexagram.否   , Hexagram.getHexagram(4 , sequence) );
    assertEquals(Hexagram.觀   , Hexagram.getHexagram(5 , sequence) );
    assertEquals(Hexagram.剝   , Hexagram.getHexagram(6 , sequence) );
    assertEquals(Hexagram.晉   , Hexagram.getHexagram(7 , sequence) );
    assertEquals(Hexagram.大有 , Hexagram.getHexagram(8 , sequence) );    
  }

}
