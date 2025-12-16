package org.example.demomanagementsystemcproject.controller;

import org.example.demomanagementsystemcproject.dto.MarketingBannerDTO;
import org.example.demomanagementsystemcproject.service.MarketingBannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 面向前台/小程序的轮播图接口，返回已启用并在有效期内的轮播图列表。
 */
@RestController
@RequestMapping("/api/banners")
public class PublicBannerController {

    private final MarketingBannerService marketingBannerService;

    public PublicBannerController(MarketingBannerService marketingBannerService) {
        this.marketingBannerService = marketingBannerService;
    }

    /**
     * 默认返回已启用的轮播图（等同于 /api/banners/active），便于前台和小程序直接调用。
     */
    @GetMapping
    public ResponseEntity<List<MarketingBannerDTO>> listActiveBanners() {
        return ResponseEntity.ok(marketingBannerService.getActiveBanners());
    }

    /**
     * 明确的 active 路径，兼容老的前端/小程序实现。
     */
    @GetMapping("/active")
    public ResponseEntity<List<MarketingBannerDTO>> listActiveBannersExplicit() {
        return ResponseEntity.ok(marketingBannerService.getActiveBanners());
    }
}
