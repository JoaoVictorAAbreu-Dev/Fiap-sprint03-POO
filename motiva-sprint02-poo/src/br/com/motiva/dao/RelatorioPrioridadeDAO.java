package br.com.motiva.dao;

import br.com.motiva.db.ConexaoBD;
import br.com.motiva.model.persistence.RelatorioPrioridadeRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RelatorioPrioridadeDAO {
    private static final String COLUNAS =
            "ID_RELATORIO, DATA_GERACAO, QT_NORMAL, QT_BAIXA, QT_MEDIA, QT_ALTA, QT_CRITICA, RESUMO";
    private static final String SQL_INSERT =
            "INSERT INTO TB_RELATORIO_PRIORIDADE "
                    + "(DATA_GERACAO, QT_NORMAL, QT_BAIXA, QT_MEDIA, QT_ALTA, QT_CRITICA, RESUMO) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT " + COLUNAS + " FROM TB_RELATORIO_PRIORIDADE WHERE ID_RELATORIO = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT " + COLUNAS + " FROM TB_RELATORIO_PRIORIDADE ORDER BY ID_RELATORIO";
    private static final String SQL_UPDATE =
            "UPDATE TB_RELATORIO_PRIORIDADE SET DATA_GERACAO = ?, QT_NORMAL = ?, QT_BAIXA = ?, "
                    + "QT_MEDIA = ?, QT_ALTA = ?, QT_CRITICA = ?, RESUMO = ? WHERE ID_RELATORIO = ?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_RELATORIO_PRIORIDADE WHERE ID_RELATORIO = ?";

    private final Connection conexaoFixa;

    public RelatorioPrioridadeDAO() {
        this(null);
    }

    public RelatorioPrioridadeDAO(Connection conexaoFixa) {
        this.conexaoFixa = conexaoFixa;
    }

    public long inserir(RelatorioPrioridadeRecord relatorio) throws SQLException {
        Objects.requireNonNull(relatorio, "O relatorio e obrigatorio.");
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(
                SQL_INSERT,
                new String[]{"ID_RELATORIO"}
        )) {
            preencherDados(statement, relatorio);
            validarUmaLinhaAlterada(statement.executeUpdate(), "inserir o relatorio");
            return obterIdGerado(statement, "relatorio");
        }
    }

    public long salvarRelatorio(RelatorioPrioridadeRecord relatorio) throws SQLException {
        return inserir(relatorio);
    }

    public Optional<RelatorioPrioridadeRecord> buscarPorId(long id) throws SQLException {
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapear(resultSet))
                        : Optional.empty();
            }
        }
    }

    public List<RelatorioPrioridadeRecord> listarTodas() throws SQLException {
        Connection connection = obterConexao();
        List<RelatorioPrioridadeRecord> relatorios = new ArrayList<>();

        try (
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                relatorios.add(mapear(resultSet));
            }
        }

        return relatorios;
    }

    public boolean atualizar(RelatorioPrioridadeRecord relatorio) throws SQLException {
        Objects.requireNonNull(relatorio, "O relatorio e obrigatorio.");
        long id = exigirId(relatorio.id());
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            preencherDados(statement, relatorio);
            statement.setLong(8, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletar(long id) throws SQLException {
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private static void preencherDados(
            PreparedStatement statement,
            RelatorioPrioridadeRecord relatorio
    ) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(relatorio.dataGeracao()));
        statement.setInt(2, relatorio.qtNormal());
        statement.setInt(3, relatorio.qtBaixa());
        statement.setInt(4, relatorio.qtMedia());
        statement.setInt(5, relatorio.qtAlta());
        statement.setInt(6, relatorio.qtCritica());
        statement.setString(7, relatorio.resumo());
    }

    private Connection obterConexao() throws SQLException {
        if (conexaoFixa == null) {
            return ConexaoBD.getInstancia().conectar();
        }
        if (conexaoFixa.isClosed()) {
            throw new SQLException("A conexao transacional do relatorio esta fechada.");
        }
        return conexaoFixa;
    }

    private static RelatorioPrioridadeRecord mapear(ResultSet resultSet) throws SQLException {
        return new RelatorioPrioridadeRecord(
                resultSet.getLong("ID_RELATORIO"),
                resultSet.getTimestamp("DATA_GERACAO").toLocalDateTime(),
                resultSet.getInt("QT_NORMAL"),
                resultSet.getInt("QT_BAIXA"),
                resultSet.getInt("QT_MEDIA"),
                resultSet.getInt("QT_ALTA"),
                resultSet.getInt("QT_CRITICA"),
                resultSet.getString("RESUMO")
        );
    }

    private static long exigirId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "O id do relatorio e obrigatorio para atualizacao."
            );
        }
        return id;
    }

    private static long obterIdGerado(PreparedStatement statement, String entidade) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        }
        throw new SQLException("O banco nao retornou o id gerado para " + entidade + ".");
    }

    private static void validarUmaLinhaAlterada(int linhasAlteradas, String operacao) throws SQLException {
        if (linhasAlteradas != 1) {
            throw new SQLException("Nao foi possivel " + operacao + ". Linhas alteradas: " + linhasAlteradas);
        }
    }
}
