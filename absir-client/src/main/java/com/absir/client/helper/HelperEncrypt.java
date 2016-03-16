/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.client.helper;

import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @author absir
 */
public class HelperEncrypt {

    /**
     * BYTE_SECRETS
     */
    public static final byte BYTE_SECRETS[] = "#$@^&%".getBytes();
    /**
     * AES 算法/模式/填充
     **/
    public static final String AES_CIPHER_MODE = "AES/ECB/PKCS5Padding";
    /**
     * HEX_DIGITS
     */
    private static char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F'};

    /**
     * @param outBuffer
     * @param secrets
     * @return
     */
    public static String hexDigit(byte[] outBuffer, byte[] secrets) {
        int len = outBuffer.length;
        char str[] = new char[len * 2];
        int sel = 0;
        if (secrets != null) {
            sel = secrets.length;
        }

        int j, k;
        for (int i = j = k = 0; i < len; i++) {
            byte byt = outBuffer[i];
            if (sel > 0) {
                if (k < sel) {
                    byt ^= secrets[k];
                    k++;

                } else {
                    k = 0;
                }
            }

            str[j++] = HEX_DIGITS[byt >>> 4 & 0x0F];
            str[j++] = HEX_DIGITS[byt & 0x0F];
        }

        return new String(str);
    }

    /**
     * @param algorithm
     * @param inBuffer
     * @param secrets
     * @return
     */
    public static String encryption(String algorithm, byte[] inBuffer, byte[] secrets) {
        return encryption(algorithm, inBuffer, secrets, 1);
    }

    /**
     * @param algorithm
     * @param inBuffer
     * @param secrets
     * @param count
     * @return
     */
    public static String encryption(String algorithm, byte[] inBuffer, byte[] secrets, int count) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance(algorithm);
            while (true) {
                mdInst.update(inBuffer);
                mdInst.update(secrets);
                inBuffer = mdInst.digest();
                mdInst.reset();
                if (--count <= 0) {
                    break;
                }
            }

