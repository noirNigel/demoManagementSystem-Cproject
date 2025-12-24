package org.example.demomanagementsystemcproject.controller;

import org.example.demomanagementsystemcproject.dto.MemberProfileDTO;
import org.example.demomanagementsystemcproject.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    public ResponseEntity<MemberProfileDTO> getProfile(@RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.warn("缺少用户信息，无法返回会员数据");
            return ResponseEntity.badRequest().build();
        }
        Long userId = Long.valueOf(userIdHeader);
        return ResponseEntity.ok(memberService.getMemberProfile(userId));
    }
}
