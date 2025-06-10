/**
 * Created by smallufo on 2025-06-10.
 */
package destiny.tools.ai


interface IFunctionSelector : AutoCloseable {
  fun selectTopK(query: String, k: Int): List<Pair<IFunctionDeclaration, Float>>
}
