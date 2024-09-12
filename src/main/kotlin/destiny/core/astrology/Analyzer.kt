/**
 * Created by smallufo on 2024-09-11.
 */
package destiny.core.astrology

import destiny.core.Circular

object Analyzer {

  internal data class Node(val planet: Planet, var visited: Boolean = false, var inCircle: Boolean = false)

  private fun findCircles(
    node: Node,
    graph: Map<Planet, Node>,
    currentPath: MutableList<Planet>,
    circular: Circular<Planet>,
    planetSignMap: Map<Planet, ZodiacSign>,
    rulerMap: Map<ZodiacSign, Planet>
  ) {
    node.visited = true
    currentPath.add(node.planet)

    val sign = planetSignMap[node.planet]
    if (sign != null) {
      val ruler = rulerMap[sign]!!
      val rulerNode = graph[ruler]
      if (rulerNode != null) {
        val cycleStart = currentPath.indexOf(ruler)
        if (cycleStart != -1) {
          // 找到了一個循環

          currentPath.subList(cycleStart, currentPath.size).forEach { circular.add(it) }
        } else if (!rulerNode.visited) {
          // 繼續尋找 circle
          findCircles(rulerNode, graph, currentPath, circular, planetSignMap, rulerMap)
        }
      }
    }

    currentPath.removeAt(currentPath.size - 1)
  }


  private fun findPaths(
    node: Node,
    graph: Map<Planet, Node>,
    currentPath: MutableList<Planet>,
    paths: MutableSet<List<Planet>>,
    circles: Set<Circular<Planet>>,
    terminals: MutableSet<Planet>,
    planetSignMap: Map<Planet, ZodiacSign>,
    rulerMap: Map<ZodiacSign, Planet>
  ) {
    node.visited = true
    currentPath.add(node.planet)

    val sign = planetSignMap[node.planet]
    if (sign != null) {
      val ruler = rulerMap[sign]!!
      val rulerNode = graph[ruler]
      if (rulerNode != null) {
        if (rulerNode.inCircle || circles.any { it.toList().contains(ruler) }) {
          // 路徑連接到一個循環，結束路徑
          currentPath.add(ruler)
          paths.add(currentPath.toList())
        } else if (!rulerNode.visited) {
          // 繼續尋找路徑
          findPaths(rulerNode, graph, currentPath, paths, circles, terminals, planetSignMap, rulerMap)
        } else {
          // 路徑結束於一個已訪問的行星
          currentPath.add(ruler)
          paths.add(currentPath.toList())
          terminals.add(ruler)
        }
      }
    }

    currentPath.removeAt(currentPath.size - 1)
    // 重置訪問狀態，允許這個節點成為其他路徑的一部分
    node.visited = false
  }


  fun analyzeHoroscope(planetSignMap: Map<Planet, ZodiacSign>, rulerMap: Map<ZodiacSign, Planet>): GraphResult {
    val graph: Map<Planet, Node> = planetSignMap.map { (p, _) ->  p to Node(p)}.toMap()

    val circles = mutableSetOf<Circular<Planet>>()
    val paths = mutableSetOf<List<Planet>>()
    val terminals = mutableSetOf<Planet>()

    // 第一步：找出所有的循環
    for (node in graph.values) {
      if (!node.visited) {
        val circular = Circular<Planet>()
        findCircles(node, graph, mutableListOf(), circular, planetSignMap, rulerMap)
        if (circular.size > 1) {
          circles.add(circular)
        }
      }
    }

    // 標記所有在循環中的行星
    circles.forEach { circle: Circular<Planet> ->
      circle.toList().forEach { planet ->
        graph[planet]?.inCircle = true
      }
    }

    // 重置訪問狀態
    graph.values.forEach { it.visited = false }

    // 第二步：找出所有的路徑
    for (node in graph.values) {
      if (!node.inCircle && !node.visited) {
        findPaths(node, graph, mutableListOf(), paths, circles, terminals, planetSignMap, rulerMap)
      }
    }

    // 找出孤立的行星
    val isolated = graph.values.filter { !it.inCircle && !paths.any { path -> path.contains(it.planet) } }.map { it.planet }.toSet()

    // 過濾掉在循環中的終端點
    val finalTerminals = terminals.filter { !graph[it]!!.inCircle }.toSet()

    return GraphResult(circles, paths, isolated, finalTerminals)
  }


}
