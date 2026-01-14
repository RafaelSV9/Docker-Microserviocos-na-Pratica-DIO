package com.shibakita.catalog.api;

import com.shibakita.catalog.core.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("ok");
  }

  @PostMapping
  public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
    return ResponseEntity.ok(service.create(dto));
  }

  @GetMapping
  public ResponseEntity<List<ProductDto>> list() {
    return ResponseEntity.ok(service.list());
  }

  @GetMapping("/{sku}")
  public ResponseEntity<ProductDto> get(@PathVariable String sku) {
    ProductDto dto = service.getBySkuCached(sku);
    return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
  }

  public record ProductDto(String sku, String name, Integer priceCents) {}
}
