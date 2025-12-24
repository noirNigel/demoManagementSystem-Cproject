package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.MemberProfileDTO;

public interface MemberService {

    MemberProfileDTO getMemberProfile(Long userId);
}
