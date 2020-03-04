/**
 * @author smallufo
 * Created on 2002/8/28 at 下午 11:30:06
 */
package destiny.tools.screen

import java.io.Serializable

class Canvas(private val width: Int, val height: Int) : Serializable {

  private val content: ByteArray = ByteArray(width * height)

  /**
   * 取得目前 Canvas 的 Container
   */
  /**
   * 設定目前 Canvas 的 Container
   */
  var parent: Canvas? = null
  private val children = mutableListOf<Child>()

  /**
   * 取得目前 Canvas 的大小
   */
  private val size: Int
    get() {
      return if (children.size == 0)
         width * height
      else {
        width * height + children.sumBy { it.canvas.size }
      }
    }

  init {
    for (i in content.indices) {
      content[i] = ' '.toByte()
    }
  }

  fun setBackground(c: Char) {
    for (i in content.indices) {
      content[i] = c.toByte()
    }
  }

  /**
   * 新增子 Canvas
   */
  fun add(c: Canvas, x: Int, y: Int) {
    c.parent = this
    children.add(Child(c, x, y))
  }

  /**
   * 設定橫向字元
   */
  fun setText(b: ByteArray, x: Int, y: Int) {
    /**
     * [x,y] = content[ (y-1)*w + x -1 ]
     */
    for (i in b.indices) {
      content[(y - 1) * width + x + i - 1] = b[i]
    }
  }

  /**
   * 設定橫向字元
   */
  fun setText(s: String, x: Int, y: Int) {
    val b = s.toByteArray()
    this.setText(b, x, y)
  }

  private fun getContent(): ByteArray {
    val result: ByteArray
    //如果沒有 children , 就傳回自己的 content[]
    if (this.children.size == 0) {
      result = this.content
    } else {
      //有 children
      for (aChildren in children) {
        val childContent = aChildren.canvas.getContent()
        for (j in childContent.indices) {
          //走訪childContent , 必須計算 childContent 每個 byte 在 parent 的座標 , 並且複製過去
          val childX = j - j / aChildren.canvas.width * aChildren.canvas.width + 1
          val childY = j / aChildren.canvas.width + 1
          this.content[(aChildren.y + (childY - 1) - 1) * this.width + (aChildren.x + (childX - 1)) - 1] =
            childContent[j]
        }
      } //for
      result = this.content
    } //else
    return result
  }

  override fun toString(): String {
    val sb = StringBuilder()
    for (i in 0 until height) {
      val s = String(this.getContent(), i * width, width)
      sb.append(s)
      sb.append('\n')
    }
    return sb.toString()
  }
}
