/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.core.astrology

import java.io.Serializable

abstract class AbstractPositionImpl<out T : AstroPoint>
  constructor(override val point: T) : IPosition<T>, Serializable
