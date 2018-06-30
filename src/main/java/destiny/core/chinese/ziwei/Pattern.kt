/**
 * Created by smallufo on 2018-06-29.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ziwei.PatternType.EVIL
import destiny.core.chinese.ziwei.PatternType.GOOD
import destiny.core.chinese.ziwei.StarLucky.*
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.StarUnlucky.*


/** 紫微在午宮坐命 */
fun fun極向離明() = { it: IPlate ->
  it.starMap[紫微]?.let { houseData ->
    houseData.stemBranch.let { sb -> sb == it.mainHouse && sb.branch == 午 }
  } ?: false
}

/** 安命在寅或申宮，紫微天府同宮。 */
fun fun紫府同宮() = { it: IPlate ->
  (it.mainHouse.branch == 寅 || it.mainHouse.branch == 申) &&
    it.starMap[紫微]?.stemBranch == it.starMap[天府]?.stemBranch
}


/**
 * 紫微、天府於三方四正照命。
 * 命宮在申，紫微在子，天府在辰，申子辰三合，謂之紫府朝垣格，主其人高官厚爵，福祿昌隆。
 * */
fun fun紫府朝垣() = { it: IPlate ->
  it.mainHouse.branch == 申
    && it.starMap[紫微]?.stemBranch?.branch == 子
    && it.starMap[天府]?.stemBranch?.branch == 辰
}

/**
 * 命宮在寅或申，天府在 事業宮，天相在財帛宮，謂之府相朝垣格，主其人大富大貴。
 */
fun fun府相朝垣() = { it: IPlate ->
  (it.mainHouse.branch == 寅 || it.mainHouse.branch == 申)
    && it.starMap[天府]?.house == House.官祿
    && it.starMap[天相]?.house == House.財帛
}


/**
 * 巨門、天機二星在卯宮或酉宮坐命，且無化忌同宮。
 * 另說： 巨機在酉宮守命不是此格，但總體而言仍不失為好的命局。
 *
 */
fun fun巨機同宮() = { it: IPlate ->
  it.mainHouse.branch == 卯
    && it.starMap[巨門]?.stemBranch?.branch == 卯
    && it.starMap[天機]?.stemBranch?.branch == 卯
}


/**
 * 天機、天梁二星同時在辰或戌宮守命，為此格。
 */
