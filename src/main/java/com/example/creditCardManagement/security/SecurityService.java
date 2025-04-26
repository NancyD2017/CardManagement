package com.example.creditCardManagement.security;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.model.entity.RefreshToken;
import com.example.creditCardManagement.model.request.LoginRequest;
import com.example.creditCardManagement.model.request.RefreshTokenRequest;
import com.example.creditCardManagement.model.request.UpsertCardHolderRequest;
import com.example.creditCardManagement.model.response.AuthResponse;
import com.example.creditCardManagement.model.response.RefreshTokenResponse;
import com.example.creditCardManagement.repository.CardHolderRepository;
import com.example.creditCardManagement.service.RefreshTokenService;
import com.example.creditCardManagement.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final CardHolderRepository cardHolderRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return AuthResponse.builder()
                .id(userDetails.getId())
                .token(jwtUtils.generateJwtToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
    }

    public CardHolder register(UpsertCardHolderRequest createUserRequest) {
        if (cardHolderRepository.findByEmail(createUserRequest.getEmail()).isPresent())
            throw new IllegalArgumentException("CardHolder with email " + createUserRequest.getEmail() + " already exists!");
        var user = CardHolder.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .email(createUserRequest.getEmail())
                .build();
        user.setRoles(createUserRequest.getRoles());

        return cardHolderRepository.save(user);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByRefreshToken(requestRefreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getId)
                .flatMap(cardHolderRepository::findById)
                .map(tokenOwner -> {
                    String token = jwtUtils.generateTokenFromUsername(tokenOwner.getUsername());
                    String refreshToken = refreshTokenService.createRefreshToken(tokenOwner.getId()).getToken();
                    return new RefreshTokenResponse(token, refreshToken);
                }).orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

    }
}
