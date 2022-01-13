package Anon.client.service.utils;

public class IpUtils {

  public static Long ipToLong(String address) {
    String[] spl = address.split("/");
    String[] addrArray = spl[0].split("\\.");
    long num = 0;
    for(int i = 0; i < addrArray.length; i++) {
      int power = 3-i;
      num += (Integer.parseInt(addrArray[i])%256 * Math.pow(256,power));
    }
    return num;
  }

  public static String longToIP(long i) {
    return ((i >> 24 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + ( i & 0xFF);
  }
}