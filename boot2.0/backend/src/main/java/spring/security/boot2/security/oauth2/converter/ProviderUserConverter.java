package spring.security.boot2.security.oauth2.converter;

// convert 메서드 사용을 위한 인터페이스
public interface ProviderUserConverter<T,R> {
    R convert(T t);
}
