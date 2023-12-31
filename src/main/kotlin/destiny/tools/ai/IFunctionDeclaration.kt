/**
 * Created by smallufo on 2023-12-30.
 */
package destiny.tools.ai


/** used by OpenAI and Gemini */
interface IFunctionDeclaration {
  data class Parameter(val name: String, val type: String, val description: String, val required: Boolean)

  val name: String
  val description: String
  val parameters: List<Parameter>
  fun invoke(parameters: List<Pair<String, Any>>): String
}

fun IFunctionDeclaration.toOpenAi(): OpenAi.FunctionDeclaration {
  return OpenAi.FunctionDeclaration(
    "function",
    OpenAi.FunctionDeclaration.Function(
      this.name, this.description,
      OpenAi.FunctionDeclaration.Function.Parameters(
        "object",
        this.parameters.associate { p -> p.name to OpenAi.FunctionDeclaration.Function.Parameters.Argument(p.type, null, p.description) },
        this.parameters.filter { it.required }.map { it.name }.toList()
      )
    )
  )
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
