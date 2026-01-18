package com.openlms.api.auth.services;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openlms.api.auth.domains.OtpPurpose;
import com.openlms.api.auth.domains.Role;
import com.openlms.api.auth.domains.User;
import com.openlms.api.auth.domains.UserOtp;
import com.openlms.api.auth.domains.VipStatus;
import com.openlms.api.auth.dtos.requests.LoginRequest;
import com.openlms.api.auth.dtos.requests.SignUpRequest;
import com.openlms.api.auth.dtos.requests.VerifyOtpRequest;
import com.openlms.api.auth.dtos.responses.AuthResponse;
import com.openlms.api.auth.repositories.UserOtpRepository;
import com.openlms.api.auth.repositories.UserRepository;
import com.openlms.api.commons.exceptions.DomainException;
import com.openlms.api.commons.exceptions.ErrorCode;
import com.openlms.api.commons.securities.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpGenerator otpGenerator;
    private final SendOtpService sendOtpService;
    private final JwtService jwtService;
    public AuthService (
        UserRepository userRepository,
        UserOtpRepository userOtpRepository,
        PasswordEncoder passwordEncoder,
        OtpGenerator otpGenerator,
        SendOtpService sendOtpService,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userOtpRepository = userOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpGenerator = otpGenerator;
        this.sendOtpService = sendOtpService;
        this.jwtService = jwtService;
    }

    @Transactional
    public void signUp(SignUpRequest request) {
        String email = request.email().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new DomainException(ErrorCode.CONFLICT, "Email already used");
        }

        Role role;
        try {
            role = Role.valueOf(request.role().toUpperCase());
        } catch (Exception e) {
            throw new DomainException(ErrorCode.BAD_REQUEST, "Invalid Role");
        }

        if (role.equals(Role.ADMIN)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Forbidden Action");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(role);
        user.setEmailVerified(false);
        user.setVipStatus(VipStatus.FREE);
        userRepository.save(user);
    }

    @Transactional
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim()).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "User Not Found"));
        if (user.isEmailVerified()) {
            return;
        }

        String otp = otpGenerator.generateOtp();

        UserOtp userOtp = new UserOtp();
        userOtp.setUser(user);
        userOtp.setPurpose(OtpPurpose.EMAIL_VERIFY);
        userOtp.setOtpHash(passwordEncoder.encode(otp));
        userOtp.setExpiresAt(OffsetDateTime.now().plusMinutes(5));
        userOtpRepository.save(userOtp);
        sendOtpService.sendOtpViaEmail(email, otp);
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase().trim()).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "User Not Found"));
        UserOtp userOtp = userOtpRepository.findTopByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), OtpPurpose.EMAIL_VERIFY).orElseThrow(() -> new DomainException(ErrorCode.BAD_REQUEST, "OTP Not Found"));

        if (userOtp.isUsed()) {
            throw new DomainException(ErrorCode.BAD_REQUEST, "OTP Already Used");
        }

        if (userOtp.isExpired()) {
            throw new DomainException(ErrorCode.BAD_REQUEST, "OTP Expired");
        }

        boolean match = passwordEncoder.matches(request.otp(), userOtp.getOtpHash());
        if (!match) {
            throw new DomainException(ErrorCode.BAD_REQUEST, "OTP Invalid");
        }

        userOtp.setUsedAt(OffsetDateTime.now());
        userOtpRepository.save(userOtp);

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase().trim()).orElseThrow(() -> new DomainException(ErrorCode.UNAUTHORIZED, "Invalid credentials"));
        
        if (user.getPasswordHash() == null) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }

        boolean match = passwordEncoder.matches(request.password(), user.getPasswordHash());
        if (!match) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }

        if (!user.isEmailVerified()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Email unverified");
        }

        String token = jwtService.issueToken(user.getId().toString(), user.getEmail(), user.getRole().name(), Map.of("vip", user.getVipStatus() == VipStatus.VIP));

        return AuthResponse.bearer(token);
    }
}
