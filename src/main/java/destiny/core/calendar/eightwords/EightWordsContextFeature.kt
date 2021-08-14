/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords


data class EightWordsContextConfig(
  val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
  val risingSignConfig: RisingSignConfig = RisingSignConfig(),

)

class EightWordsContextFeature {
}
