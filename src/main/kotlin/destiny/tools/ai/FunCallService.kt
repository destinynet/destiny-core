/**
 * Created by smallufo on 2024-10-20.
 */
package destiny.tools.ai

import jakarta.inject.Named


@Named
class FunCallService(
  private val funCalls: Set<IFunctionDeclaration>
) {

  fun invoke(funCall: FunCall): String? {
    return Companion.invoke(funCall, funCalls)
  }

  companion object {
    fun invoke(funCall: FunCall, funCalls: Set<IFunctionDeclaration>): String? {
      return funCalls.firstOrNull { it.name == funCall.name }
        ?.invoke(funCall.parameters)
    }
  }
}
