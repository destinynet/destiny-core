/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.core

/**
 * Context 與 Map<String , String> 互換
 */
interface IContextMap<T> {

  fun getMap(context: T): Map<String, String>


  fun getContext(map: Map<String, String>): T


  fun T.toMap(): Map<String, String> {
    return getMap(this)
  }

  fun Map<String,String>.toContext() : T {
    return getContext(this)
  }
}