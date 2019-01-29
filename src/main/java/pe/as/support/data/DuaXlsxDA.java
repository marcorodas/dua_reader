package pe.as.support.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

import pe.mrodas.jdbc.SqlQuery;

@UtilityClass
public class DuaXlsxDA {

    private static final SqlQuery.ExecutorList<String> DUA_EXECUTOR_LIST = (rs, list) -> {
        while (rs.next()) {
            list.add(rs.getString("dua"));
        }
    };

    private static String[] getSqlSelectPendingDua(String[] otherSql) {
        String[] sql = {
                "SELECT DISTINCT dua",
                "   FROM dua_xlsx",
                "   WHERE dua NOT IN(SELECT dua FROM dua_sunat)",
                "   ORDER BY SUBSTR(SUBSTR(dua,1,13), 5) DESC"
        };
        if (otherSql == null) {
            return sql;
        }
        return Stream.concat(Arrays.stream(sql), Arrays.stream(otherSql))
                .toArray(String[]::new);
    }

    public static List<String> getPendingList(int capacity) throws Exception {
        return new SqlQuery<String>().setSql(DuaXlsxDA.getSqlSelectPendingDua(new String[]{
                "   LIMIT :capacity"
        })).addParameter("capacity", capacity).execute(DUA_EXECUTOR_LIST);
    }

    public static List<String> getPendingList() throws Exception {
        return new SqlQuery<String>().setSql(DuaXlsxDA.getSqlSelectPendingDua(null))
                .execute(DUA_EXECUTOR_LIST);
    }
}
