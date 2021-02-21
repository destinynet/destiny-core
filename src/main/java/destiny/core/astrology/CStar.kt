/**
 * Created by smallufo on 2021-02-21.
 */
package destiny.core.astrology


sealed class CStar(nameKey: String,
                   abbrKey: String) : Star(nameKey , abbrKey , CStar::class.java.name) {
}
