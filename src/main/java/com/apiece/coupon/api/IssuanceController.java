package com.apiece.coupon.api;

import com.apiece.coupon.api.dto.IssuanceResponse;
import com.apiece.coupon.application.IssuanceService;
import com.apiece.coupon.domain.Issuance;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issuances")
public class IssuanceController {

    private final IssuanceService issuanceService;

    public IssuanceController(IssuanceService issuanceService) {
        this.issuanceService = issuanceService;
    }

    @PostMapping("/{issuanceId}/use")
    public IssuanceResponse use(
            @PathVariable Long issuanceId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        Issuance issuance = issuanceService.use(issuanceId, userId);
        return IssuanceResponse.from(issuance);
    }
}
