/*
 * @author smallufo
 * @date 2005/1/21
 * @time 上午 10:45:47
 */
package destiny.IChing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.Test;

public class HexagramDefaultComparatorTest
{
  @Test
  public void testHexagramDefaultComparator()
  {
    Hexagram[] hexagramDatas = new Hexagram[] 
      { Hexagram.乾 , Hexagram.姤   , Hexagram.遯   , Hexagram.否   , Hexagram.觀   , Hexagram.剝   , Hexagram.晉   , Hexagram.大有 ,
        Hexagram.兌 , Hexagram.困   , Hexagram.萃   , Hexagram.咸   , Hexagram.蹇   , Hexagram.謙   , Hexagram.小過 , Hexagram.歸妹 ,
        Hexagram.離 , Hexagram.旅   , Hexagram.鼎   , Hexagram.未濟 , Hexagram.蒙   , Hexagram.渙   , Hexagram.訟   , Hexagram.同人 ,
        Hexagram.震 , Hexagram.豫   , Hexagram.解   , Hexagram.恆   , Hexagram.升   , Hexagram.井   , Hexagram.大過 , Hexagram.隨   ,
        Hexagram.巽 , Hexagram.小畜 , Hexagram.家人 , Hexagram.益   , Hexagram.無妄 , Hexagram.噬嗑 , Hexagram.頤   , Hexagram.蠱   ,
        Hexagram.坎 , Hexagram.節   , Hexagram.屯   , Hexagram.既濟 , Hexagram.革   , Hexagram.豐   , Hexagram.明夷 , Hexagram.師   ,
        Hexagram.艮 , Hexagram.賁   , Hexagram.大畜 , Hexagram.損   , Hexagram.睽   , Hexagram.履   , Hexagram.中孚 , Hexagram.漸   ,
        Hexagram.坤 , Hexagram.復   , Hexagram.臨   , Hexagram.泰   , Hexagram.大壯 , Hexagram.夬   , Hexagram.需   , Hexagram.比
      };
    
    Hexagram[] expected  = new Hexagram[] 
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

    Arrays.sort(hexagramDatas,new HexagramDefaultComparator());
    for (int i=0; i < hexagramDatas.length ; i++)
    {
      assertSame(expected[i] , hexagramDatas[i]);
    }
  }

  @Test
  public void testHexagramSequence()
  {
    HexagramSequenceIF sequenceImpl = new HexagramDefaultComparator();
    
    assertEquals(Hexagram.乾   , Hexagram.getHexagram(1 , sequenceImpl) );
    assertEquals(Hexagram.坤   , Hexagram.getHexagram(2 , sequenceImpl) );
    assertEquals(Hexagram.屯   , Hexagram.getHexagram(3 , sequenceImpl) );
    assertEquals(Hexagram.蒙   , Hexagram.getHexagram(4 , sequenceImpl) );
    assertEquals(Hexagram.需   , Hexagram.getHexagram(5 , sequenceImpl) );
    assertEquals(Hexagram.訟   , Hexagram.getHexagram(6 , sequenceImpl) );
    assertEquals(Hexagram.師   , Hexagram.getHexagram(7 , sequenceImpl) );
  }
  
