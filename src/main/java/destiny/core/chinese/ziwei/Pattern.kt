/**
 * Created by smallufo on 2018-06-29.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch


/** 紫微在午宮坐命 */
fun fun極向離明() = { it: IPlate ->
  it.starMap[StarMain.紫微]?.let { houseData ->
    houseData.stemBranch.let { sb -> sb == it.mainHouse && sb.branch == Branch.午 }
  } ?: false
}

/** 安命在寅或申宮，紫微天府同宮。 */
fun fun紫府同宮() = { it: IPlate ->
  (it.mainHouse.branch == Branch.寅 || it.mainHouse.branch == Branch.申) &&
    it.starMap[StarMain.紫微]?.stemBranch == it.starMap[StarMain.天府]?.stemBranch
}


/**
 * 紫微、天府於三方四正照命。
 * 命宮在申，紫微在子，天府在辰，申子辰三合，謂之紫府朝垣格，主其人高官厚爵，福祿昌隆。
 * */
fun fun紫府朝垣() = { it: IPlate ->
  it.mainHouse.branch == Branch.申
    && it.starMap[StarMain.紫微]?.stemBranch?.branch == Branch.子
    && it.starMap[StarMain.天府]?.stemBranch?.branch == Branch.辰
}


/**
 * 巨門、天機二星在卯宮或酉宮坐命，且無化忌同宮。
 * 另說： 巨機在酉宮守命不是此格，但總體而言仍不失為好的命局。
 */
fun fun巨機同宮() = { it: IPlate ->
  it.mainHouse.branch == Branch.卯
    && it.starMap[StarMain.巨門]?.stemBranch?.branch == Branch.卯
    && it.starMap[StarMain.天機]?.stemBranch?.branch == Branch.卯
}


/**
 * 天機、天梁二星同時在辰或戌宮守命，為此格。
 */
