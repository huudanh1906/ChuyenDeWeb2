package com.example.security;

import com.example.entity.User;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);

        // Let users login with either username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with username or email: {}", usernameOrEmail);
                    return new UsernameNotFoundException(
                            "Người dùng không tồn tại với tên đăng nhập hoặc email: " + usernameOrEmail);
                });

        logger.debug("User found: ID={}, Username={}, Status={}", user.getId(), user.getUsername(), user.getStatus());

        if (user.getStatus() != 1) {
            logger.warn("Attempt to login with deactivated account: {}", usernameOrEmail);
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
        }

        return UserPrincipal.create(user);
    }

    // This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id) {
        logger.debug("Loading user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });

        logger.debug("User found by ID: Username={}, Status={}", user.getUsername(), user.getStatus());

        if (user.getStatus() != 1) {
            logger.warn("Attempt to authenticate with deactivated account ID: {}", id);
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
        }

        return UserPrincipal.create(user);
    }
}