fun fun善蔭朝綱() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 辰 || it == 戌)
      it
    else
      null
  }?.let { branch ->
    it.starMap[天機]?.stemBranch?.branch == branch
      && it.starMap[天梁]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 於三方四正中有天機、太陰、天同、天梁四星交會。
 *
 * 命宮的對宮、合宮見天機、太陰、天同、天梁等，同宮加會，謂之機月同梁格，主其人智慧超群，為最佳之幕僚、輔佐人才。
 */
fun fun機月同梁() = { it: IPlate ->

  val branches: List<Branch?> = listOf(天機, 太陰, 天同, 天梁)
    .map { star: ZStar ->
      it.starMap[star]?.stemBranch?.branch
    }

  it.mainHouse.branch.trinities.containsAll(branches)
}


/**
 * 命宮在丑或未宮，太陽與太陰在左右鄰宮相夾。有財運，利於事業發展。
 *
 * TODO 命宮坐吉曜，太陽太陰在輔宮夾命宮，謂之日月夾命格，主其人不貴則大富。
 */
fun fun日月夾命() = { it: IPlate ->

  val branches: Set<Branch?> = setOf(太陽, 太陰).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  val neighbors = it.mainHouse.branch.let { branch ->
    setOf(branch.previous, branch.next)
  }

  branches.containsAll(neighbors)
}


/**
 * 太陽、太陰入田宅宮在丑或未時，謂之日月照壁格，主其人能獲豐盛的祖產、家業。
 */
fun fun日月照壁() = { it: IPlate ->
  it.houseMap[House.田宅]?.stars?.containsAll(listOf(太陽, 太陰)) ?: false
}


/**
 * (金燦光輝)
 * 太陽在午宮坐命。
 * TODO 白天出生者，謂之金燦光輝格，主富可敵國，或為權貴。
 */
fun fun日麗中天() = { it: IPlate ->
  it.mainHouse.branch == 午
    && it.starMap[太陽]?.stemBranch?.branch == 午
}


/**
 * 命宮有紫微星，且於三方四正中有至少有左輔、右弼任何一星加會或同宮，或兩星於兩鄰宮相夾。
 */
fun fun君臣慶會() = { it: IPlate ->
  val test1: Boolean = it.mainHouse.branch == it.starMap[紫微]?.stemBranch?.branch

  val branches: Set<Branch?> = setOf(左輔, 右弼).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  // 鄰宮
  val neighbors: Set<Branch> = it.mainHouse.branch.let {
    setOf(it.next, it.previous)
  }

  test1 &&
    (it.mainHouse.branch.trinities.containsAll(branches) || neighbors.containsAll(branches))
}


/**
 * 命宮在丑或未，日月二星坐守。
 */
fun fun日月同宮() = { it: IPlate ->
  it.mainHouse.branch.let { branch ->
    if (branch == 丑 || branch == 未)
      branch
    else
      null
  }?.let { branch ->
    it.starMap[太陽]?.stemBranch?.branch == branch
      && it.starMap[太陰]?.stemBranch?.branch == branch
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
  it.mainHouse.branch == 丑
    && it.starMap[太陽]?.stemBranch?.branch == 巳
    && it.starMap[太陰]?.stemBranch?.branch == 酉
}

/**
 * (日出扶桑)
 * 太陽入命宮在卯時，白天出生者，謂之日照雷門格，主其人榮華富貴。
 *
 * 在紫微斗數中，日照雷門格（又叫日出扶桑格）也是一個非常有名的富貴格局，此格局的特點是紫微命宮在卯，
 * TODO 其中有太陽、天梁坐守；財帛宮在亥，由太陰坐守；官祿宮在未，無主星入駐。另外需有吉星會照，如魁鉞、祿存、化祿、左輔右弼等, 如見空劫、煞忌星則破格。
 */
fun fun日照雷門() = { it: IPlate ->
  it.mainHouse.branch == 卯
    && it.dayNight == DayNight.DAY
    && it.starMap[太陽]?.stemBranch?.branch == 卯
}

/**
 * 本宮在未宮，無主星坐命，且太陽在卯宮、太陰在亥宮。此時日月於三方四正中照命。
 *
 * 命宮在未，太陽在卯，太陰在亥，亥卯未三合，謂之明珠出海格，主其人財官皆美，科第榮恩。
 */
fun fun明珠出海() = { it: IPlate ->
  it.mainHouse.branch == 未
    && it.starMap[太陽]?.stemBranch?.branch == 卯
    && it.starMap[太陰]?.stemBranch?.branch == 亥
}

/**
 * 巨門太陽同時在寅或申宮坐命。
 *
 * 太陽、巨門入命宮在寅時，謂之巨日同宮格，主其人食祿豐譽，口福聞名。
 */
fun fun巨日同宮() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 寅 || it == 申)
      it
    else
      null
  }?.let { branch ->

    val branches: Set<Branch?> = listOf(太陽, 巨門).map { star ->
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
    if (it == 丑 || it == 未)
      it
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(武曲, 貪狼).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * 武曲坐命在辰或戌宮。
 *
 * TODO 武曲入命宮在辰、戌、丑、未時，若又為辰、戌、丑、未年出生者，謂之將星得地格，主其人英名顯赫，大富大貴。
 */
fun fun將星得地() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 辰 || it == 戌)
      it
    else
      null
  }?.let { branch ->
    it.starMap[武曲]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 七殺入命宮在寅、申、子、午時， TODO 逢諸吉不逢惡曜，謂之七殺廟斗格，主其人一生官祿昌隆。
 */
fun fun七殺廟斗() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 辰 || it == 戌 || it == 丑 || it == 未)
      it
    else
      null
  }?.let { branch ->
    it.starMap[七殺]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * (雄宿朝元)
 * 廉貞在申或寅宮守命。
 *
 * 廉貞入命宮在寅或申時， TODO 逢諸吉不逢惡曜，謂之雄宿朝元格，主其人富貴且名揚四海。
 */
fun fun雄宿朝垣() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 寅 || it == 申)
      it
    else
      null
  }?.let { branch ->
    it.starMap[廉貞]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 命宮在子，太陽與化碌在午，謂之對面朝天格，主其人文章蓋世、超群。
 */
fun fun對面朝天() = { it: IPlate ->
  it.mainHouse.branch == 子
    && it.starMap[太陽]?.stemBranch?.branch == 午
    && it.tranFours[太陽]?.get(FlowType.本命)?.equals(ITransFour.Value.祿) ?: false
}

/**
 * 化科入命宮，化碌入遷移宮時，謂之科名會祿格，主其人威權出眾，大官貴。
 *
 * TODO 化科在命宮，化祿在三方四正會照。
 *
 * TODO 是指命宮中有化科星坐守，三方四正有化祿或祿存星會照。紫微命盤中有此格局的人才華卓越，有名望，遠近皆榮顯，風云際會，開展運程，財名皆足。步入社會發展，可獲擢昇。多主大限行吉之地平步青云，惟財官二宮亦并有魁鉞守照方作此斷。加煞忌、辛勞更甚、以破格論。
 */
fun fun科名會祿() = { it: IPlate ->
  val 化科入命宮: Boolean = it.houseMap[House.命宮]!!.stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(ITransFour.Value.科)

  val 化碌入遷移宮: Boolean = it.houseMap[House.遷移]?.stars?.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }?.contains(ITransFour.Value.祿) ?: false

  化科入命宮 && 化碌入遷移宮
}

