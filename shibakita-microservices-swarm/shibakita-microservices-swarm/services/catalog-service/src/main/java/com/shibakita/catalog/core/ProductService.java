package com.shibakita.catalog.core;

import com.shibakita.catalog.api.ProductController.ProductDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ProductService {

  private final JdbcTemplate jdbc;
  private final StringRedisTemplate redis;

  public ProductService(JdbcTemplate jdbc, StringRedisTemplate redis) {
    this.jdbc = jdbc;
    this.redis = redis;
  }

  public ProductDto create(ProductDto dto) {
    jdbc.update(
        "INSERT INTO products(sku, name, price_cents) VALUES (?,?,?) ON CONFLICT (sku) DO UPDATE SET name=EXCLUDED.name, price_cents=EXCLUDED.price_cents",
        dto.sku(), dto.name(), dto.priceCents()
    );
    redis.delete(cacheKey(dto.sku()));
    return dto;
  }

  public List<ProductDto> list() {
    return jdbc.query("SELECT sku, name, price_cents FROM products ORDER BY id DESC",
        (rs, rowNum) -> new ProductDto(rs.getString("sku"), rs.getString("name"), rs.getInt("price_cents")));
  }

  public ProductDto getBySkuCached(String sku) {
    String key = cacheKey(sku);
    String cached = redis.opsForValue().get(key);
    if (cached != null) {
      return parse(cached);
    }

    List<ProductDto> res = jdbc.query("SELECT sku, name, price_cents FROM products WHERE sku=?",
        (rs, rowNum) -> new ProductDto(rs.getString("sku"), rs.getString("name"), rs.getInt("price_cents")),
        sku);

    if (res.isEmpty()) return null;

    ProductDto dto = res.get(0);
    redis.opsForValue().set(key, serialize(dto), Duration.ofSeconds(60));
    return dto;
  }

  private static String cacheKey(String sku) {
    return "product:" + sku;
  }

  private static String serialize(ProductDto dto) {
    return dto.sku() + "|" + dto.name().replace("|", " ") + "|" + dto.priceCents();
  }

  private static ProductDto parse(String s) {
    String[] parts = s.split("\\|", 3);
    return new ProductDto(parts[0], parts[1], Integer.parseInt(parts[2]));
  }
}
