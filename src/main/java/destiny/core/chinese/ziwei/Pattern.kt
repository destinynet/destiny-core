/**
 * Created by smallufo on 2018-07-01.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.ziwei.House.*
import destiny.core.chinese.ziwei.ITransFour.Value.*
import destiny.core.chinese.ziwei.Pattern.*
import destiny.core.chinese.ziwei.PatternType.EVIL
import destiny.core.chinese.ziwei.PatternType.GOOD
import destiny.core.chinese.ziwei.StarLucky.*
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.StarUnlucky.*


fun IPlate.拱(branch: Branch = this.mainHouse.branch): Set<Branch> = branch.let { setOf(it.prev(4), it.next(4)) }
fun IPlate.三方(branch: Branch = this.mainHouse.branch) = 拱(branch).plus(branch)
fun IPlate.三方四正(branch: Branch = this.mainHouse.branch): Set<Branch> = 三方(branch).plus(branch.opposite)

fun IPlate.neighbors(branch: Branch = this.mainHouse.branch): Set<Branch> = branch.let { setOf(it.previous, it.next) }

fun IPlate.紫府(): List<Branch?> = setOf(紫微, 天府).map { star -> this.starMap[star]?.stemBranch?.branch }
fun IPlate.輔弼(): List<Branch?> = setOf(左輔, 右弼).map { star -> this.starMap[star]?.stemBranch?.branch }
fun IPlate.昌曲(): List<Branch?> = setOf(文昌, 文曲).map { star -> this.starMap[star]?.stemBranch?.branch }
fun IPlate.魁鉞(): List<Branch?> = setOf(天魁, 天鉞).map { star -> this.starMap[star]?.stemBranch?.branch }

fun IPlate.劫空(): List<Branch?> =
  listOf(地劫, 地空).map { star -> this.starMap[star]?.stemBranch?.branch }

fun IPlate.三方四正有輔弼(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(輔弼())
fun IPlate.鄰宮有輔弼(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(輔弼())

fun IPlate.三方四正有昌曲(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(昌曲())
fun IPlate.鄰宮有昌曲(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(昌曲())

fun IPlate.三方四正有魁鉞(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(魁鉞())
fun IPlate.鄰宮有魁鉞(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(魁鉞())

fun IPlate.三方四正有祿存(branch: Branch = this.mainHouse.branch) =
  this.三方四正(branch).contains(this.starMap[祿存]?.stemBranch?.branch)

fun IPlate.三方四正有祿權科星(branch: Branch = this.mainHouse.branch): Boolean =
  this.三方四正(branch).flatMap { b ->
    this.getHouseDataOf(b).stars.map { star: ZStar ->
      this.tranFours[star]?.get(FlowType.本命)
    }
  }.any { setOf(祿, 權, 科).contains(it) }


fun IPlate.化祿入命宮(): Boolean = this.getHouseDataOf(this.mainHouse.branch).stars.map { star ->
  this.tranFours[star]?.get(FlowType.本命)
}.contains(祿)

fun IPlate.化科入命宮(): Boolean = this.getHouseDataOf(this.mainHouse.branch).stars.map { star ->
  this.tranFours[star]?.get(FlowType.本命)
}.contains(科)

enum class GoodCombination {
  輔弼,
  昌曲,
  魁鉞,
  祿存,
  祿權科星
}

enum class EvilCombination {
  空劫,
  火鈴,
  羊陀
}

// =========================== 以下 , 吉格 ===========================

/** 紫微在午宮坐命 */
fun fun極向離明() = { it: IPlate ->
  it.starMap[紫微]?.let { houseData ->
    houseData.stemBranch
      .let { sb -> sb == it.mainHouse && sb.branch == 午 }
      .let { if (it) 極向離明 else null }
  }
}

/** 安命在寅或申宮，紫微天府同宮。 */
fun fun紫府同宮() = { it: IPlate ->
  if (
    (it.mainHouse.branch == 寅 || it.mainHouse.branch == 申)
    && it.starMap[紫微]?.stemBranch == it.starMap[天府]?.stemBranch
  )
    紫府同宮
  else
    null
}

/**
 * 紫微、天府於三方四正照命。
 * 命宮在申，紫微在子，天府在辰，申子辰三合，謂之紫府朝垣格，主其人高官厚爵，福祿昌隆。
 *
 *
 * 紫府朝垣格就是紫微、天府在命宮三方四正合照命宮，且有祿存或者科、權、祿，或者有左輔右弼、文昌文曲、天魁天鉞等吉星會照。
 * */
fun fun紫府朝垣() = { it: IPlate ->

  val 三方四正有紫府 = it.三方四正().containsAll(it.紫府())

  val goods = mutableSetOf<GoodCombination>().takeIf { 三方四正有紫府 }?.apply {
    if (it.三方四正有輔弼())
      add(GoodCombination.輔弼)
    if (it.三方四正有昌曲())
      add(GoodCombination.昌曲)
    if (it.三方四正有魁鉞())
      add(GoodCombination.魁鉞)
    if (it.三方四正有祿存())
      add(GoodCombination.祿存)
    if (it.三方四正有祿權科星())
      add(GoodCombination.祿權科星)
  }?.toSet()

  if (三方四正有紫府 && goods != null && goods.isNotEmpty()) {
    紫府朝垣(goods)
  } else
    null
}


/**
 * 天府、廉貞二星在戌宮坐命會祿存、科權祿、左右、昌曲、魁鉞諸吉星，無煞方合此格，有左輔或右弼在命宮方好，甲己年生人最佳，丁年生人次之。
 *
 * 類似 [fun極向離明] 的發展，因為當紫微在中宮(午)時，廉貞天府必在戌宮。訂定這個格局的人，其構想是以紫微為帝垣，在戌宮的天府則有如一位大臣，朝拱于帝座。
 *
 * 所以歌裡才會有「乾為君象府為臣」，「輔弼忠臣身報國」的說法。
 * 這個格局。其實也可以說是「紫相朝垣」，因為命宮在戌，紫微在午，天相在寅，寅午兩宮朝向戌垣。故古人認為「天府臨戌有星拱，腰金衣紫。」
 * 從訂定格局的立意來看，顯然「天府朝垣」只能成為「輔弼」，所謂「腰金衣紫」，無非只是屬于大臣的榮譽，究竟不是領袖人材。
 * 根據現代社會結構，廉貞天府在戌宮同守的人，只是一位很好的理財人材。若甲年生人，廉貞化祿，祿存又居于寅宮相會，則其人亦能創業致富，但卻缺少開創力，只能在守成中發展，不擅長開創。若天馬在寅宮，則其人利于外埠經商。
 */
