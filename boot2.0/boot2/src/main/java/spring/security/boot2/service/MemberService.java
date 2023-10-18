package spring.security.boot2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.security.boot2.domain.Member;
import spring.security.boot2.domain.oauth.ProviderUser;
import spring.security.boot2.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void checkCertification(ProviderUser providerUser) {
        Member member = memberRepository.findByUsername(providerUser.getId());

        boolean bool = providerUser.getProvider().equals("none") || providerUser.getProvider().equals("naver");

        providerUser.isCertificated(bool);
    }

    public void register(ProviderUser providerUser) {
        Member member = Member.builder()
                .id(providerUser.getId())
                .username(providerUser.getUsername())
                .password(providerUser.getPassword())
                .authorities(providerUser.getAuthorities())
                .provider(providerUser.getProvider())
                .email(providerUser.getEmail())
                .picture(providerUser.getPicture())
                .build();

        memberRepository.save(member);
    }
}
