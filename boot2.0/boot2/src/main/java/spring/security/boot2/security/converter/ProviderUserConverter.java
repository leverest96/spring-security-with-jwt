package spring.security.boot2.security.converter;

public interface ProviderUserConverter<T,R> {
    R convert(T t);
}
