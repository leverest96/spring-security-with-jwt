package spring.security.boot2.security.oauth2.converter;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import spring.security.boot2.models.users.ProviderUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// 일반 로그인의 filter에서 사용될 converter를 고르듯 oauth2에서 사용될 converter를 결정하는 클래스
@Component
public final class DelegatingProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    private final List<ProviderUserConverter<ProviderUserRequest, ProviderUser>> converters;

    public DelegatingProviderUserConverter() {
        final List<ProviderUserConverter<ProviderUserRequest, ProviderUser>> providerUserConverters = Arrays.asList(
                new OAuth2GoogleProviderUserConverter(),
                new OAuth2NaverProviderUserConverter(),
                new OAuth2KakaoProviderUserConverter()
        );

        this.converters = Collections.unmodifiableList(new LinkedList<>(providerUserConverters));
    }

    @Nullable
    @Override
    public ProviderUser convert(final ProviderUserRequest providerUserRequest) {
        Assert.notNull(providerUserRequest, "providerUserRequest cannot be null");

        for (final ProviderUserConverter<ProviderUserRequest, ProviderUser> converter : this.converters) {
            final ProviderUser providerUser = converter.convert(providerUserRequest);

            if (providerUser != null) {
                return providerUser;
            }
        }

        return null;
    }
}