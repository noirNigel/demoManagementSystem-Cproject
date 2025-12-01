package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.MemberLevelDTO;
import java.math.BigDecimal;
import java.util.List;

public interface MemberLevelService {

    List<MemberLevelDTO> getAllLevels();
    MemberLevelDTO getLevelById(Long id);
    MemberLevelDTO createLevel(MemberLevelDTO levelDTO);
    MemberLevelDTO updateLevel(Long id, MemberLevelDTO levelDTO);
    void deleteLevel(Long id);
    void updateLevelStatus(Long id, Integer status);

    MemberLevelDTO getUserLevel(Integer points);
    BigDecimal calculateMemberPrice(BigDecimal originalPrice, Integer userPoints);
    Integer calculateEarnedPoints(BigDecimal orderAmount, Integer userPoints);
}