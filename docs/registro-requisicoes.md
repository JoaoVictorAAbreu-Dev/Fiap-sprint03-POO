# Registro de requisições — Sprint 04

Em 11/10/2026, `mvn test` executou `MotivaApiTest` com Spring Boot 4.1.1, MockMvc e banco H2 temporário em modo Oracle. Resultado do Surefire: **1 teste, 0 falhas, 0 erros, 0 ignorados**. Cada linha abaixo corresponde a uma requisição e uma asserção de status no teste; IDs são gerados pelo banco, portanto variam.

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

O teste completo está em `src/test/java/br/com/motiva/MotivaApiTest.java`. Este registro comprova o comportamento local MockMvc/H2; **não é evidência de conexão ao Oracle FIAP**.
