package destiny.tools

import java.io.Serializable
import java.util.Optional

interface AbstractDao<T> {

  val classType: Class<T>

  val count: Long
  operator fun get(id: Serializable): Optional<T>
  fun save(t: T): T
  fun update(t: T): T
  fun delete(t: T)
  fun evict(id: Serializable)

  fun flush(t: T): T
  fun merge(t: T): T
  fun refresh(t: T)
  fun findAll(): List<T>
  fun findAll(start: Int, count: Int): List<T>
}
