/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines
import kotlin.io.path.walk
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.fail

/**
 * KMP 就緒度的 **ratchet（棘輪）** —— 只准前進，不准後退。
 *
 * ## 為什麼需要這個
 *
 * destiny-core 正在逐步移除 JVM-only 相依，好讓大部分的資料結構日後能進入
 * KMP 的 `commonMain`（見 [JSerializable]）。這是長工期的工作，中間會穿插
 * 其他功能開發 —— 沒有機制擋著的話，新檔案會不知不覺又 `import java.util.Locale`，
 * 幾個月後回頭發現進度倒退。
 *
 * ## 運作方式
 *
 * 採**反向清單**（quarantine，見 `src/test/resources/$QUARANTINE`）而非白名單：
 *
 *  - 清單列出「目前仍帶 JVM-only 相依」的檔案
 *  - **不在清單上的檔案一律必須乾淨** —— 所以新增的檔案預設受管制
 *  - 想在新檔案用 JVM API，必須顯式把它加進清單，那是一個看得見的決定
 *  - 清單只准變短。改乾淨了就把該行刪掉
 *
 * 清單有**陳舊項目**（檔案已乾淨 / 已改名 / 已刪除）同樣會失敗，
 * 否則清單會謊報進度。
 *
 * ## 維護
 *
 * 重新產生清單：
 * ```
 * mvn test -DskipTests=false -Dtest=KmpReadinessRatchetTest -Dkmp.ratchet.regenerate=true
 * ```
 * 產生後**務必人工檢視 diff** —— 這個開關會把「新增的違規」一併洗白，
 * 它只該用於「一次清掉一批」之後的收尾。
 *
 * ## 不在管制範圍
 *
 *  - `src/test/` —— 測試日後屬於 `jvmTest`，本來就可用 JVM API
 *  - `destiny.tools.KotlinLogging` / [JSerializable] 這類自家接縫 ——
 *    它們的實作日後會換成 `expect`/`actual`，呼叫端不必改
 */
class KmpReadinessRatchetTest {

  @Test
  fun `KMP 就緒度不得倒退`() {
    val srcRoot = Paths.get(SRC_ROOT)
    check(srcRoot.toFile().isDirectory) { "找不到 $SRC_ROOT，請確認測試的工作目錄是模組根目錄" }

    val actualDirty: Map<String, List<String>> = scanDirtyFiles(srcRoot)

    if (System.getProperty("kmp.ratchet.regenerate") == "true") {
      regenerate(actualDirty.keys.sorted())
      return
    }

    val quarantined: Set<String> = readQuarantine()

    // (1) 倒退：不在清單上、卻帶了 JVM-only 相依
    val regressions = actualDirty.filterKeys { it !in quarantined }
    // (2) 陳舊：在清單上、但其實已經乾淨（或檔案已不存在）
    val stale = quarantined - actualDirty.keys

    if (regressions.isEmpty() && stale.isEmpty()) return

    fail(
      buildString {
        if (regressions.isNotEmpty()) {
          appendLine("【KMP 就緒度倒退】以下 ${regressions.size} 個檔案帶有 JVM-only 相依，且不在 quarantine 清單上：")
          regressions.toSortedMap().forEach { (path, reasons) ->
            appendLine("  $path")
            reasons.take(MAX_REASONS_SHOWN).forEach { appendLine("      $it") }
            if (reasons.size > MAX_REASONS_SHOWN) appendLine("      … 另有 ${reasons.size - MAX_REASONS_SHOWN} 處")
          }
          appendLine()
          appendLine("  請改用非 JVM 的替代方案；若此檔本來就該留在 jvmMain（例如 Feature、快取、曆法轉換），")
          appendLine("  請把路徑加入 src/test/resources/$QUARANTINE。")
        }
        if (stale.isNotEmpty()) {
          appendLine("【quarantine 清單陳舊】以下 ${stale.size} 個項目已經乾淨或檔案不存在，請從清單移除（這是好消息）：")
          stale.sorted().forEach { appendLine("  $it") }
        }
      }
    )
  }