/**
 * 化科入命宮，化權入遷移宮時，謂之科權逢迎格，主其人科甲及第，金榜高中。
 */
fun fun科權逢迎() = { it: IPlate ->
  val 化科入命宮: Boolean = it.houseMap[House.命宮]!!.stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(ITransFour.Value.科)

  val 化權入遷移宮: Boolean = it.houseMap[House.遷移]?.stars?.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }?.contains(ITransFour.Value.權) ?: false

  化科入命宮 && 化權入遷移宮
}

/**
 * 祿存、化碌入命宮時，謂之祿合鴛鴦格，主其人一生富貴無窮。
 * NOTE : 祿存 沒有化祿的機會。 因此，應該判讀為： 祿存 在命宮，且，命宮有星 化祿
 *
 * 雙祿交流格 : TODO 祿存和化祿俱在三方四正中。有財源，在事業上有成富的機運。又稱為祿合鴛鴦格。
 */
fun fun祿合鴛鴦() = { it: IPlate ->
  val 化祿入命宮: Boolean = it.houseMap[House.命宮]!!.stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(ITransFour.Value.祿)

  化祿入命宮 &&
    (it.mainHouse.branch == it.starMap[祿存]?.stemBranch?.branch)
}

/**
 * 化碌入財帛宮，祿存入官祿宮，三合命宮，謂之雙祿朝垣格，主其人終身富貴無窮。
 */
fun fun雙祿朝垣() = { it: IPlate ->
  val 化碌入財帛宮: Boolean = it.houseMap[House.財帛]?.stars?.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }?.contains(ITransFour.Value.祿) ?: false

  化碌入財帛宮 &&
    (it.starMap[祿存]?.house == House.官祿)
}


/**
 * 化權、化碌、化科在命宮的三方四正，謂之三奇加會格，主其文才蓋世，出將入相。
 * (一定是：命宮、官祿、財帛)
 */
fun fun三奇加會() = { it: IPlate ->
  val good3: Set<ITransFour.Value> = setOf(ITransFour.Value.祿, ITransFour.Value.權, ITransFour.Value.科)
  it.mainHouse.branch.trinities.all { branch ->
    val a: Set<ITransFour.Value?> = it.getHouseDataOf(branch).stars.map { star ->
      it.tranFours[star]?.get(FlowType.本命)
    }.toSet()
    a.any { good3.contains(it) }
  }
}


/**
 * TODO 天馬、化碌、祿存在命宮或財帛宮的三方四正，謂之祿馬交馳格，主其人富貴、榮顯
 *
 * 命宮或三方有祿存、天馬或為化祿、天馬。主奔波勞碌而招財。
 */
fun fun祿馬交馳() = { it: IPlate ->
  // PENDING
  false
}

/**
 * 太陰在亥宮守命，為本格。
 *
 * TODO 太陰入命在亥宮時，夜晚出生者，謂之月朗天門格，主其人出相入將，非貴則富。
 */
fun fun月朗天門() = { it: IPlate ->
  it.mainHouse.branch == 亥
    && it.starMap[太陰]?.stemBranch?.branch == 亥
  //&& it.dayNight == DayNight.NIGHT
}

/**
 * 太陰、天同星在子宮坐命。
 *
 * TODO : 日月滄海格：太陰入命宮在子時，夜晚出生者，謂之日月滄海格，主其人富貴、清高、忠良。
 */
