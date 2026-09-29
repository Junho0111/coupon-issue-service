package com.apiece.coupon.api;

import com.apiece.coupon.api.dto.IssuanceResponse;
import com.apiece.coupon.application.IssuanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/issuances")
public class UserIssuanceController {

    private final IssuanceService issuanceService;

    public UserIssuanceController(IssuanceService issuanceService) {
        this.issuanceService = issuanceService;
    }

    @GetMapping
    public List<IssuanceResponse> listMine(@RequestHeader("X-User-Id") Long userId) {
        return issuanceService.findByUser(userId).stream()
                .map(IssuanceResponse::from)
                .toList();
    }
}
