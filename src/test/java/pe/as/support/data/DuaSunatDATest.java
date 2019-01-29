package pe.as.support.data;

import org.junit.Test;
import pe.as.support.entities.Importacion;
import pe.as.support.entities.Mercancia;

public class DuaSunatDATest extends BaseDATest {

    @Test
    public void update() throws Exception {
        for (String dua : DuaSunatDA.getDuaWithIncompleteDetails()) {
            Mercancia mercancia = new DuaSunatWebDA(Importacion.DEFINITIVA, 3).consultar(dua);
            DuaSunatDA.update(dua, mercancia);
        }
    }
}