  // ---------------------------------------------------------------- 掃描

  @OptIn(kotlin.io.path.ExperimentalPathApi::class)
  private fun scanDirtyFiles(srcRoot: Path): Map<String, List<String>> {
    return srcRoot.walk()
      .filter { it.extension == "kt" }
      .mapNotNull { file ->
        val rel = srcRoot.relativize(file).toString().replace('\\', '/')
        violationsOf(file).takeIf { it.isNotEmpty() }?.let { rel to it }
      }
      .toMap()
  }

  private fun violationsOf(file: Path): List<String> {
    val out = mutableListOf<String>()
    file.readLines().forEachIndexed { idx, raw ->
      val line = raw.trim()
      // 略過註解 —— KDoc 裡提到 java.io.Serializable 是說明，不是相依
      if (line.startsWith("//") || line.startsWith("*") || line.startsWith("/*")) return@forEachIndexed

      if (line.startsWith("import ")) {
        val fqn = line.removePrefix("import ").substringBefore(" as ").trim()
        FORBIDDEN_IMPORT_PREFIXES.firstOrNull { fqn.startsWith(it) }
          ?.let { out += "L${idx + 1}: import $fqn" }
      } else {
        // 全限定用法（無 import），例如 `: java.io.Serializable`
        FQN_IN_BODY.find(line)?.let { out += "L${idx + 1}: ${it.value}…" }
      }
    }
    return out
  }

  // ---------------------------------------------------------------- 清單 I/O

  private fun readQuarantine(): Set<String> {
    val f = Paths.get(TEST_RESOURCES, QUARANTINE)
    check(f.isRegularFile()) { "找不到 $f" }
    return f.readLines()
      .map { it.substringBefore('#').trim() }
      .filter { it.isNotEmpty() }
      .toSet()
  }

  private fun regenerate(paths: List<String>) {
    val f = Paths.get(TEST_RESOURCES, QUARANTINE)
    f.writeText(
      buildString {
        appendLine("# destiny-core —— 仍帶 JVM-only 相依的檔案（KMP quarantine）")
        appendLine("#")
        appendLine("# 由 KmpReadinessRatchetTest 管控。不在此清單上的 .kt 一律必須無 JVM-only 相依。")
        appendLine("# 這份清單只准變短：改乾淨了就刪掉該行。詳見該測試的 KDoc。")
        appendLine("#")
        appendLine("# 目前數量：${paths.size}")
        appendLine()
        paths.forEach { appendLine(it) }
      }
    )
  }

  companion object {
    private const val SRC_ROOT = "src/main/kotlin"
    private const val TEST_RESOURCES = "src/test/resources"
    private const val QUARANTINE = "kmp-quarantine.txt"
    private const val MAX_REASONS_SHOWN = 5

    /**
     * commonMain 用不到的 package 前綴。
     *
     * 未列入 `destiny.tools.KotlinLogging`：它是自家 logging 接縫，
     * 日後換成 KMP 版 kotlin-logging 時呼叫端不必改（同 [JSerializable] 的處理）。
     */
    private val FORBIDDEN_IMPORT_PREFIXES = listOf(
      "java.", "javax.", "jakarta.",
      "org.threeten.",              // threeten-extra（JulianDate / JulianChronology）
      "org.apache.",                // commons-math3 / commons-lang3
      "com.google.common.",         // Guava
      "com.github.benmanes.",       // Caffeine
      "mu.",                        // kotlin-logging v2（JVM-only；KMP 版在 io.github.oshai）
      "kotlin.reflect.full.",       // JVM-only reflection
      "kotlin.reflect.jvm.",
    )

    /** 無 import 的全限定用法，例如 `: java.io.Serializable` */
    private val FQN_IN_BODY = Regex("""\b(java|javax|jakarta)\.[a-z][A-Za-z0-9_]*(\.[a-z][A-Za-z0-9_]*)*\.[A-Z]""")
  }
}
