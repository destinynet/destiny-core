package destiny.core.astrology

import destiny.core.chinese.StemBranch

/**
 * 目前這星體的位置，以及其「時辰」(「類似」地盤12宮)
 */
data class PositionWithBranch(
  private val pos: IPos,
  val hour: StemBranch
) : IPos by pos