  @Test
  public void testGetIndex()
  {
    HexagramSequenceIF impl = new HexagramDefaultComparator();
    
    assertEquals(1 , impl.getIndex(Hexagram.乾));
    assertEquals(2 , impl.getIndex(Hexagram.坤));
    assertEquals(3 , impl.getIndex(Hexagram.屯));
    assertEquals(4 , impl.getIndex(Hexagram.蒙));
    assertEquals(5 , impl.getIndex(Hexagram.需));
    assertEquals(6 , impl.getIndex(Hexagram.訟));
    assertEquals(7 , impl.getIndex(Hexagram.師));
    
    assertEquals(8 , impl.getIndex(Hexagram.比));
    assertEquals(9 , impl.getIndex(Hexagram.小畜));
    assertEquals(10, impl.getIndex(Hexagram.履));
    assertEquals(11, impl.getIndex(Hexagram.泰));
    assertEquals(12, impl.getIndex(Hexagram.否));
    
    assertEquals(13, impl.getIndex(Hexagram.同人));
    assertEquals(14, impl.getIndex(Hexagram.大有));
    assertEquals(15, impl.getIndex(Hexagram.謙));
    assertEquals(16, impl.getIndex(Hexagram.豫));
    assertEquals(17, impl.getIndex(Hexagram.隨));
    
    assertEquals(18, impl.getIndex(Hexagram.蠱));
    assertEquals(19, impl.getIndex(Hexagram.臨));
    assertEquals(20, impl.getIndex(Hexagram.觀));
    assertEquals(21, impl.getIndex(Hexagram.噬嗑));
    assertEquals(22, impl.getIndex(Hexagram.賁));
    
    assertEquals(23, impl.getIndex(Hexagram.剝));
    assertEquals(24, impl.getIndex(Hexagram.復));
    assertEquals(25, impl.getIndex(Hexagram.無妄));
    assertEquals(26, impl.getIndex(Hexagram.大畜));
    assertEquals(27, impl.getIndex(Hexagram.頤));
    
    assertEquals(28, impl.getIndex(Hexagram.大過));
    assertEquals(29, impl.getIndex(Hexagram.坎));
    assertEquals(30, impl.getIndex(Hexagram.離));
    
    assertEquals(31, impl.getIndex(Hexagram.咸));
    assertEquals(32, impl.getIndex(Hexagram.恆));
    assertEquals(33, impl.getIndex(Hexagram.遯));
    assertEquals(34, impl.getIndex(Hexagram.大壯));
    
    assertEquals(35, impl.getIndex(Hexagram.晉));
    assertEquals(36, impl.getIndex(Hexagram.明夷));
    assertEquals(37, impl.getIndex(Hexagram.家人));
    assertEquals(38, impl.getIndex(Hexagram.睽));
    
    assertEquals(39, impl.getIndex(Hexagram.蹇));    
    assertEquals(40, impl.getIndex(Hexagram.解));
    assertEquals(41, impl.getIndex(Hexagram.損));
    assertEquals(42, impl.getIndex(Hexagram.益));
    assertEquals(43, impl.getIndex(Hexagram.夬));
    assertEquals(44, impl.getIndex(Hexagram.姤));
    assertEquals(45, impl.getIndex(Hexagram.萃));
    
    assertEquals(46, impl.getIndex(Hexagram.升));
    assertEquals(47, impl.getIndex(Hexagram.困));
    assertEquals(48, impl.getIndex(Hexagram.井));
    assertEquals(49, impl.getIndex(Hexagram.革));
    assertEquals(50, impl.getIndex(Hexagram.鼎));
    assertEquals(51, impl.getIndex(Hexagram.震));
    
    assertEquals(52, impl.getIndex(Hexagram.艮));
    assertEquals(53, impl.getIndex(Hexagram.漸));
    assertEquals(54, impl.getIndex(Hexagram.歸妹));
    assertEquals(55, impl.getIndex(Hexagram.豐));
    assertEquals(56, impl.getIndex(Hexagram.旅));
    assertEquals(57, impl.getIndex(Hexagram.巽));
    
    assertEquals(58, impl.getIndex(Hexagram.兌));
    assertEquals(59, impl.getIndex(Hexagram.渙));
    assertEquals(60, impl.getIndex(Hexagram.節));
    assertEquals(61, impl.getIndex(Hexagram.中孚));
    
    assertEquals(62, impl.getIndex(Hexagram.小過));
    assertEquals(63, impl.getIndex(Hexagram.既濟));
    assertEquals(64, impl.getIndex(Hexagram.未濟));
  }
  
  @Test
  public void testGetHexagram()
  {
    HexagramSequenceIF impl = new HexagramDefaultComparator();
    assertSame(Hexagram.坤 , impl.getHexagram(-126));
  }
}
