package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.PointsRuleDTO;

public interface PointsRuleService {

    PointsRuleDTO getActiveRule();

    PointsRuleDTO getRule();

    PointsRuleDTO updateRule(PointsRuleDTO dto);
}
