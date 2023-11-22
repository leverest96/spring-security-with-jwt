package com.example.test.security.oauth2.converter;

public interface ProviderUserConverter<T,R> {
    R convert(T t);
}
