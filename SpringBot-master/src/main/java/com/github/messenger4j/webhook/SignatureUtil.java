package com.github.messenger4j.webhook;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Max Grabenhorst
 * @since 1.0.0
 */
public final class SignatureUtil {

  private static final String HMAC_SHA1 = "HmacSHA1";
  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

  private SignatureUtil() {}

  /**
   * Verifies the provided signature of the payload.
   *
   * @param payload the request body {@code JSON payload}
   * @param signature the SHA1 signature of the request payload
   * @param appSecret the {@code Application Secret} of the Facebook App
   * @return {@code true} if the verification was successful, otherwise {@code false}
   */
  public static boolean isSignatureValid(String payload, String signature, String appSecret) {
    try {
      final Mac mac = Mac.getInstance(HMAC_SHA1);
      mac.init(new SecretKeySpec(appSecret.getBytes(), HMAC_SHA1));
      final byte[] rawHmac = mac.doFinal(payload.getBytes());

      final String expected = signature.substring(5);
      final String actual = bytesToHexString(rawHmac);

      return expected.equals(actual);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }

  private static String bytesToHexString(byte[] bytes) {
    final char[] hexChars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      final int v = bytes[i] & 0xFF;
      hexChars[i * 2] = HEX_ARRAY[v >>> 4];
      hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }
}