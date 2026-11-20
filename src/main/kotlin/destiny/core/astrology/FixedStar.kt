/**
 * @author smallufo
 * Created on 2007/12/20 at 上午 12:16:32
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
import destiny.tools.serializers.astrology.FixedStarSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

/** 恆星  */
@Serializable(with = FixedStarSerializer::class)
sealed class FixedStar(nameKey: String, abbrKey: String) : Star(nameKey, abbrKey, Star::class.qualifiedName!!), Comparable<FixedStar> {
  /** Algol 大陵五  */
  object ALGOL : FixedStar("FixedStar.ALGOL", "FixedStar.ALGOL_ABBR")

  /** Aldebaran 畢宿五 , 傳統四王星 (Four Royal Stars) */
  object ALDEBARAN : FixedStar("FixedStar.ALDEBARAN", "FixedStar.ALDEBARAN_ABBR")

  /** Rigel 參宿七  */
  object RIGEL : FixedStar("FixedStar.RIGEL", "FixedStar.RIGEL_ABBR")

  /** Capella 五車二  */
  object CAPELLA : FixedStar("FixedStar.CAPELLA", "FixedStar.CAPELLA_ABBR")

  /** Betelgeuse 參宿四  */
  object BETELGEUSE : FixedStar("FixedStar.BETELGEUSE", "FixedStar.BETELGEUSE_ABBR")

  /** Sirius 天狼星  */
  object SIRIUS : FixedStar("FixedStar.SIRIUS", "FixedStar.SIRIUS_ABBR")

  /** Canopus 老人星  */
  object CANOPUS : FixedStar("FixedStar.CANOPUS", "FixedStar.CANOPUS_ABBR")

  /** Pollux 北河三  */
  object POLLUX : FixedStar("FixedStar.POLLUX", "FixedStar.POLLUX_ABBR")

  /** Procyon 南河三  */
  object PROCYON : FixedStar("FixedStar.PROCYON", "FixedStar.PROCYON_ABBR")

  /** Praesepe 鬼宿  */
  object PRAESEPE : FixedStar("FixedStar.PRAESEPE", "FixedStar.PRAESEPE_ABBR")

  /** Alphard 星宿一  */
  object ALPHARD : FixedStar("FixedStar.ALPHARD", "FixedStar.ALPHARD_ABBR")

  /** Regulus 軒轅十四 , 傳統四王星 (Four Royal Stars) */
  object REGULUS : FixedStar("FixedStar.REGULUS", "FixedStar.REGULUS_ABBR")

  /** Spica 角宿一  */
  object SPICA : FixedStar("FixedStar.SPICA", "FixedStar.SPICA_ABBR")

  /** Arcturus 大角  */
  object ARCTURUS : FixedStar("FixedStar.ARCTURUS", "FixedStar.ARCTURUS_ABBR")

  /** Antares 心宿二 , 傳統四王星 (Four Royal Stars) */
  object ANTARES : FixedStar("FixedStar.ANTARES", "FixedStar.ANTARES_ABBR")

  /** Vega 織女星  */
  object VEGA : FixedStar("FixedStar.VEGA", "FixedStar.VEGA_ABBR")

  /** Altair 牛郎星  */
  object ALTAIR : FixedStar("FixedStar.ALTAIR", "FixedStar.ALTAIR_ABBR")

  /** Fomalhaut 北落師門 , 傳統四王星 (Four Royal Stars) */
  object FOMALHAUT : FixedStar("FixedStar.FOMALHAUT", "FixedStar.FOMALHAUT_ABBR")

  /** Deneb 天津四  */
  object DENEB : FixedStar("FixedStar.DENEB", "FixedStar.DENEB_ABBR")

  /** Achernar 水委一 */
  object ACHERNAR : FixedStar("FixedStar.ACHERNAR", "FixedStar.ACHERNAR_ABBR")

  /** Acrux 十字架二（南十字α） */
  object ACRUX : FixedStar("FixedStar.ACRUX", "FixedStar.ACRUX_ABBR")

  /** Alcyone 昴宿六（昴宿星團 Pleiades 最亮星） */
  object ALCYONE : FixedStar("FixedStar.ALCYONE", "FixedStar.ALCYONE_ABBR")

  /** Hamal 婁宿三 */
  object HAMAL : FixedStar("FixedStar.HAMAL", "FixedStar.HAMAL_ABBR")

