/**
 * Created by smallufo on 2023-12-30.
 */
package destiny.tools.ai

import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

/** used by OpenAI, Gemini and Claude */
interface IFunctionDeclaration {
  data class Parameter(val name: String, val type: String, val description: String, val required: Boolean)

  val name: String
  val description: String
  val parameters: List<Parameter>
  fun applied(msgs: List<Msg>): Boolean
  fun invoke(parameters: Map<String, Any>): String
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FunctionDeclaration(val name: String, val description: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Parameter(
  val description: String,
  val required: Boolean = true,
  val enum: Array<String> = [],
  val minimum: Int = Int.MIN_VALUE,
  val maximum: Int = Int.MAX_VALUE
)

fun KType.toJsonSchemaType(): String {
  return when {
    this.isSubtypeOf(typeOf<String>())                                          -> "string"
    this.isSubtypeOf(typeOf<Int>()) || this.isSubtypeOf(typeOf<Long>())         -> "integer"
    this.isSubtypeOf(typeOf<Float>()) || this.isSubtypeOf(typeOf<Double>())     -> "number"
    this.isSubtypeOf(typeOf<Boolean>())                                         -> "boolean"
    this.isSubtypeOf(typeOf<List<*>>()) || this.isSubtypeOf(typeOf<Array<*>>()) -> "array"
    this.isSubtypeOf(typeOf<Map<*, *>>())                                       -> "object"
    this.isSubtypeOf(typeOf<java.util.Date>()) ||
      this.isSubtypeOf(typeOf<java.time.LocalDate>()) ||
      this.isSubtypeOf(typeOf<java.time.LocalDateTime>())                       -> "string"

    this.isSubtypeOf(typeOf<java.math.BigInteger>()) ||
      this.isSubtypeOf(typeOf<java.math.BigDecimal>())                          -> "string"

    this.isSubtypeOf(typeOf<Enum<*>>())                                         -> "string"
    else                                                                        -> "object"
  }
}

abstract class AnnotatedFunctionDeclaration : IFunctionDeclaration {
  override val name: String
    get() = this::class.annotations.filterIsInstance<FunctionDeclaration>().first().name

  override val description: String
    get() = this::class.annotations.filterIsInstance<FunctionDeclaration>().first().description

  abstract val callbackName: String

  override val parameters: List<IFunctionDeclaration.Parameter>
    get() {
      val method = this::class.memberFunctions.first { it.name == callbackName }
      return method.parameters.drop(1).map { param ->
        val annotation = param.findAnnotation<Parameter>()
        IFunctionDeclaration.Parameter(
          param.name ?: "",
          param.type.toJsonSchemaType(),
          annotation?.description ?: "",
          annotation?.required ?: true
        )
      }
    }

  override fun invoke(parameters: Map<String, Any>): String {
    val method = this::class.memberFunctions.first { it.name == callbackName }
    val args = method.valueParameters.map { param ->
      parameters[param.name]
    }.toTypedArray()
    return method.call(this, *args) as String
  }
}

fun Set<IFunctionDeclaration>.toMap(): Map<String, IFunctionDeclaration> {
  return this.associateBy { impl ->
    impl.name
  }
}

fun IFunctionDeclaration.toOpenAi(): OpenAi.FunctionDeclaration {
  return OpenAi.FunctionDeclaration(
    "function",
    OpenAi.FunctionDeclaration.Function(
      this.name, this.description,
      InputSchema(
        "object",
        this.parameters.associate { p -> p.name to InputSchema.Property(p.type, p.description, null) },
        this.parameters.filter { it.required }.map { it.name }.toList()
      )
    )
  )
}

fun IFunctionDeclaration.toDeepseek(): OpenAi.FunctionDeclaration {
  return toOpenAi()
}

fun IFunctionDeclaration.toGemini(): Gemini.FunctionDeclaration {
  return Gemini.FunctionDeclaration(
    this.name,
    this.description,
    Gemini.FunctionDeclaration.Parameters(
      "object",
      this.parameters.associate { p -> p.name to Gemini.FunctionDeclaration.Parameters.Argument(p.type, p.description) },
      this.parameters.filter { it.required }.map { it.name }.toList()
    )
  )
}

fun IFunctionDeclaration.toClaude(): Claude.Function {
  return Claude.Function(
    this.name,
    this.description,
    InputSchema(
      "object",
      this.parameters.associate { p ->
        p.name to InputSchema.Property(
          p.type,
          p.description
        )
      },
      this.parameters.filter { it.required }.map { it.name }
    )
  )
}

fun IFunctionDeclaration.toCohere(): Cohere.ToolFunction {
  return Cohere.ToolFunction(
    "function",
    Cohere.ToolFunction.Function(
      this.name,
      this.description,
      InputSchema(
        "object",
        this.parameters.associate { p ->
          p.name to InputSchema.Property(
            p.type,
            p.description
          )
        },
        this.parameters.filter { it.required }.map { it.name }
      )
    )
  )
}

fun IFunctionDeclaration.toXai(): Xai.ToolFunction {
  return Xai.ToolFunction(
    Xai.Function(
      this.name,
      this.description,
      InputSchema(
        "object",
        this.parameters.associate { p ->
          p.name to InputSchema.Property(
            p.type,
            p.description
          )
        },
        this.parameters.filter { it.required }.map { it.name }
      )
    )
  )
}
