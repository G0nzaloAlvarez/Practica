package es.ceu.gisi.modcomp.webcrawler.jflex;

import es.ceu.gisi.modcomp.webcrawler.jflex.lexico.Tipo;
import static es.ceu.gisi.modcomp.webcrawler.jflex.lexico.Tipo.CLOSE;
import static es.ceu.gisi.modcomp.webcrawler.jflex.lexico.Tipo.OPEN;
import static es.ceu.gisi.modcomp.webcrawler.jflex.lexico.Tipo.PALABRA;
import static es.ceu.gisi.modcomp.webcrawler.jflex.lexico.Tipo.SLASH;
import es.ceu.gisi.modcomp.webcrawler.jflex.lexico.Token;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Esta clase encapsula toda la lógica de interacción con el parser HTML.
 *
 *
 * @author Gonzalo Álvarez Moreno
 *
 *
 */
public class JFlexScraper {

    ArrayList<String> enlacesA = new ArrayList();
    ArrayList<String> enlacesIMG = new ArrayList();
    HTMLParser analizador;
    Stack<String> etiquetasAbiertas = new Stack();
    private boolean estaBalanceado = true;
    private int estado = 0;

    /**
     * Constructor encargado de la apertura y lectura del fichero asi como la
     * definicion de las diferentes variables
     *
     * @param fichero creacion del fichero
     * @throws java.io.FileNotFoundException
     */
    public JFlexScraper(File fichero) throws FileNotFoundException, IOException {
        Reader reader = new BufferedReader(new FileReader(fichero));
        analizador = new HTMLParser(reader);

        Token token;
        boolean etiquetaA = false;
        boolean etiquetaIMG = false;
        boolean valorHREF = false;
        boolean valorIMG = false;
        while ((token = analizador.nextToken()) != null) {
            //  System.out.print(token.getValor() + " ");
            switch (estado) {

                case 0:
                    if (token.getTipo().equals(OPEN)) {
                        estado = 1;
                    }

                    break;
                case 1:
                    if (token.getTipo().equals(PALABRA)) {
                        estado = 2;
                        etiquetasAbiertas.push(token.getValor().toLowerCase());

                        if (token.getValor().toLowerCase().equals("a")) {
                            etiquetaA = true;
                            etiquetaIMG = false;
                        } else if (token.getValor().toLowerCase().equals("img")) {
                            etiquetaIMG = true;
                            etiquetaA = false;

                        } else {
                            etiquetaIMG = false;
                            etiquetaA = false;

                        }
                    } else if (token.getTipo().equals(SLASH)) {
                        estado = 6;
                    }

                    break;

                case 2:
                    if (token.getTipo().equals(PALABRA)) {
                        estado = 3;

                        if (etiquetaA) {
                            if (token.getValor().equalsIgnoreCase("href")) {

                                valorHREF = true;
                                valorIMG = false;
                            }
                        } else if (etiquetaIMG) {
                            if (token.getValor().equalsIgnoreCase("src")) {

                                valorIMG = true;
                                valorHREF = false;
                            }
                        } else {
                            valorHREF = false;
                            valorIMG = false;
                        }
                    }

                    if (token.getTipo().equals(SLASH)) {
                        estado = 5;
                        etiquetasAbiertas.pop(); //sacamos de la pila
                    }
                    if (token.getTipo().equals(CLOSE)) {
                        estado = 0;
                    }

                    break;
                case 3:

                    if (token.getTipo() == Tipo.IGUAL) {
                        estado = 4;
                    }
                    break;

                case 4:

                    if (token.getTipo() == Tipo.VALOR) {
                        estado = 2;
                        // System.out.println("href " + valorHREF);
                        if (valorHREF) {

                            enlacesA.add(token.getValor());
                        }
                        //System.out.println("img " + valorIMG);
                        if (valorIMG) {
                            enlacesIMG.add(token.getValor());

                        }
                    }

                    break;
                case 5:
                    if (token.getTipo().equals(CLOSE)) {
                        estado = 0;

                    }
                    break;

                case 6:
                    if (token.getTipo().equals(PALABRA)) {
                        if (!token.getValor().equalsIgnoreCase(etiquetasAbiertas.pop())) {
                            estaBalanceado = false;
                        }
                        estado = 7;
                    }

                    break;
                case 7:
                    if (token.getTipo().equals(CLOSE)) {
                        estado = 0;
                    }
                    break;

            }

        }
    }

    /**
     * Devuelve los enlaces de las imagenes
     *
     * @return enlacesIMG
     */
    public List<String> getImagenes() {
        return this.enlacesIMG;
    }

    /**
     * Devuleve los hiperenlaces
     *
     * @return enlacesA
     */
    public List<String> getLinks() {
        return this.enlacesA;
    }

    /**
     * Si no se ha quedado ninguna etiqueta sin cerrar, el documento está
     * balanaceado
     *
     * @return estaBalanceado
     */
    public boolean getBalance() {
        return this.estaBalanceado;
    }

    /**
     * Muestra contenido de la pila
     *
     * @return pila
     */
    public Stack getStack() {
        return this.etiquetasAbiertas;
    }

    /**
     * Método encargado de volcar los resultados obtenidos al fichero
     * SalidaJFLEX en el mismo paquete
     */
    public void VolcarFicheroJFLEX() {
        FileWriter fichero1 = null;
        PrintWriter pr = null;
        try {
            fichero1 = new FileWriter("./src/es/ceu/gisi/modcomp/webcrawler/SalidaJFLEX.txt");
            pr = new PrintWriter(fichero1);
            pr.println("ENLACES: " + "\n\t\t" + getImagenes() + "\n" + "ENLACES IMAGENES: \n\t\t" + getLinks() + "\n");
        } catch (IOException e) {
        } finally {
            try {
                if (null != fichero1);
                fichero1.close();
            } catch (IOException e2) {
            }
        }
    }
}
