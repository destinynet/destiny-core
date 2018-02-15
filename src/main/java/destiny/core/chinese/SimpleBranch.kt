/**
 * @author smallufo
 * @date 2002/8/12
 * @time 上午 02:57:12
 */
package destiny.core.chinese

/** 實作 五行 [IFiveElement] 以及 陰陽 [IYinYang] 以及取得地支順序 getIndex() 的地支  */
enum class SimpleBranch(override val branch: Branch) : IBranch<SimpleBranch>, IFiveElement, IYinYang {
  子(Branch.子),
  丑(Branch.丑),
  寅(Branch.寅),
  卯(Branch.卯),
  辰(Branch.辰),
  巳(Branch.巳),
  午(Branch.午),
  未(Branch.未),
  申(Branch.申),
  酉(Branch.酉),
  戌(Branch.戌),
  亥(Branch.亥);


  override val fiveElement: FiveElement
    get() = Companion.getFiveElement(branch)


  override val booleanValue: Boolean
    get() = Companion.getBooleanValue(this.branch)


  override fun getAheadOf(other: SimpleBranch): Int {
    return branch.getAheadOf(other.branch)
  }

  companion object {

    operator fun get(b: Branch): SimpleBranch {
      return SimpleBranch.valueOf(b.name)
    }

    /**
     * 是否還要再做一個 static FiveElement getFiveElement(SimpleEarthlyBranches eb) ... ??
     * Java 5 中的 enum 無法被繼承 , 真是麻煩...
     */
    fun getFiveElement(b: Branch): FiveElement {
      return when (b) {
        Branch.亥, Branch.子 -> FiveElement.水
        Branch.丑, Branch.辰, Branch.未, Branch.戌 -> FiveElement.土
        Branch.寅, Branch.卯 -> FiveElement.木
        Branch.巳, Branch.午 -> FiveElement.火
        Branch.申, Branch.酉 -> FiveElement.金
      }
    }

    fun getBooleanValue(branch: Branch): Boolean {
      return Branch.getIndex(branch) % 2 == 0
    }
  }
}
