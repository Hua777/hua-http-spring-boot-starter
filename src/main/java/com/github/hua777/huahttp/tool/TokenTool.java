package com.github.hua777.huahttp.tool;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class TokenTool {

    /**
     * @param key                     算法密码
     * @param iss                     发行人
     * @param sub                     主题
     * @param issuedAtTimeThresholdMs 微调 Token 开始时间
     * @param validityPeriodMs        有效持续时间
     * @return Token
     * @throws UnsupportedEncodingException 兼容低版本
     */
    public static String createJWTByHMAC256(String key, String iss, String sub, long issuedAtTimeThresholdMs, long validityPeriodMs) throws UnsupportedEncodingException {
        String token = "";
        Algorithm algorithm = Algorithm.HMAC256(key);
        long timestampNow = System.currentTimeMillis();
        Date exp = new Date(timestampNow + validityPeriodMs);
        Date iat = new Date(timestampNow + issuedAtTimeThresholdMs);
        Date nbf = new Date(timestampNow - validityPeriodMs);
        token = JWT.create().withIssuer(iss)
                .withSubject(sub)
                .withExpiresAt(exp)
                .withIssuedAt(iat)
                .withNotBefore(nbf)
                .sign(algorithm);
        return token;
    }

    /**
     * HMAC256 算法解析 JSON Web Token
     *
     * @param key   算法密码
     * @param iss   发行人
     * @param sub   主题
     * @param token Token
     * @return 解析结果
     * @throws UnsupportedEncodingException 兼容低版本
     */
    public static DecodedJWT verifierJWTByHMAC256(String key, String iss, String sub, String token) throws JWTVerificationException, UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(iss)
                .withSubject(sub)
                .build();
        return verifier.verify(token);
    }
}
