package com.example.test.utility;

import com.example.test.security.oauth2.oauth2user.Attributes;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OAuth2Utils {
    public static Attributes getMainAttributes(final OAuth2User oAuth2User) {
        return Attributes.builder()
                .mainAttributes(oAuth2User.getAttributes())
                .build();
    }

    public static Attributes getSubAttributes(final OAuth2User oAuth2User, final String subAttributesKey) {
        final Map<String, Object> subAttributes = (Map<String, Object>) oAuth2User.getAttributes().get(subAttributesKey);

        return Attributes.builder()
                .subAttributes(subAttributes)
                .build();
    }

    public static Attributes getOtherAttributes(final OAuth2User oAuth2User,
                                                final String subAttributesKey,
                                                final String otherAttributesKey) {
        final Map<String, Object> subAttributes = (Map<String, Object>) oAuth2User.getAttributes().get(subAttributesKey);
        final Map<String, Object> otherAttributes = (Map<String, Object>) subAttributes.get(otherAttributesKey);

        return Attributes.builder()
                .subAttributes(subAttributes)
                .otherAttributes(otherAttributes)
                .build();
    }
}