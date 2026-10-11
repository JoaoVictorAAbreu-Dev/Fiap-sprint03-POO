# MOTIVA — Sprint 04: Spring Boot, JPA e API REST

Evolução do projeto da Sprint 03. A API preserva as quatro tabelas Oracle e o motor de prioridade da Sprint 02. A interface é JSON; a antiga camada JDBC/console foi substituída por `Controller → Service → Repository → JPA`.

## Integrantes

| Nome | RM |
|---|---:|
| João Victor Alves de Abreu | 564946 |
| Luiz Henrique Barbosa Dias | 562399 |
| Rodrigo Kenshin Viana Matayoshi | 564026 |

## Requisitos e execução

- Java 17+; Maven 3.6.3+; Spring Boot 4.1.1.
- Acesso ao schema Oracle FIAP com as tabelas da Sprint 03.
- Configure `DB_URL`, `DB_USER` e `DB_PASSWORD` no ambiente. Exemplo:

```bash
export DB_URL='jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL'
export DB_USER='SEU_USUARIO'
export DB_PASSWORD='SUA_SENHA'
mvn spring-boot:run
```

A aplicação usa `spring.jpa.hibernate.ddl-auto=validate`: ela confere o esquema e não cria nem apaga tabelas. Para um schema vazio, rode `sql/schema-oracle.sql` e, se desejar, `sql/dados-exemplo.sql` uma vez no Oracle. O script de criação é o da Sprint 03 e usa `IDENTITY`. Por isso as entidades usam `@GeneratedValue(strategy = IDENTITY)`, em vez de `@SequenceGenerator`: **não existem sequences nomeadas nesse schema**. Trocar para uma sequence sem migrar o banco quebraria o reaproveitamento exigido. O Oracle gerencia a sequence interna de cada coluna identity.

As credenciais ficam somente em variáveis de ambiente. O driver `ojdbc17` vem do Maven. A URL padrão usa o SID `ORCL` informado no enunciado (`@host:porta:SID`); se o seu acesso usa um service name, configure `DB_URL` como `jdbc:oracle:thin:@//host:porta/servico`. Usuário e senha não têm valor padrão. O mapeamento usa os tipos JDBC `NUMERIC` para as colunas Oracle `NUMBER` e `CHAR` para as flags `S/N`, mantendo `ddl-auto=validate` compatível com o script existente.

## Arquitetura e regras

- `model`: entidades JPA das tabelas `TB_EQUIPE_MANUTENCAO`, `TB_TRECHO_RODOVIA`, `TB_INTERVENCAO_OPERACIONAL`, `TB_RELATORIO_PRIORIDADE`; o domínio de trecho, IoT e enums foi reaproveitado.
- `repository`: interfaces `JpaRepository` e consultas derivadas, como `findByAlturaVegetacaoCmGreaterThanEqual`, `findByTipoIntervencao` e `findByDataGeracaoBetweenOrderByDataGeracaoDesc`.
- `service`: validações, CRUD e `MotorRegrasPrioridade`. A geração de relatório e intervenções ocorre numa transação.
- `controller`: rotas HTTP e códigos de resposta. Requisições usam DTOs com Bean Validation; erros comuns são tratados por `@ControllerAdvice`.

O motor usa a **altura projetada** (`altura atual × fator de crescimento`, acrescida de 10 cm para área sensível): abaixo de 40 cm `NORMAL`; de 40 a 59,99 cm `MEDIA`/pulverização; de 60 a 89,99 cm `ALTA`/roçada manual; a partir de 90 cm `CRITICA`/roçada mecanizada. `BAIXA` permanece no histórico por compatibilidade com a Sprint 03. Intervenções geradas exigem equipes ativas com os nomes `Equipe Alpha`, `Equipe Beta` e `Equipe Preventiva`, conforme o tipo; se faltar uma equipe necessária, a transação é desfeita.

## Endpoints