fun fun月生滄海() = { it: IPlate ->
  it.mainHouse.branch == 子
    && it.starMap[太陰]?.stemBranch?.branch == 子
    && it.starMap[天同]?.stemBranch?.branch == 子
}

/**
 * 巨門在子或午宮坐命。
 *
 * TODO : 巨門入命宮在子或午時，又逢化權、化碌同宮，謂之石中隱玉格，主其人大富大貴，福祿豐隆。
 */
fun fun石中隱玉() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 子 || it == 午)
      it
    else
      null
  }?.let { branch ->
    it.starMap[巨門]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 天梁守命，入午宮。
 *
 * 天梁入命宮在午時， TODO 逢諸吉不逢惡曜，謂之壽星入廟格，主其人官貴清高，福壽綿長。
 */
fun fun壽星入廟() = { it: IPlate ->
  it.mainHouse.branch == 午
    && it.starMap[天梁]?.stemBranch?.branch == 午
}

/**
 * 破軍守命居子或午宮。
 *
 * 破軍入命宮在子或午時，不逢惡耀等等，謂之英星入廟格，主其人高官厚祿，大貴顯榮。
 */
fun fun英星入廟() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 子 || it == 午)
      it
    else
      null
  }?.let { branch ->
    it.starMap[破軍]?.stemBranch?.branch == branch
  } ?: false
}

/**
 * 天機、天梁入命宮在辰或戌宮，謂之機梁加會格，主其人富貴、仁慈、善良。
 */
fun fun機梁加會() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 辰 || it == 戌)
      it
    else
      null
  }?.let { branch ->

    val branches: Set<Branch?> = listOf(天機, 天梁).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * 文昌、文曲兩星在丑或未宮守命。
 *
 * TODO 文昌、文曲入命宮，或夾命宮，或三合命宮，謂之文桂文華格，主其人多學而廣，非富則貴。
 * */
fun fun文桂文華() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 丑 || it == 未)
      it
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(文昌, 文曲).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * 天魁、天鉞入命宮，或夾命宮，或三合命宮，謂之魁鉞拱命格，主其人文章蓋世，高官厚祿，逢凶化吉，大富大貴。
 */
fun fun魁鉞拱命() = { it: IPlate ->
  val branches: Set<Branch?> = listOf(天魁, 天鉞).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  val 入命宮: Boolean = setOf(it.mainHouse.branch).containsAll(branches)
  val 夾命宮: Boolean = it.mainHouse.branch.let { branch ->
    setOf(branch.next, branch.previous)
  }.let { set -> branches.containsAll(set) }

  val 三合命宮: Boolean = it.mainHouse.branch.let { branch ->
    setOf(branch.next(4), branch.prev(4))
  }.let { set -> branches.containsAll(set) }

  入命宮 || 夾命宮 || 三合命宮
}

/**
 * 命宮在寅或申宮，遇紫微與天府來夾。
 */
fun fun紫府夾命() = { it: IPlate ->
  val branches: Set<Branch?> = listOf(紫微, 天府).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  it.mainHouse.branch.let {
    if (it == 寅 || it == 申)
      it
    else
      null
  }?.let { branch ->
    branches.containsAll(setOf(branch.previous, branch.next))
  } ?: false
}

/**
 * 命宮入丑或未宮，左輔右弼同宮，為本格。
 * 左輔、右弼入命宮 TODO 或三合命宮，鄰夾命宮，謂之左右同宮格，主其人終生福厚、豐隆。
 * */
fun fun左右同宮() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 丑 || it == 未)
      it
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(左輔, 右弼).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

// =========================== 以下 , 惡格 ===========================

/**
 * 擎羊坐命午宮，為本格。
 *
 * TODO 擎羊與貪狼，或擎羊與天同入命宮在午時，謂之馬頭帶箭格，主其人威震邊疆，沙場馳名。
 */
fun fun馬頭帶劍() = { it: IPlate ->
  it.mainHouse.branch == 午
    && it.starMap[擎羊]?.stemBranch?.branch == 午
}

/**
 * 紫微、貪狼同在卯或酉坐命。
 */
fun fun極居卯酉() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 卯 || it == 酉)
      it
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(紫微, 貪狼).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * 命宮裡無任何十四顆主星坐命。
 */
