package destiny.tools.screen

import kotlin.test.Test

class CanvasTest {

  @Test
  fun testRun() {
    val c1 = Canvas(30, 30)
    val c2 = Canvas(20, 20)
    val c3 = Canvas(10, 10)
    c1.add(c2, 5, 5)
    c2.add(c3, 5, 7)

    c1.setBackground('|')
    c2.setBackground('-')
    c3.setBackground('.')

    c1.setText("這是最大的c1".toByteArray(), 5, 2)
    c2.setText("大家好".toByteArray(), 5, 5)
    c2.setText("這是c2".toByteArray(), 5, 6)
    c3.setText("這是c3".toByteArray(), 2, 2)
    println(c3)
    println(c2)
    println(c1)
  }

}