/**
 * Created by smallufo on 2022-07-13.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.Gender
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.eightwords.hazards.ChildHazard.*

private val 子午卯酉 = listOf(子, 午, 卯, 酉)
private val 寅申巳亥 = listOf(寅, 申, 巳, 亥)
private val 辰戌丑未 = listOf(辰, 戌, 丑, 未)

private val 寅卯辰 = listOf(寅, 卯, 辰)
private val 巳午未 = listOf(巳, 午, 未)
private val 申酉戌 = listOf(申, 酉, 戌)
private val 亥子丑 = listOf(亥, 子, 丑)

private val 巳酉丑 = listOf(巳, 酉, 丑)
private val 申子辰 = listOf(申, 子, 辰)
private val 亥卯未 = listOf(亥, 卯, 未)
private val 寅午戌 = listOf(寅, 午, 戌)

interface IHazardFactory {
  fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard?
}


/**
 * 寅申巳亥月：辰戌丑未時。
 * 子午卯酉月：寅申巳亥時。
 * 辰戌丑未月：子午卯酉時。
 * 俗忌百曰不出大門，房門不忌。夫百曰關者，專以十二生肖月忌各所內百犯之，童限月內百曰必有星辰難養。(象吉)
 *
 * 寅申巳亥月生辰戌丑未時是。
 * 子午卯酉月生寅申巳亥時是。
 * 辰戌丑未月生子午卯酉時是。
 * 俗忌百日不出門，止忌大門、房門不忌無妨。夫百日關者，專以十二生肖月忌各所值時犯之，童限月內百日必有星辰難養。又云：忌過週歲，又云：止忌百日、之外則無妨。(鰲頭)
 *
 * 寅申巳亥月忌辰戌丑未時。
 * 辰戌丑未月忌子午卯酉時，
 * 子午卯酉月忌寅申巳亥時。
 * 生孩百日內勿出大門前。(星平會海)
 *
 * 寅申巳亥月：辰戌醜未時。
 * 子午卯酉月：寅申巳亥時。
 * 辰戌丑未月：子午卯酉時。
 * 童限犯之初生百日內、勿出大門外。凡正月寅巳時生人，犯此百日內忌出入門前。(生育禮俗)
 *
 * 凡
 * 正、四、七、十月逢辰、戌、丑、未時，
 * 二、五、八、十一月逢寅、申、巳、亥時，
 * 三、六、九、十二月逢子、午、卯、酉時生人是。
 * 犯此百日內忌出入門前。(黃曆解祕)
 */
val p百日關 = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (寅申巳亥.contains(eightWords.month.branch) && 辰戌丑未.contains(eightWords.hour.branch)) ||
      (子午卯酉.contains(eightWords.month.branch) && 寅申巳亥.contains(eightWords.hour.branch)) ||
      (辰戌丑未.contains(eightWords.month.branch) && 子午卯酉.contains(eightWords.hour.branch))
    ) {
      百日關
    } else {
      null
    }
  }
}

/**
 * 甲乙馬頭龍不住，丙丁雞猴奔山崗  ，戊己逢藏蛇在草，庚辛遇虎於林下，壬癸丑亥時須忌，孩兒直此有煩惱。夫千曰關者，如甲乙生人午時是也，余仿此。犯之主有驚風、吐乳之災，忌住難星。（象吉）
 * 甲乙馬龍頭、丙丁猴雞山、庚辛虎林下、戊己蛇藏草、壬癸丑亥時。未滿千日勿往外媽厝，主驚風、吐乳，制化即安。凡午年寅巳亥時生人，犯上忌三歲上高落低之患。（生育禮俗）
 */
val p千日關A = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (listOf(甲, 乙).contains(eightWords.year.stem) && listOf(午).contains(eightWords.hour.branch)) ||
      (listOf(丙, 丁).contains(eightWords.year.stem) && listOf(酉, 申).contains(eightWords.hour.branch)) ||
      (listOf(戊, 己).contains(eightWords.year.stem) && listOf(巳).contains(eightWords.hour.branch)) ||
      (listOf(庚, 辛).contains(eightWords.year.stem) && listOf(寅).contains(eightWords.hour.branch)) ||
      (listOf(壬, 癸).contains(eightWords.year.stem) && listOf(丑, 亥).contains(eightWords.hour.branch))
    ) {
      千日關
    } else {
      null
    }
  }
}