fun fun命無正曜() = { it: IPlate ->
  !StarMain.values.map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet().contains(it.mainHouse.branch)
}

/**
 * 擎羊、陀羅於左右鄰宮夾命。
 */
fun fun羊陀夾命() = { it: IPlate ->
  val branches: Set<Branch?> = listOf(擎羊, 陀羅).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  val neighbors: Set<Branch> = it.mainHouse.branch.let {
    setOf(it.previous, it.next)
  }

  branches.containsAll(neighbors)
}

/**
 * 火星、鈴星在左右鄰宮相夾命宮，即為此格。
 *
 * TODO : 若為火鈴夾貪格情況，就不 為火鈴夾命格。
 */
fun fun火鈴夾命() = { it: IPlate ->
  val branches: Set<Branch?> = listOf(火星, 鈴星).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  val neighbors: Set<Branch> = it.mainHouse.branch.let {
    setOf(it.previous, it.next)
  }

  branches.containsAll(neighbors)
}

/**
 * 在寅宮，貪狼坐命，遇陀羅同宮。
 */
fun fun風流綵杖() = { it: IPlate ->
  it.mainHouse.branch == 寅
    && it.starMap[貪狼]?.stemBranch?.branch == 寅
    && it.starMap[陀羅]?.stemBranch?.branch == 寅
}

/**
 * 巨門、天機同在酉宮坐命，有化忌同宮。性質為奔波飄蕩。不利於感情、 事業。
 */
fun fun巨機化酉() = { it: IPlate ->
  val 化忌入命宮: Boolean = it.houseMap[House.命宮]!!.stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(ITransFour.Value.忌)

  化忌入命宮
    && it.mainHouse.branch == 酉
    && it.starMap[巨門]?.stemBranch?.branch == 酉
    && it.starMap[天機]?.stemBranch?.branch == 酉
}

/**
 * 太陽在戌宮坐命，此時太陰在辰宮；或太陰在辰宮坐命，太陽在戌宮。
 * 取其日在戌時，月在辰時， 兩星光芒皆弱不旺。勞碌命，求人不如勞己。無閒享清福。
 */
fun fun日月反背() = { it: IPlate ->
  val value1 = (it.mainHouse.branch == 戌 && it.starMap[太陽]?.stemBranch?.branch == 戌)
  val value2 = (it.mainHouse.branch == 辰 && it.starMap[太陰]?.stemBranch?.branch == 辰)
  value1 || value2
}

/**
 * 天梁在巳亥寅申宮坐命，與天馬同宮。
 * 天馬只會出現於四馬地(巳 亥寅申。 此格表示勞而無獲之象。若顯現在感情生活上，對婚姻生活帶來不利影響。
 */
fun fun梁馬飄蕩() = { it: IPlate ->
  // TODO : PENDING 天馬
}

/**
 * 廉貞、七殺同在丑或未宮守命。
 * 此格人應注意法律方面的問題。
 */