fun fun天府朝垣() = { it: IPlate ->

  val 天府廉貞在戌宮坐命: Boolean = it.mainHouse.branch.let { branch ->
    branch == 戌
      && setOf(branch).containsAll(listOf(天府, 廉貞).map { star: ZStar -> it.starMap[star]?.stemBranch?.branch })
  }

  val goods = mutableSetOf<GoodCombination>().takeIf { 天府廉貞在戌宮坐命 }?.apply {
    if (it.三方四正有輔弼())
      add(GoodCombination.輔弼)
    if (it.三方四正有昌曲())
      add(GoodCombination.昌曲)
    if (it.三方四正有魁鉞())
      add(GoodCombination.魁鉞)
    if (it.三方四正有祿存())
      add(GoodCombination.祿存)
    if (it.三方四正有祿權科星())
      add(GoodCombination.祿權科星)
  }?.toSet()

  if (天府廉貞在戌宮坐命 && goods != null && goods.isNotEmpty())
    天府朝垣(goods)
  else
    null
}


/**
 * 命宮在寅或申，天府在 事業宮，天相在財帛宮，謂之府相朝垣格，主其人大富大貴。
 *
 * 命宮三方四正有祿存、科權祿、左右、昌曲、魁鉞加會方合此格，有四煞劫空化忌加會則破格。此格尚主與與親人朋友感情深，人情味濃。
 */
fun fun府相朝垣() = { it: IPlate ->

  val 府相宮位正確 = (it.mainHouse.branch == 寅 || it.mainHouse.branch == 申)
    && it.starMap[天府]?.house == House.官祿
    && it.starMap[天相]?.house == House.財帛

  val goods = mutableSetOf<GoodCombination>().takeIf { 府相宮位正確 }?.apply {
    if (it.三方四正有輔弼())
      add(GoodCombination.輔弼)
    if (it.三方四正有昌曲())
      add(GoodCombination.昌曲)
    if (it.三方四正有魁鉞())
      add(GoodCombination.魁鉞)
    if (it.三方四正有祿存())
      add(GoodCombination.祿存)
    if (it.三方四正有祿權科星())
      add(GoodCombination.祿權科星)
  }?.toSet()

  if (府相宮位正確 && goods != null && goods.isNotEmpty())
    府相朝垣(goods)
  else
    null
}


/**
 * 巨門、天機二星在卯宮或酉宮坐命，且無化忌同宮。
 * 另說： 巨機在酉宮守命不是此格，但總體而言仍不失為好的命局。
 *
 */
fun fun巨機同宮() = { it: IPlate ->
  if (
    it.mainHouse.branch == 卯
    && it.starMap[巨門]?.stemBranch?.branch == 卯
    && it.starMap[天機]?.stemBranch?.branch == 卯
  )
    巨機同宮
  else
    null
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
  }?.let {
    if (it)
      善蔭朝綱
    else
      null
  }
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

  if (it.三方四正().containsAll(branches))
    機月同梁
  else
    null
}


/**
 * 太陽、太陰入田宅宮在丑或未時，謂之日月照壁格，主其人能獲豐盛的祖產、家業。
 */
fun fun日月照壁() = { it: IPlate ->
  it.houseMap[House.田宅]?.stars?.containsAll(listOf(太陽, 太陰))?.let {
    if (it)
      日月照壁
    else
      null
  }
}


/**
 * (金燦光輝)
 * 太陽在午宮坐命。
 * TODO 白天出生者，謂之金燦光輝格，主富可敵國，或為權貴。
 */
fun fun日麗中天() = { it: IPlate ->
  if (
    it.mainHouse.branch == 午
    && it.starMap[太陽]?.stemBranch?.branch == 午
  )
    日麗中天(it.dayNight)
  else
    null
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

  if (branches.containsAll(it.neighbors()))
    日月夾命
  else
    null
}


/**
 * 命宮有紫微星，且於三方四正中有至少有左輔、右弼任何一星加會或同宮，或兩星於兩鄰宮相夾。
 *
 * 命宮有紫微星，得天府、天相、左輔、右弼、文昌、文曲、三台、八座、龍池、鳳閣、恩光、天貴等吉星在三方四正會合，無煞方合此格，加祿存並吉化更佳。
 *
 * (用此法)
 * 「君臣慶會」通常指的是紫微星坐宮和三合宮，坐入三組六吉星（左輔、右弼一組，文昌、文曲一組，天魁、天鉞一組）各一顆以上所形成的格局，
 *
 * 例如紫微在辰宮、文曲在申宮、左輔在辰宮、天魁在子宮、天鉞在申宮，此盤的紫微會入六吉星中的四顆，就形成「君臣慶會」格。
 */
fun fun君臣慶會() = { it: IPlate ->
  val 紫微地支: Branch = it.starMap[紫微]?.stemBranch?.branch!!
  //val 命宮有紫微: Boolean = it.mainHouse.branch == it.starMap[紫微]?.stemBranch?.branch

  val goods: Set<GoodCombination>? = mutableSetOf<GoodCombination>().apply {
    // 必備條件
    if (it.輔弼().intersect(it.三方(紫微地支)).isNotEmpty()) {
      add(GoodCombination.輔弼)
    }
    if (it.昌曲().intersect(it.三方(紫微地支)).isNotEmpty()) {
      add(GoodCombination.昌曲)
    }
    if (it.魁鉞().intersect(it.三方(紫微地支)).isNotEmpty()) {
      add(GoodCombination.魁鉞)
    }

  }.takeIf { it.size == 3 }?.apply {
    // 附加條件
    if (it.三方四正有祿權科星(紫微地支)) {
      add(GoodCombination.祿權科星)
    }
  }?.toSet()


  if (goods != null && goods.size >= 3) {
    君臣慶會(it.getHouseDataOf(紫微地支).house, goods)
  } else {
    null
  }
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
  }?.let {
    if (it)
      日月同宮
    else
      null
  }
}

/**
 * 日月位於三方四正中，且太陽在巳、太陰在酉或太陽在辰、太陰在戌。為本格。
 * 命宮在丑，太陽在巳，太陰在酉，巳酉丑三合， 謂之日月雙明格，主其人財官皆美，金榜高中。
 *
 * 第一種情況是天梁在丑宮坐守命宮，太陽、太陰分別在巳（官祿宮）、酉（財帛宮）會照命宮，且兩顆主星皆處于廟旺之地，稱為“日月并明”
 * 第二種是命宮在午宮無主星，官祿宮在寅由巨門、太陽坐守，財帛在子有天同、太陰入駐，日月皆處廟旺之地同時會照命宮，而且有祿存、科權祿、左右、昌曲、魁鉞加會，即為“日月并明”格。
 */
