/**
 * Created by smallufo on 2025-06-10.
 */
package destiny.tools.ai


/**
 * Tool selector interface for semantic search of function declarations.
 * Implementations may use embedding models, keyword matching, or hybrid approaches.
 */
interface IToolSelector : AutoCloseable {

  /**
   * Select top-K tools using semantic similarity.
   * @param query User query string
   * @param k Maximum number of tools to return
   * @param threshold Minimum similarity score (0.0 to 1.0)
   * @return List of (tool, score) pairs sorted by score descending
   */
  fun selectTopK(query: String, k: Int, threshold: Float = 0.5f): List<Pair<IFunctionDeclaration, Float>>

  /**
   * Select top-K tools using two-stage filtering:
   * 1. Keyword pre-filter to get candidates
   * 2. Semantic ranking on candidates only
   *
   * This reduces embedding calculations significantly when there are many tools.
   *
   * @param query User query string
   * @param k Maximum number of tools to return
   * @param prefilterCandidates Number of candidates to keep after keyword pre-filter
   * @param threshold Minimum similarity score (0.0 to 1.0)
   * @return List of (tool, score) pairs sorted by score descending
   */
  fun selectWithPrefilter(
    query: String,
    k: Int,
    prefilterCandidates: Int = 10,
    threshold: Float = 0.3f
  ): List<Pair<IFunctionDeclaration, Float>> {
    // Default implementation falls back to selectTopK
    return selectTopK(query, k, threshold)
  }

  /**
   * Get the number of indexed tools.
   */
  val toolCount: Int
}
