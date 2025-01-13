package destiny.core

import destiny.tools.ai.model.IDigestFormat

@Deprecated("")
interface IDailyDigest<out M : IDaily, T> : IDigestFormat<@UnsafeVariance M, T> {

  //override fun digest(model: @UnsafeVariance M, locale: Locale): T?

}
