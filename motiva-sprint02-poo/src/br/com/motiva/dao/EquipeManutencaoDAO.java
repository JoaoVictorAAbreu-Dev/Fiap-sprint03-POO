package br.com.motiva.dao;

import br.com.motiva.db.ConexaoBD;
import br.com.motiva.model.persistence.EquipeManutencaoRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EquipeManutencaoDAO {
    private static final String SQL_INSERT =
            "INSERT INTO TB_EQUIPE_MANUTENCAO (NOME, ESPECIALIDADE, ATIVA) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT ID_EQUIPE, NOME, ESPECIALIDADE, ATIVA "
                    + "FROM TB_EQUIPE_MANUTENCAO WHERE ID_EQUIPE = ?";
    private static final String SQL_SELECT_BY_NAME =
            "SELECT ID_EQUIPE, NOME, ESPECIALIDADE, ATIVA "
                    + "FROM TB_EQUIPE_MANUTENCAO WHERE NOME = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT ID_EQUIPE, NOME, ESPECIALIDADE, ATIVA "
                    + "FROM TB_EQUIPE_MANUTENCAO ORDER BY ID_EQUIPE";
    private static final String SQL_UPDATE =
            "UPDATE TB_EQUIPE_MANUTENCAO SET NOME = ?, ESPECIALIDADE = ?, ATIVA = ? "
                    + "WHERE ID_EQUIPE = ?";
    private static final String SQL_DELETE =
            "DELETE FROM TB_EQUIPE_MANUTENCAO WHERE ID_EQUIPE = ?";

    private final Connection conexaoFixa;

    public EquipeManutencaoDAO() {
        this(null);
    }

    public EquipeManutencaoDAO(Connection conexaoFixa) {
        this.conexaoFixa = conexaoFixa;
    }

    public long inserir(EquipeManutencaoRecord equipe) throws SQLException {
        Objects.requireNonNull(equipe, "A equipe e obrigatoria.");
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(
                SQL_INSERT,
                new String[]{"ID_EQUIPE"}
        )) {
            statement.setString(1, equipe.nome());
            statement.setString(2, equipe.especialidade());
            statement.setString(3, paraChar(equipe.ativa()));

            validarUmaLinhaAlterada(statement.executeUpdate(), "inserir a equipe");
            return obterIdGerado(statement, "equipe");
        }
    }

    public Optional<EquipeManutencaoRecord> buscarPorId(long id) throws SQLException {
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

    public Optional<EquipeManutencaoRecord> buscarPorNome(String nome) throws SQLException {
        Objects.requireNonNull(nome, "O nome da equipe e obrigatorio.");
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, nome);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapear(resultSet))
                        : Optional.empty();
            }
        }
    }

    public List<EquipeManutencaoRecord> listarTodas() throws SQLException {
        Connection connection = obterConexao();
        List<EquipeManutencaoRecord> equipes = new ArrayList<>();

        try (
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                equipes.add(mapear(resultSet));
            }
        }

        return equipes;
    }

    public boolean atualizar(EquipeManutencaoRecord equipe) throws SQLException {
        Objects.requireNonNull(equipe, "A equipe e obrigatoria.");
        long id = exigirId(equipe.id(), "equipe");
        Connection connection = obterConexao();

        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, equipe.nome());
            statement.setString(2, equipe.especialidade());
            statement.setString(3, paraChar(equipe.ativa()));
            statement.setLong(4, id);
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

    private static EquipeManutencaoRecord mapear(ResultSet resultSet) throws SQLException {
        return new EquipeManutencaoRecord(
                resultSet.getLong("ID_EQUIPE"),
                resultSet.getString("NOME"),
                resultSet.getString("ESPECIALIDADE"),
                "S".equalsIgnoreCase(resultSet.getString("ATIVA"))
        );
    }

    private Connection obterConexao() throws SQLException {
        if (conexaoFixa == null) {
            return ConexaoBD.getInstancia().conectar();
        }
        if (conexaoFixa.isClosed()) {
            throw new SQLException("A conexao transacional da equipe esta fechada.");
        }
        return conexaoFixa;
    }

    private static String paraChar(boolean valor) {
        return valor ? "S" : "N";
    }

    private static long exigirId(Long id, String entidade) {
        if (id == null) {
            throw new IllegalArgumentException("O id da " + entidade + " e obrigatorio para atualizacao.");
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
