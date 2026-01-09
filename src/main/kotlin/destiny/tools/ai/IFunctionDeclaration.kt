/**
 * Created by smallufo on 2023-12-30.
 */
package destiny.tools.ai

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

interface IFunctionDeclaration {
  data class Parameter(val name: String, val type: String, val description: String, val required: Boolean)

  val name: String
  val description: String
  val keywords: Array<String>
  val parameters: List<Parameter>
  fun applied(msgs: List<Msg>): Boolean
  fun invoke(parameters: Map<String, Any>): String
  val fullDescriptionForIndexing: String
    get() = "$description . keywords : ${keywords.joinToString(", ")}"
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FunctionDeclaration(val name: String,
                                     val description: String,
                                     val keywords: Array<String>)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Parameter(
  val description: String,
  val required: Boolean = true,
  val enum: Array<String> = [],
  val minimum: Int = Int.MIN_VALUE,
  val maximum: Int = Int.MAX_VALUE
)


abstract class AnnotatedFunctionDeclaration : IFunctionDeclaration {
  override val name: String
    get() = this::class.annotations.filterIsInstance<FunctionDeclaration>().first().name

  override val description: String
    get() = this::class.annotations.filterIsInstance<FunctionDeclaration>().first().description

  override val keywords: Array<String>
    get() = this::class.annotations.filterIsInstance<FunctionDeclaration>().first().keywords

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
