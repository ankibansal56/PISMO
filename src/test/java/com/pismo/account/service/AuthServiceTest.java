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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRoles(Set.of("USER"));

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        user.setEnabled(true);
    }

    @Test
    @DisplayName("Should register user successfully with default USER role")
    void register_Success_WithDefaultRole() {
        // Arrange
        RegisterRequest requestWithoutRoles = new RegisterRequest();
        requestWithoutRoles.setUsername("newuser");
        requestWithoutRoles.setEmail("new@example.com");
        requestWithoutRoles.setPassword("password123");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        MessageResponse response = authService.register(requestWithoutRoles);

        // Assert
        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should register user successfully with specified roles")
    void register_Success_WithSpecifiedRoles() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        MessageResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void register_Fail_DuplicateUsername() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> authService.register(registerRequest)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_Fail_DuplicateEmail() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> authService.register(registerRequest)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully and return JWT token")
    void login_Success() {
        // Arrange
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername("testuser")
            .password("encodedPassword")
            .authorities(new SimpleGrantedAuthority("ROLE_USER"))
            .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        JwtResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found after authentication")
    void login_Fail_UserNotFound() {
        // Arrange
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername("testuser")
            .password("encodedPassword")
            .authorities(new SimpleGrantedAuthority("ROLE_USER"))
            .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("token");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Should create role when role does not exist")
    void register_CreateNewRole_WhenNotExists() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");

        registerRequest.setRoles(Set.of("ADMIN"));

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        MessageResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(roleRepository).save(any(Role.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle multiple roles during registration")
    void register_Success_WithMultipleRoles() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");

        registerRequest.setRoles(Set.of("USER", "ADMIN"));

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        MessageResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        verify(roleRepository).findByName("ROLE_USER");
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(userRepository).save(any(User.class));
    }
}
