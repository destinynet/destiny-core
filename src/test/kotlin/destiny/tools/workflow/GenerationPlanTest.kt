/**
 * Created by smallufo on 2026-01-18.
 */
package destiny.tools.workflow

import destiny.tools.ai.*
import destiny.tools.ai.model.FormatSpec
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GenerationPlanTest {

  // 測試用的 SegmentOutput 類型
  data class StringOutput(val value: String) : SegmentOutput
  data class IntOutput(val value: Int) : SegmentOutput
  data class ListOutput(val values: List<String>) : SegmentOutput

  // 可序列化的測試類型 (用於 FormatSpec)
  @Serializable
  data class TestAiOutput(val message: String, val score: Int) : SegmentOutput

  @Serializable
  data class TestItemOutput(val itemId: String, val result: String) : SegmentOutput

  // 測試用 Input
  data class TestInput(val prompt: String) : SegmentInput
  data class TestItemInput(val itemId: String) : SegmentInput

  @Test
  fun `test simple linear plan`() {
    val STEP_A = SegmentId("step-a")
    val STEP_B = SegmentId("step-b")

    val plan = GenerationPlan(
      planId = "test-linear",
      name = "Linear Test",
      segments = listOf(
        Segment.StaticSegment(
          id = STEP_A,
          content = StringOutput("Hello")
        ),
        Segment.ComputeSegment(
          id = STEP_B,
          dependsOn = setOf(STEP_A),
          compute = { ctx ->
            val a: StringOutput = ctx.get(STEP_A)
            StringOutput(a.value + " World")
          }
        )
      ),
      assembler = { ctx ->
        val b: StringOutput = ctx.get(STEP_B)
        b.value
      }
    )

    assertEquals(2, plan.segments.size)

    // 驗證拓撲排序
    val sorted = plan.topologicalSort()
    assertEquals(STEP_A, sorted[0].id)
    assertEquals(STEP_B, sorted[1].id)

    // 驗證執行層級
    val layers = plan.executionLayers()
    assertEquals(2, layers.size)
    assertEquals(listOf(STEP_A), layers[0].map { it.id })
    assertEquals(listOf(STEP_B), layers[1].map { it.id })
  }

  @Test
  fun `test parallel execution layers`() {
    val STEP_A = SegmentId("step-a")
    val STEP_B = SegmentId("step-b")
    val STEP_C = SegmentId("step-c")
    val STEP_D = SegmentId("step-d")

    // A → C
    // B → C
    // C → D
    val plan = GenerationPlan(
      planId = "test-parallel",
      name = "Parallel Test",
      segments = listOf(
        Segment.StaticSegment(id = STEP_A, content = StringOutput("A")),
        Segment.StaticSegment(id = STEP_B, content = StringOutput("B")),
        Segment.ComputeSegment(
          id = STEP_C,
          dependsOn = setOf(STEP_A, STEP_B),
          compute = { ctx ->
            val a: StringOutput = ctx.get(STEP_A)
            val b: StringOutput = ctx.get(STEP_B)
            StringOutput(a.value + b.value)
          }
        ),
        Segment.ComputeSegment(
          id = STEP_D,
          dependsOn = setOf(STEP_C),
          compute = { ctx ->
            val c: StringOutput = ctx.get(STEP_C)
            StringOutput(c.value + "D")
          }
        )
      ),
      assembler = { ctx -> ctx.get<StringOutput>(STEP_D).value }
    )

    val layers = plan.executionLayers()
    assertEquals(3, layers.size)

    // Layer 0: A and B can run in parallel
    assertEquals(2, layers[0].size)
    assertTrue(layers[0].any { it.id == STEP_A })
    assertTrue(layers[0].any { it.id == STEP_B })

    // Layer 1: C depends on A and B
    assertEquals(1, layers[1].size)
    assertEquals(STEP_C, layers[1][0].id)

    // Layer 2: D depends on C
    assertEquals(1, layers[2].size)
    assertEquals(STEP_D, layers[2][0].id)
  }

  @Test
  fun `test duplicate segment id validation`() {
    val STEP_A = SegmentId("step-a")

    assertFailsWith<IllegalArgumentException> {
      GenerationPlan(
        planId = "test-duplicate",
        name = "Duplicate Test",
        segments = listOf(
          Segment.StaticSegment(id = STEP_A, content = StringOutput("A1")),
          Segment.StaticSegment(id = STEP_A, content = StringOutput("A2"))
        ),
        assembler = { "" }
      )
    }
  }

  @Test
  fun `test unknown dependency validation`() {
    val STEP_A = SegmentId("step-a")
    val STEP_B = SegmentId("step-b")
    val STEP_UNKNOWN = SegmentId("unknown")

    assertFailsWith<IllegalArgumentException> {
      GenerationPlan(
        planId = "test-unknown-dep",
        name = "Unknown Dep Test",
        segments = listOf(
          Segment.StaticSegment(id = STEP_A, content = StringOutput("A")),
          Segment.ComputeSegment(
            id = STEP_B,
            dependsOn = setOf(STEP_UNKNOWN),
            compute = { StringOutput("B") }
          )
        ),
        assembler = { "" }
      )
    }
  }

  @Test
  fun `test circular dependency detection`() {
    val STEP_A = SegmentId("step-a")
    val STEP_B = SegmentId("step-b")

    assertFailsWith<IllegalArgumentException> {
      GenerationPlan(
        planId = "test-circular",
        name = "Circular Test",
        segments = listOf(
          Segment.ComputeSegment(
            id = STEP_A,
            dependsOn = setOf(STEP_B),
            compute = { StringOutput("A") }
          ),
          Segment.ComputeSegment(
            id = STEP_B,
            dependsOn = setOf(STEP_A),
            compute = { StringOutput("B") }
          )
        ),
        assembler = { "" }
      )
    }
  }

  @Test
  fun `test SegmentContext reified get`() {
    val context = MutableSegmentContext()
    val STEP_A = SegmentId("step-a")

    context.put(STEP_A, StringOutput("test"))

    // 使用 reified 版本
    val result: StringOutput = context.get(STEP_A)
    assertEquals("test", result.value)

    // 使用明確泛型參數
    val result2 = context.get<StringOutput>(STEP_A)
    assertEquals("test", result2.value)
  }

  @Test
  fun `test SegmentContext getOrNull`() {
    val context = MutableSegmentContext()
    val STEP_A = SegmentId("step-a")
    val STEP_B = SegmentId("step-b")

    context.put(STEP_A, StringOutput("test"))

    assertEquals("test", context.getOrNull<StringOutput>(STEP_A)?.value)
    assertEquals(null, context.getOrNull<StringOutput>(STEP_B))
  }

  @Test
  fun `test ExecutionEngine with static and compute segments`() = runBlocking {
    val STEP_A = SegmentId("step-a")
    val STEP_B = SegmentId("step-b")
    val STEP_C = SegmentId("step-c")

    val plan = GenerationPlan(
      planId = "test-engine",
      name = "Engine Test",
      segments = listOf(
        Segment.StaticSegment(id = STEP_A, content = IntOutput(10)),
        Segment.StaticSegment(id = STEP_B, content = IntOutput(20)),
        Segment.ComputeSegment(
          id = STEP_C,
          dependsOn = setOf(STEP_A, STEP_B),
          compute = { ctx ->
            val a: IntOutput = ctx.get(STEP_A)
            val b: IntOutput = ctx.get(STEP_B)
            IntOutput(a.value + b.value)
          }
        )
      ),
      assembler = { ctx -> ctx.get<IntOutput>(STEP_C).value }
    )

    // Mock orchestrator - won't be called since we only have static/compute segments
    val mockOrchestrator = object : IChatOrchestrator {
      override suspend fun <T : Any> chatComplete(
        formatSpec: FormatSpec<out T>,
        messages: List<Msg>,
        postProcessors: List<IPostProcessor>,
        locale: Locale,
        funCalls: Set<IFunctionDeclaration>,
        chatOptionsTemplate: ChatOptions,
        providerImpl: (Provider) -> IChatCompletion
      ): Reply.Normal<out T> = throw UnsupportedOperationException("Should not be called")
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException("Should not be called") }
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    assertEquals(30, result.getOrThrow())

    val metadata = (result as ExecutionResult.Success).metadata
    assertTrue(metadata.totalDurationMs >= 0)
    assertEquals(3, metadata.segmentDurations.size)
    assertEquals(0, metadata.totalAiCalls)
  }

  @Test
  fun `test Validators requireAll`() {
    val outputs = listOf(StringOutput("a"), StringOutput("b"), StringOutput("c"))

    // 全部成功
    val result1 = Validators.requireAll.validate(3, 3, outputs)
    assertTrue(result1.isAccepted())

    // 部分失敗
    val result2 = Validators.requireAll.validate(3, 2, outputs.take(2))
    assertTrue(result2.isRejected())
  }

  @Test
  fun `test Validators requireAtLeast`() {
    val outputs = listOf(StringOutput("a"), StringOutput("b"))

    // 達到最低要求
    val result1 = Validators.requireAtLeast(2).validate(5, 2, outputs)
    assertTrue(result1.isAccepted())

    // 未達最低要求
    val result2 = Validators.requireAtLeast(3).validate(5, 2, outputs)
    assertTrue(result2.isRejected())
  }

  @Test
  fun `test Validators requireSuccessRate`() {
    val outputs = listOf(StringOutput("a"), StringOutput("b"))

    // 80% 成功率，要求 50%
    val result1 = Validators.requireSuccessRate(0.5).validate(10, 8, outputs)
    assertTrue(result1.isAccepted())

    // 30% 成功率，要求 50%
    val result2 = Validators.requireSuccessRate(0.5).validate(10, 3, outputs)
    assertTrue(result2.isRejected())
  }

  @Test
  fun `test ParallelOutput`() {
    val successful = listOf(StringOutput("a"), StringOutput("b"))
    val failed = listOf(FailedItem(2, RuntimeException("error"), 3))

    val output = ParallelOutput(successful, failed)

    assertEquals(2, output.successful.size)
    assertEquals(1, output.failed.size)
    assertEquals(3, output.totalCount)
    assertEquals(2.0 / 3, output.successRate, 0.001)
    assertEquals(false, output.isAllSuccessful)
  }

  @Test
  fun `test complex MergedUserEvents-style plan structure`() {
    // 模擬 MergedUserEventsCoordinator 的複雜結構
    val NATAL = SegmentId("natal")
    val EVENTS = SegmentId("events")
    val MATRIX = SegmentId("matrix")
    val EVIDENCE = SegmentId("evidence")
    val INTRO = SegmentId("intro")
    val HYPOTHESIS = SegmentId("hypothesis")
    val CONCLUSION = SegmentId("conclusion")

    val plan = GenerationPlan(
      planId = "complex-test",
      name = "Complex Structure Test",
      segments = listOf(
        // Step 1: 無依賴
        Segment.StaticSegment(id = NATAL, content = StringOutput("natal")),

        // Step 2: 依賴 Step 1
        Segment.StaticSegment(id = EVENTS, dependsOn = setOf(NATAL), content = ListOutput(listOf("e1", "e2"))),

        // Compute: 依賴 Step 2
        Segment.ComputeSegment(
          id = MATRIX,
          dependsOn = setOf(EVENTS),
          compute = { StringOutput("matrix") }
        ),

        // Step 3: 依賴 Step 1, 2
        Segment.ComputeSegment(
          id = EVIDENCE,
          dependsOn = setOf(NATAL, EVENTS),
          compute = { StringOutput("evidence") }
        ),

        // Step 4A: 依賴多個
        Segment.ComputeSegment(
          id = INTRO,
          dependsOn = setOf(NATAL, MATRIX, EVIDENCE),
          compute = { StringOutput("intro") }
        ),

        // Step 4B: 依賴 Step 3, 2
        Segment.ComputeSegment(
          id = HYPOTHESIS,
          dependsOn = setOf(EVIDENCE, EVENTS),
          compute = { StringOutput("hypothesis") }
        ),

        // Step 4C: 依賴多個
        Segment.ComputeSegment(
          id = CONCLUSION,
          dependsOn = setOf(NATAL, HYPOTHESIS, MATRIX),
          compute = { StringOutput("conclusion") }
        )
      ),
      assembler = { ctx -> ctx.get<StringOutput>(CONCLUSION).value }
    )

    // 驗證結構正確
    assertEquals(7, plan.segments.size)

    // 驗證執行層級
    val layers = plan.executionLayers()

    // Layer 0: NATAL (無依賴)
    assertTrue(layers[0].any { it.id == NATAL })

    // 所有層級的 segment 數量總和等於 7
    assertEquals(7, layers.sumOf { it.size })

    // 最後一層應該包含 CONCLUSION
    assertTrue(layers.last().any { it.id == CONCLUSION })
  }

  // ============================================================
  // AiSegment 執行測試
  // ============================================================

  @Test
  fun `test AiSegment execution with mock orchestrator`() = runBlocking {
    val AI_STEP = SegmentId("ai-step")
    val aiCallCount = AtomicInteger(0)

    val formatSpec = FormatSpec.of<TestAiOutput>("TestOutput", "Test AI output")

    val plan = GenerationPlan(
      planId = "test-ai-segment",
      name = "AI Segment Test",
      segments = listOf(
        Segment.AiSegment(
          id = AI_STEP,
          inputBuilder = { TestInput("Generate something") },
          promptBuilder = { input, _ ->
            val testInput = input as TestInput
            "Please ${testInput.prompt}"
          },
          formatSpec = formatSpec
        )
      ),
      assembler = { ctx ->
        val output: TestAiOutput = ctx.get(AI_STEP)
        "${output.message}: ${output.score}"
      }
    )

    val mockOrchestrator = createMockOrchestrator { kClass ->
      aiCallCount.incrementAndGet()
      when (kClass) {
        TestAiOutput::class -> TestAiOutput("Hello", 42)
        else -> throw IllegalArgumentException("Unknown type: $kClass")
      }
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() }
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    assertEquals("Hello: 42", result.getOrThrow())
    assertEquals(1, aiCallCount.get())

    val metadata = (result as ExecutionResult.Success).metadata
    assertEquals(1, metadata.totalAiCalls)
  }

  @Test
  fun `test AiSegment with dependencies`() = runBlocking {
    val STATIC_STEP = SegmentId("static")
    val AI_STEP = SegmentId("ai-step")

    val formatSpec = FormatSpec.of<TestAiOutput>("TestOutput", "Test AI output")

    val plan = GenerationPlan(
      planId = "test-ai-with-deps",
      name = "AI with Dependencies Test",
      segments = listOf(
        Segment.StaticSegment(
          id = STATIC_STEP,
          content = StringOutput("prefix")
        ),
        Segment.AiSegment(
          id = AI_STEP,
          dependsOn = setOf(STATIC_STEP),
          inputBuilder = { ctx ->
            val prefix: StringOutput = ctx.get(STATIC_STEP)
            TestInput("Generate with ${prefix.value}")
          },
          promptBuilder = { input, _ ->
            val testInput = input as TestInput
            testInput.prompt
          },
          formatSpec = formatSpec
        )
      ),
      assembler = { ctx ->
        val output: TestAiOutput = ctx.get(AI_STEP)
        output.message
      }
    )

    val mockOrchestrator = createMockOrchestrator { kClass ->
      when (kClass) {
        TestAiOutput::class -> TestAiOutput("Generated", 100)
        else -> throw IllegalArgumentException("Unknown type: $kClass")
      }
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() }
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    assertEquals("Generated", result.getOrThrow())
  }

  // ============================================================
  // ParallelAiSegment 執行測試
  // ============================================================

  @Test
  fun `test ParallelAiSegment execution`() = runBlocking {
    val PARALLEL_STEP = SegmentId("parallel-ai")
    val aiCallCount = AtomicInteger(0)

    val formatSpec = FormatSpec.of<TestItemOutput>("TestItemOutput", "Test item output")
    val items = listOf("item-1", "item-2", "item-3")

    val plan = GenerationPlan(
      planId = "test-parallel-ai",
      name = "Parallel AI Test",
      segments = listOf(
        Segment.ParallelAiSegment<String, TestItemOutput>(
          id = PARALLEL_STEP,
          itemsProvider = { items },
          itemInputBuilder = { item, _ -> TestItemInput(item) },
          promptBuilder = { input, _ ->
            val itemInput = input as TestItemInput
            "Process ${itemInput.itemId}"
          },
          formatSpec = formatSpec
        )
      ),
      assembler = { ctx ->
        val output = ctx.get<ParallelOutput<TestItemOutput>>(PARALLEL_STEP)
        output.successful.map { it.result }.joinToString(",")
      }
    )

    val mockOrchestrator = createMockOrchestrator { kClass ->
      val count = aiCallCount.incrementAndGet()
      when (kClass) {
        TestItemOutput::class -> TestItemOutput("item-$count", "result-$count")
        else -> throw IllegalArgumentException("Unknown type: $kClass")
      }
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() }
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    assertEquals(3, aiCallCount.get())

    val metadata = (result as ExecutionResult.Success).metadata
    assertEquals(3, metadata.totalAiCalls)

    // 驗證 ParallelOutput 結構
    val output = result.metadata.let {
      // 從 assembler 結果驗證
      result.getOrThrow().split(",").size
    }
    assertEquals(3, output)
  }

  @Test
  fun `test ParallelAiSegment with validator - all required`() = runBlocking {
    val PARALLEL_STEP = SegmentId("parallel-ai")
    var callIndex = 0

    val formatSpec = FormatSpec.of<TestItemOutput>("TestItemOutput", "Test item output")
    val items = listOf("item-1", "item-2", "item-3")

    val plan = GenerationPlan(
      planId = "test-parallel-validator",
      name = "Parallel with Validator Test",
      segments = listOf(
        Segment.ParallelAiSegment<String, TestItemOutput>(
          id = PARALLEL_STEP,
          itemsProvider = { items },
          itemInputBuilder = { item, _ -> TestItemInput(item) },
          promptBuilder = { input, _ ->
            val itemInput = input as TestItemInput
            "Process ${itemInput.itemId}"
          },
          formatSpec = formatSpec,
          resultValidator = Validators.requireAll
        )
      ),
      assembler = { ctx ->
        val output = ctx.get<ParallelOutput<TestItemOutput>>(PARALLEL_STEP)
        output.successful.size
      }
    )

    // Mock 全部成功
    val mockOrchestrator = createMockOrchestrator { kClass ->
      callIndex++
      when (kClass) {
        TestItemOutput::class -> TestItemOutput("item-$callIndex", "success-$callIndex")
        else -> throw IllegalArgumentException("Unknown type: $kClass")
      }
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() }
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    assertEquals(3, result.getOrThrow())
  }

  @Test
  fun `test ParallelAiSegment allows partial failure without validator`() = runBlocking {
    val PARALLEL_STEP = SegmentId("parallel-ai")
    var callIndex = 0

    val formatSpec = FormatSpec.of<TestItemOutput>("TestItemOutput", "Test item output")
    val items = listOf("item-1", "item-2", "item-3")

    val plan = GenerationPlan(
      planId = "test-parallel-partial",
      name = "Parallel Partial Failure Test",
      segments = listOf(
        Segment.ParallelAiSegment<String, TestItemOutput>(
          id = PARALLEL_STEP,
          itemsProvider = { items },
          itemInputBuilder = { item, _ -> TestItemInput(item) },
          promptBuilder = { input, _ ->
            val itemInput = input as TestItemInput
            "Process ${itemInput.itemId}"
          },
          formatSpec = formatSpec,
          resultValidator = null  // 允許部分失敗
        )
      ),
      assembler = { ctx ->
        val output = ctx.get<ParallelOutput<TestItemOutput>>(PARALLEL_STEP)
        output.successful.size to output.failed.size
      }
    )

    // Mock: 第二個呼叫失敗
    val mockOrchestrator = createMockOrchestratorWithFailure(
      failOnCallIndex = 2,
      successResponse = { kClass, index ->
        when (kClass) {
          TestItemOutput::class -> TestItemOutput("item-$index", "success-$index")
          else -> throw IllegalArgumentException("Unknown type: $kClass")
        }
      }
    )

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() },
      maxRetries = 1  // 只重試一次，確保失敗
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    val (successCount, failedCount) = result.getOrThrow()
    assertEquals(2, successCount)
    assertEquals(1, failedCount)
  }

  @Test
  fun `test AiSegment retry on failure`() = runBlocking {
    val AI_STEP = SegmentId("ai-step")
    val attemptCount = AtomicInteger(0)

    val formatSpec = FormatSpec.of<TestAiOutput>("TestOutput", "Test AI output")

    val plan = GenerationPlan(
      planId = "test-retry",
      name = "Retry Test",
      segments = listOf(
        Segment.AiSegment(
          id = AI_STEP,
          inputBuilder = { TestInput("test") },
          promptBuilder = { _, _ -> "test prompt" },
          formatSpec = formatSpec
        )
      ),
      assembler = { ctx -> ctx.get<TestAiOutput>(AI_STEP).message }
    )

    // Mock: 前兩次失敗，第三次成功
    val mockOrchestrator = object : IChatOrchestrator {
      override suspend fun <T : Any> chatComplete(
        formatSpec: FormatSpec<out T>,
        messages: List<Msg>,
        postProcessors: List<IPostProcessor>,
        locale: Locale,
        funCalls: Set<IFunctionDeclaration>,
        chatOptionsTemplate: ChatOptions,
        providerImpl: (Provider) -> IChatCompletion
      ): Reply.Normal<out T> {
        val attempt = attemptCount.incrementAndGet()
        if (attempt < 3) {
          throw RuntimeException("Simulated failure on attempt $attempt")
        }
        @Suppress("UNCHECKED_CAST")
        return Reply.Normal(
          content = TestAiOutput("Success after retries", attempt) as T,
          think = null,
          provider = Provider.CLAUDE,
          model = "test-model"
        )
      }
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() },
      maxRetries = 3,
      retryDelayMs = 10  // 短延遲加速測試
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    assertEquals("Success after retries", result.getOrThrow())
    assertEquals(3, attemptCount.get())
  }

  @Test
  fun `test AiSegment fails after max retries`() = runBlocking {
    val AI_STEP = SegmentId("ai-step")
    val attemptCount = AtomicInteger(0)

    val formatSpec = FormatSpec.of<TestAiOutput>("TestOutput", "Test AI output")

    val plan = GenerationPlan(
      planId = "test-max-retries",
      name = "Max Retries Test",
      segments = listOf(
        Segment.AiSegment(
          id = AI_STEP,
          inputBuilder = { TestInput("test") },
          promptBuilder = { _, _ -> "test prompt" },
          formatSpec = formatSpec
        )
      ),
      assembler = { ctx -> ctx.get<TestAiOutput>(AI_STEP).message }
    )

    // Mock: 永遠失敗
    val mockOrchestrator = object : IChatOrchestrator {
      override suspend fun <T : Any> chatComplete(
        formatSpec: FormatSpec<out T>,
        messages: List<Msg>,
        postProcessors: List<IPostProcessor>,
        locale: Locale,
        funCalls: Set<IFunctionDeclaration>,
        chatOptionsTemplate: ChatOptions,
        providerImpl: (Provider) -> IChatCompletion
      ): Reply.Normal<out T> {
        attemptCount.incrementAndGet()
        throw RuntimeException("Always fails")
      }
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() },
      maxRetries = 3,
      retryDelayMs = 10
    )

    val result = engine.execute(plan)

    assertTrue(result.isFailed())
    assertEquals(3, attemptCount.get())

    val failed = result as ExecutionResult.Failed
    assertEquals(AI_STEP, failed.failedSegment)
  }

  @Test
  fun `test mixed segments - static, compute, ai, parallel`() = runBlocking {
    val STATIC = SegmentId("static")
    val COMPUTE = SegmentId("compute")
    val AI = SegmentId("ai")
    val PARALLEL = SegmentId("parallel")
    val FINAL_COMPUTE = SegmentId("final")

    val aiFormatSpec = FormatSpec.of<TestAiOutput>("TestOutput", "Test AI output")
    val parallelFormatSpec = FormatSpec.of<TestItemOutput>("TestItemOutput", "Test item output")

    val plan = GenerationPlan(
      planId = "test-mixed",
      name = "Mixed Segments Test",
      segments = listOf(
        Segment.StaticSegment(id = STATIC, content = IntOutput(10)),
        Segment.ComputeSegment(
          id = COMPUTE,
          dependsOn = setOf(STATIC),
          compute = { ctx ->
            val input: IntOutput = ctx.get(STATIC)
            IntOutput(input.value * 2)
          }
        ),
        Segment.AiSegment(
          id = AI,
          dependsOn = setOf(COMPUTE),
          inputBuilder = { ctx ->
            val computed: IntOutput = ctx.get(COMPUTE)
            TestInput("Value is ${computed.value}")
          },
          promptBuilder = { input, _ -> (input as TestInput).prompt },
          formatSpec = aiFormatSpec
        ),
        Segment.ParallelAiSegment<Int, TestItemOutput>(
          id = PARALLEL,
          dependsOn = setOf(COMPUTE),
          itemsProvider = { ctx ->
            val computed: IntOutput = ctx.get(COMPUTE)
            (1..computed.value / 10).toList()  // 產生 [1, 2] 因為 computed = 20
          },
          itemInputBuilder = { item, _ -> TestItemInput("item-$item") },
          promptBuilder = { input, _ -> "Process ${(input as TestItemInput).itemId}" },
          formatSpec = parallelFormatSpec
        ),
        Segment.ComputeSegment(
          id = FINAL_COMPUTE,
          dependsOn = setOf(AI, PARALLEL),
          compute = { ctx ->
            val aiOutput: TestAiOutput = ctx.get(AI)
            val parallelOutput = ctx.get<ParallelOutput<TestItemOutput>>(PARALLEL)
            StringOutput("AI: ${aiOutput.score}, Parallel: ${parallelOutput.successful.size}")
          }
        )
      ),
      assembler = { ctx -> ctx.get<StringOutput>(FINAL_COMPUTE).value }
    )

    var aiCallCount = 0
    val mockOrchestrator = createMockOrchestrator { kClass ->
      aiCallCount++
      when (kClass) {
        TestAiOutput::class -> TestAiOutput("AI Result", 99)
        TestItemOutput::class -> TestItemOutput("id-$aiCallCount", "result-$aiCallCount")
        else -> throw IllegalArgumentException("Unknown type: $kClass")
      }
    }

    val engine = DefaultExecutionEngine(
      orchestrator = mockOrchestrator,
      postProcessors = emptyList(),
      providerImpl = { throw UnsupportedOperationException() }
    )

    val result = engine.execute(plan)

    assertTrue(result.isSuccess())
    assertEquals("AI: 99, Parallel: 2", result.getOrThrow())

    // AI 呼叫: 1 (AiSegment) + 2 (ParallelAiSegment with 2 items) = 3
    assertEquals(3, aiCallCount)

    val metadata = (result as ExecutionResult.Success).metadata
    assertEquals(3, metadata.totalAiCalls)
    assertEquals(5, metadata.segmentDurations.size)
  }

  // ============================================================
  // Helper Methods
  // ============================================================

  private fun createMockOrchestrator(
    responseFactory: (KClass<*>) -> Any
  ): IChatOrchestrator = object : IChatOrchestrator {
    override suspend fun <T : Any> chatComplete(
      formatSpec: FormatSpec<out T>,
      messages: List<Msg>,
      postProcessors: List<IPostProcessor>,
      locale: Locale,
      funCalls: Set<IFunctionDeclaration>,
      chatOptionsTemplate: ChatOptions,
      providerImpl: (Provider) -> IChatCompletion
    ): Reply.Normal<out T> {
      @Suppress("UNCHECKED_CAST")
      val content = responseFactory(formatSpec.kClass) as T
      return Reply.Normal(
        content = content,
        think = null,
        provider = Provider.CLAUDE,
        model = "test-model"
      )
    }
  }

  private fun createMockOrchestratorWithFailure(
    failOnCallIndex: Int,
    successResponse: (KClass<*>, Int) -> Any
  ): IChatOrchestrator {
    val callCount = AtomicInteger(0)
    return object : IChatOrchestrator {
      override suspend fun <T : Any> chatComplete(
        formatSpec: FormatSpec<out T>,
        messages: List<Msg>,
        postProcessors: List<IPostProcessor>,
        locale: Locale,
        funCalls: Set<IFunctionDeclaration>,
        chatOptionsTemplate: ChatOptions,
        providerImpl: (Provider) -> IChatCompletion
      ): Reply.Normal<out T> {
        val index = callCount.incrementAndGet()
        if (index == failOnCallIndex) {
          throw RuntimeException("Simulated failure on call $index")
        }
        @Suppress("UNCHECKED_CAST")
        val content = successResponse(formatSpec.kClass, index) as T
        return Reply.Normal(
          content = content,
          think = null,
          provider = Provider.CLAUDE,
          model = "test-model"
        )
      }
    }
  }
}
