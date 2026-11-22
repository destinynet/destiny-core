/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.tools

import kotlin.math.PI

private const val DEGREES_TO_RADIANS = PI / 180.0
private const val RADIANS_TO_DEGREES = 180.0 / PI

/**
 * 度 → 弧度。純 Kotlin，取代 JDK 的 `Math.toRadians`（JVM 才有）。
 * 與 JDK 乘同一個常數，結果逐位元相同（`AnglesTest`）。
 */
fun toRadians(degrees: Double): Double = degrees * DEGREES_TO_RADIANS

/** 弧度 → 度。取代 JDK 的 `Math.toDegrees`，結果逐位元相同 */
fun toDegrees(radians: Double): Double = radians * RADIANS_TO_DEGREES