/**
 * 甲乙馬頭龍不住、丙丁雞叫奔山崗、戊己逢蛇藏在草、庚辛遇虎竹林下、壬癸丑亥時須忌。孩兒值此有吁嗟。夫千日關者，且如甲乙生人忌午時是、餘仿此，犯之乃驚風、吐乳。（鰲頭）
 * 甲乙馬頭籠不住、丙丁雞叫奔山岡、戊己逢蛇在草藏、庚辛遇虎於林下、壬癸丑亥時須忌。孩兒值此有嗟。過千日之外則不妨。（星平會海）
 */
val p千日關B = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (listOf(甲, 乙).contains(eightWords.year.stem) && listOf(午).contains(eightWords.hour.branch)) ||
      (listOf(丙, 丁).contains(eightWords.year.stem) && listOf(酉).contains(eightWords.hour.branch)) ||
      (listOf(戊, 己).contains(eightWords.year.stem) && listOf(巳).contains(eightWords.hour.branch)) ||
      (listOf(庚, 辛).contains(eightWords.year.stem) && listOf(寅).contains(eightWords.hour.branch)) ||
      (listOf(壬, 癸).contains(eightWords.year.stem) && listOf(丑, 亥).contains(eightWords.hour.branch))
    ) {
      千日關
    } else {
      null
    }
  }
}

/**
 * 凡午年寅、申、巳、亥時生人，犯此忌三歲上高落低之患。另一說法；三歲之前不宜到外婆家，或莫至外婆供奉祖先牌位處。（黃曆解秘）
 */
val p千日關C = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (eightWords.year.branch == 午 && 寅申巳亥.contains(eightWords.hour.branch)) {
      千日關
    } else {
      null
    }
  }
}


/**
 * 春忌牛羊水上波，夏逢辰戌見閻羅，秋逢子午君須避，冬時生人虎兔時，
 * 甲乙丙丁申子辰，戊己庚生亥卯未，辛兼壬癸寅午戌，生孩切慮不成人。日主旺不防，弱則難養。（象吉）
 *
 * 春季牛羊水上波，夏逢辰戌見閻羅，秋遇子午君須避，冬季生人虎兔嗟。
 * 甲乙丙丁申子辰，戊己庚辛亥卯未，辛兼壬癸寅午戌，生孩切慮不成人。日主生人不旺，弱則難養。（鰲頭）
 *
 * 春忌牛羊水上波，夏逢辰戌見閻羅，秋逢子午當須避，冬季生人虎兔磨。日主旺無妨。（星平會海）
 *
 * 春忌牛羊水上波，
 * 夏逢辰戌見閻王，
 * 秋怕子午當迴避，
 * 冬季生人虎兔磨。
 * 勿看功果做佛事，日主弱宜制化。凡七、八、九、十二月子午寅卯時生人，犯此帶天德、月德可解。（生育禮俗）
 */
val p閻王關A = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (寅卯辰.contains(eightWords.month.branch) && listOf(丑, 未).contains(eightWords.hour.branch)) ||
      (巳午未.contains(eightWords.month.branch) && listOf(辰, 戌).contains(eightWords.hour.branch)) ||
      (申酉戌.contains(eightWords.month.branch) && listOf(子, 午).contains(eightWords.hour.branch)) ||
      (亥子丑.contains(eightWords.month.branch) && listOf(寅, 卯).contains(eightWords.hour.branch))
    ) {
      閻王關
    } else {
      null
    }
  }
}

/**
 * 凡七、八、九、十、十二月子、午、寅、卯時生人。
 * 另正、二、三月丑、未時，
 * 四、五、六月辰、戌時，
 * 七、八、九月子、午時，
 * 十、十一、十二月寅、卯時生人是，
 * 犯此小時應該避免看誦經作法或作功德場合，難養，帶天德、月德可解。（黃曆解秘）
 */
