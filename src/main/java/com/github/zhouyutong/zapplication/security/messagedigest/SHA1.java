package com.github.zhouyutong.zapplication.security.messagedigest;

import com.google.common.base.Charsets;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <b>信息摘要算法SHA-1</b><br>
 *
 * @author ziroom
 * @thread-safe
 * @since 2015-10-31
 */
public final class SHA1 {
    private SHA1() {
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

        Hasher hasher = Hashing.sha1().newHasher();
        hasher.putString(message, Charsets.UTF_8);
        String digest = hasher.hash().toString();
        return digest;
    }
}
