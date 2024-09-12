/**
 * Created by smallufo on 2024-09-11.
 */
package destiny.core.astrology

import destiny.core.Circular

object Analyzer {

  internal data class Node(val planet: Planet, var visited: Boolean = false, var inCircle: Boolean = false)

  fun analyzeHoroscope(planetSignMap: Map<Planet, ZodiacSign>, rulerMap: Map<ZodiacSign, Planet>): GraphResult {
    val graph = planetSignMap.mapValues { (_, sign) -> rulerMap[sign]!! }
    val circles = findCircles(graph)
    val (paths, terminals) = findPaths(graph, circles, planetSignMap, rulerMap)
    val isolated = findIsolated(planetSignMap, circles, paths)

    return GraphResult(circles, paths, isolated, terminals)
  }


  private fun findCircles(graph: Map<Planet, Planet>): Set<Circular<Planet>> {
    val circles = mutableSetOf<Circular<Planet>>()
    val visited = mutableSetOf<Planet>()

    fun dfs(start: Planet, current: Planet, path: MutableList<Planet>) {
      if (current in path && path.size > path.indexOf(current) + 1) {
        val planetsInCircular = mutableListOf<Planet>()

        for (planet in path.subList(path.indexOf(current), path.size)) {
          planetsInCircular.add(planet)
        }
        if (planetsInCircular.size > 1) {
          // 確保 circular 至少包含兩顆星體
          circles.add(Circular(planetsInCircular))
        }
        return
      }

      if (current in visited) return

      visited.add(current)
      path.add(current)

      val next = graph[current]
      if (next != null && next != current) {
        // 確保不包括自己统治自己的情况
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
        if (circles.any { it.contains(next) }) {
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
      } else if (path.size > 1) {
        // End of path
        paths.add(path.toList())
        terminals.add(current)
      }

      path.removeAt(path.size - 1)
    }

    for (planet in graph.keys) {
      if (circles.none { it.contains(planet) }) {
        dfs(planet, planet, mutableListOf())
      }
    }

    // 移除被其他路徑完全包含的路徑
    val filteredPaths = paths.filter { path ->
      paths.none { other -> other != path && other.containsAll(path) }
    }

    return Pair(filteredPaths.toSet(), terminals)
  }


  private fun findIsolated(
    planetSignMap: Map<Planet, ZodiacSign>,
    circles: Set<Circular<Planet>>,
    paths: Set<List<Planet>>
  ): Set<Planet> {
    val involvedPlanets = circles.flatMap { it.toList() }.toSet() + paths.flatten().toSet()
    return planetSignMap.keys - involvedPlanets
  }

}