fun fun日月並明() = { it: IPlate ->
  if (
    it.mainHouse.branch == 丑
    && it.starMap[太陽]?.stemBranch?.branch == 巳
    && it.starMap[太陰]?.stemBranch?.branch == 酉
  )
    日月並明
  else
    null
}


/**
 * (日出扶桑)
 * 太陽入命宮在卯時，白天出生者，謂之日照雷門格，主其人榮華富貴。
 *
 * 在紫微斗數中，日照雷門格（又叫日出扶桑格）也是一個非常有名的富貴格局，此格局的特點是紫微命宮在卯，
 * TODO 其中有太陽、天梁坐守；財帛宮在亥，由太陰坐守；官祿宮在未，無主星入駐。另外需有吉星會照，如魁鉞、祿存、化祿、左輔右弼等, 如見空劫、煞忌星則破格。
 */
fun fun日照雷門() = { it: IPlate ->
  if (
    it.mainHouse.branch == 卯
    && it.dayNight == DayNight.DAY
    && it.starMap[太陽]?.stemBranch?.branch == 卯
  )
    日照雷門
  else
    null
}


/**
 * 本宮在未宮，無主星坐命，且太陽在卯宮、太陰在亥宮。此時日月於三方四正中照命。
 *
 * 命宮在未，太陽在卯，太陰在亥，亥卯未三合，謂之明珠出海格，主其人財官皆美，科第榮恩。
 */
fun fun明珠出海() = { it: IPlate ->
  if (
    it.mainHouse.branch == 未
    && it.starMap[太陽]?.stemBranch?.branch == 卯
    && it.starMap[太陰]?.stemBranch?.branch == 亥
  )
    明珠出海
  else
    null
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
  }?.let {
    if (it)
      巨日同宮
    else
      null
  }
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
  }?.let {
    if (it)
      貪武同行
    else
      null
  }
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
  }?.let {
    if (it)
      將星得地
    else
      null
  }
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
  }?.let {
    if (it)
      七殺廟斗
    else
      null
  }
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
  }?.let {
    if (it)
      雄宿朝垣
    else
      null
  }
}


/**
 * 命宮在子，太陽與化碌在午，謂之對面朝天格，主其人文章蓋世、超群。
 */
fun fun對面朝天() = { it: IPlate ->

  val 命宮在子 = (it.mainHouse.branch == 子)
  val 太陽在午 = (it.starMap[太陽]?.stemBranch?.branch == 午)
  val 化祿在午 = (it.getTransFourHouseOf(祿)).stemBranch.branch == 午

  if (命宮在子 && 太陽在午 && 化祿在午)
    對面朝天
  else
    null
}

/**
 * 化科入命宮，化祿入遷移宮時，謂之科名會祿格，主其人威權出眾，大官貴。
 *
 * 化科在命宮，化祿在三方四正會照。
 *
 * TODO 是指命宮中有化科星坐守，三方四正有化祿或祿存星會照。紫微命盤中有此格局的人才華卓越，有名望，遠近皆榮顯，風云際會，開展運程，財名皆足。步入社會發展，可獲擢昇。多主大限行吉之地平步青云，惟財官二宮亦并有魁鉞守照方作此斷。加煞忌、辛勞更甚、以破格論。
 */
fun fun科名會祿() = { it: IPlate ->

  val 化祿入遷移宮 = it.getTransFourHouseOf(祿).house == House.遷移

  if ((it.化科入命宮() && 化祿入遷移宮) || (it.化科入命宮() && it.三方四正有祿存()))
    科名會祿
  else
    null
}

/**
 * 化科入命宮，化權入遷移宮時，謂之科權逢迎格，主其人科甲及第，金榜高中。
 */
fun fun科權逢迎() = { it: IPlate ->
  val 化權入遷移宮 = it.getTransFourHouseOf(權).house == House.遷移

  if (it.化科入命宮() && 化權入遷移宮)
    科權逢迎
  else
    null
}

/**
 * 祿存、化碌入命宮時，謂之祿合鴛鴦格，主其人一生富貴無窮。
 * NOTE : 祿存 沒有化祿的機會。 因此，應該判讀為： 祿存 在命宮，且，命宮有星 化祿
 *
 * 祿存、化祿同宮守命；或者兩星一個在命宮，一個在遷移，相互守照，命宮三方四正有吉星會照，無煞忌星衝破。
 *
 * 雙祿交流格 : TODO 祿存和化祿俱在三方四正中。有財源，在事業上有成富的機運。又稱為祿合鴛鴦格。
 */
fun fun祿合鴛鴦() = { it: IPlate ->

  val 化祿入對宮 = it.getTransFourHouseOf(權).stemBranch.branch == it.mainHouse.branch.next(6)
  val 祿存入命宮 = (it.mainHouse.branch == it.starMap[祿存]?.stemBranch?.branch)
  val 祿存在對宮 = (it.mainHouse.branch.next(6) == it.starMap[祿存]?.stemBranch?.branch)

  if (
    (it.化祿入命宮() && 祿存入命宮) || ((祿存入命宮 && 化祿入對宮) || (祿存在對宮 && it.化祿入命宮()))
  ) 祿合鴛鴦
  else
    null
}

/**
 * (嚴格)
 * 化碌入財帛宮，祿存入官祿宮，三合命宮，謂之雙祿朝垣格，主其人終身富貴無窮。
 *
 * (比較寬鬆)
 * 命宮無祿星坐守，但祿存與化祿分別位于命宮的三合宮，一守官祿一守財帛，是為最佳結構。
 *
 * (更寬鬆 , 考量三方四正) ==> 用此法
 * 所謂雙祿朝垣格，是指命宮有吉星坐守，命宮三方四正有祿存和化祿加會，無煞忌星沖照，即為此格。
 */
fun fun雙祿朝垣() = { it: IPlate ->
  val 化祿宮位 = it.getTransFourHouseOf(祿).stemBranch.branch
  val 祿存宮位 = it.starMap[祿存]?.stemBranch?.branch

  if (
    it.三方四正().containsAll(setOf(化祿宮位, 祿存宮位))
  )
    雙祿朝垣
  else
    null
}


/**
 * 化權、化碌、化科在命宮的三方四正，謂之三奇加會格，主其文才蓋世，出將入相。
 * (一定是：命宮、官祿、財帛)
 */
