package com.example.jwtbearer;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.Base64;

/**
 * OAuth2.0 JWT Bearer Token フロー サンプル
 *
 * <p>
 * 依存例（pom.xml、build.gradleコメント参照）
 *
 * 実行方法：main()を実行
 *
 * 必要なもの：
 * - PEM形式のRSA秘密鍵ファイル（例：private_key.pem）
 *
 * Token EndpointはダミーURL（https://auth.example.com/oauth2/token）
 */
public class JwtBearerClient {
    // === 設定値 ===
    // 鍵ファイルパス
    private static final String PRIVATE_KEY_PEM = "private_key.pem";
    // 認可サーバーのトークンエンドポイント
    private static final String TOKEN_ENDPOINT = "https://auth.example.com/oauth2/token";
    // JWTクレーム
    private static final String ISSUER = "your-issuer";
    private static final String SUBJECT = "your-subject";
    private static final String AUDIENCE = TOKEN_ENDPOINT;

    public static void main(String[] args) throws Exception {
        // 1. 秘密鍵のロード
        RSAPrivateKey privateKey = loadPrivateKey(PRIVATE_KEY_PEM);

        // 2. JWT生成
        String jwt = createSignedJWT(privateKey);
        System.out.println("===== 送信JWT =====\n" + jwt + "\n");

        // 3. トークンエンドポイントへPOST
        Map<String, Object> tokenResponse = requestToken(jwt);

        // 4. レスポンス表示
        System.out.println("===== HTTPステータス =====\n" + tokenResponse.get("status") + "\n");
        System.out.println("===== レスポンスJSON =====\n" + tokenResponse.get("body") + "\n");

        // 5. access_tokenデコード
        String accessToken = (String) tokenResponse.get("access_token");
        if (accessToken != null) {
            decodeJwt(accessToken);
        } else {
            System.out.println("access_tokenがレスポンスに含まれていません");
        }
    }

    /**
     * PEM形式RSA秘密鍵ファイルを読み込み、RSAPrivateKeyに変換
     */
    private static RSAPrivateKey loadPrivateKey(String pemPath) throws Exception {
        String pem = Files.readString(Paths.get(pemPath));
        // PEMヘッダ・フッタ除去
        StringBuilder sb = new StringBuilder();
        for (String line : pem.split("\\R")) {
            if (!line.startsWith("-----")) {
                sb.append(line.trim());
            }
        }
        String base64 = sb.toString();
        byte[] der = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(keySpec);
    }

    /**
     * JWT生成・署名（iss, sub, aud, exp, iat）
     */
    private static String createSignedJWT(RSAPrivateKey privateKey) throws Exception {
        // JWTクレームセット
        long now = System.currentTimeMillis() / 1000L;
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER) // iss
                .subject(SUBJECT) // sub
                .audience(AUDIENCE) // aud
                .expirationTime(new Date((now + 300) * 1000)) // exp（5分後）
                .issueTime(new Date(now * 1000)) // iat
                .build();

        // ヘッダ＋署名
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claims);
        JWSSigner signer = new RSASSASigner(privateKey);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    /**
     * JWT Bearer TokenフローでトークンエンドポイントにPOST
     */
    private static Map<String, Object> requestToken(String jwt) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // リクエストボディ
        String body = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + jwt;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        Map<String, Object> result = new HashMap<>();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_ENDPOINT, entity, String.class);
            result.put("status", response.getStatusCode().value());
            result.put("body", response.getBody());
            // レスポンスJSONからaccess_token等を抽出
            ObjectMapper mapper = new ObjectMapper();
            Map<?,?> json = mapper.readValue(response.getBody(), Map.class);
            result.put("access_token", json.get("access_token"));
            result.put("token_type", json.get("token_type"));
            result.put("expires_in", json.get("expires_in"));
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("body", e.getMessage());
        }
        return result;
    }

    /**
     * JWT(access_token)のデコードとペイロード表示
     */
    private static void decodeJwt(String jwt) {
        try {
            // JWTはピリオド区切り（ヘッダ.ペイロード.署名）
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) {
                System.out.println("access_tokenはJWT形式ではありません");
                return;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            System.out.println("===== access_tokenデコード結果（ペイロード） =====\n" + payload + "\n");
        } catch (Exception e) {
            System.out.println("JWTデコード失敗: " + e.getMessage());
        }
    }
}
