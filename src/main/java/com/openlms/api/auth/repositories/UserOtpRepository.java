package com.openlms.api.auth.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openlms.api.auth.domains.OtpPurpose;
import com.openlms.api.auth.domains.UserOtp;

public interface UserOtpRepository extends JpaRepository<UserOtp, UUID> {
    Optional<UserOtp> findTopByUserIdAndPurposeOrderByCreatedAtDesc(UUID userId, OtpPurpose purpose);
}
