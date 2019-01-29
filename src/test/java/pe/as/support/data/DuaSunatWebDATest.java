package pe.as.support.data;

import java.io.IOException;
import java.util.stream.Collectors;

import org.junit.Test;
import pe.as.support.entities.Importacion;
import pe.as.support.entities.Mercancia;

public class DuaSunatWebDATest {

    @Test
    public void consultar() throws IOException {
        String dua = "118-14-448066-   5";
        Mercancia mercancia = new DuaSunatWebDA(Importacion.DEFINITIVA, 3).consultar(dua);
        if (mercancia != null) {
            String detalles = mercancia.getDetalles().stream().collect(Collectors.joining(" - "));
            System.out.println(String.format("[%s] - %s", dua, detalles));
        }
    }

}