  /** Polaris 勾陳一（北極星） */
  object POLARIS : FixedStar("FixedStar.POLARIS", "FixedStar.POLARIS_ABBR")

  /** Castor 北河二 */
  object CASTOR : FixedStar("FixedStar.CASTOR", "FixedStar.CASTOR_ABBR")

  /** Denebola 五帝座一 */
  object DENEBOLA : FixedStar("FixedStar.DENEBOLA", "FixedStar.DENEBOLA_ABBR")

  /** Toliman 南門二（半人馬α，Rigil Kentaurus） */
  object TOLIMAN : FixedStar("FixedStar.TOLIMAN", "FixedStar.TOLIMAN_ABBR")

  /** Agena 馬腹一（半人馬β，Hadar） */
  object AGENA : FixedStar("FixedStar.AGENA", "FixedStar.AGENA_ABBR")

  /** Alphecca 貫索四 */
  object ALPHECCA : FixedStar("FixedStar.ALPHECCA", "FixedStar.ALPHECCA_ABBR")

  /** Vindemiatrix 太微左垣四 */
  object VINDEMIATRIX : FixedStar("FixedStar.VINDEMIATRIX", "FixedStar.VINDEMIATRIX_ABBR")

  /** Markab 室宿一 */
  object MARKAB : FixedStar("FixedStar.MARKAB", "FixedStar.MARKAB_ABBR")

  /** Scheat 室宿二 */
  object SCHEAT : FixedStar("FixedStar.SCHEAT", "FixedStar.SCHEAT_ABBR")

  /** Alpheratz 壁宿二 */
  object ALPHERATZ : FixedStar("FixedStar.ALPHERATZ", "FixedStar.ALPHERATZ_ABBR")

  /** Bellatrix 參宿五 */
  object BELLATRIX : FixedStar("FixedStar.BELLATRIX", "FixedStar.BELLATRIX_ABBR")

  /** Elnath 五車五 */
  object ELNATH : FixedStar("FixedStar.ELNATH", "FixedStar.ELNATH_ABBR")

  /** Zosma 西上相 */
  object ZOSMA : FixedStar("FixedStar.ZOSMA", "FixedStar.ZOSMA_ABBR")

  /** Sadalsuud 虛宿一 */
  object SADALSUUD : FixedStar("FixedStar.SADALSUUD", "FixedStar.SADALSUUD_ABBR")

  /** Rasalhague 候（蛇夫α） */
  object RASALHAGUE : FixedStar("FixedStar.RASALHAGUE", "FixedStar.RASALHAGUE_ABBR")

  override fun compareTo(other: FixedStar): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }


  companion object : IPoints<FixedStar> {

    override val type: KClass<out Point> = FixedStar::class

    override val values by lazy {
      arrayOf(
        REGULUS, SPICA, ALGOL, ALDEBARAN, RIGEL,
        CAPELLA, BETELGEUSE, SIRIUS, CANOPUS, POLLUX,
        PROCYON, PRAESEPE, ALPHARD, ARCTURUS, ANTARES,
        VEGA, ALTAIR, FOMALHAUT, DENEB,
        ACHERNAR, ACRUX, ALCYONE, HAMAL, POLARIS,
        CASTOR, DENEBOLA, TOLIMAN, AGENA, ALPHECCA,
        VINDEMIATRIX, MARKAB, SCHEAT, ALPHERATZ, BELLATRIX,
        ELNATH, ZOSMA, SADALSUUD, RASALHAGUE
      )
    }
    /** 全部恆星（[values] 的 List 版） */
    val list by lazy { listOf(*values) }

    /**
     * 預設組：盤面與解讀素材預設帶的 19 顆（四王星、Algol、Sirius…）。
     * 需要較完整星表的計算（例如 paran）用 [list]；兩者的差別是用途，不是亮度或重要性。
     */
    val defaultList: List<FixedStar> by lazy {
      listOf(
        REGULUS, SPICA, ALGOL, ALDEBARAN, RIGEL,
        CAPELLA, BETELGEUSE, SIRIUS, CANOPUS, POLLUX,
        PROCYON, PRAESEPE, ALPHARD, ARCTURUS, ANTARES,
        VEGA, ALTAIR, FOMALHAUT, DENEB
      )
    }

    override fun fromString(value: String, locale: Locale): FixedStar? {
      return values.firstOrNull {
        it.getTitle(locale).equals(value, ignoreCase = true)
      }
    }
  }
}