fun fun三奇加會() = { it: IPlate ->
  val good3: Set<ITransFour.Value> = setOf(祿, 權, 科)
  it.mainHouse.branch.trinities.all { branch ->
    val a: Set<ITransFour.Value?> = it.getHouseDataOf(branch).stars.map { star ->
      it.tranFours[star]?.get(FlowType.本命)
    }.toSet()
    a.any { good3.contains(it) }
  }.let {
    if (it)
      三奇加會
    else
      null
  }
}


/**
 * 命宮或三方有祿存、天馬或為化祿、天馬。主奔波勞碌而招財。
 *
 * 目前用此法 =>
 * 祿馬交馳最好（標準）的組合是祿和馬應分處對宮，祿存或化祿在對宮，天馬在本宮，這點是必須清晰認識的。
 * 因為祿馬交馳的本義是本宮的天馬把對宮的祿運輸到本宮，
 * 例如天馬在命，而祿在遷移，因為馬是來來回回的，所以這例子中命宮最能享祿馬交馳的好處（因為天馬在命宮），當然遷移宮也能受益。
 *
 * TODO 天馬、化碌、祿存在命宮或財帛宮的三方四正，謂之祿馬交馳格，主其人富貴、榮顯
 * 若祿跟馬同在本宮或祿馬同在對宮，祿馬交馳的力量就會大減。另外，若天馬同宮有截空，空亡，當然亦是減力的，祿馬交馳也不喜歡疊祿。
 */
fun fun祿馬交馳() = { it: IPlate ->
  val 天馬在命: Boolean = it.mainHouse.branch == it.starMap[天馬]?.stemBranch?.branch
  val 天馬在遷: Boolean = it.mainHouse.branch.opposite == it.starMap[天馬]?.stemBranch?.branch

  val 祿存在命 = it.mainHouse.branch == it.starMap[祿存]?.stemBranch?.branch
  val 祿存在遷 = it.mainHouse.branch.opposite == it.starMap[祿存]?.stemBranch?.branch

  val 化祿入遷移: Boolean = it.getHouseDataOf(it.mainHouse.branch.opposite).stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(祿)

  if (
    天馬在命 && (祿存在遷 || 化祿入遷移)
    ||
    天馬在遷 && (祿存在命 || it.化祿入命宮())
  )
    祿馬交馳
  else
    null
}

/**
 * 太陰在亥宮守命，為本格。
 *
 * TODO 太陰入命在亥宮時，夜晚出生者，謂之月朗天門格，主其人出相入將，非貴則富。
 */
fun fun月朗天門() = { it: IPlate ->
  if (
    it.mainHouse.branch == 亥
    && it.starMap[太陰]?.stemBranch?.branch == 亥
  )
    月朗天門(it.dayNight)
  else
    null
}

/**
 * 太陰、天同星在子宮坐命。
 *
 * 日月滄海格：太陰入命宮在子時，夜晚出生者，謂之日月滄海格，主其人富貴、清高、忠良。
 */
fun fun月生滄海() = { it: IPlate ->
  if (
    it.mainHouse.branch == 子
    && it.starMap[太陰]?.stemBranch?.branch == 子
    && it.starMap[天同]?.stemBranch?.branch == 子
  )
    月生滄海(it.dayNight)
  else
    null
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
  }?.let {
    if (it)
      石中隱玉
    else
      null
  }
}

/**
 * 天梁守命，入午宮。
 *
 * 天梁入命宮在午時， TODO 逢諸吉不逢惡曜，謂之壽星入廟格，主其人官貴清高，福壽綿長。
 */
fun fun壽星入廟() = { it: IPlate ->
  if (
    it.mainHouse.branch == 午
    && it.starMap[天梁]?.stemBranch?.branch == 午
  )
    壽星入廟
  else
    null
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
  }?.let {
    if (it)
      英星入廟
    else
      null
  }
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
  }?.let {
    if (it)
      機梁加會
    else
      null
  }
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
    setOf(branch).containsAll(it.昌曲())
  }?.let {
    if (it)
      文桂文華
    else
      null
  }
}

/**
 * 天魁、天鉞入命宮，或夾命宮，或三合命宮，謂之魁鉞拱命格，主其人文章蓋世，高官厚祿，逢凶化吉，大富大貴。
 */
fun fun魁鉞拱命() = { it: IPlate ->

  val 魁鉞入命: Boolean = setOf(it.mainHouse.branch).containsAll(it.魁鉞())

  val 魁鉞夾命: Boolean = it.魁鉞() == it.neighbors()

  val 三合命宮: Boolean = it.魁鉞().containsAll(it.拱())

  if (魁鉞入命 || 魁鉞夾命 || 三合命宮)
    魁鉞拱命
  else
    null
}

/**
 * 命宮在寅或申宮，遇紫微與天府來夾。
 */
fun fun紫府夾命() = { it: IPlate ->
  val 紫府有夾命 = it.紫府().containsAll(it.neighbors())
  val 命在寅或申 = it.mainHouse.branch.let { (it == 寅 || it == 申) }

  if (紫府有夾命 && 命在寅或申) {
    紫府夾命
  } else
    null
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
    setOf(branch).containsAll(it.輔弼())
  }?.let {
    if (it)
      左右同宮
    else
      null
  }
}

/**
 * A : 三方四正會太陽、太陰，且日月均在廟旺之宮位。
 *
 * 當太陽、太陰在廟旺之地會照命宮時，稱之為丹墀桂墀格，又稱為 [fun日月並明]。這個格局因為太陽、太陰皆處廟旺之地，能量充足，主星得力，因此主富貴。
 *
 * 當太陽入廟，處于旺位時便稱其為“丹墀”；
 * 當太陰入廟，處于旺位時稱之為“桂墀”。
 *
 *
 * B : 而中有當太陽、太陰一在辰宮，一在戌宮守命對照時，才稱之為丹墀桂墀格。
 * 這個格局因為太陽、太陰皆處廟旺之地，能量充足，主星得力，因此主富貴。
 */
fun fun丹墀桂墀() = { it: IPlate ->
  val 日月: Set<Branch?> = listOf(太陽, 太陰).map { star -> it.starMap[star]?.stemBranch?.branch }.toSet()
  val 三方四正有日月: Boolean = it.三方四正().containsAll(日月)

  val 日旺廟 = it.starStrengthMap[太陽]?.let { it <= 2 } ?: false
  val 月旺廟 = it.starStrengthMap[太陰]?.let { it <= 2 } ?: false

  // B
  val 日月在辰戌 = setOf(辰, 戌) == 日月

  if (日旺廟 && 月旺廟 && (三方四正有日月 || 日月在辰戌))
    丹墀桂墀
  else
    null
}

