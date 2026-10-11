# Validação no Oracle FIAP — 11/10/2026

A validação foi feita com as credenciais do integrante fornecidas para esta execução, passadas somente como variáveis de ambiente ao processo local. Nenhuma senha foi incluída no código, neste registro ou no Git.

## Resultado

- Conexão TCP com `oracle.fiap.com.br:1521`: estabelecida.
- Autenticação JDBC Thin com o SID `ORCL`: bem-sucedida no schema acadêmico.
- Banco identificado pelo Hibernate: Oracle Database **19.3**; driver `ojdbc17` via Maven.
- Tabelas presentes no schema: `TB_EQUIPE_MANUTENCAO`, `TB_TRECHO_RODOVIA`, `TB_INTERVENCAO_OPERACIONAL`, `TB_RELATORIO_PRIORIDADE`.
- Spring Boot **4.1.1** iniciou com **Java 17.0.20.1** e `spring.jpa.hibernate.ddl-auto=validate`; o Hibernate validou as entidades contra as tabelas reais sem erro.
- A API foi iniciada localmente e fez as consultas de leitura abaixo no Oracle real.

| Requisição | HTTP | Registros observados |
|---|---:|---:|
| `GET /api/equipes` | 200 | 3 |
| `GET /api/trechos` | 200 | 6 |
| `GET /api/intervencoes` | 200 | 10 |
| `GET /api/relatorios` | 200 | 2 |
| `GET /api/relatorios/periodo?inicio=2020-01-01T00:00:00&fim=2030-01-01T00:00:00` | 200 | 2 |

## Escrita transacional no Oracle

`OracleWriteIntegrationTest` foi executado de forma opt-in com Java 17.0.20.1 e as mesmas credenciais, usando `@SpringBootTest`, MockMvc, `@Transactional` e `@Rollback`. Resultado: **1 teste, 0 falhas, 0 erros**. O teste verificou no Oracle real:

- `POST`, `GET`, `PUT` e `DELETE` de equipe, trecho e intervenção temporários, com status 201/200/204 e valores atualizados;
- `POST /api/relatorios` com status 201, um relatório adicional e intervenções geradas visíveis dentro da transação;
- rollback ao terminar o teste. Uma nova conexão JDBC confirmou as contagens **antes e depois**: 3 equipes, 6 trechos, 10 intervenções e 2 relatórios.

O teste é ignorado no `mvn test` comum; para executá-lo, configure `DB_URL`, `DB_USER` e `DB_PASSWORD` no ambiente e rode `FIAP_ORACLE_TEST=true mvn -Dtest=OracleWriteIntegrationTest test`. Em 11/10/2026, as quatro sequences nomeadas foram criadas no schema Oracle FIAP já existente, após conferir os maiores IDs (3 equipes, 6 trechos, 3 relatórios e 11 intervenções). Em seguida, `OracleWriteIntegrationTest` passou no banco real: 1 teste, 0 falhas, 0 erros e 0 ignorados. A migração não recriou tabelas nem modificou os registros existentes. O banco pode avançar os contadores das sequences mesmo com rollback, sem deixar os registros de QA. As contagens anteriores são um retrato do schema naquele momento e podem mudar. O processo local da API foi encerrado após as consultas.
