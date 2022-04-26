package destiny.core

/**
 * 整合 [IPattern] , 以及 [Descriptive]
 * 另外新增 list of [Paragraph] 作為段落解說
 */
interface IPatternParasDescription : IPattern, Descriptive {
  val pattern: IPattern
  val paras: List<Paragraph>
}
