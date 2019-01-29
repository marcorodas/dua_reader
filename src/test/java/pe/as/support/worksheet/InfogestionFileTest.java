package pe.as.support.worksheet;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

public class InfogestionFileTest {

    @Test
    public void completeWithDuaColumn() throws IOException, InvalidFormatException {
        File fileIn = new File("C:\\Users\\skynet\\Desktop\\tabla cruda.xls");
        File fileOut = new File("C:\\Users\\skynet\\Desktop\\tabla cruda.out.xls");
        try (InfogestionFile infogestionFile = new InfogestionFile(fileIn)) {
            if (infogestionFile.addColumnsWithDUA()) {
                infogestionFile.writeTo(fileOut);
            }
        }
    }

}