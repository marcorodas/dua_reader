package pe.as.support.data;

import java.io.Closeable;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import lombok.experimental.UtilityClass;
import pe.as.support.entities.Mercancia;

import pe.mrodas.jdbc.Adapter;
import pe.mrodas.jdbc.SqlInsert;
import pe.mrodas.jdbc.SqlQuery;
import pe.mrodas.jdbc.SqlUpdate;

@UtilityClass
public class DuaSunatDA {

    public static void loadDataLocalInFile(File file) throws Exception {
        String sql = String.join(" ", new String[]{
                "LOAD DATA LOCAL INFILE '%s' INTO TABLE %s",
                "CHARACTER SET latin1",
                "FIELDS TERMINATED BY \',\' ENCLOSED BY \'\"'",
                "LINES TERMINATED BY \'\\n\'"
        });
        try (Statement statement = Adapter.getConnection().createStatement()) {
            statement.executeUpdate(String.format(sql, file.toPath().toString().replace("\\", "\\\\"), DuaSunatDA.TABLE));
        }
    }

    public static class BatchInsert implements Closeable {

        private static final String SQL = String.format("INSERT INTO %s VALUES(?,?,?,?,?,?)", DuaSunatDA.TABLE);
        private final PreparedStatement statement;

        public BatchInsert() throws Exception {
            statement = Adapter.getConnection().prepareStatement(SQL);
        }

        public void addBatch(String dua, List<String> detalles) throws SQLException {
            int index = 1;
            statement.setString(index, dua);
            for (int i = 0; i < 5; i++) {
                index++;
                if (i < detalles.size()) {
                    statement.setString(index, detalles.get(i));
                } else {
                    statement.setNull(index, Types.VARCHAR);
                }
            }
            statement.addBatch();
        }

        public void execute() throws SQLException {
            statement.executeBatch();
        }

        @Override
        public void close() {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String TABLE = "dua_sunat";

    public static List<String> getDuaWithIncompleteDetails() throws Exception {
        return new SqlQuery<String>().setSql(new String[]{
                "SELECT DUA",
                "   FROM " + TABLE,
                "   WHERE LENGTH(TRIM(col1)) = 0"
        }).execute((rs, list) -> {
            while (rs.next()) {
                list.add(rs.getString("DUA"));
            }
        });
    }

    public static void insert(String dua, Mercancia mercancia) throws Exception {
        if (mercancia == null || mercancia.getDetalles().isEmpty()) return;
        SqlInsert insert = new SqlInsert(TABLE).addField("DUA", dua);
        DuaSunatDA.addColumnFields(insert, mercancia.getDetalles());
        insert.execute();
    }

    public static void update(String dua, Mercancia mercancia) throws Exception {
        if (mercancia == null || mercancia.getDetalles().isEmpty()) return;
        SqlUpdate update = new SqlUpdate(TABLE).addFilter("DUA", dua);
        DuaSunatDA.addColumnFields(update, mercancia.getDetalles());
        update.execute();
    }

    private static void addColumnFields(SqlQuery.Save save, List<String> details) throws Exception {
        int size = details.size();
        if (size > 5) System.out.println(details.subList(5, size));
        for (int i = 0; i < size; i++) {
            if ((i + 1) > 5) break;
            save.addField(String.format("col%d", i + 1), details.get(i));
        }
    }

}
