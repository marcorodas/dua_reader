package pe.as.support.worksheet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class DuaDbFile implements AutoCloseable {

    private Sheet workingSheet;
    @Getter
    private final File file;
    private static final String DETAILS_SHEET_NAME = "dua_details";

    public DuaDbFile(File file) throws IOException, InvalidFormatException {
        this.file = file;
        this.workingSheet = WorkbookFactory.create(file).getSheetAt(0);
    }

    public DuaDbFile removeOtherSheets(String sheetName) {
        Workbook workbook = workingSheet.getWorkbook();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (!workbook.getSheetAt(i).getSheetName().equals(sheetName)) {
                workbook.removeSheetAt(i);
            }
        }
        workingSheet = workbook.getSheet(sheetName);
        return this;
    }

    public void createSheetDetails(int numCols) {
        Sheet sheet = workingSheet.getWorkbook().getSheet(DETAILS_SHEET_NAME);
        if (sheet == null) {
            workingSheet = workingSheet.getWorkbook().createSheet(DETAILS_SHEET_NAME);
            List<String> colNames = IntStream.rangeClosed(1, numCols)
                    .mapToObj(String::valueOf).map("col"::concat)
                    .collect(Collectors.toList());
            colNames.add(0, "dua");
            this.createHeaders(colNames.toArray(new String[0]));
        }
    }

    private void addHeader(String name, CellStyle style) {
        Row row = workingSheet.getRow(0) == null ? workingSheet.createRow(0)
                : workingSheet.getRow(0);
        int i = 0;
        Cell cell = row.getCell(i);
        while (cell != null) {
            i++;
            cell = row.getCell(i);
        }
        cell = row.createCell(i, CellType.STRING);
        cell.setCellValue(name);
        cell.setCellStyle(style);
    }

    private void createHeaders(String... headers) {
        if (headers == null || headers.length == 0) return;
        CellStyle style = this.getHeaderStyle();
        Row row = workingSheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = workingSheet.getWorkbook().createCellStyle();
        Font font = workingSheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    @Override
    public void close() throws IOException {
        workingSheet.getWorkbook().close();
    }
}