            return hexDigit(inBuffer, secrets);

        } catch (Exception e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * @param algorithm
     * @param inputStream
     * @param secrets
     * @return
     */
    public static String encryption(String algorithm, InputStream inputStream, byte[] secrets) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance(algorithm);
            byte[] inBuffer = new byte[255];
            int len = 0;
            while ((len = inputStream.read(inBuffer)) > 0) {
                mdInst.update(inBuffer, 0, len);
            }
            byte[] outBuffer = mdInst.digest();
            return hexDigit(outBuffer, secrets);

        } catch (Exception e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }

            return null;

        } finally {
            HelperIO.closeQuietly(inputStream);
        }
    }

    /**
     * @param algorithm
     * @param string
     * @return
     */
    public static String encryption(String algorithm, String string) {
        return encryption(algorithm, string, null);
    }

    /**
     * @param algorithm
     * @param string
     * @param secrets
     * @return
     */
    public static String encryption(String algorithm, String string, byte[] secrets) {
        byte[] inBuffer = string.getBytes();
        return encryption(algorithm, inBuffer, secrets);
    }

    /**
     * @param inBuffer
     * @return
     */
    public static String encryptionMD5(byte[] inBuffer) {
        return encryption("MD5", inBuffer, null);
    }

    /**
     * @param inputStream
     * @return
     */
    public static String encryptionMD5(InputStream inputStream) {
        return encryption("MD5", inputStream, null);
    }

    /**
     * @param string
     * @return
     */
    public static String encryptionMD5(String string) {
        return encryption("MD5", string, null);
    }

    /**
     * @param string
     * @param secrets
     * @return
     */
    public static String encryptionMD5(String string, byte[] secrets) {
        return encryption("MD5", string, secrets);
    }

    /**
     * @param string
     * @param secrets
     * @param count
     * @return
     */
    public static String encryptionMD5(String string, byte[] secrets, int count) {
        return encryption("MD5", string.getBytes(), secrets, 0);
    }

    /**
     * @param mode
     * @param secret
     * @param length
     * @return
     */
    public static SecretKeySpec getSecretKeySpec(String mode, String secret, int length) {
        byte[] data = null;
        if (secret == null) {
            secret = "";
        }

        if (length > 0 && length != secret.length()) {
            StringBuffer sb = new StringBuffer(length);
            sb.append(secret);
            while (sb.length() < length) {
                sb.append('0');
            }

            if (sb.length() > length) {
                sb.setLength(length);
            }

            data = sb.toString().getBytes(KernelCharset.UTF8);

        } else {
            data = secret.getBytes(KernelCharset.UTF8);
        }

        return new SecretKeySpec(data, mode);
    }

    /**
     * @param mode
     * @param cipherMode
     * @param inBuffer
     * @param secret
     * @return
     */
    public static byte[] encrypt(String mode, String cipherMode, byte[] inBuffer, String secret) {
        return encrypt(getSecretKeySpec(mode, secret, 0), cipherMode, inBuffer);
    }

    /**
     * @param secretKeySpec
     * @param cipherMode
     * @param inBuffer
     * @return
     */
    public static byte[] encrypt(SecretKeySpec secretKeySpec, String cipherMode, byte[] inBuffer) {
        try {
            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(inBuffer);

        } catch (Exception e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param mode
     * @param cipherMode
     * @param inBuffer
     * @param secret
     * @return
     */
    public static byte[] decrypt(String mode, String cipherMode, byte[] inBuffer, String secret) {
        return decrypt(getSecretKeySpec(mode, secret, 0), cipherMode, inBuffer);
    }

    /**
     * @param secretKeySpec
     * @param inBuffer
     * @param content
     * @return
     */
    public static byte[] decrypt(SecretKeySpec secretKeySpec, String inBuffer, byte[] content) {
        try {
            Cipher cipher = Cipher.getInstance(inBuffer);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(content);

        } catch (Exception e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param inBuffer
     * @param secret
     * @return
     */
    public static byte[] aesEncrypt(byte[] inBuffer, String secret) {
        return encrypt("AES", AES_CIPHER_MODE, inBuffer, secret);
    }

    /**
     * @param secretKeySpec
     * @param inBuffer
     * @return
     */
    public static byte[] aesEncrypt(SecretKeySpec secretKeySpec, byte[] inBuffer) {
        return encrypt(secretKeySpec, AES_CIPHER_MODE, inBuffer);
    }

    /**
     * @param inBuffer
     * @param secret
     * @return
     */
    public static byte[] aesDecrypt(byte[] inBuffer, String secret) {
        return decrypt("AES", AES_CIPHER_MODE, inBuffer, secret);
    }

    /**
     * @param secretKeySpec
     * @param inBuffer
     * @return
     */
    public static byte[] aesDecrypt(SecretKeySpec secretKeySpec, byte[] inBuffer) {
        return decrypt(secretKeySpec, AES_CIPHER_MODE, inBuffer);
    }

    /**
     * @param inBuffer
     * @param secret
     * @return
     */
    public static String aesEncryptBase64(String inBuffer, String secret) {
        return aesEncryptBase64(inBuffer, getSecretKeySpec("AES", secret, 16));
    }

    /**
     * @param inBuffer
     * @param secretKeySpec
     * @return
     */
    public static String aesEncryptBase64(String inBuffer, SecretKeySpec secretKeySpec) {
        byte[] buffer = aesEncrypt(secretKeySpec, inBuffer.getBytes(KernelCharset.getDefault()));
        return buffer == null ? null : Base64.encodeBase64String(buffer);
    }

    /**
     * @param inBuffer
     * @param secret
     * @return
     */
    public static String aesDecryptBase64(String inBuffer, String secret) {
        return aesDecryptBase64(inBuffer, getSecretKeySpec("AES", secret, 16));
    }

    /**
     * @param inBuffer
     * @param secretKeySpec
     * @return
     */
    public static String aesDecryptBase64(String inBuffer, SecretKeySpec secretKeySpec) {
        byte[] buffer = aesDecrypt(secretKeySpec, Base64.decodeBase64(inBuffer));
        return buffer == null ? null : new String(buffer, KernelCharset.getDefault());
    }
}
