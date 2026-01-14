# ☁️ Shibakita Microservices on Docker Swarm (Java / Spring Boot)
Projeto prático inspirado na história do **Toshiro Shibakita**: sair do “on-prem / nuvem privada cara de manter” e migrar para uma arquitetura moderna com **microsserviços** e **containers**, ganhando escalabilidade, independência entre aplicações e infraestrutura.

## 🧱 Arquitetura
- **Nginx (Gateway)**: entrada única HTTP
- **auth-service (Spring Boot)**: login e token (JWT)
- **catalog-service (Spring Boot)**: catálogo de produtos (PostgreSQL + cache Redis)
- **orders-service (Spring Boot)**: pedidos (PostgreSQL)
- **Docker Swarm**: orquestração, replicas, rolling update

Rotas (via gateway):
- `POST /auth/login`
- `GET /catalog/products`
- `POST /catalog/products`
- `GET /catalog/products/{sku}`
- `GET /orders/orders`
- `POST /orders/orders`

## ✅ Pré-requisitos
- Docker Desktop / Docker Engine
- (Opcional) Java 17 e Maven (se for rodar sem container)

## 🚀 Como rodar no Docker Swarm
### 1) Iniciar Swarm
```bash
bash scripts/init-swarm.sh
```

### 2) Build e deploy do stack
```bash
bash scripts/deploy.sh
```

Acesse:
- Gateway health: `http://localhost/health`
- Login: `POST http://localhost/auth/login`
- Catalog: `http://localhost/catalog/products`
- Orders: `http://localhost/orders/orders`

## 🔐 Exemplo de login
```bash
curl -X POST http://localhost/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"rafael","password":"123"}'
```

## 🧪 Teste rápido
```bash
# criar produto
curl -X POST http://localhost/catalog/products \
  -H "Content-Type: application/json" \
  -d '{"sku":"SKU-001","name":"Máquina de corte","priceCents":19990}'

# listar produtos
curl http://localhost/catalog/products

# criar pedido
curl -X POST http://localhost/orders/orders \
  -H "Content-Type: application/json" \
  -d '{"customer":"Rafael","sku":"SKU-001","qty":1}'

# listar pedidos
curl http://localhost/orders/orders
```

## 🧩 Roadmap (evoluções)
- [ ] Traefik + HTTPS
- [ ] Autenticação real com DB
- [ ] Observabilidade (Prometheus + Grafana)
- [ ] CI/CD (GitHub Actions)