| Método | Rota | Resultado |
|---|---|---|
| GET | `/api/trechos`, `/api/equipes`, `/api/intervencoes` | Lista, 200 |
| GET | `/api/trechos/{id}`, `/api/equipes/{id}`, `/api/intervencoes/{id}` | Item, 200 ou 404 |
| POST | `/api/trechos`, `/api/equipes`, `/api/intervencoes` | Cria, 201 e `Location` |
| PUT | `/api/trechos/{id}`, `/api/equipes/{id}`, `/api/intervencoes/{id}` | Atualiza, 200 ou 404 |
| DELETE | `/api/trechos/{id}`, `/api/equipes/{id}`, `/api/intervencoes/{id}` | Remove, 204 ou 404 |
| POST | `/api/relatorios` | Analisa trechos, grava relatório e intervenções, 201 |
| GET | `/api/relatorios`, `/api/relatorios/{id}` | Histórico, 200 ou 404 no item |
| GET | `/api/relatorios/periodo?inicio=...&fim=...` | Histórico inclusivo entre datas ISO 8601, 200 |

Dados inválidos retornam 400. Violações de unicidade ou outras restrições do banco retornam 409. A exclusão de equipe ou trecho referenciado por intervenção retorna 400 com mensagem; exclua as intervenções antes.

## Exemplos cURL

```bash
curl -i -X POST http://localhost:8080/api/equipes -H 'Content-Type: application/json' -d '{"nome":"Equipe Alpha","especialidade":"Mecanizada","ativa":true}'
curl -i -X POST http://localhost:8080/api/trechos -H 'Content-Type: application/json' -d '{"quilometro":10,"sentido":"NORTE","alturaVegetacaoCm":100,"tipoCrescimento":"NORMAL","areaSensivel":false,"monitoradoIot":false,"codigoSensor":null}'
curl -i http://localhost:8080/api/trechos/1
curl -i -X PUT http://localhost:8080/api/trechos/1 -H 'Content-Type: application/json' -d '{"quilometro":11,"sentido":"NORTE","alturaVegetacaoCm":100,"tipoCrescimento":"NORMAL","areaSensivel":false,"monitoradoIot":false,"codigoSensor":null}'
curl -i -X POST http://localhost:8080/api/relatorios
curl -i 'http://localhost:8080/api/relatorios/periodo?inicio=2026-01-01T00:00:00&fim=2026-12-31T23:59:59'
curl -i -X DELETE http://localhost:8080/api/trechos/1
```

No exemplo de relatório, cadastre as equipes necessárias antes de chamar `POST /api/relatorios`. O ID real pode diferir de `1`.

## Testes e registro de requisições

```bash
mvn test
mvn package
```

`MotivaApiTest` executa a API com `@SpringBootTest` + MockMvc e H2 em modo Oracle. O teste carrega **o próprio `sql/schema-oracle.sql` da Sprint 03** e inicia com `ddl-auto=validate`, para detectar divergências entre as entidades JPA e as colunas existentes. Verifica CRUD, consultas derivadas, relatório, histórico, período, validação 400 e ausência 404.

No QA de 11/10/2026, `mvn clean verify` passou com **Java 17.0.20.1** (1 teste, 0 falhas/erros). A aplicação também subiu em HTTP local com esse schema em H2 e respondeu corretamente a **14 requisições** de CRUD, relatório e erros, registradas em [registro das requisições](docs/registro-requisicoes.md). O ambiente não tem `DB_USER`/`DB_PASSWORD` do Oracle FIAP; **a conexão e a execução contra o Oracle remoto ainda não foram verificadas**. Para essa validação final, configure as variáveis acima e execute `mvn spring-boot:run`; `ddl-auto=validate` impede alterações automáticas no schema.

## Perguntas de reflexão

**Por que Repository é interface?** Declaramos o contrato (`JpaRepository` e nomes de consultas); o Spring Data gera e registra a implementação em tempo de execução, quando sobe a aplicação.

**O DAO morreu?** Não. O papel de isolar o acesso a dados permanece no Repository. O código repetitivo de conexão, comandos e mapeamento foi absorvido pelo Spring Data/JPA.

**Por que validar altura no Service?** A regra vale para qualquer entrada, seja HTTP, uma rotina interna ou outro cliente. O Controller cuida de transporte; o Service mantém a regra de negócio centralizada.

**Onde está o SQL de `findByTipoIntervencao()`?** Não o escrevemos: o Spring Data interpreta o nome do método e cria a consulta para o provedor JPA, que gera o SQL adequado ao Oracle.
