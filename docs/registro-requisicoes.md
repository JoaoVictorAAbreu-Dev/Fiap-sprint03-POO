# Registro de requisições — Sprint 04

Em 11/10/2026, `mvn clean verify` executou `MotivaApiTest` e `RelatorioEspecialidadeTest` com Java 17.0.20.1, Spring Boot 4.1.1, MockMvc e banco H2 temporário em modo Oracle. O teste carrega o script original `sql/schema-oracle.sql` e faz `ddl-auto=validate`. Resultado do Surefire: **2 testes, 0 falhas, 0 erros, 0 ignorados**. Cada linha abaixo corresponde a uma requisição e uma asserção de status no teste; IDs são gerados pelo banco, portanto variam.

| Requisição | Status verificado | Evidência adicional |
|---|---:|---|
| `POST /api/equipes` | 201 | três equipes cadastradas |
| `POST /api/trechos` | 201 | trecho criado |
| `GET /api/trechos/{id}` | 200 | quilômetro 10 |
| `PUT /api/trechos/{id}` | 200 | quilômetro 11 |
| `GET /api/trechos/999999` | 404 | item ausente |
| `POST /api/trechos` com altura -1 | 400 | rejeição de entrada inválida |
| `POST /api/relatorios` | 201 | `qtCritica=1` e intervenção persistida |
| `GET /api/relatorios` | 200 | histórico contém relatório |
| `GET /api/relatorios/periodo` | 200 | período contém relatório |
| `GET /api/intervencoes` | 200 | equipe Alpha vinculada à intervenção |
| `DELETE /api/intervencoes/{id}` | 204 | intervenção removida |
| `DELETE /api/trechos/{id}` | 204 | trecho removido |

Os testes estão em `src/test/java/br/com/motiva/MotivaApiTest.java` e `src/test/java/br/com/motiva/RelatorioEspecialidadeTest.java`. Eles verificam consultas derivadas, rejeição quando falta uma equipe ativa e escolha por especialidade com nomes livres.

## Execução HTTP local

A aplicação foi iniciada em `localhost:18080` com H2 no classpath de teste, `sql/schema-oracle.sql` e `ddl-auto=validate`. Uma sequência automatizada de chamadas HTTP reais, incluindo uma equipe com nome diferente do seed e especialidade `ROCADA MECANIZADA`, retornou:

```text
POST /api/equipes -> 201 (3 vezes)
POST /api/trechos -> 201
GET /api/trechos/1 -> 200
PUT /api/trechos/1 -> 200
POST /api/relatorios -> 201 (qtCritica=1)
GET /api/relatorios -> 200
GET /api/relatorios/periodo -> 200
GET /api/intervencoes -> 200 (intervenção ligada à equipe de especialidade ROCADA MECANIZADA)
GET /api/trechos/999999 -> 404
POST /api/trechos com altura -1 -> 400
DELETE /api/intervencoes/1 -> 204
DELETE /api/trechos/1 -> 204
RESULTADO: 14 requisições verificadas, 0 falhas
```

Este registro comprova o comportamento local MockMvc/H2 e HTTP/H2; **não é evidência de conexão ao Oracle FIAP**.
