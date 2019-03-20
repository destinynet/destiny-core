/**
 * Created by smallufo on 2019-03-04.
 */
package destiny.core.chinese.holo

import destiny.core.chinese.IStemBranch
import destiny.core.chinese.IYinYang
import destiny.core.chinese.StemBranch
import destiny.iching.Hexagram
import destiny.iching.IHexagram
import destiny.iching.IHexagramText
import destiny.iching.contentProviders.IHexData
import java.io.Serializable


interface IHoloHexagram : IHexagram, TimeRange<Double> {

  enum class Scale {
    LIFE,    // 半輩子，意味： 先天卦 or 後天卦
    MAJOR,   // 大運 : 陽爻9年 , 陰爻6年
    YEAR,    // 流年
    MONTH,   // 流月
  }

  val scale: Scale

  /** 元堂 在第幾爻 (1~6) */
  val yuanTang: Int

  /** 六爻納甲 */
  val stemBranches: List<StemBranch>

  /** start of GMT JulianDay */
  override val start: Double

  /** end of GMT JulianDay */
  override val endExclusive: Double

}

data class HoloHexagram(
  override val scale: IHoloHexagram.Scale,
  val hexagram: IHexagram,
  override val yuanTang: Int,
  override val stemBranches: List<StemBranch>,
  override val start: Double,
  override val endExclusive: Double) : IHoloHexagram, IHexagram by hexagram, Serializable {

  override fun toString(): String {
    return "$${Hexagram.of(hexagram)} 之 $yuanTang"
  }
}

/** 除了 卦象、元堂 之外，另外包含干支資訊 (並非元堂爻的納甲) */
interface IHoloHexagramWithStemBranch : IHoloHexagram {
  /**
   * 此干支 可能表示 大運、流年、流月、流日 or 流時 的干支
   * 大運 : 代表本命先後天卦中，走到哪一爻，該爻的納甲
   * 流年 : 流年干支
   * 流月 : 流月干支
   * ... 其餘類推
   * */
  val stemBranch: IStemBranch
}

/**
 * 流年、流月、流日 or 流時 卦象 ,
 * @param stemBranch 流年、流月、流日 or 流時 的干支  (並非元堂爻的納甲)
 **/
data class HoloHexagramWithStemBranch(
  val holoHexagram: IHoloHexagram,
  override val stemBranch: IStemBranch) : IHoloHexagramWithStemBranch, IHoloHexagram by holoHexagram

/** 先天卦 or 後天卦 的單一爻 , 內含 6年 or 9年 的流年資料結構 */
data class HoloLine(val yinYang: IYinYang,
                    val yuanTang: Boolean,
                    /**
                     * 每個爻 可以變化出多個卦 ,
                     * 大運的爻，可以變化出 6 or 9 個流年卦象
                     * 流年的爻，可以變化出 12 個流月卦象
                     * 流月的爻，可以變化出 30 個流日卦象
                     * 流日的爻，可以變化出 12 個流時卦象
                     * */
                    val hexagrams: List<IHoloHexagramWithStemBranch>) : IYinYang by yinYang, TimeRange<Double> {


  override val start: Double
    get() = hexagrams.minBy { it.start }!!.start

  override val endExclusive: Double
    get() = hexagrams.maxBy { it.endExclusive }!!.endExclusive
}


/** 純粹用於 先天卦 or 後天卦 , 包含六爻中，每爻的流年資訊 */
interface ILifeHoloHexagram : IHoloHexagram {
  val lines: List<HoloLine>
}

/** 先天卦 or 後天卦 */
data class LifeHoloHexagram(override val lines: List<HoloLine>,
                            override val stemBranches: List<StemBranch>) : ILifeHoloHexagram {

  init {
    require(lines.count { it.yuanTang } == 1) {
      "傳入的六爻，只能有一個 元堂，不能多也不能少。"
    }
  }

  override val yinYangs: List<IYinYang> = lines

  override val scale: IHoloHexagram.Scale = IHoloHexagram.Scale.LIFE

  override val yuanTang: Int = lines.indexOfFirst { it.yuanTang } + 1

  override val start: Double = lines.minBy { it.start }!!.start

  override val endExclusive: Double = lines.maxBy { it.endExclusive }!!.endExclusive

}


