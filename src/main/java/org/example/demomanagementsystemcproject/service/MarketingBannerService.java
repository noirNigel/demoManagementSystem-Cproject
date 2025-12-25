package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.MarketingBannerDTO;

import java.util.List;

public interface MarketingBannerService {
    List<MarketingBannerDTO> getAllBanners();

    List<MarketingBannerDTO> getActiveBanners();

    MarketingBannerDTO getBannerById(Long id);

    MarketingBannerDTO createBanner(MarketingBannerDTO bannerDTO);

    MarketingBannerDTO updateBanner(Long id, MarketingBannerDTO bannerDTO);

    void deleteBanner(Long id);

    void updateBannerStatus(Long id, Integer status);
}
