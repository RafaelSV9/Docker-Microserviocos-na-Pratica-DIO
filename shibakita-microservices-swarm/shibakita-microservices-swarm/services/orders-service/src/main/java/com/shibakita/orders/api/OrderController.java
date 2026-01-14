package com.shibakita.orders.api;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final JdbcTemplate jdbc;

  public OrderController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("ok");
  }

  @PostMapping
  public ResponseEntity<OrderDto> create(@RequestBody OrderDto dto) {
    jdbc.update("INSERT INTO orders(customer, sku, qty) VALUES (?,?,?)",
        dto.customer(), dto.sku(), dto.qty());
    return ResponseEntity.ok(dto);
  }

  @GetMapping
  public ResponseEntity<List<OrderDto>> list() {
    var res = jdbc.query("SELECT customer, sku, qty FROM orders ORDER BY id DESC",
        (rs, rowNum) -> new OrderDto(rs.getString("customer"), rs.getString("sku"), rs.getInt("qty")));
    return ResponseEntity.ok(res);
  }

  public record OrderDto(String customer, String sku, Integer qty) {}
}
