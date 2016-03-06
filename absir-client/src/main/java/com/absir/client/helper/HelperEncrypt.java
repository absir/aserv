/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.client.helper;

import java.io.InputStream;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;

/**
 * @author absir
 * 
 */
public class HelperEncrypt {

	/** BYTE_SECRETS */
	public static final byte BYTE_SECRETS[] = "#$@^&%".getBytes();

	/** HEX_DIGITS */
	private static char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

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
		try {
			MessageDigest mdInst = MessageDigest.getInstance(algorithm);
			mdInst.update(inBuffer);
			byte[] outBuffer = mdInst.digest();
			return hexDigit(outBuffer, secrets);

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
	 * @param mode
	 * @param secrect
	 * @param length
	 * @return
	 */
	public static SecretKeySpec getSecretKeySpec(String mode, String secrect, int length) {
		byte[] data = null;
		if (secrect == null) {
			secrect = "";
		}

		if (length > 0 && length != secrect.length()) {
			StringBuffer sb = new StringBuffer(length);
			sb.append(secrect);
			while (sb.length() < length) {
				sb.append('0');
			}

			if (sb.length() > length) {
				sb.setLength(length);
			}

			data = sb.toString().getBytes(KernelCharset.UTF8);

		} else {
			data = secrect.getBytes(KernelCharset.UTF8);
		}

		return new SecretKeySpec(data, mode);
	}

	/**
	 * @param mode
	 * @param cliperMode
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static byte[] encrypt(String mode, String cliperMode, byte[] inBuffer, String secrect) {
		return encrypt(getSecretKeySpec(mode, secrect, 0), cliperMode, inBuffer);
	}

	/**
	 * @param secrectKeySpec
	 * @param modeCiper
	 * @param inBuffer
	 * @return
	 */
	public static byte[] encrypt(SecretKeySpec secrectKeySpec, String cliperMode, byte[] inBuffer) {
		try {
			Cipher cipher = Cipher.getInstance(cliperMode);
			cipher.init(Cipher.ENCRYPT_MODE, secrectKeySpec);
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
	 * @param cliperMode
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static byte[] decrypt(String mode, String cliperMode, byte[] inBuffer, String secrect) {
		return decrypt(getSecretKeySpec(mode, secrect, 0), cliperMode, inBuffer);
	}

	/**
	 * @param secrectKeySpec
	 * @param inBuffer
	 * @param content
	 * @return
	 */
	public static byte[] decrypt(SecretKeySpec secrectKeySpec, String inBuffer, byte[] content) {
		try {
			Cipher cipher = Cipher.getInstance(inBuffer);
			cipher.init(Cipher.DECRYPT_MODE, secrectKeySpec);
			return cipher.doFinal(content);

		} catch (Exception e) {
			if (Environment.getEnvironment() == Environment.DEVELOP) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/** AES 算法/模式/填充 **/
	public static final String AES_CIPHER_MODE = "AES/ECB/PKCS5Padding";

	/**
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static byte[] aesEncrypt(byte[] inBuffer, String secrect) {
		return encrypt("AES", AES_CIPHER_MODE, inBuffer, secrect);
	}

	/**
	 * @param secrectKeySpec
	 * @param inBuffer
	 * @return
	 */
	public static byte[] aesEncrypt(SecretKeySpec secrectKeySpec, byte[] inBuffer) {
		return encrypt(secrectKeySpec, AES_CIPHER_MODE, inBuffer);
	}

	/**
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static byte[] aesDecrypt(byte[] inBuffer, String secrect) {
		return decrypt("AES", AES_CIPHER_MODE, inBuffer, secrect);
	}

	/**
	 * @param secrectKeySpec
	 * @param inBuffer
	 * @return
	 */
	public static byte[] aesDecrypt(SecretKeySpec secrectKeySpec, byte[] inBuffer) {
		return decrypt(secrectKeySpec, AES_CIPHER_MODE, inBuffer);
	}

	/**
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static String aesEncryptBase64(String inBuffer, String secrect) {
		return aesEncryptBase64(inBuffer, getSecretKeySpec("AES", secrect, 16));
	}

	/**
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static String aesEncryptBase64(String inBuffer, SecretKeySpec secrectKeySpec) {
		byte[] buffer = aesEncrypt(secrectKeySpec, inBuffer.getBytes(KernelCharset.getDefault()));
		return buffer == null ? null : Base64.encodeBase64String(buffer);
	}

	/**
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static String aesDecryptBase64(String inBuffer, String secrect) {
		return aesDecryptBase64(inBuffer, getSecretKeySpec("AES", secrect, 16));
	}

	/**
	 * @param inBuffer
	 * @param secrect
	 * @return
	 */
	public static String aesDecryptBase64(String inBuffer, SecretKeySpec secrectKeySpec) {
		byte[] buffer = aesDecrypt(secrectKeySpec, Base64.decodeBase64(inBuffer));
		return buffer == null ? null : new String(buffer, KernelCharset.getDefault());
	}
}
