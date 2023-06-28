package destiny.core.chart

enum class WidthHeight {
  WIDTH, HEIGHT;

  companion object {
    /**
     * 直立式 , 黃金比例，取寬高的 padding
     */
    fun portraitGoldenPadding(which : WidthHeight, value : Int): Pair<Double, Double> {
      val paddingX =
        if (which === WIDTH) value * (2 - Constants.GOLDEN_RATIO) / 2 else value / Constants.GOLDEN_RATIO * (2 - Constants.GOLDEN_RATIO) / 2
      val paddingY =
        if (which === WIDTH) (value * Constants.GOLDEN_RATIO - value) / 2 else (value - value / Constants.GOLDEN_RATIO) / 2

      return paddingX to paddingY

    }
  }
}
