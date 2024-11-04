/**
 * Created by smallufo on 2024-11-04.
 */
package destiny.core.astrology


object MidPointAstroPointComparator : Comparator<MidPoint> {
  override fun compare(mp1: MidPoint, mp2: MidPoint): Int {
    val p1Comparison = AstroPointComparator.compare(mp1.p1, mp2.p1)
    return if (p1Comparison != 0) {
      p1Comparison
    } else {
      AstroPointComparator.compare(mp1.p2, mp2.p2)
    }
  }
}

object MidPointSignQualityComparator : Comparator<MidPoint> {
  override fun compare(mp1: MidPoint, mp2: MidPoint): Int {
    val qualityCompare = mp1.degree.sign.quality.compareTo(mp2.degree.sign.quality)
    return if (qualityCompare != 0) {
      qualityCompare
    } else {
      mp1.degree.signDegree.second.compareTo(mp2.degree.signDegree.second)
    }
  }
}

object MidPointDegreeComparator : Comparator<MidPoint> {
  override fun compare(mp1: MidPoint, mp2: MidPoint): Int {
    return mp1.degree.compareTo(mp2.degree)
  }
}

object MidPointSignDegreeComparator : Comparator<MidPoint> {
  override fun compare(mp1: MidPoint, mp2: MidPoint): Int {
    return mp1.degree.signDegree.second.compareTo(mp2.degree.signDegree.second)
  }

}
