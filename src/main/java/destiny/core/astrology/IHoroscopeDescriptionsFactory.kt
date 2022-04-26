/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core.astrology

import destiny.core.IPattern
import destiny.core.IPatternParasDescription


interface IHoroscopeDescriptionsFactory {

  fun getPatternDescriptions(model: IHoroscopeModel): List<IPatternParasDescription>

  fun getDescription(pattern: IPattern): IPatternParasDescription?
}
