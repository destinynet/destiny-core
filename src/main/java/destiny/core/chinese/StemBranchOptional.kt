/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese

import destiny.tools.ArrayTools
import java.io.Serializable


/**
 * 干、支 可其中一個為空，也可兩個都為空
 * nullable & UnConstrained
 */
interface IStemBranchOptional : Serializable {
  val stem: Stem?
  val branch: Branch?

}

data class StemBranchOptional(
  override val stem: Stem?,
  override val branch: Branch?) : IStemBranchOptional {


  val index: Int?
    get() = getIndex(this)

  fun next(n: Int): StemBranchOptional? {
    val index = getIndex(this)

    return index?.let {
      get(it + n)
    }
  }

  override fun toString(): String {
    return "[$stem $branch]"
  }

  companion object {

    // 0[甲子] ~ 59[癸亥]
    private val ARRAY: Array<StemBranchOptional> by lazy {
      var n = 0
      generateSequence {
        StemBranchOptional(Stem[n % 10], Branch[n % 12]).takeIf { n++ < 60 }
      }.toList().toTypedArray()
    }

    fun empty(): StemBranchOptional {
      return StemBranchOptional(null, null)
    }


    // 0[甲子] ~ 59[癸亥]
    private operator fun get(index: Int): StemBranchOptional {
      return ArrayTools[ARRAY, index]
    }

    operator fun get(stem: Stem?, branch: Branch?): StemBranchOptional {

      return if (stem != null && branch != null) {
        val sIndex = Stem.getIndex(stem)
        val bIndex = Branch.getIndex(branch)

        when (sIndex - bIndex) {
          0, -10 -> get(bIndex)
          2, -8 -> get(bIndex + 12)
          4, -6 -> get(bIndex + 24)
          6, -4 -> get(bIndex + 36)
          8, -2 -> get(bIndex + 48)
          else -> throw AssertionError("Invalid Stem/Branch Combination! $stem & $branch , value = ${sIndex - bIndex}")
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

    operator fun iterator(): Iterator<StemBranchOptional> {
      return ARRAY.iterator()
    }
  }


}
