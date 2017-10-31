/*
 * *
 *  This file is part of the bittrex4j project.
 *
 *  @author CCob
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 * /
 */

package com.cobnet.bittrex4j;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class EncryptionUtility {

    public static String calculateHash(String secret, String url, String algorithm) {

        try {
            Mac shaHmac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
            shaHmac.init(secretKey);
            byte[] hash = shaHmac.doFinal(url.getBytes());
            return Hex.encodeHexString(hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String generateNonce() {

        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            random.setSeed(System.currentTimeMillis());
            byte[] nonceBytes = new byte[16];
            random.nextBytes(nonceBytes);

            String nonce = new String(Base64.getEncoder().encode(nonceBytes), "UTF-8");
            return nonce;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
