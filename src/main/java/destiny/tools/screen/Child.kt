package destiny.tools.screen

/**
 * @author smallufo
 * @date 2002/8/29
 * @time 上午 02:23:42
 */
internal class Child(val canvas: Canvas, val x: Int, val y: Int) {

  fun setParent(c: Canvas) {
    this.canvas.parent = c
  }
}
