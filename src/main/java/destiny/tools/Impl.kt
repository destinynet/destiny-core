/**
 * Created by smallufo on 2020-03-08.
 */
package destiny.tools

annotation class Domain(val key: String, val value: String, val default: Boolean = false)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Impl(val value: Array<Domain> = [])
