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

	public static double distance(Vector entrada, Vector entrada2) {
		// TODO Auto-generated method stub
		return 0;
	}

	static public Vector sum(Vector v1, Vector v2) {
		Integer newX = v1.x != null && v2.x != null ? v1.x + v2.x : null;
		Integer newY = v1.y != null && v2.y != null ? v1.y + v2.y : null;
		Integer newZ = v1.z != null && v2.z != null ? v1.z + v2.z : null;

		return new Vector(newX, newY, newZ);
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
