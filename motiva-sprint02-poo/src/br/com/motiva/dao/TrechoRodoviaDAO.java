package br.com.motiva.dao;

import br.com.motiva.db.ConexaoBD;
import br.com.motiva.model.TipoCrescimento;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.model.persistence.TrechoRodoviaRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TrechoRodoviaDAO {
    private static final String COLUNAS =
            "ID_TRECHO, QUILOMETRO, SENTIDO, ALTURA_VEGETACAO_CM, TIPO_CRESCIMENTO, "
                    + "AREA_SENSIVEL, MONITORADO_IOT, CODIGO_SENSOR";
    private static final String SQL_INSERT =
            "INSERT INTO TB_TRECHO_RODOVIA "
                    + "(QUILOMETRO, SENTIDO, ALTURA_VEGETACAO_CM, TIPO_CRESCIMENTO, "
                    + "AREA_SENSIVEL, MONITORADO_IOT, CODIGO_SENSOR) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT " + COLUNAS + " FROM TB_TRECHO_RODOVIA WHERE ID_TRECHO = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT " + COLUNAS + " FROM TB_TRECHO_RODOVIA ORDER BY ID_TRECHO";
    private static final String SQL_UPDATE =
            "UPDATE TB_TRECHO_RODOVIA SET QUILOMETRO = ?, SENTIDO = ?, "
                    + "ALTURA_VEGETACAO_CM = ?, TIPO_CRESCIMENTO = ?, AREA_SENSIVEL = ?, "
                    + "MONITORADO_IOT = ?, CODIGO_SENSOR = ? WHERE ID_TRECHO = ?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_TRECHO_RODOVIA WHERE ID_TRECHO = ?";

    private final Connection conexaoFixa;

    public TrechoRodoviaDAO() {
        this(null);
    }

    public TrechoRodoviaDAO(Connection conexaoFixa) {
        this.conexaoFixa = conexaoFixa;
    }

    public long inserir(TrechoRodoviaRecord trecho) throws SQLException {
        Objects.requireNonNull(trecho, "O trecho e obrigatorio.");
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(
                SQL_INSERT,
                new String[]{"ID_TRECHO"}
        )) {
            preencherDados(statement, trecho);
            validarUmaLinhaAlterada(statement.executeUpdate(), "inserir o trecho");
            return obterIdGerado(statement, "trecho");
        }
    }

    public Optional<TrechoRodoviaRecord> buscarPorId(long id) throws SQLException {
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

    public List<TrechoRodoviaRecord> listarTodas() throws SQLException {
        Connection connection = obterConexao();
        List<TrechoRodoviaRecord> trechos = new ArrayList<>();

        try (
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                trechos.add(mapear(resultSet));
            }
        }

        return trechos;
    }

    public List<TrechoRodovia> listarComoDominio() throws SQLException {
        List<TrechoRodovia> trechos = new ArrayList<>();
        for (TrechoRodoviaRecord trecho : listarTodas()) {
            trechos.add(trecho.toDomain());
        }
        return trechos;
    }

    public boolean atualizar(TrechoRodoviaRecord trecho) throws SQLException {
        Objects.requireNonNull(trecho, "O trecho e obrigatorio.");
        long id = exigirId(trecho.id(), "trecho");
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            preencherDados(statement, trecho);
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
            TrechoRodoviaRecord trecho
    ) throws SQLException {
        statement.setInt(1, trecho.quilometro());
        statement.setString(2, trecho.sentido());
        statement.setBigDecimal(3, BigDecimal.valueOf(trecho.alturaVegetacaoCm()));
        statement.setString(4, trecho.tipoCrescimento().name());
        statement.setString(5, paraChar(trecho.areaSensivel()));
        statement.setString(6, paraChar(trecho.monitoradoIot()));
        statement.setString(7, trecho.codigoSensor());
    }

    private Connection obterConexao() throws SQLException {
        if (conexaoFixa == null) {
            return ConexaoBD.getInstancia().conectar();
        }
        if (conexaoFixa.isClosed()) {
            throw new SQLException("A conexao transacional do trecho esta fechada.");
        }
        return conexaoFixa;
    }

    private static TrechoRodoviaRecord mapear(ResultSet resultSet) throws SQLException {
        return new TrechoRodoviaRecord(
                resultSet.getLong("ID_TRECHO"),
                resultSet.getInt("QUILOMETRO"),
                resultSet.getString("SENTIDO"),
                resultSet.getDouble("ALTURA_VEGETACAO_CM"),
                TipoCrescimento.valueOf(resultSet.getString("TIPO_CRESCIMENTO")),
                "S".equalsIgnoreCase(resultSet.getString("AREA_SENSIVEL")),
                "S".equalsIgnoreCase(resultSet.getString("MONITORADO_IOT")),
                resultSet.getString("CODIGO_SENSOR")
        );
    }

    private static String paraChar(boolean valor) {
        return valor ? "S" : "N";
    }

    private static long exigirId(Long id, String entidade) {
        if (id == null) {
            throw new IllegalArgumentException("O id do " + entidade + " e obrigatorio para atualizacao.");
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
