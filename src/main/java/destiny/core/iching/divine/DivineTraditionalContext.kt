package destiny.core.iching.divine

import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.chinese.SixAnimal
import destiny.core.iching.IHexagram
import java.util.*

interface ISingleHexagramContext {

  /** 單一卦象 ,（不含任何文字）的排卦結果 [SingleHexagram] */
  fun getSingleHexagram(
    hexagram: IHexagram,
    settings: SettingsOfStemBranch = SettingsOfStemBranch.GingFang,
    hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang
  ): ISingleHexagram
}

interface ISingleHexagramWithNameContext : ISingleHexagramContext {
  fun getSingleHexagramWithName(
    hexagram: IHexagram,
    settings: SettingsOfStemBranch = SettingsOfStemBranch.GingFang,
    hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang,
    locale: Locale = Locale.TAIWAN
  ): ISingleHexagramWithName
}


/** 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
interface ICombinedDivineContext : ISingleHexagramWithNameContext {
  fun getCombinedDivine(src: IHexagram, dst: IHexagram, settings: SettingsOfStemBranch, hiddenEnergy: HiddenEnergy): ICombinedWithMeta
}


/** 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照) */
interface ICombinedWithMetaNameContext : ICombinedDivineContext {

  fun getCombinedWithMetaName(src: IHexagram,
                              dst: IHexagram,
                              settings: SettingsOfStemBranch = SettingsOfStemBranch.GingFang,
                              hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang,
                              locale: Locale = Locale.TAIWAN): ICombinedWithMetaName
}

/**
 * 具備「日干支」「月令」 , 可以排出六獸 [SixAnimal] 以及神煞 , 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] )
 * 通常用於 書籍、古書 當中卦象對照
 * */
interface ICombinedWithMetaNameDayMonthContext : ICombinedWithMetaNameContext {

  fun getCombinedWithMetaNameDayMonth(eightWordsNullable: IEightWordsNullable,
                                      config: DivineTraditionalConfig): ICombinedWithMetaNameDayMonth
}
