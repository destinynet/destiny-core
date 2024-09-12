/**
 * Created by smallufo on 2024-09-11.
 */
package destiny.core.astrology

import destiny.core.Circular

object Analyzer {

  internal data class Node(val planet: Planet, var visited: Boolean = false, var inCircle: Boolean = false)

  fun analyzeHoroscope(
    planetSignMap: Map<Planet, ZodiacSign>,
    rulerMap: Map<ZodiacSign, Planet>
  ): GraphResult {
    val graph = buildGraph(planetSignMap, rulerMap)
    val circles = findCircles(graph)
    val (paths, terminals) = findPaths(graph, circles, planetSignMap, rulerMap)
    val isolated = findIsolated(planetSignMap, rulerMap , circles, paths)

    return GraphResult(circles, paths, isolated, terminals)
  }

  private fun buildGraph(
    planetSignMap: Map<Planet, ZodiacSign>,
    rulerMap: Map<ZodiacSign, Planet>
  ): Map<Planet, Planet> {
    return planetSignMap.mapValues { (_, sign) -> rulerMap[sign]!! }
  }

  private fun findCircles(graph: Map<Planet, Planet>): Set<Circular<Planet>> {
    val circles = mutableSetOf<Circular<Planet>>()
    val visited = mutableSetOf<Planet>()

    fun dfs(start: Planet, current: Planet, path: MutableList<Planet>) {
      if (current in path && path.size > path.indexOf(current) + 1) {
        val circle = Circular<Planet>()
        for (planet in path.subList(path.indexOf(current), path.size)) {
          circle.add(planet)
        }
        if (circle.size > 1) {  // 确保 circular 至少包含两颗星体
          circles.add(circle)
        }
        return
      }

      if (current in visited) return

      visited.add(current)
      path.add(current)

      val next = graph[current]
      if (next != null && next != current) {  // 确保不包括自己统治自己的情况
        dfs(start, next, path)
      }

      path.removeAt(path.size - 1)
    }

    for (planet in graph.keys) {
      if (planet !in visited) {
        dfs(planet, planet, mutableListOf())
      }
    }

    return circles
  }

  private fun findPaths(
    graph: Map<Planet, Planet>,
    circles: Set<Circular<Planet>>,
    planetSignMap: Map<Planet, ZodiacSign>,
    rulerMap: Map<ZodiacSign, Planet>
  ): Pair<Set<List<Planet>>, Set<Planet>> {
    val paths = mutableSetOf<List<Planet>>()
    val terminals = mutableSetOf<Planet>()

    fun dfs(start: Planet, current: Planet, path: MutableList<Planet>) {
      path.add(current)

      val next = graph[current]
      if (next != null && next != current) {
        if (circles.any { it.toList().contains(next) }) {
          // Path ends at a circle
          path.add(next)
          paths.add(path.toList())
        } else if (planetSignMap[next] == rulerMap.entries.find { it.value == next }?.key) {
          // Next planet is in its ruling sign
          path.add(next)
          paths.add(path.toList())
          terminals.add(next)
        } else if (next !in path) {
          // Continue the path
          dfs(start, next, path)
        }
      } else {
        // End of path or self-ruling
        paths.add(path.toList())
        terminals.add(current)
      }

      path.removeAt(path.size - 1)
    }

    for (planet in graph.keys) {
      if (circles.none { it.toList().contains(planet) }) {
        dfs(planet, planet, mutableListOf())
      }
    }

    // 移除被其他路径完全包含的路径
    val filteredPaths = paths.filter { path ->
      paths.none { other -> other != path && other.containsAll(path) }
    }

    return Pair(filteredPaths.filter { it.size > 1 }.toSet(), terminals)
  }

  private fun findIsolated(
    planetSignMap: Map<Planet, ZodiacSign>,
    rulerMap: Map<ZodiacSign, Planet>,
    circles: Set<Circular<Planet>>,
    paths: Set<List<Planet>>
  ): Set<Planet> {
    val involvedPlanets = circles.flatMap { it.toList() }.toSet() + paths.flatMap { it }.toSet()
    return planetSignMap.keys.filter { planet ->
      planet !in involvedPlanets &&
        planetSignMap.values.none { sign -> rulerMap[sign] == planet }
    }.toSet()
  }


}
