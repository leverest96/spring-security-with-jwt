package com.example.test.security.authentication;

// AuthenticationManager과 AuthenticationProvider 중 무엇을 구현해야할지에 대한 chatgpt의 답변
// AuthenticationManager은 AuthenticationProvider의 집합체입니다.
// 즉, AuthenticationProvider를 등록하고, 등록된 AuthenticationProvider 중 하나를 사용하여 Authentication을 처리하는 역할을 합니다.
// 따라서, AuthenticationManager를 직접 커스터마이징하지 않고,
// 필요한 경우에는 AuthenticationProvider를 직접 커스터마이징하여 AuthenticationManager에 등록하여 사용할 수 있습니다.
// 이를 통해, 해당 AuthenticationProvider에서만 처리하고자 하는 로직을 구현하고,
// 다른 AuthenticationProvider에서는 그대로 사용하면서 AuthenticationManager의 역할을 충실하게 수행할 수 있습니다.
// 따라서, AuthenticationProvider를 커스터마이징하는 것만으로도 대부분의 경우 충분합니다.
// 하지만, AuthenticationManager를 커스터마이징해야 하는 특별한 경우가 있을 수도 있습니다.
// 예를 들어, AuthenticationProvider의 구성이 복잡하고 다양한 경우 등이 그러한 예시가 될 수 있습니다.

//@RequiredArgsConstructor
//@Configuration
//public class MemberAuthenticationManager implements AuthenticationManager {
//    private final List<MemberAuthenticationProvider> authenticationProviders;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        AuthenticationException lastException = null;
//
//        // Authentication 객체를 받아서, 해당 객체를 처리할 수 있는 AuthenticationProvider를 찾아서 인증을 수행
//        for (MemberAuthenticationProvider authenticationProvider : authenticationProviders) {
//            // supports 메서드로 어떤 Authentication 객체를 처리할 수 있는지 확인
//            if (!authenticationProvider.supports(authentication.getClass())) {
//                continue;
//            }
//
//            try {
//                // Authentication 객체를 찾아 인증을 시도
//                Authentication result = authenticationProvider.authenticate(authentication);
//
//                // 인증이 성공하면 Authentication 객체를 반환
//                if (result != null) {
//                    return result;
//                }
//            } catch (AuthenticationException e) {
//                lastException = e;
//            }
//        }
//
//        // 모든 AuthenticationProvider에서 인증이 실패한 경우, 마지막 예외를 기반으로 AuthenticationException 발생
//        if (lastException != null) {
//            throw lastException;
//        } else {
//            // 지정된 Authentication 클래스를 지원하는 AuthenticationProvider를 찾을 수 없을 때 발생하는 예외
//            throw new ProviderNotFoundException("해당하는 AuthenticationProvider가 없습니다. Authentication : " + authentication.getClass());
//        }
//    }
//}