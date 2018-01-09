/**
 * Created by smallufo on 2015-05-14.
 */
package destiny.core.calendar

class ConversionException : RuntimeException {

  constructor(message: String) : super(message)

  constructor(cause: Throwable) : super(cause)
}
