package utils

import org.apache.commons.codec.binary.Hex

object HashUtil {
  def encode(input: String): String =
    Hex.encodeHexString(java.security.MessageDigest.getInstance("SHA-1").digest(input.getBytes("UTF-8")))
}
