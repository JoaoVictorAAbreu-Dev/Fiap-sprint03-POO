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

As contagens são um retrato do schema naquele momento e podem mudar. A execução remota foi somente de leitura: **CRUD e geração de relatório com escrita no Oracle não foram testados**. O processo local foi encerrado após as consultas.
