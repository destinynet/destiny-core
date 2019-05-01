/**
 * Created by smallufo on 2015-04-13.
 */
package destiny.tools

import org.slf4j.LoggerFactory
import java.net.NetworkInterface
import kotlin.test.Test
import kotlin.test.assertEquals

class HashidsTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun test_HashByMac() {
    val mac = searchForMac()!!
    val a = Hashids(mac)

    for (i in 0..9) {
      logger.info("encode {} = {}", i, a.encode(i.toLong()))
    }
  }

  @Test
  fun testArray() {
    val a = Hashids()

    for (i in 0..99) {
      val t = System.currentTimeMillis()
      val encoded = a.encode(t)
      logger.info("encoded = {}", encoded)

      val decoded = a.decode(encoded)
      logger.info("{}", decoded)
    }
  }

  @Test
  fun test_maximum_nummber() {

    val big = 9007199254740992L

    val a = Hashids()
    val res = a.encode(big)

    logger.info("big = {} => {}", big, a.encode(big))
    logger.info("decode {} = {}", a.encode(big), a.decode(a.encode(big))[0])


    val b = a.decode(res)
    assertEquals(big, b[0])
  }

  fun searchForMac(): String? {
    var firstInterface: String? = null
    val addressByNetwork = HashMap<String, String>()
    val networkInterfaces = NetworkInterface.getNetworkInterfaces()

    while (networkInterfaces.hasMoreElements()) {
      val network = networkInterfaces.nextElement()

      val bmac = network.hardwareAddress
      if (bmac != null) {
        val sb = StringBuilder()
        for (i in bmac.indices) {
          sb.append(String.format("%02X%s", bmac[i], if (i < bmac.size - 1) "-" else ""))
        }

        if (!sb.toString().isEmpty()) {
          addressByNetwork.put(network.name, sb.toString())
          println("Address = " + sb.toString() + " @ [" + network.name + "] " + network.displayName)
        }

        if (!sb.toString().isEmpty() && firstInterface == null) {
          firstInterface = network.name
        }
      }
    }

    return if (firstInterface != null) {
      addressByNetwork[firstInterface]
    } else null

  }
}