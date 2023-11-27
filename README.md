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
