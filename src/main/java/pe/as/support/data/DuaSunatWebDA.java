package pe.as.support.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pe.as.support.entities.Dua;
import pe.as.support.entities.Importacion;
import pe.as.support.entities.Mercancia;
import pe.as.support.entities.Response;

public class DuaSunatWebDA {

    //Sample: http://www.sunat.gob.pe/ol-ad-itconsultadua/RPSGDui?ndui=1181810001434 1721710014205
    private static final String HOST = "http://www.sunat.gob.pe";
    private static final String CONSULTA_DUA = "/ol-ad-itconsultadua/RPSGDui";

    private Response response;
    private final int tipo, posSerieEnDua;

    public DuaSunatWebDA(Importacion tipo, int posSerieEnDua) {
        this.tipo = tipo.getValue();
        this.posSerieEnDua = posSerieEnDua;
    }

    public Mercancia consultar(String rawDua) throws IOException {
        Dua dua = new Dua(tipo, rawDua);
        if (dua.getParts().length < 4) return null;
        if (response == null) response = this.getResponse(dua);
        String ndui = response.getDua().getNdui();
        if (!dua.getNdui().equals(ndui)) response = this.getResponse(dua);
        int serie = Integer.parseInt(dua.getParts()[posSerieEnDua].trim());
        for (Mercancia mercancia : response.getMercancias()) {
            if (mercancia.getSerie() == serie) {
                return mercancia;
            }
        }
        return null;
    }

    private Response getResponse(Dua dua) throws IOException {
        String url = String.format("%s%s?ndui=%s", HOST, CONSULTA_DUA, dua.getNdui());
        try (InputStream stream = new URL(url).openStream()) {
            Document doc = Jsoup.parse(stream, StandardCharsets.ISO_8859_1.name(), url);
            Elements table = doc.getElementsByTag("table");
            if (table == null || table.size() < 2) {
                return new Response(dua);
            }
            Element secondTable = table.get(1);
            Elements rows = secondTable.select("tr");
            Response response = new Response(dua);
            Mercancia mercancia = null;
            for (Element row : rows) {
                String firstColTxt = row.selectFirst("td").text();
                if (DuaSunatWebDA.allDigits(firstColTxt)) {
                    mercancia = new Mercancia(Integer.parseInt(firstColTxt));
                    response.getMercancias().add(mercancia);
                }
                if (mercancia == null) continue;
                Element detalle = row.selectFirst("td[colspan='8']");
                if (detalle == null || detalle.text().trim().isEmpty()) continue;
                mercancia.getDetalles().add(detalle.text());
            }
            return response;
        }
    }

    private static boolean allDigits(String txt) {
        return txt != null && !txt.isEmpty() && txt.chars().allMatch(Character::isDigit);
    }
}
