/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auxiliar;

import Sistema.Avion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author pedro
 */
public class Escenario {

    private int numeroFilasAeropuerto;
    private int numeroColumnasAeropuerto;
    private int numeroPistas;
    private List<Pista> pistas;
    private Map<Integer, List<Avion>> entradaSimuladaAviones;

    String path;

    public Escenario() {
    }

    public void readEscenario(File file) {
        try {
            readEscenario(file.getCanonicalFile().toString());
        } catch (IOException ex) {
            Logger.getLogger(Escenario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readEscenario(String path) {
        // Abriendo el fichero
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(path)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Escenario.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Leyendo el fichero
        String line = null;
        try {
            // First line: medidas del aeropuerto
            String[] firstLine = br.readLine().split(" ");
            numeroFilasAeropuerto = Integer.valueOf(firstLine[0]);
            numeroColumnasAeropuerto = Integer.valueOf(firstLine[1]);

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Escenario.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Cerrando el fichero
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Escenario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public int getNumeroFilasAeropuerto() {
        return numeroFilasAeropuerto;
    }

    public void setNumeroFilasAeropuerto(int numeroFilasAeropuerto) {
        this.numeroFilasAeropuerto = numeroFilasAeropuerto;
    }

    public int getNumeroColumnasAeropuerto() {
        return numeroColumnasAeropuerto;
    }

    public void setNumeroColumnasAeropuerto(int numeroColumnasAeropuerto) {
        this.numeroColumnasAeropuerto = numeroColumnasAeropuerto;
    }

    public int getNumeroPistas() {
        return numeroPistas;
    }

    public void setNumeroPistas(int numeroPistas) {
        this.numeroPistas = numeroPistas;
    }

    public List<Pista> getPistas() {
        return pistas;
    }

    public void setPistas(List<Pista> pistas) {
        this.pistas = pistas;
    }

    public Map<Integer, List<Avion>> getEntradaSimuladaAviones() {
        return entradaSimuladaAviones;
    }

    public void setEntradaSimuladaAviones(Map<Integer, List<Avion>> entradaSimuladaAviones) {
        this.entradaSimuladaAviones = entradaSimuladaAviones;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
