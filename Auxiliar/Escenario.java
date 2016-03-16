/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auxiliar;

import Sistema.Avion;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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

    private final String formatLineSeparator = " ";
    private final String formatStepTimeSeparator = "st";
    private final String formatAvionSeparator = "av";

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
            String[] firstLine = br.readLine().split(formatLineSeparator);
            numeroFilasAeropuerto = Integer.valueOf(firstLine[0]);
            numeroColumnasAeropuerto = Integer.valueOf(firstLine[1]);

            // Second line: pistas en el aeropuerto
            numeroPistas = Integer.valueOf(br.readLine());

            // Next lines: las pistas (donde se situan, por donde se entra y cuantos step se queda parada cuando un avion entra en ella)
            pistas = new LinkedList<>();
            for (int i = 0; i < numeroPistas; i++) {
                // La pista que vamos a añadir
                Pista newPista;

                // Capturamos la nueva linea
                String[] newLinePista = br.readLine().split(formatLineSeparator);

                // Posicion en el mapa
                int coorX = Integer.valueOf(newLinePista[0]);
                int coorY = Integer.valueOf(newLinePista[1]);

                // Punto por el que se entra
                int entradaX = Integer.valueOf(newLinePista[2]);
                int entradaY = Integer.valueOf(newLinePista[3]);

                // Numero de steps ocupado cuando un avion entra en la pista
                int stepsAterrizaje = Integer.valueOf(newLinePista[4]);

                // Se añade la pista
                newPista = new Pista(coorX, coorY, entradaX, entradaY, stepsAterrizaje);
                pistas.add(newPista);
            }

            // Next lines: los aviones (cuando llegan, por donde, con cuánto combustible y cuánto gastan por step)
            entradaSimuladaAviones = new HashMap<>();
            Integer stepTime = null;
            int idAvion = 0;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    // Es un nuevo step time
                    if (line.contains(formatStepTimeSeparator)) {
                        stepTime = Integer.valueOf(line.substring(2));
                    } else {
                        if (stepTime == null) {
                            throw new IllegalArgumentException("El formato del documento es erroneo: se pensó que venía una linea tipo StepTime y no fue así.");
                        }
                        // Conseguimos la lista actual de aviones en este steptime
                        List<Avion> stepTimeListAviones = entradaSimuladaAviones.get(stepTime);
                        stepTimeListAviones = stepTimeListAviones == null ? new LinkedList<>() : stepTimeListAviones;

                        // Es un nuevo avion
                        String[] newLineAvion = line.split(formatLineSeparator);

                        if (newLineAvion[0].contains(formatAvionSeparator)) {
                            Vector posicionActual = new Vector(Integer.valueOf(newLineAvion[1]), Integer.valueOf(newLineAvion[2]), null);
                            double combustibleActual = Double.valueOf(newLineAvion[3]);
                            double combustibleGastadoPorSteptime = Double.valueOf(newLineAvion[4]);

                            // Añadimos el avion
                            stepTimeListAviones.add(new Avion(formatAvionSeparator + String.valueOf(idAvion), posicionActual, combustibleActual, combustibleGastadoPorSteptime));
                            entradaSimuladaAviones.put(stepTime, stepTimeListAviones);

                            // Actualizamos el id
                            idAvion++;
                        } else {
                            throw new IllegalArgumentException("El formato del documento es erroneo: una linea no contiene av");
                        }
                    }
                }
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
    // ToString
    // =========================================================================
    @Override
    public String toString() {
        String result = "";

        // Configuracion del aeropuerto
        result = result + "Tenemos un aeropuerto de " + numeroFilasAeropuerto + "x" + numeroColumnasAeropuerto + " con " + numeroPistas + " pistas de aterrizaje.\n";
        result = result + "Las pistas son las siguientes:\n";

        // Las pistas del aeropuerto
        for (Pista pista : pistas) {
            result = result + pista.toString();
        }
        // Los aviones que llegan
        for (Map.Entry pair : entradaSimuladaAviones.entrySet()) {
            // Cogemos el par <step, aviones>
            // Mostramos los aviones para este stepTime
            result = result + "=============";
            result = result + "Step time: " + pair.getKey();
            result = result + "=============\n";
            List<Avion> stepTimeAviones = (List<Avion>) pair.getValue();
            for (Avion next : stepTimeAviones) {
                result = result + next.toString() + "\n";
            }
        }
        return result;
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
