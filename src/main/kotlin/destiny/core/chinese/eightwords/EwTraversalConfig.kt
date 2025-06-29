package destiny.core.chinese.eightwords

import destiny.core.calendar.eightwords.IPersonPresentConfig

data class EwTraversalConfig(
  /** 神煞 */
  val shanSha: Boolean = true,
  val personPresentConfig: IPersonPresentConfig = PersonPresentConfig(),
)
