package com.pismo.account.service;

import com.pismo.account.domain.entity.Role;
import com.pismo.account.domain.entity.User;
import com.pismo.account.dto.request.LoginRequest;
import com.pismo.account.dto.request.RegisterRequest;
import com.pismo.account.dto.response.JwtResponse;
import com.pismo.account.dto.response.MessageResponse;
import com.pismo.account.exception.DuplicateResourceException;
import com.pismo.account.repository.RoleRepository;
import com.pismo.account.repository.UserRepository;
import com.pismo.account.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MessageResponse register(RegisterRequest registerRequest) {
        log.info("Registering new user: {}", registerRequest.getUsername());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        
        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            registerRequest.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName("ROLE_" + roleName.toUpperCase());
                            return roleRepository.save(newRole);
                        });
                roles.add(role);
            });
        } else {
            // Default role
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });
            roles.add(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        log.info("User registered successfully: {}", user.getUsername());
        return new MessageResponse("User registered successfully");
    }

    public JwtResponse login(LoginRequest loginRequest) {
        log.info("User login attempt: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in successfully: {}", loginRequest.getUsername());

        return new JwtResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }
}
