/**
 * Created by smallufo on 2025-05-08.
 */
package destiny.core.misc

import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.temporal.ChronoField
import kotlin.math.abs


object NumerologyTools {

  /**
   * 計算完整的畢達哥拉斯數字命理學結果
   */
  fun getPythagoreanNumerology(birthDate: ChronoLocalDate, currentYear: Int = LocalDate.now().year): PythagoreanNumerology {
    // 解析年、月、日
    val year = birthDate.get(ChronoField.YEAR_OF_ERA)
    val month = birthDate.get(ChronoField.MONTH_OF_YEAR)
    val day = birthDate.get(ChronoField.DAY_OF_MONTH)

    // 計算生命靈數
    val lifePathRaw = reduceSum(year) + reduceSum(month) + reduceSum(day)
    val lifePathNumber = reduceFinal(lifePathRaw)

    // 計算生日數字
    val birthdayNumber = reduceFinal(day)

    // 計算個人年數
    val personalYearNumber = calculatePersonalYearNumber(month, day, currentYear)

    // 計算挑戰數
    val challengeNumbers = calculateChallengeNumbers(year, month, day)

    // 計算高潮數
    val pinnacleNumbers = calculatePinnacleNumbers(year, month, day)

    return PythagoreanNumerology(
      lifePathNumber,
      birthdayNumber,
      personalYearNumber,
      challengeNumbers,
      pinnacleNumbers
    )
  }

  /**
   * 計算個人年數
   * 公式: (出生月 + 出生日 + 當前年份) 化簡為單一數字
   */
  fun calculatePersonalYearNumber(birthMonth: Int, birthDay: Int, currentYear: Int): Int {
    val sum = reduceSum(birthMonth) + reduceSum(birthDay) + reduceSum(currentYear)
    return reduceFinal(sum)
  }

  /**
   * 計算4個挑戰數
   * 第一挑戰: |出生月 - 出生日| 化簡
   * 第二挑戰: |出生年 - 出生月| 化簡
   * 第三挑戰: |第一挑戰 - 第二挑戰| 化簡
   * 第四挑戰: |出生年 - 出生日| 化簡
   */
  fun calculateChallengeNumbers(birthYear: Int, birthMonth: Int, birthDay: Int): List<Int> {
    val monthSum = reduceSum(birthMonth)
    val daySum = reduceSum(birthDay)
    val yearSum = reduceSum(birthYear)

    val challenge1 = reduceFinal(abs(monthSum - daySum))
    val challenge2 = reduceFinal(abs(yearSum - monthSum))
    val challenge3 = reduceFinal(abs(challenge1 - challenge2))
    val challenge4 = reduceFinal(abs(yearSum - daySum))

    return listOf(challenge1, challenge2, challenge3, challenge4)
  }

  /**
   * 計算4個高潮數
   * 第一高潮: (出生月 + 出生日) 化簡
   * 第二高潮: (出生日 + 出生年) 化簡
   * 第三高潮: (第一高潮 + 第二高潮) 化簡
   * 第四高潮: (出生月 + 出生年) 化簡
   */
  fun calculatePinnacleNumbers(birthYear: Int, birthMonth: Int, birthDay: Int): List<Int> {
    val monthSum = reduceSum(birthMonth)
    val daySum = reduceSum(birthDay)
    val yearSum = reduceSum(birthYear)

    val pinnacle1 = reduceFinal(monthSum + daySum)
    val pinnacle2 = reduceFinal(daySum + yearSum)
    val pinnacle3 = reduceFinal(pinnacle1 + pinnacle2)
    val pinnacle4 = reduceFinal(monthSum + yearSum)

    return listOf(pinnacle1, pinnacle2, pinnacle3, pinnacle4)
  }

  /**
   * 將數字各位相加 (例如: 1990 -> 1+9+9+0 = 19)
   */
  fun reduceSum(num: Int): Int {
    return num.toString().map { it.digitToInt() }.sum()
  }

  /**
   * 將數字遞歸化簡至個位數，但保留主數 11、22、33
   */
  fun reduceFinal(num: Int): Int {
    val masterNumbers = setOf(11, 22, 33)
    var n = num
    while (n > 9 && n !in masterNumbers) {
      n = reduceSum(n)
    }
    return n
  }
}