/**
 * 化科在命宮，化權在三方朝是。
 * 此格聰明過人，必考入高等學府，且主其人文章冠世，或在學術、科技上有創新和發明。又宜從任管理之職，或在政治上作投機。
 */
fun fun甲第登庸() = { it: IPlate ->

  it.mainHouse.branch.let { branch ->
    setOf(branch.previous, branch.next)
  }

  val 三合有化權 = it.拱().any { branch ->
    it.getHouseDataOf(branch).stars.map { star -> it.tranFours[star]?.get(FlowType.本命) }.contains(權)
  }

  if (it.化科入命宮() && 三合有化權)
    甲第登庸
  else
    null
}

/**
 * 是一個好壞參半的格局
 * 巨門在辰宮坐命，辛年生人；天同在戌坐命，丁年生人，即為此格。
 */
fun fun化星返貴() = { it: IPlate ->
  val 巨門在辰坐命 = it.mainHouse.branch.let {
    if (it == 辰)
      it
    else
      null
  }?.let { branch ->
    it.starMap[巨門]?.stemBranch?.branch == branch
  } ?: false

  val 天同在戌坐命 = it.mainHouse.branch.let {
    if (it == 戌)
      it
    else
      null
  }?.let { branch ->
    it.starMap[天同]?.stemBranch?.branch == branch
  } ?: false

  if (
    (巨門在辰坐命 && it.year.stem == Stem.辛)
    || (天同在戌坐命 && it.year.stem == Stem.丁)
  )
    化星返貴
  else
    null
}


/**
 * 天魁、天鉞落入命盤三方四正，有學識，能取得高學歷，為人端莊，一生多助人，亦多得眾人相助，尤其能逢兇化吉，遇難呈祥，格局略低於坐貴向貴格。
 *
 * 在紫微斗數中，天魁星就是天乙貴人，另外一個是玉堂貴人就是天鉞星，天鉞星和天魁星是一對，因此這個格局也叫做「坐貴向貴」
 */
fun fun天乙拱命() = { it: IPlate ->
  if (it.三方四正().containsAll(it.魁鉞()))
    天乙拱命
  else
    null
}

/**
 * 廉貞坐命，官祿宮為武曲來會，三方四正再會文昌或文曲。主個人可獲功績名望。
 *
 * (比較嚴格, 綁定命宮為寅、申)
 * 所謂廉貞文武格是指廉貞星在寅、申宮坐守命宮，三方四正有武曲、文昌、文曲星拱照，即為此格。
 * 古人對這個格局的評價非常高，有「命中文武喜朝垣，入廟平生福氣全，純粹文能高折桂，戰征武定鎮三邊」之說。也就是說紫微命盤中有此格局的人文武雙全，很適合做大將軍。
 */
fun fun廉貞文武() = { it: IPlate ->
  val 廉貞坐命 = it.starMap[廉貞]?.stemBranch?.branch == it.mainHouse.branch
  val 武曲官祿 = it.starMap[武曲]?.house == House.官祿

  val 三方四正有昌或曲 = it.昌曲().intersect(it.三方四正()).isNotEmpty()

  if (廉貞坐命 && 武曲官祿 && 三方四正有昌或曲)
    廉貞文武
  else
    null
}

/**
 * 下列任何一顆星入其正位，
 * 武曲正位在財帛宮、
 * 廉貞正位在官祿宮、
 * 天同正位在福德宮、
 * 太陰正位在田宅宮、
 * 天梁正位在父母宮。
 */
fun fun星臨正位(): (IPlate) -> Pattern? = { it: IPlate ->

  val matched = mutableSetOf<ZStar>().apply {
    if (it.starMap[武曲]?.house == 財帛)
      add(武曲)
    if (it.starMap[廉貞]?.house == 財帛)
      add(廉貞)
    if (it.starMap[天同]?.house == 福德)
      add(天同)
    if (it.starMap[太陰]?.house == 田宅)
      add(太陰)
    if (it.starMap[天梁]?.house == 父母)
      add(天梁)
  }.toSet()

  if (matched.isNotEmpty())
    星臨正位(matched)
  else
    null
}


// =========================== 以下 , 惡格 ===========================

/**
 * 擎羊坐命午宮，為本格。
 *
 * TODO 擎羊與貪狼，或擎羊與天同入命宮在午時，謂之馬頭帶箭格，主其人威震邊疆，沙場馳名。
 */
fun fun馬頭帶劍() = { it: IPlate ->
  if (
    it.mainHouse.branch == 午
    && it.starMap[擎羊]?.stemBranch?.branch == 午
  )
    馬頭帶劍
  else
    null
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
  }?.let {
    if (it)
      極居卯酉
    else
      null
  }
}

/**
 * 命宮裡無任何十四顆主星坐命。
 */
fun fun命無正曜() = { it: IPlate ->
  StarMain.values.map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet().contains(it.mainHouse.branch).let {
    if (!it)
      命無正曜
    else
      null
  }
}

/**
 * 擎羊、陀羅於左右鄰宮夾命。
 */
fun fun羊陀夾命() = { it: IPlate ->
  val branches: Set<Branch?> = listOf(擎羊, 陀羅).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  if (branches.containsAll(it.neighbors()))
    羊陀夾命
  else
    null
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

  if (branches.containsAll(it.neighbors()))
    火鈴夾命
  else
    null
}

/**
 * 在寅宮，貪狼坐命，遇陀羅同宮。
 */
fun fun風流綵杖() = { it: IPlate ->
  if (
    it.mainHouse.branch == 寅
    && it.starMap[貪狼]?.stemBranch?.branch == 寅
    && it.starMap[陀羅]?.stemBranch?.branch == 寅
  )
    風流綵杖
  else
    null
}

/**
 * 巨門、天機同在酉宮坐命，有化忌同宮。性質為奔波飄蕩。不利於感情、 事業。
 */
fun fun巨機化酉() = { it: IPlate ->
  val 化忌入命宮 = it.getTransFourHouseOf(忌).stemBranch.branch == it.mainHouse.branch

  if (
    化忌入命宮
    && it.mainHouse.branch == 酉
    && it.starMap[巨門]?.stemBranch?.branch == 酉
    && it.starMap[天機]?.stemBranch?.branch == 酉
  )
    巨機化酉
  else
    null
}

