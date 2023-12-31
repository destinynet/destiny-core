/**
 * Created by smallufo on 2023-12-30.
 */
package destiny.tools.ai


/** used by OpenAI and Gemini */
interface IFunctionDeclaration {
  data class Parameter(val name: String, val type: String, val description: String, val required: Boolean)
  val name: String
  val description : String
  val parameters: List<Parameter>
  fun invoke(parameters: List<Pair<String, Any>>): String
}

