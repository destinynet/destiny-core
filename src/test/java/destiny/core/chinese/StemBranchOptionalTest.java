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
    StemBranchOptional.get(Optional.of(甲), Optional.of(子));
    StemBranchOptional.get("甲子");
    StemBranchOptional.get('甲', '子');
    StemBranchOptional.get(Optional.of(甲), Optional.of(寅));
    StemBranchOptional.get(Optional.of(甲), Optional.empty());
    StemBranchOptional.get(Optional.of(癸), Optional.of(丑));
    StemBranchOptional.get(Optional.of(癸), Optional.of(亥));
    StemBranchOptional.get(Optional.empty(), Optional.of(亥));
    StemBranchOptional.get(Optional.empty(), Optional.empty());
  }

  @Test
  public void testCheck_failed() {
    try {
      // 錯誤的組合
      StemBranchOptional.get(Optional.of(甲), Optional.of(丑));
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
    assertSame(StemBranchOptional.get("甲子") , StemBranchOptional.get(Optional.of(甲), Optional.of(子)) );
    assertSame(StemBranchOptional.get("甲子") , StemBranchOptional.get('甲', '子'));
    assertSame(StemBranchOptional.get('甲', '子') , StemBranchOptional.get(Optional.of(甲), Optional.of(子)) );
    assertSame(StemBranchOptional.get(甲, 子) , StemBranchOptional.get(Optional.of(甲), Optional.of(子)) );

    assertNotSame(StemBranchOptional.get("甲子"), StemBranchOptional.get('甲', '寅'));
  }

  @Test
  public void testGetIndex() {
    assertEquals(Optional.of(0), StemBranchOptional.get(Optional.of(甲), Optional.of(子)).getIndexOpt());
    assertEquals(Optional.of(59), StemBranchOptional.get(Optional.of(癸), Optional.of(亥)).getIndexOpt());

    assertEquals(Optional.empty(), StemBranchOptional.get(Optional.of(甲), Optional.empty()).getIndexOpt());
    assertEquals(Optional.empty(), StemBranchOptional.get(Optional.empty(), Optional.of(子)).getIndexOpt());
  }

  @Test
  public void testNext() {
    //assertEquals(new StemBranchOptional(Optional.of(甲), Optional.of(子)), StemBranchOptional.get(Optional.of(甲), Optional.of(子)));
    assertSame(
      StemBranchOptional.get(Optional.of(乙), Optional.of(丑)),
      StemBranchOptional.get(Optional.of(甲), Optional.of(子)).nextOpt(1).get()
    );
  }

}