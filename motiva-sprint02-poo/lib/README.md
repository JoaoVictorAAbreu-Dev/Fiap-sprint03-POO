# Driver JDBC Oracle

O driver binario nao e criado nem distribuido por este projeto. Obtenha o
`ojdbc17.jar` pelos canais oficiais da Oracle ou pelo ambiente disponibilizado
pela FIAP e coloque-o nesta pasta com o nome:

```text
lib/ojdbc17.jar
```

Nunca salve usuario ou senha dentro do JAR ou no repositorio. Configure
preferencialmente as variaveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`.
Como alternativa local, copie `config/db.properties.example` para
`config/db.properties`; esse arquivo real e ignorado pelo Git.

Classpath para execucao a partir de `motiva-sprint02-poo`:

- Windows: `out;lib/ojdbc17.jar`
- Linux/macOS: `out:lib/ojdbc17.jar`

No IntelliJ IDEA, adicione o arquivo em **File > Project Structure > Modules >
Dependencies > + > JARs or Directories**.
