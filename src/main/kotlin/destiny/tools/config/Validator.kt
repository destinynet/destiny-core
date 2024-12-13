/**
 * Created by smallufo on 2024-12-14.
 */
package destiny.tools.config


interface Validator<T> {
  fun validate(config: T): Boolean
}
