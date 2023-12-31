package main;

import java.util.ArrayList;
import java.util.HashSet;

public class Filtro {
	private String nombre;
	private String texto;
	private ArrayList<Condicion> condiciones;
	
	public Filtro(String codigo, String texto) {
        this.nombre = codigo;
        this.texto = texto;
        this.condiciones = new ArrayList<>();
    }
	
	public void agregarCondicion(Condicion condicion) {
        this.condiciones.add(condicion);
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String codigo) {
		this.nombre = codigo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public ArrayList<Condicion> getCondiciones() {
		return condiciones;
	}

	public void setCondiciones(ArrayList<Condicion> condiciones) {
		this.condiciones = condiciones;
	}
	
	
	
}
