package com.zhouyutong.zapplication.security.crypto;

import com.zhouyutong.zapplication.security.exception.DecryptException;
import com.zhouyutong.zapplication.security.exception.EncryptException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <b>对称加密AES算法工具</b><br>
 *
 * @author ziroom
 * @thread-safe
 * @since 2015-10-31
 */
public class AES {

	private static final String ALGORITHM = "AES";
	private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";

	/**
	 * 加密
	 *
	 * @param message - 待加密消息
	 * @param keyStr  - 密钥
	 * @return
	 * @throws NullPointerException
	 * @throws EncryptException
	 */
	public static String encrypt(String message, String keyStr) throws EncryptException {
		checkNotNull(message);
		checkNotNull(keyStr);

		try {
			SecretKeySpec key = new SecretKeySpec(keyStr.toLowerCase().getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes()));
		} catch (Exception e) {
			throw new EncryptException("AES.encrypt error.", e);
		}
	}

	/**
	 * 解密
	 *
	 * @param encryptMessage - 密文
	 * @param keyStr         - 密钥
	 * @return
	 * @throws NullPointerException
	 * @throws DecryptException
	 */
	public static String decrypt(String encryptMessage, String keyStr) throws DecryptException {
		checkNotNull(encryptMessage);
		checkNotNull(keyStr);

		try {
			SecretKeySpec key = new SecretKeySpec(keyStr.toLowerCase().getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.getDecoder().decode(encryptMessage)));
		} catch (Exception e) {
			throw new DecryptException("DES.decrypt error.", e);
		}
	}
}
