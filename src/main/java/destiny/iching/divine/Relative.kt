/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

/** 六親 */
enum class Relative {
  兄弟, 官鬼, 妻財, 父母, 子孫;

  fun abbreviate(): String {
    return if (this == 妻財) {
      "財"
    } else {
      this.toString()[0].toString()
    }
  }
}