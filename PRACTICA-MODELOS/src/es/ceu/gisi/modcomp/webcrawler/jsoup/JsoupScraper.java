/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ceu.gisi.modcomp.webcrawler.jsoup;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Esta clase encapsula toda la lógica de interacción con el analizador Jsoup.
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class JsoupScraper {

    private final Document doc;

    /**
     * Este constructor crea un documento a partir de la URL de la página web a
     * analizar.
     *
     * @param url Una URL que apunte a un documento HTML (p.e.
     * http://www.servidor.com/index.html)
     */
    public JsoupScraper(URL url) throws IOException {
        // La variable deberá inicializarse de alguna manera utilizando una URL...
        // De momento, se inicializa a null para que compile...
        doc = Jsoup.connect(url.toString()).get();;
    }

    /**
     * Este constructor crea un documento a partir del contenido HTML del String
     * que se pasa como parámetro.
     *
     * @param html Un documento HTML contenido en un String.
     */
    public JsoupScraper(String html) throws IOException {
        doc = Jsoup.parse(html);
    }

    /**
     * Realiza estadísticas sobre el número de etiquetas de un cierto tipo.
     *
     * @param etiqueta La etiqueta sobre la que se quieren estadísticas
     *
     * @return El número de etiquetas de ese tipo que hay en el documento HTML
     */
    public int estadisticasEtiqueta(String etiqueta) {

        Elements estadisticas = doc.select(etiqueta);
        return estadisticas.size();
    }

    /**
     * Obtiene todos los hiperenlaces que se encuentran en el documento creado.
     *
     * @return Una lista con todas las URLs de los hiperenlaces
     */
    List<String> enlaces = new ArrayList<String>();

    public List<String> obtenerHiperenlaces() {

        Elements links = doc.getElementsByTag("a");
        for (Element e : links) {
            enlaces.add(e.attr("href"));
        }
        return enlaces;
    }

    /**
     * Obtiene todos los hiperenlaces de las imágenes incrustadas.
     *
     * @return Una lista con todas las URLs de los hiperenlaces
     */
    //List<String> enlaces = new ArrayList<String>();
    public List<String> obtenerHiperenlacesImagenes() {
        //List<String> enlaces = new ArrayList<String>();
        Elements elementos = doc.getElementsByTag("IMG");
        for (Element e : elementos) {
            enlaces.add(e.attr("src"));
        }

        return enlaces;
    }

    /**
     * Obtiene la imagen a la que hace referencia LA PRIMERA etiqueta IMG que
     * encontramos.
     *
     * @return El nombre (o ruta) de la primera imagen insertada en el documento
     * HTML.
     */
    public String obtenerContenidoImg() {
        Element elemento = doc.getElementsByTag("IMG").first();
        String imagen = elemento.attr("src");
        return imagen;
    }

    public void VolcarFichero() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("./SalidaJSOUP.txt");
            pw = new PrintWriter(fichero);
            pw.println("ENLACES: " + "\n" + enlaces + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero);
                fichero.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
