/**
 * Created by smallufo on 2024-07-01.
 */
package destiny.tools.ai.model

import kotlin.reflect.KClass


object DomainObjectFinder {
  private val cache = mutableMapOf<String, Domain>()

  fun findDomainObject(s: String, clazz: KClass<*> = Domain::class): Domain? {
    return cache.getOrPut(s) {
      findDomainObjectInternal(s, clazz) ?: return null
    }
  }

  private fun findDomainObjectInternal(s: String, clazz: KClass<*>): Domain? {
    if (clazz != Domain::class && !Domain::class.java.isAssignableFrom(clazz.java)) {
      return null
    }

    val directMatch = clazz.sealedSubclasses.firstOrNull { it.simpleName == s }?.objectInstance
    if (directMatch is Domain) {
      return directMatch
    }

    for (subclass in clazz.sealedSubclasses) {
      if (subclass.isSealed) {
        val nestedMatch = findDomainObjectInternal(s, subclass)
        if (nestedMatch != null) {
          return nestedMatch
        }
      }
    }

    return null
  }
}