/**
 * 太陽在戌宮坐命，此時太陰在辰宮；或太陰在辰宮坐命，太陽在戌宮。
 * 取其日在戌時，月在辰時， 兩星光芒皆弱不旺。勞碌命，求人不如勞己。無閒享清福。
 */
fun fun日月反背() = { it: IPlate ->
  val value1 = (it.mainHouse.branch == 戌 && it.starMap[太陽]?.stemBranch?.branch == 戌)
  val value2 = (it.mainHouse.branch == 辰 && it.starMap[太陰]?.stemBranch?.branch == 辰)
  if (value1 || value2)
    日月反背
  else
    null
}

/**
 * 天梁在巳亥寅申宮坐命，與天馬同宮。
 * 天馬只會出現於四馬地(巳亥寅申。 此格表示勞而無獲之象。若顯現在感情生活上，對婚姻生活帶來不利影響。
 */
fun fun梁馬飄蕩() = { it: IPlate ->
  it.mainHouse.branch.let { branch ->
    if (listOf(寅, 巳, 申, 亥).contains(branch))
      branch
    else
      null
  }?.let { branch ->
    it.starMap[天梁]?.stemBranch?.branch == branch
      && it.starMap[天馬]?.stemBranch?.branch == branch
  }?.let {
    if (it)
      梁馬飄蕩
    else
      null
  }
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
  }?.let {
    if (it)
      貞殺同宮
    else
      null
  }
}

/**
 * 「殺拱廉貞」──即廉貞守命，七殺合照 。古歌雲：「貞逢七殺實堪傷，十載淹留有災殃，運至經求多不遂，錢財勝似雪澆湯。」
 *
 * 這個格局非常之有名，但照王亭之所知，其格局結構僅有兩種，即
 *
 * 廉貞貪狼于巳宮守命。武曲七殺于酉宮來會；
 * 或
 * 廉貞貪狼于亥宮守命，武曲七殺于卯宮來會。亦即是廉貞、貪狼、武曲、七殺四顆星曜的星系組合。
 *
 * 另一說 :
 * ①廉貞、七殺二星分守身宮和命宮；
 * ②廉貞七殺在丑未宮守命； ==> 此即 [fun貞殺同宮]
 * ③七殺在卯酉守命，三合有廉貞。
 * 以上諸等，命宮三方四正無吉星加會，反而加會羊陀火鈴天刑化忌劫空等諸多惡曜，便爲殺拱廉貞之格。
 */
fun fun殺拱廉貞() = { it: IPlate ->
  val 廉貞貪狼在巳宮守命: Boolean = it.mainHouse.branch.let { branch ->
    branch == 巳
      && setOf(branch).containsAll(listOf(廉貞, 貪狼).map { star ->
      it.starMap[star]?.stemBranch?.branch
    })
  }

  val 武曲七殺于酉宮來會: Boolean = setOf(酉).containsAll(listOf(武曲, 七殺).map { star ->
    it.starMap[star]?.stemBranch?.branch
  })

  val 廉貞貪狼在亥宮守命: Boolean = it.mainHouse.branch.let { branch ->
    branch == 亥
      && setOf(branch).containsAll(listOf(廉貞, 貪狼).map { star ->
      it.starMap[star]?.stemBranch?.branch
    })
  }

  val 武曲七殺于卯宮來會: Boolean = setOf(卯).containsAll(listOf(武曲, 七殺).map { star ->
    it.starMap[star]?.stemBranch?.branch
  })

  if (
    (廉貞貪狼在巳宮守命 && 武曲七殺于酉宮來會) || (廉貞貪狼在亥宮守命 && 武曲七殺于卯宮來會)
  )
    殺拱廉貞
  else
    null
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
  }?.let {
    if (it)
      刑囚夾印
    else
      null
  }
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
  }?.let {
    if (it)
      巨逢四煞
    else
      null
  }
}

/**
 * 地劫、地空二星或其中之一星守命。
 * 有精神上孤獨，錢不易留住之跡象。
 */
fun fun命裡逢空() = { it: IPlate ->
  if (
    it.劫空().contains(it.mainHouse.branch)
  )
    命裡逢空
  else
    null

}

/**
 * 地劫、地空二星在左右鄰宮夾命。
 * 有精神上孤獨，錢不易留住之跡象。
 */
fun fun空劫夾命() = { it: IPlate ->
  if (
    it.劫空().containsAll(it.neighbors())
  )
    空劫夾命
  else
    null
}

/**
 * 文昌或文曲守命，遇空劫 或火鈴或羊陀對星來夾。有懷才不遇跡象。
 */
fun fun文星遇夾() = { it: IPlate ->

  val 命宮有文星: Boolean = it.昌曲().contains(it.mainHouse.branch)

  val evils = mutableSetOf<EvilCombination>().takeIf { 命宮有文星 }?.apply {
    fun空劫夾命().invoke(it)?.also { add(EvilCombination.空劫) }
    fun火鈴夾命().invoke(it)?.also { add(EvilCombination.火鈴) }
    fun羊陀夾命().invoke(it)?.also { add(EvilCombination.羊陀) }
  }?.toSet()

  if (命宮有文星 && evils != null && evils.isNotEmpty())
    文星遇夾(evils)
  else
    null
}

/**
 * 化忌坐命，擎羊、陀羅於兩鄰宮相夾。
 *
 * 祿存在命宮，則必為羊陀所夾。若有化忌星同宮，羊陀凶性得以充分發揮。雖有祿存守命，亦不為美。
 */
fun fun羊陀夾忌() = { it: IPlate ->
  val 羊陀夾命: Boolean = fun羊陀夾命().invoke(it) != null
  val 化忌入命宮 = it.getTransFourHouseOf(忌).stemBranch.branch == it.mainHouse.branch

  if (羊陀夾命 && 化忌入命宮)
    羊陀夾忌
  else
    null
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
        it.getTransFourHouseOf(忌).stemBranch.branch == branch
      }

    val 另宮: Branch? = 鄰宮化忌?.let { branch -> neighbors.minus(branch).first() }
    另宮?.let { branch ->
      val 另宮有天梁: Boolean = it.starMap[天梁]?.stemBranch?.branch == branch
      val 另宮有擎羊: Boolean = it.starMap[擎羊]?.stemBranch?.branch == branch
      另宮有天梁 || 另宮有擎羊
    } ?: false
  }?.let {
    if (it)
      刑忌夾印
    else
      null
  }
}