val p閻王關B = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {

    return if (
      (listOf(午, 未, 申, 酉, 亥).contains(eightWords.month.branch) && listOf(子, 午, 寅, 卯).contains(eightWords.hour.branch)) ||
      (寅卯辰.contains(eightWords.month.branch) && listOf(丑, 未).contains(eightWords.hour.branch)) ||
      (巳午未.contains(eightWords.month.branch) && listOf(辰, 戌).contains(eightWords.hour.branch)) ||
      (申酉戌.contains(eightWords.month.branch) && listOf(子, 午).contains(eightWords.hour.branch)) ||
      (亥子丑.contains(eightWords.month.branch) && listOf(寅, 卯).contains(eightWords.hour.branch))
    ) {
      閻王關
    } else {
      null
    }
  }
}

/**
 * 子丑寅生人、酉午未時真。
 * 卯辰巳生人、申戌亥為刑。
 * 午未申生人、莫犯丑寅卯。
 * 酉戌亥生人、子辰巳難乎。
 * 夫鬼門關者，以十二支生人、逢各所值時辰、論小兒時上、並童限逢之不可遠行。 (象吉)
 *
 * 子丑寅生人，巳午未時嗔，
 * 卯辰巳生人，申亥戌為刑，
 * 午未申生命，莫犯丑寅卯，
 * 酉戌亥生命，子巳辰時嗔。
 * 支生取時嗔，限忌遠行。（鰲頭）
 *
 */
val p鬼門關A = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (listOf(子, 丑, 寅).contains(eightWords.year.branch) && listOf(酉, 午, 未).contains(eightWords.hour.branch)) ||
      (listOf(卯, 辰, 巳).contains(eightWords.year.branch) && listOf(申, 戌, 亥).contains(eightWords.hour.branch)) ||
      (listOf(午, 未, 申).contains(eightWords.year.branch) && listOf(丑, 寅, 卯).contains(eightWords.hour.branch)) ||
      (listOf(酉, 戌, 亥).contains(eightWords.year.branch) && listOf(子, 辰, 巳).contains(eightWords.hour.branch))
    ) {
      鬼門關
    } else {
      null
    }
  }
}

/**
 * 子嫌酉上午嫌丑，
 * 寅未申卯不須安，
 * 亥怕辰宮戌怕巳，
 * 古賢立定鬼門關。
 * 人命生時莫犯此，
 * 六月炎天也遭寒。
 * 命限逢之、不可遠行。（星平會海）
 *
 * 子嫌酉年午嫌丑，
 * 寅未申卯不須安，
 * 亥怕辰兮戌怕巳，
 * 古賢立號鬼門關。
 * 不宜遠行、勿入陰廟宮寺。
 * 凡甲子丙子戊子生人，犯此忌夜出入門外。（生育禮俗）
 *
 *
 */
val p鬼門關B = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (listOf(
        Pair(子, 酉),
        Pair(午, 丑),
        Pair(亥, 辰),
        Pair(戌, 巳),
      ).contains(eightWords.year.branch to eightWords.hour.branch)
    ) {
      鬼門關
    } else {
      null
    }
  }
}

/**
 * 凡
 * 子年逢酉時生，
 * 丑年逢午時生，
 * 寅年逢未時生，
 * 卯年逢申時生，
 * 辰年逢亥時生，
 * 巳年逢戌時生，
 * 午年逢丑時生，
 * 未年逢寅時生，
 * 申年逢卯時生，
 * 酉年逢子時生，
 * 戌年逢巳時生，
 * 亥年逢辰時生是。
 * 一生不宜進陰廟、有應公、萬善祠、墳墓區及殯儀館等。宜燒地府錢給地府眾鬼神制化則吉。（黃曆解秘）
 */
val p鬼門關C = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (listOf(
        Pair(子, 酉),
        Pair(丑, 午),
        Pair(寅, 未),
        Pair(卯, 申),
        Pair(辰, 亥),
        Pair(巳, 戌),
        Pair(午, 丑),
        Pair(未, 寅),
        Pair(申, 卯),
        Pair(酉, 子),
        Pair(戌, 巳),
        Pair(亥, 辰),
      ).contains(eightWords.year.branch to eightWords.hour.branch)
    ) {
      鬼門關
    } else {
      null
    }
  }
}