fun fun善蔭朝綱() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == Branch.辰 || it == Branch.戌)
      it
    else
      null
  }?.let { branch ->
    it.starMap[StarMain.天機]?.stemBranch?.branch == branch
      && it.starMap[StarMain.天梁]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 於三方四正中有天機、太陰、天同、天梁四星交會。
 *
 * 命宮的對宮、合宮見天機、太陰、天同、天梁等，同宮加會，謂之機月同梁格，主其人智慧超群，為最佳之幕僚、輔佐人才。
 */
fun fun機月同梁() = { it: IPlate ->

  val branches: List<Branch?> = listOf(StarMain.天機, StarMain.太陰, StarMain.天同, StarMain.天梁)
    .map { star: ZStar ->
      it.starMap[star]?.stemBranch?.branch
    }

  it.trinities.containsAll(branches)
}


/**
 * 太陽在午宮坐命。
 */
fun fun日麗中天() = { it: IPlate ->
  it.mainHouse.branch == Branch.午
    && it.starMap[StarMain.太陽]?.stemBranch?.branch == Branch.午
}

/**
 * 太陽在卯宮坐命。
 */
fun fun日出扶桑() = { it: IPlate ->
  it.mainHouse.branch == Branch.卯
    && it.starMap[StarMain.太陽]?.stemBranch?.branch == Branch.卯
}

/**
 * 命宮有紫微星，且於三方四正中有至少有左輔、右弼任何一星加會或同宮，或兩星於兩鄰宮相夾。
 */
fun fun君臣慶會() = { it: IPlate ->
  val test1: Boolean = it.mainHouse.branch == it.starMap[StarMain.紫微]?.stemBranch?.branch

  val branches: Set<Branch?> = setOf(StarLucky.左輔, StarLucky.右弼).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  // 鄰宮
  val neighbors: Set<Branch> = it.mainHouse.branch.let {
    setOf(it.next, it.previous)
  }

  test1 &&
    (it.trinities.containsAll(branches) || neighbors.containsAll(branches))
}


/**
 * 命宮在丑或未，日月二星坐守。
 */
fun fun日月同宮() = { it: IPlate ->
  it.mainHouse.branch.let { branch ->
    if (branch == Branch.丑 || branch == Branch.未)
      branch
    else
      null
  }?.let { branch ->
    it.starMap[StarMain.太陽]?.stemBranch?.branch == branch
      && it.starMap[StarMain.太陰]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 日月位於三方四正中，且太陽在巳、太陰在酉或太陽在辰、太陰在戌。為本格。
 * 命宮在丑，太陽在巳，太陰在酉，巳酉丑三合， 謂之日月雙明格，主其人財官皆美，金榜高中。
 *
 * 第一種情況是天梁在丑宮坐守命宮，太陽、太陰分別在巳（官祿宮）、酉（財帛宮）會照命宮，且兩顆主星皆處于廟旺之地，稱為“日月并明”
 * 第二種是命宮在午宮無主星，官祿宮在寅由巨門、太陽坐守，財帛在子有天同、太陰入駐，日月皆處廟旺之地同時會照命宮，而且有祿存、科權祿、左右、昌曲、魁鉞加會，即為“日月并明”格。
 */
fun fun日月並明() = { it: IPlate ->
  it.mainHouse.branch == Branch.丑
    && it.starMap[StarMain.太陽]?.stemBranch?.branch == Branch.巳
    && it.starMap[StarMain.太陰]?.stemBranch?.branch == Branch.酉
}

/**
 * 本宮在未宮，無主星坐命，且太陽在卯宮、太陰在亥宮。此時日月於三方四正中照命。
 *
 * 命宮在未，太陽在卯，太陰在亥，亥卯未三合，謂之明珠出海格，主其人財官皆美，科第榮恩。
 */
fun fun明珠出海() = { it: IPlate ->
  it.mainHouse.branch == Branch.未
    && it.starMap[StarMain.太陽]?.stemBranch?.branch == Branch.卯
    && it.starMap[StarMain.太陰]?.stemBranch?.branch == Branch.亥
}

/**
 * 巨門太陽同時在寅或申宮坐命。
 *
 * 太陽、巨門入命宮在寅時，謂之巨日同宮格，主其人食祿豐譽，口福聞名。
 */
fun fun巨日同宮() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == Branch.寅 || it == Branch.申)
      it
    else
      null
  }?.let { branch ->

    val branches: Set<Branch?> = listOf(StarMain.太陽, StarMain.巨門).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * 命宮在丑或未，武曲貪狼二星坐守。
 *
 * 貪狼武曲入命宮在丑或未時，謂之貪武同行格，主其人先貧後富，大器晚成，三十歲後發福。
 */
fun fun貪武同行() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == Branch.丑 || it == Branch.未)
      it
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(StarMain.武曲, StarMain.貪狼).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * 武曲坐命在辰或戌宮。
 */
fun fun將星得地() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == Branch.辰 || it == Branch.戌)
      it
    else
      null
  }?.let { branch ->
    it.starMap[StarMain.武曲]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 廉貞在申或寅宮守命。
 */
fun fun雄宿朝垣() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == Branch.寅 || it == Branch.申)
      it
    else
      null
  }?.let { branch ->
    it.starMap[StarMain.廉貞]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 太陰在亥宮守命，為本格。
 *
 * 太陰入命在亥宮時，夜晚出生者，謂之月朗天門格，主其人出相入將，非貴則富。
 */
fun fun月朗天門() = { it: IPlate ->

}

sealed class Pattern(val name: String, val predicate: (IPlate) -> Boolean) {

  class 極向離明 : Pattern("極向離明", fun極向離明())
  class 紫府同宮 : Pattern("紫府同宮", fun紫府同宮())
  class 紫府朝垣 : Pattern("紫府朝垣", fun紫府朝垣())
  class 巨機同宮 : Pattern("巨機同宮", fun巨機同宮())
  class 善蔭朝綱 : Pattern("善蔭朝綱", fun善蔭朝綱())
  class 機月同梁 : Pattern("機月同梁", fun機月同梁())
  class 日麗中天 : Pattern("日麗中天", fun日麗中天())
  class 日出扶桑 : Pattern("日出扶桑", fun日出扶桑())
  class 君臣慶會 : Pattern("君臣慶會", fun君臣慶會())
  class 日月同宮 : Pattern("日月同宮", fun日月同宮())
  class 日月並明 : Pattern("日月並明", fun日月並明())
  class 明珠出海 : Pattern("明珠出海", fun明珠出海())
  class 巨日同宮 : Pattern("巨日同宮", fun巨日同宮())
  class 貪武同行 : Pattern("貪武同行", fun貪武同行())
  class 將星得地 : Pattern("將星得地", fun將星得地())
  class 雄宿朝垣 : Pattern("雄宿朝垣", fun雄宿朝垣())

  companion object {
    fun values(): List<Pattern> {
      return listOf(極向離明(), 紫府同宮(), 紫府朝垣(), 巨機同宮(), 善蔭朝綱(), 機月同梁(), 日麗中天(), 日出扶桑(), 君臣慶會(),
                    日月同宮(), 日月並明(), 明珠出海(), 巨日同宮(), 貪武同行(), 將星得地(), 雄宿朝垣())
    }
  }
}
