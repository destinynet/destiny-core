/**
 * Created by smallufo on 2025-05-08.
 */
package destiny.core.misc


/**
 * 畢達哥拉斯 生命靈數
 */
data class PythagoreanNumerology(
  val lifePathNumber: Int,
  val birthdayNumber: Int,
  val personalYearNumber: Int,
  val challengeNumbers: List<Int>,  // 4個挑戰數
  val pinnacleNumbers: List<Int>    // 4個高潮數
)
