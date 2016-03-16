/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auxiliar;

/**
 *
 * @author pedro
 */
public class Vector {

    public Integer x;
    public Integer y;
    public Integer z;

    public Vector(Integer x, Integer y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // =========================================================================
    // ToString
    // =========================================================================
    @Override
    public String toString() {
        return "Vector{" + "x=" + x + ",y=" + y + ",z=" + z + '}';
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

}
