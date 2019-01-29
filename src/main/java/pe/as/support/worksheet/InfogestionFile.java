package pe.as.support.worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lombok.Setter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import pe.as.support.data.DuaSunatWebDA;
import pe.as.support.entities.Importacion;
import pe.as.support.entities.Mercancia;

public class InfogestionFile implements AutoCloseable {
    private Sheet workingSheet;
    @Setter
    private CellAddress pivot;

    public InfogestionFile(File file) throws IOException, InvalidFormatException {
        workingSheet = WorkbookFactory.create(file).getSheetAt(0);
    }

    public void setWorkingSheet(int i) {
        workingSheet = workingSheet.getWorkbook().getSheetAt(i);
    }

    public void setWorkingSheet(String name) {
        workingSheet = workingSheet.getWorkbook().getSheet(name);
    }

    private CellAddress getDefaultPivot() {
        for (Row row : workingSheet) {
            String value = row.getCell(0).getRichStringCellValue().toString();
            if ("Partida Arancelaria".equals(value)) {
                for (Cell cell : row) {
                    value = cell.getRichStringCellValue().toString();
                    if ("DUA".equals(value)) {
                        return cell.getAddress();
                    }
                }
                return null;
            }
        }
        return null;
    }

    public boolean addColumnsWithDUA() throws IOException {
        if (pivot == null) {
            pivot = this.getDefaultPivot();
        }
        if (pivot == null) return false;
        int column = pivot.getColumn();
        int row = pivot.getRow() + 1;
        Cell duaCell = workingSheet.getRow(row).getCell(column);
        DuaSunatWebDA webDA = new DuaSunatWebDA(Importacion.DEFINITIVA, 3);
        while (duaCell != null) {
            String duaValue = duaCell.getRichStringCellValue().getString();
            Mercancia mercancia = webDA.consultar(duaValue);
            if (mercancia != null) {
                List<String> detalles = mercancia.getDetalles();
                for (int i = 0; i < detalles.size(); i++) {
                    String detalle = detalles.get(i);
                    duaCell.getRow().createCell(column + i + 1).setCellValue(detalle);
                }
            }
            row++;
            duaCell = workingSheet.getRow(row).getCell(column);
        }
        return true;
    }

    public void writeTo(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workingSheet.getWorkbook().write(fos);
        }
    }

    @Override
    public void close() throws IOException {
        workingSheet.getWorkbook().close();
    }
}
