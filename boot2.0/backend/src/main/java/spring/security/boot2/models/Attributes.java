package spring.security.boot2.models;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

// oauth2 로그인시 attribute들에 대한 처리를 위한 클래스
@Getter
@Builder
public class Attributes {
    private Map<String, Object> mainAttributes;
    private Map<String, Object> subAttributes;
    private Map<String, Object> otherAttributes;
}