/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auxiliar;

/**
 * @author Javier Moreno
 */
public class Pista {

    private int coordenadaX;
    private int coordenadaY;

    private int entradaX;
    private int entradaY;

    private int stepsAterrizaje;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================
    public Pista() {
        
    }
    
    public Pista(int coorX, int coorY, int entX, int entY, int time) {
        this.coordenadaX = coorX;
        this.coordenadaY = coorY;
        this.entradaX = entX;
        this.entradaY = entY;
        this.stepsAterrizaje = time;
    }
    
    // =========================================================================
    // ToString
    // =========================================================================
    @Override
    public String toString(){
      return "Pista: ["+getCoordenadaX()+
            ","+getCoordenadaY()+
            ","+getEntradaX()+
            ","+getEntradaY()+
            ","+getStepsAterrizaje()+"]\n";
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public void setCoordenadaX(int coordenadaX) {
        this.coordenadaX = coordenadaX;
    }

    public int getCoordenadaX() {
        return this.coordenadaX;
    }

    public void setCoordenadaY(int coordenadaY) {
        this.coordenadaY = coordenadaY;
    }

    public int getCoordenadaY() {
        return this.coordenadaY;
    }

    public void setEntradaX(int entradaX) {
        this.entradaX = entradaX;
    }

    public int getEntradaX() {
        return this.entradaX;
    }

    public void setEntradaY(int entradaY) {
        this.entradaY = entradaY;
    }

    public int getEntradaY() {
        return this.entradaY;
    }

    public void setStepsAterrizaje(int stepsAterrizaje) {
        this.stepsAterrizaje = stepsAterrizaje;
    }

    public int getStepsAterrizaje() {
        return this.stepsAterrizaje;
    }
}