fun fun貞殺同宮() = { it: IPlate ->
  it.mainHouse.branch.let { branch ->
    if (branch == 丑 || branch == 未)
      branch
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(廉貞, 七殺).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * (刑囚會印)
 * 刑囚夾印，刑仗唯司
 * 廉貞、天相在子或午宮坐命有擎羊同宮。天相為印，廉貞為囚，擎羊 化氣為刑。應注意法律訴訟問題。
 * 廉貞、天相、擎羊 其實無法「夾」，故，「刑囚會印」比較貼切
 *
 * 「刑囚夾印」在紫微斗數裡是一種破敗格局，主是非官訟，其中
 *    「刑」代表「羊刃」，乃六煞星之首；
 *    「囚」指的是廉貞星，廉貞星乃次桃花星；
 *    「印」指的是天相星，是一顆宰相星，代表參謀作業，化氣為蔭。
 */
fun fun刑囚夾印() = { it: IPlate ->
  it.mainHouse.branch.let {
    if (it == 子 || it == 午)
      it
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(廉貞, 天相, 擎羊).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()

    setOf(branch).containsAll(branches)
  } ?: false
}

/**
 * 巨門守命，且在三方四正中，與羊陀火鈴四煞同時有會照或同宮關係。
 * 此格局應防意外之災或為不得已苦衷流落四方。
 */
fun fun巨逢四煞() = { it: IPlate ->
  it.mainHouse.branch.let { branch ->
    if (branch == it.starMap[巨門]?.stemBranch?.branch)
      branch
    else
      null
  }?.let { branch ->
    val branches: Set<Branch?> = listOf(擎羊, 陀羅, 火星, 鈴星).map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet()
    branch.trinities.containsAll(branches)
  } ?: false
}

/**
 * 地劫、地空二星或其中之一星守命。
 * 有精神上孤獨，錢不易留住之跡象。
 */
fun fun命裡逢空() = { it: IPlate ->
  it.houseMap[House.命宮]!!.stars.any { setOf(地劫, 地空).contains(it) }
}

/**
 * 地劫、地空二星在左右鄰宮夾命。
 * 有精神上孤獨，錢不易留住之跡象。
 */
fun fun空劫夾命() = { it: IPlate ->
  val neighbors: Set<Branch> = it.mainHouse.branch.let { branch ->
    setOf(branch.previous, branch.next)
  }

  val branches: Set<Branch?> = listOf(地劫, 地空).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  branches.containsAll(neighbors)
}

/**
 * 文昌或文曲守命，遇空劫 或火鈴或羊陀對星來夾。有懷才不遇跡象。
 */
fun fun文星遇夾() = { it: IPlate ->
  val 命宮有文星: Boolean = it.houseMap[House.命宮]!!.stars.any { setOf(文昌, 文曲).contains(it) }

  val 空劫夾命: Boolean = fun空劫夾命().invoke(it)
  val 火鈴夾命: Boolean = fun火鈴夾命().invoke(it)
  val 羊陀夾命: Boolean = fun羊陀夾命().invoke(it)

  命宮有文星 && (空劫夾命 || 火鈴夾命 || 羊陀夾命)
}

/**
 * 化忌坐命，擎羊、陀羅於兩鄰宮相夾。
 *
 * 祿存在命宮，則必為羊陀所夾。若有化忌星同宮，羊陀凶性得以充分發揮。雖有祿存守命，亦不為美。
 */
fun fun羊陀夾忌() = { it: IPlate ->
  val 羊陀夾命: Boolean = fun羊陀夾命().invoke(it)
  val 化忌入命宮: Boolean = it.houseMap[House.命宮]!!.stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(ITransFour.Value.忌)

  羊陀夾命 && 化忌入命宮
}

/**
 * 天相受化忌和天梁於左右鄰宮相夾；或天相受化忌和擎羊於左右鄰宮相夾。
 */
fun fun刑忌夾印() = { it: IPlate ->
  it.starMap[天相]?.stemBranch?.branch?.let { branch ->
    setOf(branch.previous, branch.next)
  }?.let { neighbors ->
    val 鄰宮化忌: Branch? =
      neighbors.firstOrNull { branch ->
        it.getHouseDataOf(branch).stars.any { star ->
          it.tranFours[star]?.get(FlowType.本命) == ITransFour.Value.忌
        }
      }

    val 另宮: Branch? = 鄰宮化忌?.let { branch -> neighbors.minus(branch).first() }
    另宮?.let { branch ->
      val 另宮有天梁: Boolean = it.starMap[天梁]?.stemBranch?.branch == branch
      val 另宮有擎羊: Boolean = it.starMap[擎羊]?.stemBranch?.branch == branch
      另宮有天梁 || 另宮有擎羊
    } ?: false
  } ?: false
}

/**
 * 天馬遇地劫、地空同宮或三方沖照。奔波，空忙一場。
 */
fun fun馬落空亡() = { it: IPlate ->
  // TODO : 天馬
}


/**
 * 祿存、化祿同時坐命，遇地劫、地空同宮。
 * 此即 [fun祿合鴛鴦] 格
 *
 * 祿存、化祿同時坐命，本為 雙祿交流格。但若遇地空、地劫同宮，此時雙祿為被衝破情形，稱為兩重華蓋。
 * 華蓋表示有宗教緣分。皈依宗教，反可享主清福。但因雙祿被衝破，較不易累積錢財。
 */
fun fun兩重華蓋() = { it: IPlate ->
  val 祿合鴛鴦 = fun祿合鴛鴦().invoke(it)

  val 空劫入命: Boolean = listOf(地劫, 地空).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet().let { branches -> setOf(it.mainHouse.branch).containsAll(branches) }

  祿合鴛鴦 && 空劫入命
}


/**
 * 祿存或化祿坐命，在三方四正中，有被地劫、地空衝破。
 * 吉處藏凶之象，應居安思危。
 */
fun fun祿逢衝破() = { it: IPlate ->
  val 化祿入命宮: Boolean = it.houseMap[House.命宮]!!.stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(ITransFour.Value.祿)

  val 祿存坐命: Boolean = it.starMap[祿存]?.stemBranch?.branch == it.mainHouse.branch

  val 三方四正: Set<Branch> = it.mainHouse.branch.let { b -> b.trinities.plus(b.opposite) }

  val 空劫: Set<Branch?> = listOf(地劫, 地空).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  (化祿入命宮 || 祿存坐命) && 三方四正.containsAll(空劫)
}

/**
 * 貪狼坐命在子宮。
 * 廉貞、貪狼坐命於亥宮，遇陀羅同宮。
 *
 * 無論男女，多風流，感情債不斷。
 *
 * 泛水桃花的極致是貪狼居子宮遇擎羊同宮
 */
fun fun泛水桃花() = { it: IPlate ->
  val 貪狼坐命在子宮 = it.mainHouse.branch == 子
    && it.starMap[貪狼]?.stemBranch?.branch == 子

  val 貪狼坐命在亥宮 = it.mainHouse.branch == 亥
    && it.starMap[貪狼]?.stemBranch?.branch == 亥
  val 廉貞在亥 = it.starMap[廉貞]?.stemBranch?.branch == 亥
  val 陀羅在亥 = it.starMap[陀羅]?.stemBranch?.branch == 亥

  貪狼坐命在子宮 || (貪狼坐命在亥宮 && 廉貞在亥 && 陀羅在亥)
}

enum class PatternType {
  GOOD, EVIL
}

/**
 * 參考資料
 * https://goo.gl/hDUun2 ( http://vioheart.pixnet.net )
 * http://www.ai5429.com/c/clock108/
 */
sealed class Pattern(val name: String, val type: PatternType, val predicate: (IPlate) -> Boolean) {

  class 極向離明 : Pattern("極向離明", GOOD, fun極向離明())
  class 紫府同宮 : Pattern("紫府同宮", GOOD, fun紫府同宮())
  class 紫府朝垣 : Pattern("紫府朝垣", GOOD, fun紫府朝垣())
  class 府相朝垣 : Pattern("府相朝垣", GOOD, fun府相朝垣())
  class 巨機同宮 : Pattern("巨機同宮", GOOD, fun巨機同宮())
  class 善蔭朝綱 : Pattern("善蔭朝綱", GOOD, fun善蔭朝綱())
  class 機月同梁 : Pattern("機月同梁", GOOD, fun機月同梁())
  class 日月照壁 : Pattern("日月照壁", GOOD, fun日月照壁())
  class 日麗中天 : Pattern("日麗中天", GOOD, fun日麗中天())
  class 日月夾命 : Pattern("日月夾命", GOOD, fun日月夾命())
  class 君臣慶會 : Pattern("君臣慶會", GOOD, fun君臣慶會())
  class 日月同宮 : Pattern("日月同宮", GOOD, fun日月同宮())
  class 日月並明 : Pattern("日月並明", GOOD, fun日月並明())
  class 日照雷門 : Pattern("日照雷門", GOOD, fun日照雷門())
  class 明珠出海 : Pattern("明珠出海", GOOD, fun明珠出海())
  class 巨日同宮 : Pattern("巨日同宮", GOOD, fun巨日同宮())
  class 貪武同行 : Pattern("貪武同行", GOOD, fun貪武同行())
  class 將星得地 : Pattern("將星得地", GOOD, fun將星得地())
  class 七殺廟斗 : Pattern("七殺廟斗", GOOD, fun七殺廟斗())
  class 雄宿朝垣 : Pattern("雄宿朝垣", GOOD, fun雄宿朝垣())
  class 對面朝天 : Pattern("對面朝天", GOOD, fun對面朝天())
  class 科名會祿 : Pattern("科名會祿", GOOD, fun科名會祿())
  class 科權逢迎 : Pattern("科權逢迎", GOOD, fun科權逢迎())
  class 祿合鴛鴦 : Pattern("祿合鴛鴦", GOOD, fun祿合鴛鴦())
  class 雙祿朝垣 : Pattern("雙祿朝垣", GOOD, fun雙祿朝垣())
  class 三奇加會 : Pattern("三奇加會", GOOD, fun三奇加會())
  class 祿馬交馳 : Pattern("祿馬交馳", GOOD, fun祿馬交馳())
  class 月朗天門 : Pattern("月朗天門", GOOD, fun月朗天門())
  class 月生滄海 : Pattern("月生滄海", GOOD, fun月生滄海())
  class 石中隱玉 : Pattern("石中隱玉", GOOD, fun石中隱玉())
  class 壽星入廟 : Pattern("壽星入廟", GOOD, fun壽星入廟())
  class 英星入廟 : Pattern("英星入廟", GOOD, fun英星入廟())
  class 機梁加會 : Pattern("機梁加會", GOOD, fun機梁加會())
  class 文桂文華 : Pattern("文桂文華", GOOD, fun文桂文華())
  class 魁鉞拱命 : Pattern("魁鉞拱命", GOOD, fun魁鉞拱命())
  class 紫府夾命 : Pattern("紫府夾命", GOOD, fun紫府夾命())
  class 左右同宮 : Pattern("左右同宮", GOOD, fun左右同宮())

  class 馬頭帶劍 : Pattern("馬頭帶劍", EVIL, fun馬頭帶劍())
  class 極居卯酉 : Pattern("極居卯酉", EVIL, fun極居卯酉())
  class 命無正曜 : Pattern("命無正曜", EVIL, fun命無正曜())
  class 羊陀夾命 : Pattern("羊陀夾命", EVIL, fun羊陀夾命())
  class 火鈴夾命 : Pattern("火鈴夾命", EVIL, fun火鈴夾命())
  class 風流綵杖 : Pattern("風流綵杖", EVIL, fun風流綵杖())
  class 巨機化酉 : Pattern("巨機化酉", EVIL, fun巨機化酉())
  class 日月反背 : Pattern("日月反背", EVIL, fun日月反背())
  class 貞殺同宮 : Pattern("貞殺同宮", EVIL, fun貞殺同宮())
  class 刑囚夾印 : Pattern("刑囚夾印", EVIL, fun刑囚夾印())
  class 巨逢四煞 : Pattern("巨逢四煞", EVIL, fun巨逢四煞())
  class 命裡逢空 : Pattern("命裡逢空", EVIL, fun命裡逢空())
  class 空劫夾命 : Pattern("空劫夾命", EVIL, fun空劫夾命())
  class 文星遇夾 : Pattern("文星遇夾", EVIL, fun文星遇夾())
  class 羊陀夾忌 : Pattern("羊陀夾忌", EVIL, fun羊陀夾忌())
  class 刑忌夾印 : Pattern("刑忌夾印", EVIL, fun刑忌夾印())
  class 兩重華蓋 : Pattern("兩重華蓋", EVIL, fun兩重華蓋())
  class 祿逢衝破 : Pattern("祿逢衝破", EVIL, fun祿逢衝破())
  class 泛水桃花 : Pattern("泛水桃花", EVIL, fun泛水桃花())

  companion object {
    fun values(): List<Pattern> {
      return listOf(
        極向離明(), 紫府同宮(), 紫府朝垣(), 府相朝垣(), 巨機同宮(), 善蔭朝綱(), 機月同梁(), 日月照壁(), 日麗中天(), 日月夾命(),
        君臣慶會(), 日月同宮(), 日月並明(), 日照雷門(), 明珠出海(), 巨日同宮(), 貪武同行(), 將星得地(), 七殺廟斗(), 雄宿朝垣(),
        對面朝天(), 科名會祿(), 科權逢迎(), 祿合鴛鴦(), 雙祿朝垣(), 三奇加會(), 祿馬交馳(), 月朗天門(), 月生滄海(), 石中隱玉(),
        壽星入廟(), 英星入廟(), 機梁加會(), 文桂文華(), 魁鉞拱命(), 紫府夾命(), 左右同宮(),

        馬頭帶劍(), 極居卯酉(), 命無正曜(), 羊陀夾命(), 火鈴夾命(), 風流綵杖(), 巨機化酉(), 日月反背(), 貞殺同宮(), 刑囚夾印(),
        巨逢四煞(), 命裡逢空(), 空劫夾命(), 文星遇夾(), 羊陀夾忌(), 刑忌夾印(), 兩重華蓋(), 祿逢衝破(), 泛水桃花())
    }
  }


}

