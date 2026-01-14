package com.shibakita.auth.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {

  private final String secret;

  public JwtService(@Value("${app.jwt.secretFile:}") String secretFile) {
    this.secret = readSecret(secretFile);
  }

  public String issueToken(String subject) {
    String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    long now = Instant.now().getEpochSecond();
    long exp = now + 3600;
    String payloadJson = "{\"sub\":\"" + escape(subject) + "\",\"iat\":" + now + ",\"exp\":" + exp + "}";

    String header = base64Url(headerJson.getBytes(StandardCharsets.UTF_8));
    String payload = base64Url(payloadJson.getBytes(StandardCharsets.UTF_8));
    String unsigned = header + "." + payload;

    String signature = hmacSha256(unsigned, secret);
    return unsigned + "." + signature;
  }

  private static String readSecret(String secretFile) {
    try {
      if (secretFile == null || secretFile.isBlank()) return "dev-secret";
      return Files.readString(Path.of(secretFile), StandardCharsets.UTF_8).trim();
    } catch (Exception e) {
      return "dev-secret";
    }
  }

  private static String hmacSha256(String data, String secret) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return base64Url(sig);
    } catch (Exception e) {
      throw new RuntimeException("JWT signing failed", e);
    }
  }

  private static String base64Url(byte[] bytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private static String escape(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}
