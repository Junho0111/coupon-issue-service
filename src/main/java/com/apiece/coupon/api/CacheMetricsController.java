package com.apiece.coupon.api;

import com.apiece.coupon.api.dto.CacheMetricsResponse;
import com.apiece.coupon.application.CacheMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics/cache")
public class CacheMetricsController {

    private final CacheMetrics cacheMetrics;

    public CacheMetricsController(CacheMetrics cacheMetrics) {
        this.cacheMetrics = cacheMetrics;
    }

    @GetMapping
    public CacheMetricsResponse snapshot() {
        return CacheMetricsResponse.from(cacheMetrics.snapshot());
    }

    @PostMapping("/reset")
    public void reset() {
        cacheMetrics.reset();
    }
}

// 특정 지표를 메모리에 기록할거고 이 컨트롤러를 통해서 조회할것임