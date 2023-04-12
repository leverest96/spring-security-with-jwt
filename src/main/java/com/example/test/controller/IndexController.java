package com.example.test.controller;

import com.example.test.security.userdetails.MemberDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String directIndexPage() {
        return "index";
    }

    @GetMapping("/register")
    // @AuthenticationPrincipal 어노테이션을 적용하면 SecurityContext에 저장된 Authentication의 Principal을 해당 파라미터에 주입
    public String directRegisterPage(@AuthenticationPrincipal final MemberDetails memberDetails) {
        if (memberDetails != null) {
            return "redirect:/";
        }

        return "register";
    }

    @GetMapping("/login")
    // @AuthenticationPrincipal 어노테이션을 적용하면 SecurityContext에 저장된 Authentication의 Principal을 해당 파라미터에 주입
    public String directLoginPage(@AuthenticationPrincipal final MemberDetails memberDetails) {
        if (memberDetails != null) {
            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/member")
    // @AuthenticationPrincipal 어노테이션을 적용하면 SecurityContext에 저장된 Authentication의 Principal을 해당 파라미터에 주입
    public String directMemberPage(@AuthenticationPrincipal final MemberDetails memberDetails,
                                   final Model model) {
        model.addAttribute("email", memberDetails.getEmail());
        model.addAttribute("nickname", memberDetails.getNickname());

        return "member";
    }

    @GetMapping("/admin")
    // @AuthenticationPrincipal 어노테이션을 적용하면 SecurityContext에 저장된 Authentication의 Principal을 해당 파라미터에 주입
    public String directAdminPage(@AuthenticationPrincipal final MemberDetails memberDetails,
                                  final Model model) {
        model.addAttribute("email", memberDetails.getEmail());
        model.addAttribute("nickname", memberDetails.getNickname());

        return "admin";
    }
}