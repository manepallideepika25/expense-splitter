package com.deepika.expense_splitter.auth;

import com.deepika.expense_splitter.exception.UnauthorizedException;
import com.deepika.expense_splitter.security.JwtService;
import com.deepika.expense_splitter.user.User;
import com.deepika.expense_splitter.user.UserRepository;
import com.deepika.expense_splitter.user.UserService;
import com.deepika.expense_splitter.user.dto.CreateUserRequest;
import com.deepika.expense_splitter.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public UserResponse register(CreateUserRequest request)
    {
        return userService.createUser(request);
    }
    public AuthResponse login(LoginRequest request)
    {
        User user=userRepository.findByEmail(request.email())
                .orElseThrow(()->new UnauthorizedException("Invalid email or password"));
        if(!passwordEncoder.matches(request.password(),user.getPassword()))
            throw new UnauthorizedException("Invalid email or password");
        String token=jwtService.generateToken(user);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(),
                new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }
}
