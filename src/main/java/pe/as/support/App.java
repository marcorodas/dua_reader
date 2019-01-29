package pe.as.support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import me.tongfei.progressbar.ProgressBar;
import pe.as.support.data.DuaSunatDA;
import pe.as.support.data.DuaSunatWebDA;
import pe.as.support.data.DuaXlsxDA;
import pe.as.support.entities.Importacion;
import pe.as.support.entities.Mercancia;

import pe.mrodas.jdbc.DBLayer;

public class App {

    public static void configDB() {
        DBLayer.Connector.configureWithPropFile("db.properties");
    }

    public static void main(String[] args) throws Exception {
        App.configDB();
        DuaSunatWebDA webDA = new DuaSunatWebDA(Importacion.DEFINITIVA, 3);
        for (int i = 0; i < 10; i++) {
            App.completeDuaDetails(webDA);
        }
    }

    private static void completeDuaDetails(DuaSunatWebDA webDA) throws Exception {
        List<String> pendingList = DuaXlsxDA.getPendingList(100);
        if (pendingList.isEmpty()) return;
        File file = Files.createTempFile("dua_details", ".csv").toFile();
        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file)).build()) {
            for (List<String> rawRow : App.getDuaDetails(webDA, pendingList)) {
                String[] row = new String[6];
                for (int i = 0; i < rawRow.size(); i++) {
                    if (i == 6) break;
                    row[i] = rawRow.get(i);
                }
                writer.writeNext(row);
            }
        }
        try (ProgressBar progressBar = new ProgressBar("Upload Data", 1)) {
            DuaSunatDA.loadDataLocalInFile(file);
            progressBar.step();
        }
    }

    private static List<List<String>> getDuaDetails(DuaSunatWebDA webDA, List<String> pendingList) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (String dua : ProgressBar.wrap(pendingList, "Sunat Web Query")) {
            Mercancia mercancia = webDA.consultar(dua);
            if (mercancia != null) {
                List<String> row = mercancia.getDetalles();
                row.add(0, dua);
                rows.add(row);
            }
        }
        return rows;
    }
}
