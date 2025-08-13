/**
 * Created by smallufo on 2025-08-13.
 */
package destiny.tools.ai

import kotlinx.serialization.Serializable


@Serializable
data class ListContainer<T>(val array: List<T>)
