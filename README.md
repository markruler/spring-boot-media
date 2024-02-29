# 파일 분할 업로드

## 구성

```shell
spring init \
  --boot-version=2.7.17 \
  --java-version=17 \
  --dependencies=web \
  --type=gradle-project \
  --group-id=com.example \
  --version=0.1.0 \
  spring-boot-chunk-file-upload
```

```shell
# fallocate -l 1G test.dat
fallocate -l 500M test.dat
```

### Self-Signed Certificate

동영상 압축 시 `ffmpeg.wasm`을 사용하는데 SharedArrayBuffer를 사용하기 위해 `https`로 서비스해야 함.

The Cross-Origin-Opener-Policy header has been ignored,
because the URL's origin was untrustworthy.
It was defined either in the final response or a redirect.
Please deliver the response using the HTTPS protocol.
You can also use the 'localhost' origin instead.
See https://www.w3.org/TR/powerful-features/#potentially-trustworthy-origin
and https://html.spec.whatwg.org/#the-cross-origin-opener-policy-header.

```shell

CLASSPATH=src/main/resources

openssl req \
  -x509 \
  -newkey rsa:4096 \
  -sha256 \
  -days 3650 \
  -nodes \
  -keyout $CLASSPATH/key.pem \
  -out $CLASSPATH/cert.pem \
  -subj "/CN=localhost"

openssl pkcs12 \
  -export \
  -in $CLASSPATH/cert.pem \
  -inkey $CLASSPATH/key.pem \
  -out $CLASSPATH/keystore.p12 \
  -name alias-name \
  -CAfile $CLASSPATH/cert.pem \
  -caname root
```

```yaml
# application.yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-type: PKCS12
    key-alias: alias-name
    key-store-password: test
```

## 실행

```shell
./gradlew bootRun
```

## 문제

- 모바일 웹 브라우저에서 파일 업로드 시 속도가 매우 느림.
    - 반면 PC 웹 브라우저에서 파일 업로드 시 속도가 빠름.
- Break point를 걸었을 때 `MultipartResolver`에 병목이 발생해서 Stream upload 방식도 추가해봤지만 동일함.
- `클라이언트에서 요청 전달이 멈춰버림.
    - 간혹 빠르면 `Controller`에 도달하기 전에 멈춰버림.

## 참조

- [Will Spring hold contents in memory or stores in the disk?](https://stackoverflow.com/questions/1952633) -
  Stack Overflow
- [Spring Boot에서 S3에 파일을 업로드하는 세 가지 방법](https://techblog.woowahan.com/11392/)
