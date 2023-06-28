/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei

import java.io.Serializable

abstract class HouseAbstractImpl<T> protected constructor(override val star: ZStar) : IHouse<T>, Serializable
