/**
 * Created by smallufo on 2025-01-29.
 */
package destiny.tools.serializers

import destiny.core.Circular
import destiny.core.Graph
import destiny.core.astrology.Planet
import destiny.core.toString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.util.*


@OptIn(ExperimentalSerializationApi::class)
object GraphPlanetSerializer : KSerializer<Graph<Planet>> {
  override val descriptor = buildClassSerialDescriptor("Graph") {
    element<Set<Circular<Planet>>>("circles")
    element<String?>("circles_description", isOptional = true)
    element<Set<List<Planet>>>("paths")
    element<String?>("paths_description", isOptional = true)
    element<Set<Planet>>("isolated")
    element<String?>("isolated_description", isOptional = true)
    element<Set<Planet>>("terminals")
    element<String?>("terminals_description", isOptional = true)
  }


  override fun serialize(encoder: Encoder, value: Graph<Planet>) {

    val locale = Locale.ENGLISH

    val circlesDesc: String? = value.circles.takeIf { it.isNotEmpty() }?.let { circulars: Set<Circular<Planet>> ->
      buildString {
        append("This chart contains ${circulars.size} closed cycle through rulership : ")
        append(
          circulars.joinToString(", ") { circular: Circular<Planet> ->
            circular.toList().joinToString("→", postfix = (if (circular.size == 2) "(mutual reception)" else "")) { it.toString(locale) }
          }
        )
      }
    }

    val pathsDesc = value.paths.takeIf { it.isNotEmpty() }
      ?.joinToString(", ", prefix = "This chart shows ${value.paths.size} path(s) through rulership : ") {
        it.joinToString("→") { planet ->
          planet.toString(locale)
        }
      }


    val isolatedDesc = buildString {
      value.isolated.takeIf { it.isNotEmpty() }
        ?.also { isolatedPlanets ->
          append("The following planets are in the signs they rule, with no connections to other planets through rulership: ")
          isolatedPlanets.forEach { p ->
            append(p.toString(locale))
          }
        }
    }.takeIf { it.isNotBlank() }

    val terminalsDesc = value.terminals.takeIf { it.isNotEmpty() }
      ?.let { terminals: Set<Planet> ->
        terminals.joinToString(",", prefix = "final dispositor (path terminal, which is not connected to a circle) : ") {
          it.toString(locale)
        }
      }

    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, SetSerializer(Circular.serializer(Planet.serializer())), value.circles)
      encodeNullableSerializableElement(descriptor, 1, String.serializer(), circlesDesc)
      encodeSerializableElement(descriptor, 2, SetSerializer(ListSerializer(Planet.serializer())), value.paths)
      encodeNullableSerializableElement(descriptor, 3, String.serializer(), pathsDesc)
      encodeSerializableElement(descriptor, 4, SetSerializer(Planet.serializer()), value.isolated)
      encodeNullableSerializableElement(descriptor, 5, String.serializer(), isolatedDesc)
      encodeSerializableElement(descriptor, 6, SetSerializer(Planet.serializer()), value.terminals)
      encodeNullableSerializableElement(descriptor, 7, String.serializer(), terminalsDesc)
    }
  }

  override fun deserialize(decoder: Decoder): Graph<Planet> {

    var circles: Set<Circular<Planet>> = setOf()
    var paths: Set<List<Planet>> = setOf()
    var isolated: Set<Planet> = setOf()
    var terminals: Set<Planet> = setOf()
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0 -> circles = decodeSerializableElement(descriptor, 0, SetSerializer(Circular.serializer(Planet.serializer())))
          1 -> decodeNullableSerializableElement(descriptor, 1, String.serializer())
          2 -> paths = decodeSerializableElement(descriptor, 2, SetSerializer(ListSerializer(Planet.serializer())))
          3 -> decodeNullableSerializableElement(descriptor, 3, String.serializer())
          4 -> isolated = decodeSerializableElement(descriptor, 4, SetSerializer(Planet.serializer()))
          5 -> decodeNullableSerializableElement(descriptor, 5, String.serializer())
          6 -> terminals = decodeSerializableElement(descriptor, 6, SetSerializer(Planet.serializer()))
          7 -> decodeNullableSerializableElement(descriptor, 7, String.serializer())
          CompositeDecoder.DECODE_DONE -> break
          else -> error("Unexpected index: $index")
        }
      }
    }
    return Graph(circles, paths, isolated, terminals)

  }
}
