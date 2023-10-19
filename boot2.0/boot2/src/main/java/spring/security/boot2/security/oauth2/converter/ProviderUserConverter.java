package spring.security.boot2.security.oauth2.converter;

public interface ProviderUserConverter<T,R> {
    R convert(T t);
}
