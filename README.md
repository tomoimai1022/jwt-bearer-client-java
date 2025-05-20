# JWT Bearer Token Client Sample (Java)

このリポジトリは、**OAuth2.0 JWT Bearer Token認証フロー**の仕組みを学ぶためのJavaサンプルプロジェクトです。

> ⚡️ 本サンプルは Windsurf（AI Coding Assistant）によって自動生成されました。

---

## 特徴
- **Nimbus JOSE + JWT** ライブラリによるJWT生成・署名
- **Spring Web (RestTemplate)** でのトークンエンドポイントへのPOST
- PEM形式のRSA秘密鍵でJWT署名
- レスポンスの access_token（JWT）デコード＆中身出力
- コード内に詳細な解説コメント付き

---

## 構成ファイル
- `build.gradle` : Gradleビルド用設定
- `src/main/java/com/example/jwtbearer/JwtBearerClient.java` : サンプル本体
- `.gitignore` : ビルド生成物や秘密鍵等を除外
- `private_key.pem` : サンプル用RSA秘密鍵（Git管理除外）

---

## セットアップ・実行方法

### 1. 秘密鍵の生成
```sh
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

### 2. 依存取得＆ビルド
```sh
./gradlew build
```

### 3. サンプル実行
```sh
./gradlew run
```

---

## 注意
- デフォルトのトークンエンドポイントはダミーURLです。
- JWT Bearerフロー対応の認可サーバー（例: Google, Auth0, Okta, Keycloak等）でご利用ください。
- `private_key.pem` など秘密鍵は必ずGit管理対象外にしてください。

---

## 生成者
このリポジトリは Windsurf（AI Coding Assistant）によって自動生成されました。

- [Windsurf公式サイト](https://windsurf.ai/)
- [GitHubリポジトリ](https://github.com/windsurf-ai)

---

## ライセンス
MIT License
