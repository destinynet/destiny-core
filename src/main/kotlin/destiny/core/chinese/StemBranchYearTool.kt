/**
 * Created by smallufo on 2024-10-22.
 */
package destiny.core.chinese


object StemBranchYearTool {

  private const val BASE_YEAR = 1984
  private val BASE_STEM_BRANCH = StemBranch.甲子

  fun getYearStemBranch(year: Int): StemBranch {
    val offset = (year - BASE_YEAR) % 60
    return if (offset >= 0) {
      BASE_STEM_BRANCH.next(offset)
    } else {
      BASE_STEM_BRANCH.next(offset + 60)
    }
  }
}
