/**
 * Created by smallufo on 2015-04-13.
 */
package destiny.tools;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HashidsTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void test_HashByMac() throws SocketException {
    String mac = searchForMac();
    Hashids a = new Hashids(mac);

    for (int i = 0; i < 10; i++) {
      logger.info("encode {} = {}", i, a.encode(i));
    }
  }

  @Test
  public void testArray() throws Exception {
    Hashids a = new Hashids();

    for (int i = 0; i < 100; i++) {
      long t = System.currentTimeMillis();
      String encoded = a.encode(t);
      logger.info("encoded = {}", encoded);

      long[] decoded = a.decode(encoded);
      logger.info("{}", decoded);
    }
  }

  @Test
  public void test_maximum_nummber() throws SocketException, UnknownHostException {

    long big = 9007199254740992L;

    Hashids a = new Hashids();
    String res = a.encode(big);

    logger.info("big = {} => {}", big, a.encode(big));
    logger.info("decode {} = {}", a.encode(big), a.decode(a.encode(big))[0]);


    long[] b = a.decode(res);
    Assert.assertEquals(big, b[0]);
  }

  public String searchForMac() throws SocketException {
    String firstInterface = null;
    Map<String, String> addressByNetwork = new HashMap<>();
    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

    while (networkInterfaces.hasMoreElements()) {
      NetworkInterface network = networkInterfaces.nextElement();

      byte[] bmac = network.getHardwareAddress();
      if (bmac != null) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bmac.length; i++) {
          sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));
        }

        if (!sb.toString().isEmpty()) {
          addressByNetwork.put(network.getName(), sb.toString());
          System.out.println("Address = " + sb.toString() + " @ [" + network.getName() + "] " + network.getDisplayName());
        }

        if (!sb.toString().isEmpty() && firstInterface == null) {
          firstInterface = network.getName();
        }
      }
    }

    if (firstInterface != null) {
      return addressByNetwork.get(firstInterface);
    }

    return null;
  }
}