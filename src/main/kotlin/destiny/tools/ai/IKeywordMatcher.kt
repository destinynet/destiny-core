/**
 * Created by smallufo on 2026-01-11.
 */
package destiny.tools.ai


/**
 * Keyword-based pre-filter for tool selection.
 * Uses lightweight string matching (BM25/TF-IDF) for fast candidate filtering
 * before expensive embedding calculations.
 */
interface IKeywordMatcher {

  /**
   * Match tools by keywords and return candidates sorted by relevance.
   * @param query User query string
   * @param topN Maximum number of candidates to return
   * @return List of candidate tools with their keyword match scores
   */
  fun matchCandidates(query: String, topN: Int): List<Pair<IFunctionDeclaration, Float>>

  /**
   * Get all searchable tokens for a query (for debugging/testing).
   */
  fun tokenize(text: String): List<String>
}
