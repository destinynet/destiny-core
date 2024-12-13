/**
 * Created by smallufo on 2024-12-14.
 */
package destiny.tools.config


interface Parser<T> {

  fun parse(content: String): T

}