/**
 * 天馬遇地劫、地空同宮或三方沖照。奔波，空忙一場。
 *
 * 命馬遇空亡、截路、天空、四煞、化忌同宮或三方沖照，主奔波 ，白忙白勞。賦云：「馬遇空亡，終身奔走。」又云： 「天馬逢空亡死絕，災病悔吝。」
 * 命宮天馬，與劫空同宮，對宮有祿存，或祿坐空亡，又逢空劫煞星。生逢此格，一生總是變來變去，東奔西走，辛苦勞碌一場空，爲空虛之兆，貧賤之命。經云：“馬遇空亡，終身奔走”。
 *
 * 空亡用日空
 */
fun fun馬落空亡() = { it: IPlate ->
  val 命宮天馬: Boolean = it.mainHouse.branch == it.starMap[天馬]?.stemBranch?.branch

  val 空劫入命: Boolean = setOf(it.mainHouse.branch).containsAll(it.劫空())

  val 對宮祿存: Boolean = it.mainHouse.branch.opposite == it.starMap[祿存]?.stemBranch?.branch

  if (命宮天馬 && 空劫入命 && 對宮祿存)
    馬落空亡
  else
    null
}


/**
 * 祿存、化祿同時坐命，遇地劫、地空同宮。
 * 此即 [fun祿合鴛鴦] 格
 *
 * 祿存、化祿同時坐命，本為 雙祿交流格。但若遇地空、地劫同宮，此時雙祿為被衝破情形，稱為兩重華蓋。
 * 華蓋表示有宗教緣分。皈依宗教，反可享主清福。但因雙祿被衝破，較不易累積錢財。
 */
fun fun兩重華蓋() = { it: IPlate ->
  val 祿合鴛鴦 = fun祿合鴛鴦().invoke(it) != null

  val 空劫入命: Boolean = setOf(it.mainHouse.branch).containsAll(it.劫空())

  if (祿合鴛鴦 && 空劫入命)
    兩重華蓋
  else
    null
}


/**
 * 祿存或化祿坐命，在三方四正中，有被地劫、地空衝破。
 * 吉處藏凶之象，應居安思危。
 */
fun fun祿逢衝破() = { it: IPlate ->
  val 化祿入命宮: Boolean = it.houseMap[House.命宮]!!.stars.map { star ->
    it.tranFours[star]?.get(FlowType.本命)
  }.contains(祿)

  val 祿存坐命: Boolean = it.starMap[祿存]?.stemBranch?.branch == it.mainHouse.branch

  if (
    (化祿入命宮 || 祿存坐命) && it.三方四正().containsAll(it.劫空())
  )
    祿逢衝破
  else
    null
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

  if (
    貪狼坐命在子宮 || (貪狼坐命在亥宮 && 廉貞在亥 && 陀羅在亥)
  )
    泛水桃花
  else
    null
}

/**
 * 天梁在巳、亥、申爲陷地，
 * 太陰在卯、辰、巳、午、未爲陷地。
 * ①天梁在陷地守命與太陰加會；
 * ②太陰在陷地守命與天梁加會。
 * 以上二者，命宮及三方無祿存、科權祿、左右、昌曲、魁鉞等吉星同宮和加會，
 * 反而會有羊陀火鈴劫空刑姚化忌大耗等凶星，爲天梁拱月格。
 *
 * 人命逢此，窮困而事業無成，不聚財，飄流在外，不務正業，成事不足，敗事有餘。男命浪蕩，好酒色嫖賭，女命多淫，私通內亂，故古詩云：
 *
 * 王亭之的意見。所謂「天梁拱月」，乃是天梁居巳亥，太陰居丑未，或大陰居寅申，天梁居子午的格局
 */
fun fun天梁拱月() = { it: IPlate ->
  val 天梁陷地守命: Boolean = it.mainHouse.branch.let { branch ->
    if (listOf(巳, 亥, 申).contains(branch))
      branch
    else
      null
  }?.let { branch ->
    it.starMap[天梁]?.stemBranch?.branch == branch
  } ?: false

  val 太陰陷地: Boolean = it.starMap[太陰]?.stemBranch?.branch?.let { branch ->
    listOf(卯, 辰, 巳, 午).contains(branch)
  } ?: false

  if (天梁陷地守命 && 太陰陷地)
    天梁拱月
  else
    null
}


/**
 * 武曲(金)、廉貞(火)分別守身宮與命宮，金火相剋！
 * 如周武王和奸臣費仲，仇人相見，分外眼紅，必兇禍百出，終身不得安寧。
 *
 * 武曲為財星屬金，廉貞為囚星屬火。二星一守命宮，一守身宮，乃火金相克，如仇人相見，分外眼紅，必兇禍百出，終身不得安寧，
 * 二星有一化忌加煞，定遭暴病、險厄。
 * 若是命宮三方四正臨廟旺，加會星并得吉化，則不作此論。
 */
fun fun財與囚仇() = { it: IPlate ->
  val 武曲廉貞: Set<Branch?> = listOf(武曲, 廉貞).map { star ->
    it.starMap[star]?.stemBranch?.branch
  }.toSet()

  val 命身: Set<Branch> = setOf(it.mainHouse.branch, it.bodyHouse.branch)

  if (武曲廉貞 == 命身)
    財與囚仇
  else
    null
}


/**
 * 廉貞(火)在亥逢化忌(水)，是火入泉鄉，主大凶。
 */
fun fun火入泉鄉() = { it: IPlate ->
  if (it.starMap[廉貞]?.stemBranch?.branch == 亥)
    火入泉鄉
  else
    null
}


enum class PatternType {
  GOOD, EVIL
}


/**
 * 參考資料
 * https://goo.gl/hDUun2 ( http://vioheart.pixnet.net )
 * http://www.ai5429.com/c/clock108/
 */
