/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese

import destiny.tools.ArrayTools
import java.io.Serializable
import java.util.*

open class StemBranchOptional internal constructor(open val stem: Stem?, open val branch: Branch?) : Serializable {


  open val index: Int?
    get() = StemBranchOptional.getIndex(this)

  val stemOptional: Optional<Stem>
    get() = Optional.ofNullable(stem)

  val branchOptional: Optional<Branch>
    get() = Optional.ofNullable(branch)


  init {
    check(stem, branch)
  }

  open fun next(n: Int): StemBranchOptional? {
    val index = getIndex(this)
    return if (index != null) {
      get(index + n)
    } else {
      null
    }
  }

  override fun toString(): String {
    return "[" + stem + ' '.toString() + branch + ']'.toString()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other)
      return true
    if (other !is StemBranchOptional)
      return false
    val that = other as StemBranchOptional?
    return stem == that!!.stem && branch == that.branch
  }

  override fun hashCode(): Int {
    return Objects.hash(stem, branch)
  }

  companion object {

    // 0[甲子] ~ 59[癸亥]
    private val ARRAY : Array<StemBranchOptional> by lazy {
      var n = 0
      generateSequence {
        StemBranchOptional(Stem[n % 10], Branch[n % 12]).takeIf { n++ < 60 } }.toList().toTypedArray()
    }

    fun empty(): StemBranchOptional {
      return StemBranchOptional(null, null)
    }


    // 0[甲子] ~ 59[癸亥]
    private operator fun get(index: Int): StemBranchOptional {
      return ArrayTools[ARRAY, index]
    }

    operator fun get(stem: Stem?, branch: Branch?): StemBranchOptional {
      check(stem, branch)

      return if (stem != null && branch != null) {
        val sIndex = Stem.getIndex(stem)
        val bIndex = Branch.getIndex(branch)
        when (sIndex - bIndex) {
          0, -10 -> get(bIndex)
          2, -8 -> get(bIndex + 12)
          4, -6 -> get(bIndex + 24)
          6, -4 -> get(bIndex + 36)
          8, -2 -> get(bIndex + 48)
          else -> throw AssertionError("Invalid Stem/Branch Combination!")
        }
      } else {
        StemBranchOptional(stem, branch)
      }
    }

    operator fun get(stemChar: Char, branchChar: Char): StemBranchOptional {
      return get(Stem[stemChar], Branch[branchChar])
    }

    operator fun get(stemBranch: String): StemBranchOptional {
      return if (stemBranch.length != 2)
        throw RuntimeException("The length of $stemBranch must equal to 2 !")
      else
        get(stemBranch[0], stemBranch[1])
    }

    operator fun get(sb: StemBranch): StemBranchOptional {
      return get(sb.stem, sb.branch)
    }

    private fun getIndex(sb: StemBranchOptional): Int? {

      if (sb.stem != null && sb.branch != null) {
        return ARRAY.first { it == sb }.let { ARRAY.indexOf(it) }
      }
      return null
    }

    private fun check(stem: Stem?, branch: Branch?) {
      if (stem != null && branch != null) {
        if (stem.booleanValue != SimpleBranch.getBooleanValue(branch)) {
          throw RuntimeException("Stem/Branch combination illegal ! $stem cannot be combined with $branch")
        }
      }
    }

    operator fun iterator(): Iterator<StemBranchOptional> {
      return ARRAY.iterator()
    }
  }


}
