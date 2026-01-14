package com.shibakita.auth.api;

import com.shibakita.auth.core.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

  private final JwtService jwtService;

  public AuthController(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("ok");
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
    if (req.username() == null || req.username().isBlank() || req.password() == null || req.password().isBlank()) {
      return ResponseEntity.badRequest().body(new LoginResponse(null, "invalid_credentials"));
    }

    String token = jwtService.issueToken(req.username());
    return ResponseEntity.ok(new LoginResponse(token, "ok"));
  }

  public record LoginRequest(String username, String password) {}
  public record LoginResponse(String token, String status) {}
}
