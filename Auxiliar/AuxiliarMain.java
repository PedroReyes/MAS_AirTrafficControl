/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auxiliar;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class AuxiliarMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            String filePath = "/Users/pedro/Dropbox/Vida_laboral/Master/ProgramacionSistemasMultiagentes/TrabajoSegundaParte/MAS_AirTrafficControl/Auxiliar/Simulacion.txt";
            File fin = new File(filePath);//dir.getCanonicalPath() + File.separator + "Auxiliar" + File.separator + "Simulacion.txt");
            System.out.println(fin.getCanonicalFile());
            Escenario escenario = new Escenario();
            escenario.readEscenario(fin.getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(AuxiliarMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
