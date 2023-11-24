# Spring Security with JWT

## 1. 목적
- Spring Security를 학습하기 위한 목적으로 파진 레포지토리

## 2. 내용
###    - 1차
- Spring Boot 3.x / Spring Security 6.x
- Spring Boot 3.x를 사용하기 위해 적용할 Security 코드 개발
- IntelliJ Ultimate를 사용하여 프론트엔드와 백엔드를 같은 포트로 실행
- Social Login 즉, oauth2를 사용하지 않고 Spring Security를 이용한 일반 로그인만 진행
- 23.11.15 2차를 진행한 뒤 추후 social login 및 redis를 적용한 코드로 develop 예정
- 23.11.22 2차를 기준으로 social login 도입 추후 redis 적용한 코드로 develop 예정
  - 가장 큰 차이 : yaml 파일의 ```client-authentication-method: POST``` 에서 ```client-authentication-method: client_secret_post```로 수정 필요
- 23.11.24 redis 추가 완료
  - 아직 요류 해결 중
###    - 2차
- Spring Boot 2.x / Spring Security 5.x (사실 확실하지 않음)
- Spring Boot 2.x를 사용하기 위해 적용할 Security 코드 개발
- IntelliJ Community를 사용하여 프론트엔드 포트 5173과 백엔드 포트 8080을 나누어 진행
- 일반 로그인 및 Social Login 즉, oauth2 모두 사용하여 Spring Security 코드 진행
- redis를 이용해보려 했지만 Cors 에러로 인해 시간이 지체되어 이후 1차 버전 develop 때 적용 예정

## 3. 세부 내용
자세한 내용은 코드에 기술
