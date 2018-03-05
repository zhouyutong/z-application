package com.zhouyutong.zapplication.security.messagedigest;

import com.google.common.base.Charsets;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <b>信息摘要算法MD5</b><br>
 *
 * @author ziroom
 * @thread-safe
 * @since 2015-10-31
 */
public final class MD5 {
    private MD5() {
    }

    /**
     * 生成十六进制编码的摘要串
     *
     * @param message
     * @return - hex string
     * @throws NullPointerException
     */
    public static String generateDigestString(String message) {
        checkNotNull(message);

        Hasher hasher = Hashing.md5().newHasher();
        hasher.putString(message, Charsets.UTF_8);
        String digest = hasher.hash().toString();
        return digest;
    }
}