sealed class Pattern(val name: String, val type: PatternType, val notes: String? = null) {
  object 極向離明 : Pattern("極向離明", GOOD)
  object 紫府同宮 : Pattern("紫府同宮", GOOD)
  class 紫府朝垣(goods: Set<GoodCombination>) : Pattern("紫府朝垣", GOOD, goods.joinToString(","))
  class 天府朝垣(goods: Set<GoodCombination>) : Pattern("天府朝垣", GOOD, goods.joinToString(","))
  class 府相朝垣(goods: Set<GoodCombination>) : Pattern("府相朝垣", GOOD, goods.joinToString(","))
  object 巨機同宮 : Pattern("巨機同宮", GOOD)
  object 善蔭朝綱 : Pattern("善蔭朝綱", GOOD)
  object 機月同梁 : Pattern("機月同梁", GOOD)
  object 日月照壁 : Pattern("日月照壁", GOOD)
  class 日麗中天(dayNight: DayNight) : Pattern("日麗中天", GOOD, dayNight.toString())
  object 日月夾命 : Pattern("日月夾命", GOOD)
  class 君臣慶會(house: House, goods: Set<GoodCombination>) :
    Pattern("君臣慶會", GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  object 日月同宮 : Pattern("日月同宮", GOOD)
  object 日月並明 : Pattern("日月並明", GOOD)
  object 日照雷門 : Pattern("日照雷門", GOOD)
  object 明珠出海 : Pattern("明珠出海", GOOD)
  object 巨日同宮 : Pattern("巨日同宮", GOOD)
  object 貪武同行 : Pattern("貪武同行", GOOD)
  object 將星得地 : Pattern("將星得地", GOOD)
  object 七殺廟斗 : Pattern("七殺廟斗", GOOD)
  object 雄宿朝垣 : Pattern("雄宿朝垣", GOOD)
  object 對面朝天 : Pattern("對面朝天", GOOD)
  object 科名會祿 : Pattern("科名會祿", GOOD)
  object 科權逢迎 : Pattern("科權逢迎", GOOD)
  object 祿合鴛鴦 : Pattern("祿合鴛鴦", GOOD)
  object 雙祿朝垣 : Pattern("雙祿朝垣", GOOD)
  object 三奇加會 : Pattern("三奇加會", GOOD)
  object 祿馬交馳 : Pattern("祿馬交馳", GOOD)
  class 月朗天門(dayNight: DayNight) : Pattern("月朗天門", GOOD, dayNight.toString())
  class 月生滄海(dayNight: DayNight) : Pattern("月生滄海", GOOD, dayNight.toString())
  object 石中隱玉 : Pattern("石中隱玉", GOOD)
  object 壽星入廟 : Pattern("壽星入廟", GOOD)
  object 英星入廟 : Pattern("英星入廟", GOOD)
  object 機梁加會 : Pattern("機梁加會", GOOD)
  object 文桂文華 : Pattern("文桂文華", GOOD)
  object 魁鉞拱命 : Pattern("魁鉞拱命", GOOD)
  object 紫府夾命 : Pattern("紫府夾命", GOOD)
  object 左右同宮 : Pattern("左右同宮", GOOD)
  object 丹墀桂墀 : Pattern("丹墀桂墀", GOOD)
  object 甲第登庸 : Pattern("甲第登庸", GOOD)
  object 化星返貴 : Pattern("化星返貴", GOOD)
  object 天乙拱命 : Pattern("天乙拱命", GOOD)
  object 廉貞文武 : Pattern("廉貞文武", GOOD)
  class 星臨正位(stars: Set<ZStar>) : Pattern("星臨正位", GOOD, stars.joinToString(","))

  // =========================== 以下 , 惡格 ===========================

  object 馬頭帶劍 : Pattern("馬頭帶劍", EVIL)
  object 極居卯酉 : Pattern("極居卯酉", EVIL)
  object 命無正曜 : Pattern("命無正曜", EVIL)
  object 羊陀夾命 : Pattern("羊陀夾命", EVIL)
  object 火鈴夾命 : Pattern("火鈴夾命", EVIL)
  object 風流綵杖 : Pattern("風流綵杖", EVIL)
  object 巨機化酉 : Pattern("巨機化酉", EVIL)
  object 日月反背 : Pattern("日月反背", EVIL)
  object 梁馬飄蕩 : Pattern("梁馬飄蕩", EVIL)
  object 貞殺同宮 : Pattern("貞殺同宮", EVIL)
  object 殺拱廉貞 : Pattern("殺拱廉貞", EVIL)
  object 刑囚夾印 : Pattern("刑囚夾印", EVIL)
  object 巨逢四煞 : Pattern("巨逢四煞", EVIL)
  object 命裡逢空 : Pattern("命裡逢空", EVIL)
  object 空劫夾命 : Pattern("空劫夾命", EVIL)
  class 文星遇夾(evils: Set<EvilCombination>) : Pattern("文星遇夾", EVIL, evils.joinToString(","))

  object 羊陀夾忌 : Pattern("羊陀夾忌", EVIL)
  object 刑忌夾印 : Pattern("刑忌夾印", EVIL)
  object 馬落空亡 : Pattern("馬落空亡", EVIL)
  object 兩重華蓋 : Pattern("兩重華蓋", EVIL)
  object 祿逢衝破 : Pattern("祿逢衝破", EVIL)
  object 泛水桃花 : Pattern("泛水桃花", EVIL)
  object 天梁拱月 : Pattern("天梁拱月", EVIL)
  object 財與囚仇 : Pattern("財與囚仇", EVIL)
  object 火入泉鄉 : Pattern("火入泉鄉", EVIL)

  companion object {
    fun values(): List<(IPlate) -> Pattern?> {
      return listOf(
        fun極向離明(), fun紫府同宮(), fun紫府朝垣(), fun天府朝垣(), fun府相朝垣(), fun巨機同宮(), fun善蔭朝綱(), fun機月同梁(),
        fun日月照壁(), fun日麗中天(), fun日月夾命(), fun君臣慶會(), fun日月同宮(), fun日月並明(), fun日照雷門(), fun明珠出海(),
        fun巨日同宮(), fun貪武同行(), fun將星得地(), fun七殺廟斗(), fun雄宿朝垣(), fun對面朝天(), fun科名會祿(), fun科權逢迎(),
        fun祿合鴛鴦(), fun祿合鴛鴦(), fun雙祿朝垣(), fun三奇加會(), fun祿馬交馳(), fun月朗天門(), fun月生滄海(), fun石中隱玉(),
        fun壽星入廟(), fun英星入廟(), fun機梁加會(), fun文桂文華(), fun魁鉞拱命(), fun紫府夾命(), fun左右同宮(), fun丹墀桂墀(),
        fun甲第登庸(), fun化星返貴(), fun天乙拱命(), fun廉貞文武(), fun星臨正位(),

        fun馬頭帶劍(), fun極居卯酉(), fun命無正曜(), fun羊陀夾命(), fun火鈴夾命(), fun風流綵杖(), fun巨機化酉(), fun日月反背(),
        fun梁馬飄蕩(), fun貞殺同宮(), fun殺拱廉貞(), fun刑囚夾印(), fun巨逢四煞(), fun命裡逢空(), fun空劫夾命(), fun文星遇夾(),
        fun羊陀夾忌(), fun刑忌夾印(), fun馬落空亡(), fun兩重華蓋(), fun祿逢衝破(), fun泛水桃花(), fun天梁拱月(), fun財與囚仇(),
        fun火入泉鄉()
                   )
    }
  }
}