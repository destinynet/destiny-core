/**
 * @author smallufo
 * Created on 2007/7/24 at 下午 1:34:43
 */
package destiny.core.astrology

import destiny.core.Point

abstract class AstroPoint(nameKey: String, resource: String, abbrKey: String?, val unicode: Char? = null) : Point(nameKey, resource, abbrKey)
