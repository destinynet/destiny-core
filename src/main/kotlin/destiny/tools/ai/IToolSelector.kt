/**
 * Created by smallufo on 2025-06-10.
 */
package destiny.tools.ai


interface IToolSelector : AutoCloseable {
  fun selectTopK(query: String, k: Int, threshold: Float = 0.5f): List<Pair<IFunctionDeclaration, Float>>
}
