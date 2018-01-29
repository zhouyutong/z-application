package com.github.zhouyutong.zapplication.security.crypto;

import com.github.zhouyutong.zapplication.security.exception.DecryptException;
import com.github.zhouyutong.zapplication.security.exception.EncryptException;
import com.github.zhouyutong.zapplication.utils.HexByteTransformer;
import com.google.common.base.Charsets;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.NoSuchAlgorithmException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <b>对称加密DES算法工具</b><br>
 *
 * @author ziroom
 * @thread-safe
 * @since 2015-10-31
 */
public final class DES {
    //DESede/CBC/PKCS5Padding 第一段是加密算法的名称DES或DESede，第二段是分组加密的模式CBC和ECB,默认ECB，第三段是指最后一个分组的填充方式默认PKCS5Padding
    public static final String ALGORITHM_DES = "DES";

    private DES() {
    }

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
            DESKeySpec desKey = new DESKeySpec(keyStr.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            byte[] result = cipher.doFinal(message.getBytes(Charsets.UTF_8));
            return HexByteTransformer.bytes2Hex(result);
        } catch (Exception e) {
            throw new EncryptException("DES.encrypt error.", e);
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
            DESKeySpec desKey = new DESKeySpec(keyStr.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, securekey);
            byte[] result = cipher.doFinal(HexByteTransformer.hex2Bytes(encryptMessage));
            return new String(result, Charsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptException("DES.decrypt error.", e);
        }
    }

    /**
     * 生成一个密钥
     *
     * @return byte[] 密钥
     * @throws Exception
     */
    public static byte[] initSecretKey() throws NoSuchAlgorithmException {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_DES);
        //初始化此密钥生成器，使其具有确定的密钥大小
        kg.init(56);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }
}