/**
 * 爻辭
 */
interface ILinePoem : IYinYang {
  /** line index , 1~6 */
  val line: Int
  val poems: List<String>
}

data class LinePoem(
  /** line index , 1~6 */
  override val line: Int,
  override val poems: List<String>,
  val yinYang: IYinYang) : ILinePoem, IYinYang by yinYang {
  constructor(line: Int, poems: List<String>, hexagram: IHexagram) : this(
    line, poems, hexagram.getYinYang(line)
  )
}


/**
 * 河洛理數64卦訣 , 卦辭
 */
interface IPoemHexagram : IHexagram {
  /** 卦象 之詩 */
  val poems: List<String>

  /** 六爻之詩 , 1 <= lineIndex <= 6 */
  fun getLinePoem(lineIndex: Int): ILinePoem
}

data class PoemData(override val hexagram: IHexagram,
                    override val hex: List<String>,
                    /** 六爻 , size = 6 */
                    private val linePoems: List<ILinePoem>) : IHexData<List<String>, ILinePoem> {

  override fun getLine(lineIndex: Int): ILinePoem {
    return linePoems[lineIndex - 1]
  }

  // 並未實作 用九、用六
  override val extraLine: ILinePoem? = null
}


data class PoemHexagram(
  val hexagram: IHexagram,
  override val poems: List<String>,

  /** 六爻 , size = 6 */
  private val linePoems: List<ILinePoem>
) : IPoemHexagram, IHexagram by hexagram {

  override fun getLinePoem(lineIndex: Int): ILinePoem {
    return linePoems[lineIndex - 1]
  }
}


/** 結合了 河洛卦象 以及 河洛詩詞 */
interface IHoloPoemHexagram : IPoemHexagram, IHoloHexagram


/** 因為 Diamond problem , 這裡擇一實作（不用 delegate） , 選擇 實作 [IPoemHexagram] 因為其 methods 較少 */
data class HoloPoemHexagram(
  val holoHexagram: IHoloHexagram,
  val poemHexagram: IPoemHexagram
) : IHoloPoemHexagram, IHoloHexagram by holoHexagram {


  override val poems: List<String> = poemHexagram.poems

  override fun getLinePoem(lineIndex: Int): ILinePoem {
    return poemHexagram.getLinePoem(lineIndex)
  }
}


/** 終身卦 解釋 */
interface IHoloLifeDescHexagram : IHexagram {
  /** 卦的解釋 */
  val hexContent: String

  /**
   * 六爻的解釋
   * @param lineIndex 1 (incl.) ~ 6 (incl.)
   * */
  fun getLineContent(lineIndex: Int): String
}

/** 終身卦 解釋 */
data class HoloLifeDescHexagram(
  val hexagram: IHexagram,
  /** 卦的解釋 */
  override val hexContent: String,
  /** 六爻的解釋 */
  private val lineContents: List<String>) : IHoloLifeDescHexagram, IHexagram by hexagram {

  override fun getLineContent(lineIndex: Int): String {
    return lineContents[lineIndex - 1]
  }

  init {
    require(lineContents.size == 6) {
      "line contents size should be equal to 6"
    }
  }
}

/** 最終組合卦象 */
interface IHoloFullHexagram : IHoloHexagram, IPoemHexagram, IHoloLifeDescHexagram

/** 最終組合卦象 */
data class HoloFullHexagram(
  val holoHexagram: IHoloHexagram,
  val poemHexagram: IPoemHexagram,
  val lifeDescHexagram: IHoloLifeDescHexagram,
  val hexagramText: IHexagramText
) : IHoloFullHexagram, IHoloHexagram by holoHexagram {

  override val poems: List<String> = poemHexagram.poems

  override fun getLinePoem(lineIndex: Int): ILinePoem {
    return poemHexagram.getLinePoem(lineIndex)
  }

  override val hexContent: String = lifeDescHexagram.hexContent
  override fun getLineContent(lineIndex: Int): String {
    return lifeDescHexagram.getLineContent(lineIndex)
  }
}
