package pe.as.support.entities;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Dua {

    private int tipoImportacion;
    private int codigoAduana, anho, numDeclaracion;
    private String rawDua, ndui;
    private String[] parts;

    public Dua(int tipoImportacion, String rawDua) {
        this.rawDua = rawDua;
        this.tipoImportacion = tipoImportacion;
        parts = rawDua.split("-");
        codigoAduana = Integer.parseInt(parts[0]);
        if (parts.length > 1) anho = Integer.parseInt(parts[1]) % 100;
        if (parts.length > 2) numDeclaracion = Integer.parseInt(parts[2]);
        ndui = String.format("%03d%02d%02d%06d", codigoAduana, anho, tipoImportacion, numDeclaracion);
    }
}
