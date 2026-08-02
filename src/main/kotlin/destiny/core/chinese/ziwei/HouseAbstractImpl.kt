/**
 * Created by smallufo on 2017-04-14.
 */
package destiny.core.chinese.ziwei

import destiny.tools.JSerializable

abstract class HouseAbstractImpl<T> protected constructor(override val star: ZStar) : IHouse<T>, JSerializable
