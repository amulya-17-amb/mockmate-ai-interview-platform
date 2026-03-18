package com.mockmate.service;

import com.mockmate.config.JwtService;
import com.mockmate.dto.LoginRequest;
import com.mockmate.model.Candidate;
import com.mockmate.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginRequest.TokenResponse register(LoginRequest.Register request) {
        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        Candidate candidate = Candidate.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Candidate.Role.CANDIDATE)
                .build();
        candidate = candidateRepository.save(candidate);
        String token = jwtService.generateToken(candidate);
        return new LoginRequest.TokenResponse(token, candidate.getEmail(),
                candidate.getFullName(), candidate.getId());
    }

    public LoginRequest.TokenResponse login(LoginRequest.Login request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Candidate candidate = candidateRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtService.generateToken(candidate);
        return new LoginRequest.TokenResponse(token, candidate.getEmail(),
                candidate.getFullName(), candidate.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return candidateRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