/**
 * 甲己巳酉丑、孩兒難保守。庚辛亥卯未、父母哭斷腸。壬癸寅午戌、生下不見日。乙戊丙丁子、不過三朝死。此關童命犯之難養，童限遇之亦凶，以午明順。(鰲頭)
 * 甲己巳酉丑、孩兒難保守。庚辛亥卯年、爺娘哭斷腸。壬癸寅午戌、生下不見日。乙戊丙丁子、不過三朝死。(星平會海)
 */
val p雞飛關A = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (listOf(甲, 己).contains(eightWords.day.stem) && 巳酉丑.contains(eightWords.hour.branch)) ||
      (listOf(庚, 辛).contains(eightWords.day.stem) && 亥卯未.contains(eightWords.hour.branch)) ||
      (listOf(壬, 癸).contains(eightWords.day.stem) && 寅午戌.contains(eightWords.hour.branch)) ||
      (listOf(乙, 戊, 丙, 丁).contains(eightWords.day.stem) && eightWords.hour.branch == 子)
    ) {
      雞飛關
    } else {
      null
    }
  }
}

/**
 * 甲乙巳酉丑、孩兒難保守。庚辛亥卯未、父母哭斷腸。壬癸寅午戌、生下不見日。己戊丙丁子、不過三朝死。此關童命犯之難養，夜生不妨，限遇亦凶，以年干生人取用。(象吉)
 */
val p雞飛關B = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (listOf(甲, 乙).contains(eightWords.day.stem) && 巳酉丑.contains(eightWords.hour.branch)) ||
      (listOf(庚, 辛).contains(eightWords.day.stem) && 亥卯未.contains(eightWords.hour.branch)) ||
      (listOf(壬, 癸).contains(eightWords.day.stem) && 寅午戌.contains(eightWords.hour.branch)) ||
      (listOf(己, 戊, 丙, 丁).contains(eightWords.day.stem) && eightWords.hour.branch == 子)
    ) {
      雞飛關
    } else {
      null
    }
  }
}

/**
 * 甲己巳酉丑、庚辛亥卯未、壬庚午戌、乙戊丙丁子，不過三日關。勿看殺生，童限難養，夜生不妨。凡辰戌未時生人，犯此忌雞對面啼叫。(生育禮俗)
 */
val p雞飛關C = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (listOf(甲, 己).contains(eightWords.day.stem) && 巳酉丑.contains(eightWords.hour.branch)) ||
      (listOf(庚, 辛).contains(eightWords.day.stem) && 亥卯未.contains(eightWords.hour.branch)) ||
      (listOf(乙, 戊, 丙, 丁).contains(eightWords.day.stem) && eightWords.hour.branch == 子)
    ) {
      雞飛關
    } else {
      null
    }
  }
}

/**
 * 甲、己日逢巳、酉、丑時生，
 * 乙、丙、丁、戊日逢子時生，
 * 庚日逢亥、卯、未時生，
 * 辛、壬、癸日逢寅、午、戌時生是。
 * 避免孩子看殺雞、殺魚、殺鴨等行為，可燒牛頭馬面錢、或稱牛馬將軍錢，制化即吉。(黃曆解秘)
 */
val p雞飛關D = object : IHazardFactory {
  override fun getHazard(eightWords: IEightWords, gender: Gender?): ChildHazard? {
    return if (
      (listOf(甲, 己).contains(eightWords.day.stem) && 巳酉丑.contains(eightWords.hour.branch)) ||
      (庚 == eightWords.day.stem && 亥卯未.contains(eightWords.hour.branch)) ||
      (listOf(辛, 壬, 癸).contains(eightWords.day.stem) && 寅午戌.contains(eightWords.hour.branch)) ||
      (listOf(乙, 丙, 丁, 戊).contains(eightWords.day.stem) && eightWords.hour.branch == 子)
    ) {
      雞飛關
    } else {
      null
    }
  }
}
