/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Optional;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static org.junit.Assert.*;

public class StemBranchOptionalTest {

  @Test
  public void testCheck_passed() {
    StemBranchOptional.get(甲, 子);
    StemBranchOptional.get("甲子");
    StemBranchOptional.get('甲', '子');
    StemBranchOptional.get(甲, 寅);
    StemBranchOptional.get(甲, null);
    StemBranchOptional.get(癸, 丑);
    StemBranchOptional.get(癸, 亥);
    StemBranchOptional.get(null, 亥);
    StemBranchOptional.get(null, null);
  }

  @Test
  public void testCheck_failed() {
    try {
      // 錯誤的組合
      StemBranchOptional.get(甲, 丑);
      fail("error");
    } catch (RuntimeException e) {
      assertTrue(true);
    }

    try {
      // 錯誤的組合
      StemBranchOptional.get("甲丑");
      fail("error");
    } catch (RuntimeException e) {
      assertTrue(true);
    }

    try {
      // 錯誤的組合
      StemBranchOptional.get('甲' , '丑');
      fail("error");
    } catch (RuntimeException e) {
      assertTrue(true);
    }


    try {
      // 錯誤的組合
      StemBranchOptional.get(甲 , 丑);
      fail("error");
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }


  @Test
  public void testSame() {
    assertSame(StemBranchOptional.get("甲子") , StemBranchOptional.get(甲, 子) );
    assertSame(StemBranchOptional.get("甲子") , StemBranchOptional.get('甲', '子'));
    assertSame(StemBranchOptional.get('甲', '子') , StemBranchOptional.get(甲, 子) );
    assertSame(StemBranchOptional.get(甲, 子) , StemBranchOptional.get(甲, 子) );

    assertNotSame(StemBranchOptional.get("甲子"), StemBranchOptional.get('甲', '寅'));
  }

  @Test
  public void testGetIndex() {
    assertEquals(Optional.of(0), StemBranchOptional.get(甲, 子).getIndexOpt());
    assertEquals(Optional.of(59), StemBranchOptional.get(癸, 亥).getIndexOpt());

    assertEquals(Optional.empty(), StemBranchOptional.get(甲, null).getIndexOpt());
    assertEquals(Optional.empty(), StemBranchOptional.get(null, 子).getIndexOpt());
  }

  @Test
  public void testNext() {
    //assertEquals(new StemBranchOptional(甲, 子), StemBranchOptional.get(甲, 子));
    assertSame(
      StemBranchOptional.get(乙, 丑),
      StemBranchOptional.get(甲, 子).nextOpt(1).get()
    );
  }

}