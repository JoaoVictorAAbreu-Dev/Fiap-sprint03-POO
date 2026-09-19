package br.com.motiva.dao;

import br.com.motiva.db.ConexaoBD;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.persistence.IntervencaoOperacionalRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IntervencaoOperacionalDAO {
    private static final String COLUNAS =
            "ID_INTERVENCAO, ID_TRECHO, ID_EQUIPE, TIPO_INTERVENCAO, NIVEL_PRIORIDADE, "
                    + "ALTURA_PROJETADA_CM, JUSTIFICATIVA, DATA_INTERVENCAO, STATUS";
    private static final String SQL_INSERT =
            "INSERT INTO TB_INTERVENCAO_OPERACIONAL "
                    + "(ID_TRECHO, ID_EQUIPE, TIPO_INTERVENCAO, NIVEL_PRIORIDADE, "
                    + "ALTURA_PROJETADA_CM, JUSTIFICATIVA, DATA_INTERVENCAO, STATUS) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT " + COLUNAS + " FROM TB_INTERVENCAO_OPERACIONAL WHERE ID_INTERVENCAO = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT " + COLUNAS + " FROM TB_INTERVENCAO_OPERACIONAL ORDER BY ID_INTERVENCAO";
    private static final String SQL_UPDATE =
            "UPDATE TB_INTERVENCAO_OPERACIONAL SET ID_TRECHO = ?, ID_EQUIPE = ?, "
                    + "TIPO_INTERVENCAO = ?, NIVEL_PRIORIDADE = ?, ALTURA_PROJETADA_CM = ?, "
                    + "JUSTIFICATIVA = ?, DATA_INTERVENCAO = ?, STATUS = ? "
                    + "WHERE ID_INTERVENCAO = ?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_INTERVENCAO_OPERACIONAL WHERE ID_INTERVENCAO = ?";

    private final Connection conexaoFixa;

    public IntervencaoOperacionalDAO() {
        this(null);
    }

    public IntervencaoOperacionalDAO(Connection conexaoFixa) {
        this.conexaoFixa = conexaoFixa;
    }

    public long inserir(IntervencaoOperacionalRecord intervencao) throws SQLException {
        Objects.requireNonNull(intervencao, "A intervencao e obrigatoria.");
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(
                SQL_INSERT,
                new String[]{"ID_INTERVENCAO"}
        )) {
            preencherDados(statement, intervencao);
            validarUmaLinhaAlterada(statement.executeUpdate(), "inserir a intervencao");
            return obterIdGerado(statement, "intervencao");
        }
    }

    public Optional<IntervencaoOperacionalRecord> buscarPorId(long id) throws SQLException {
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

    public List<IntervencaoOperacionalRecord> listarTodas() throws SQLException {
        Connection connection = obterConexao();
        List<IntervencaoOperacionalRecord> intervencoes = new ArrayList<>();

        try (
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                intervencoes.add(mapear(resultSet));
            }
        }

        return intervencoes;
    }

    public boolean atualizar(IntervencaoOperacionalRecord intervencao) throws SQLException {
        Objects.requireNonNull(intervencao, "A intervencao e obrigatoria.");
        long id = exigirId(intervencao.id());
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            preencherDados(statement, intervencao);
            statement.setLong(9, id);
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
            IntervencaoOperacionalRecord intervencao
    ) throws SQLException {
        statement.setLong(1, intervencao.idTrecho());
        statement.setLong(2, intervencao.idEquipe());
        statement.setString(3, intervencao.tipoIntervencao().name());
        statement.setString(4, intervencao.nivelPrioridade().name());
        statement.setBigDecimal(5, BigDecimal.valueOf(intervencao.alturaProjetadaCm()));
        statement.setString(6, intervencao.justificativa());
        statement.setTimestamp(7, Timestamp.valueOf(intervencao.dataIntervencao()));
        statement.setString(8, intervencao.status());
    }

    private Connection obterConexao() throws SQLException {
        if (conexaoFixa == null) {
            return ConexaoBD.getInstancia().conectar();
        }
        if (conexaoFixa.isClosed()) {
            throw new SQLException("A conexao transacional da intervencao esta fechada.");
        }
        return conexaoFixa;
    }

    private static IntervencaoOperacionalRecord mapear(ResultSet resultSet) throws SQLException {
        return new IntervencaoOperacionalRecord(
                resultSet.getLong("ID_INTERVENCAO"),
                resultSet.getLong("ID_TRECHO"),
                resultSet.getLong("ID_EQUIPE"),
                TipoIntervencao.valueOf(resultSet.getString("TIPO_INTERVENCAO")),
                NivelPrioridade.valueOf(resultSet.getString("NIVEL_PRIORIDADE")),
                resultSet.getDouble("ALTURA_PROJETADA_CM"),
                resultSet.getString("JUSTIFICATIVA"),
                resultSet.getTimestamp("DATA_INTERVENCAO").toLocalDateTime(),
                resultSet.getString("STATUS")
        );
    }

    private static long exigirId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "O id da intervencao e obrigatorio para atualizacao."
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
