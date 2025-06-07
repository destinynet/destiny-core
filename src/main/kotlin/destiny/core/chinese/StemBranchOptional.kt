/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese

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
    return index?.let { get(it + n) }
  }

  override fun toString(): String {
    return "[$stem $branch]"
  }

  companion object {

    // 0[甲子] ~ 59[癸亥]
    private val SIXTY_COMBINATIONS: List<StemBranchOptional> by lazy {
      (0 until 60).map { n ->
        StemBranchOptional(Stem[n % 10], Branch[n % 12])
      }
    }

    // 建立 StemBranchOptional 到索引的映射
    private val indexMap: Map<StemBranchOptional, Int> by lazy {
      SIXTY_COMBINATIONS.withIndex().associate { (index, sb) -> sb to index }
    }

    fun empty(): StemBranchOptional {
      return StemBranchOptional(null, null)
    }


    // 0[甲子] ~ 59[癸亥]
    private operator fun get(index: Int): StemBranchOptional {
      return SIXTY_COMBINATIONS[index.mod(60)]
    }

    operator fun get(stem: Stem?, branch: Branch?): StemBranchOptional {

      return if (stem != null && branch != null) {
        val sIndex = stem.index
        val bIndex = branch.index

        when (sIndex - bIndex) {
          0, -10 -> get(bIndex)
          2, -8  -> get(bIndex + 12)
          4, -6  -> get(bIndex + 24)
          6, -4  -> get(bIndex + 36)
          8, -2  -> get(bIndex + 48)
          else   -> StemBranchOptional(stem, branch)
        }
      } else {
        StemBranchOptional(stem, branch)
      }
    }

    operator fun get(stemChar: Char, branchChar: Char): StemBranchOptional {
      return get(Stem[stemChar], Branch[branchChar])
    }

    operator fun get(stemBranch: String): StemBranchOptional {
      require(stemBranch.length == 2) {
        "The length of $stemBranch must equal to 2 !"
      }
      return get(stemBranch[0], stemBranch[1])
    }

    operator fun get(sb: StemBranch): StemBranchOptional {
      return get(sb.stem, sb.branch)
    }

    private fun getIndex(sb: StemBranchOptional): Int? {
      return if (sb.stem != null && sb.branch != null) {
        indexMap[sb]
      } else {
        null
      }
    }

    operator fun iterator(): Iterator<StemBranchOptional> {
      return SIXTY_COMBINATIONS.iterator()
    }
  }


}
