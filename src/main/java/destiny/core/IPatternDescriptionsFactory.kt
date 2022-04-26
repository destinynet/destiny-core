/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core


interface IPatternDescriptionsFactory<T> : java.io.Serializable {

  fun getPatternDescriptions(model: T): List<IPatternParasDescription>

  fun getDescription(pattern: IPattern): IPatternParasDescription?
}
