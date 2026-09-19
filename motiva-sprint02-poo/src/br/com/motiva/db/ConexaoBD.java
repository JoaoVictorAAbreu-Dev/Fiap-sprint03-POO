package br.com.motiva.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Gerencia a conexao compartilhada com o Oracle para a aplicacao academica.
 */
public final class ConexaoBD {
    private static final String DRIVER_ORACLE = "oracle.jdbc.OracleDriver";
    private static final int TEMPO_VALIDACAO_SEGUNDOS = 2;
    private static final List<Path> CAMINHOS_CONFIGURACAO = List.of(
            Path.of("config", "db.properties"),
            Path.of("motiva-sprint02-poo", "config", "db.properties")
    );

    private static final ConexaoBD INSTANCIA = new ConexaoBD();

    private Connection conexao;

    private ConexaoBD() {
    }

    public static ConexaoBD getInstancia() {
        return INSTANCIA;
    }

    public synchronized Connection conectar() throws SQLException {
        if (estaConectado()) {
            return conexao;
        }

        fecharConexaoInvalida();
        carregarDriverOracle();

        Configuracao configuracao = carregarConfiguracao();
        conexao = DriverManager.getConnection(
                configuracao.url(),
                configuracao.usuario(),
                configuracao.senha()
        );
        return conexao;
    }

    public synchronized boolean desconectar() {
        if (conexao == null) {
            return true;
        }

        try {
            if (!conexao.isClosed()) {
                conexao.close();
            }
            conexao = null;
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexao com o Oracle: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean estaConectado() {
        if (conexao == null) {
            return false;
        }

        try {
            return !conexao.isClosed() && conexao.isValid(TEMPO_VALIDACAO_SEGUNDOS);
        } catch (SQLException e) {
            return false;
        }
    }

    private void fecharConexaoInvalida() throws SQLException {
        if (conexao == null) {
            return;
        }

        try {
            conexao.close();
        } catch (SQLException e) {
            throw new SQLException("Nao foi possivel descartar a conexao Oracle invalida.", e);
        } finally {
            conexao = null;
        }
    }

    private void carregarDriverOracle() throws SQLException {
        try {
            Class.forName(DRIVER_ORACLE);
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver Oracle nao encontrado. Adicione lib/ojdbc17.jar ao classpath.",
                    e
            );
        }
    }

    private Configuracao carregarConfiguracao() throws SQLException {
        Properties propriedades = carregarPropriedadesLocais();

        String url = primeiraConfiguracaoDisponivel("DB_URL", "db.url", propriedades);
        String usuario = primeiraConfiguracaoDisponivel("DB_USER", "db.user", propriedades);
        String senha = primeiraConfiguracaoDisponivel("DB_PASSWORD", "db.password", propriedades);

        validarConfiguracaoObrigatoria(url, "DB_URL", "db.url");
        validarConfiguracaoObrigatoria(usuario, "DB_USER", "db.user");
        validarConfiguracaoObrigatoria(senha, "DB_PASSWORD", "db.password");

        return new Configuracao(url.trim(), usuario.trim(), senha);
    }

    private Properties carregarPropriedadesLocais() throws SQLException {
        Properties propriedades = new Properties();

        for (Path caminho : CAMINHOS_CONFIGURACAO) {
            if (!Files.isRegularFile(caminho)) {
                continue;
            }

            try (InputStream entrada = Files.newInputStream(caminho)) {
                propriedades.load(entrada);
                return propriedades;
            } catch (IOException e) {
                throw new SQLException(
                        "Nao foi possivel ler a configuracao local em " + caminho + ".",
                        e
                );
            }
        }

        return propriedades;
    }

    private String primeiraConfiguracaoDisponivel(
            String nomeVariavelAmbiente,
            String nomePropriedade,
            Properties propriedades
    ) {
        String valorAmbiente = System.getenv(nomeVariavelAmbiente);
        if (valorAmbiente != null && !valorAmbiente.isBlank()) {
            return valorAmbiente;
        }
        return propriedades.getProperty(nomePropriedade);
    }

    private void validarConfiguracaoObrigatoria(
            String valor,
            String nomeVariavelAmbiente,
            String nomePropriedade
    ) throws SQLException {
        if (valor == null || valor.isBlank()) {
            throw new SQLException(
                    "Configuracao ausente: defina " + nomeVariavelAmbiente
                            + " ou a propriedade " + nomePropriedade + "."
            );
        }
    }

    private record Configuracao(String url, String usuario, String senha) {
    }
}
