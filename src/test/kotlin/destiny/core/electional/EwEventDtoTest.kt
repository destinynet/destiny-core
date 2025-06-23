/**
 * Created by smallufo on 2025-06-23.
 */
package destiny.core.electional

import destiny.core.Scale
import destiny.core.chinese.Branch.子
import destiny.core.chinese.Stem.甲
import destiny.core.electional.Ew.Companion.findSpanByBranches
import destiny.core.electional.Ew.Companion.findSpanByStems
import kotlin.test.Test
import kotlin.test.assertSame

class EwEventDtoTest {

  @Test
  fun testFindSpanByStems() {
    setOf(
      Ew.NatalStems(setOf(Scale.DAY), 甲),
      Ew.NatalStems(setOf(Scale.HOUR), 甲)
    ).findSpanByStems().also { span ->
      assertSame(Span.HOURS , span)
    }

    setOf(
      Ew.NatalStems(setOf(Scale.DAY), 甲),
      Ew.NatalStems(setOf(Scale.DAY), 甲)
    ).findSpanByStems().also { span ->
      assertSame(Span.DAY, span)
    }

    setOf(
      Ew.NatalStems(setOf(Scale.DAY, Scale.HOUR), 甲),
      Ew.NatalStems(setOf(Scale.DAY), 甲)
    ).findSpanByStems().also { span ->
      assertSame(Span.HOURS, span)
    }
  }

  @Test
  fun testFindSpanByBranches() {
    setOf(
      Ew.NatalBranches(setOf(Scale.DAY), 子),
      Ew.NatalBranches(setOf(Scale.HOUR), 子)
    ).findSpanByBranches().also { span ->
      assertSame(Span.HOURS, span)
    }

    setOf(
      Ew.NatalBranches(setOf(Scale.DAY, Scale.HOUR), 子),
      Ew.NatalBranches(setOf(Scale.HOUR), 子)
    ).findSpanByBranches().also { span ->
      assertSame(Span.HOURS, span)
    }

    setOf(
      Ew.NatalBranches(setOf(Scale.DAY, Scale.HOUR), 子),
      Ew.NatalBranches(setOf(Scale.DAY), 子)
    ).findSpanByBranches().also { span ->
      assertSame(Span.HOURS, span)
    }

    setOf(
      Ew.NatalBranches(setOf(Scale.DAY), 子),
      Ew.NatalBranches(setOf(Scale.DAY), 子)
    ).findSpanByBranches().also { span ->
      assertSame(Span.DAY, span)
    }
  }
}
