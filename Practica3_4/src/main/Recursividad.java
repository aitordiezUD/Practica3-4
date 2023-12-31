package main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Recursividad {
	
	protected static ArrayList<ArrayList<Carta>> solucionesManosSinFiltro;
	protected static ArrayList<ArrayList<Carta>> solucionesManosConFiltro;
	protected static int n;
	
	public static void main(String[] args) {
		BD.setRun(true); //Poner a true para incorporar la parte de Bases de Datos
		BD.init();
		BD.delete(); //Para borrar las tablas que estan ya creadas;
		String ejemplo1 = "Buenos dias";
		String ejemplo2 = "Hola, mundo!";
		String ejemplo3 = "Feliz Navidad y prospero año nuevo";
		
//		INVERTIR FRASE:
		System.out.println(invertirFrase(ejemplo1, ""));
		System.out.println(invertirFrase(ejemplo2, ""));
		System.out.println(invertirFrase(ejemplo3, ""));
		 
//		INVERTIR PALABRAS:
		System.out.println(invertirPalabras(ejemplo1));
		System.out.println(invertirPalabras(ejemplo2));
		System.out.println(invertirPalabras(ejemplo3));
		
		try {
			Thread.sleep(20000); //Este sleep es para poder visualizar unos segundos las soluciones de Invertir Frases y Palabras
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	PROBANDO POSIBLES MANOS
		ArrayList<Carta> baraja = (ArrayList<Carta>) crearBaraja();
		n = 3;
		 
		solucionesManosSinFiltro= new ArrayList<ArrayList<Carta>>();
		posiblesManos(n, baraja, new ArrayList<Carta>(), solucionesManosSinFiltro);
		imprimirSolucionManos(baraja.size(), n);
		 
	//  PROBANDO POSIBLES MANOS CON FILTRO
		solucionesManosConFiltro = new ArrayList<>();
		
//		CREACION DEL FILTRO DE CARTAS CON AL MENOS UN AS
		Filtro filtroAs = new Filtro("as", "Solo manos al menos un as");
		filtroAs.agregarCondicion(new Condicion() {
			@Override
			public boolean seCumple(ArrayList<Carta> mano) {
				// TODO Auto-generated method stub
				for (Carta c : mano) {
					if (c.getValor().equals("As")) {
						return true;
					}
				}
				return false;
			}
		 });
		
		 System.out.println("Comenzando filtro As.");
		 BD.guardaManos(n, baraja, new ArrayList<>(), solucionesManosConFiltro, filtroAs);
		 System.out.println("Filtro As finalizado.");
		 
//		CREACION DEL FILTRO POKER
//		Este ejemplo lo voy a probar con manos de 3 cartas, que tengan al menos 2 cartas de la misma figura en 3 cartas
		n = 3;
		solucionesManosConFiltro = new ArrayList<>();
		Filtro filtroPoker = new Filtro("Poker", "En una mano de 3 cartas, existen al menos 2 cartas de la misma figura");
		filtroPoker.agregarCondicion(new Condicion() {
			
			@Override
			public boolean seCumple(ArrayList<Carta> mano) {
				// TODO Auto-generated method stub
				String figura1 = "";
				int contadorFigura1 = 0;
				String figura2 = "";
				int contadorFigura2 = 0;
				for (Carta c : mano) {
					if (figura1.equals("")) {
						figura1 = c.getPalo();
					} else if (figura2.equals("") && !c.getPalo().equals(figura1)) {
						figura2 = c.getPalo();
					}
					
					if (figura1.equals(c.getPalo())) {
						contadorFigura1++;
					} else if (figura2.equals(c.getPalo())) {
						contadorFigura2++;
					}
				}
				return contadorFigura1 >= 2 || contadorFigura2>=2; 
			}
		});
		
		System.out.println("Comenzando filtro Poker");
		BD.guardaManos(n, baraja, new ArrayList<>(), solucionesManosConFiltro, filtroPoker);
		System.out.println("Filtro Poker finalizado");
		
//		 CREACION DEL FILTRO DE CARTAS FULL
//		 Este ejemplo lo voy a probar con manos de 3 cartas, que tengan una carta de un palo y las otras dos de otro ya que sino el tiempo de ejecución es demasiado alto
		 n = 3;
		 solucionesManosConFiltro = new ArrayList<>();
		 Filtro filtroFull = new Filtro("Full", "En una mano de 3 cartas existen 2 cartas de una figura y 1 de otra figura");
		 filtroFull.agregarCondicion(new Condicion() {
			@Override
			public boolean seCumple(ArrayList<Carta> mano) {
				// TODO Auto-generated method stub
				String figura1 = "";
				int contadorFigura1 = 0;
				String figura2 = "";
				int contadorFigura2 = 0;
				
				for (Carta c : mano) {
					if (figura1.equals("")) {
						figura1 = c.getPalo();
					} else if (figura2.equals("") && !c.getPalo().equals(figura1)) {
						figura2 = c.getPalo();
					}
					if (figura1.equals(c.getPalo())) {
						contadorFigura1++;
					} else if (figura2.equals(c.getPalo())) {
						contadorFigura2++;
					}
				}
				return (contadorFigura1 + contadorFigura2) == 3 && !figura2.equals("");
			}
		 });
		 
		 System.out.println("Comenzando filtro Full");
		 BD.guardaManos(n, baraja, new ArrayList<Carta>(), solucionesManosConFiltro, filtroFull);
		 System.out.println("Filtro Full finalizado");
		 
//		 CREACION DEL FILTRO ESCALERA
//		 Este ejemplo lo voy a probar con manos de 3 cartas ya que sino el tiempo de ejecución es demasiado alto
		 n = 3;
		 solucionesManosConFiltro = new ArrayList<>();
		 Filtro filtroEscalera = new Filtro("Escalera", "En una mano de 3 cartas existen 3 cartas consecutivas del mismo palo");
		 filtroEscalera.agregarCondicion(new Condicion() {
			@Override
			public boolean seCumple(ArrayList<Carta> mano) {
				// TODO Auto-generated method stub
				ArrayList<Integer> listaValores = new ArrayList<>();
				String palo = "";
				for (Carta c : mano) {
					int valor;
					if (palo.equals("")) {
						palo = c.getPalo();
					}
					if (!palo.equals(c.getPalo())) {
						return false;
					}
					switch (c.getValor()) {
						case "2":
							valor = 2;
							break;
						case "3":
							valor = 3;
							break;
						case "4":
							valor = 4;
							break;
						case "5":
							valor = 5;
							break;
						case "6":
							valor = 6;
							break;
						case "7":
							valor = 7;
							break;
						case "8":
							valor = 8;
							break;
						case "9":
							valor = 9;
							break;
						case "10":
							valor = 10;
							break;
						case "Jota":
							valor = 11;
							break;
						case "Reina":
							valor = 12;
							break;
						case "Rey":
							valor = 13;
							break;
						default:
							valor = 14;
							break;
					}
					listaValores.add(valor);
				}
				Collections.sort(listaValores);
				int menor = listaValores.get(0);
				int mayor = listaValores.get(listaValores.size()-1);
				return (mayor-menor == listaValores.size()-1);
			}
		});
		 
		System.out.println("Comenzando filtro Escalera");
		BD.guardaManos(n, baraja, new ArrayList<Carta>(), solucionesManosConFiltro, filtroEscalera);
		System.out.println("Filtro Escalera finalizado");
		 
		 
		BD.fin(); 
	}
	

	
	
	

     public static void imprimirSolucionManos(int sizeBaraja, int n) {
		 System.out.println("Manos esperadas: " + calcularManosPosibles(sizeBaraja, n));
		 System.out.println("Manos obtenidas: " + solucionesManosSinFiltro.size());
     }
    
	 public static BigInteger calcularManosPosibles(int n, int k) {
		 BigInteger numerador = factorial(n);
		 BigInteger denominador = factorial(k).multiply(factorial(n - k));
		 return numerador.divide(denominador);
	 }
	 
	 private static BigInteger factorial(int n) {
		 if (n<=1) {
			 return BigInteger.ONE;
		 }else {
			 return BigInteger.valueOf(n).multiply(factorial(n - 1));
		 }
	 };
	 
    public static void posiblesManos(int n, ArrayList<Carta> baraja, ArrayList<Carta> actual, ArrayList<ArrayList<Carta>> soluciones) {
    	if (n == 0) {
    		if (!contieneSolucion(soluciones, actual)) {    			
    			soluciones.add(new ArrayList<>(actual));
    			System.err.println(actual);
    		}
    	}else {
    		for (int i=0; i<baraja.size(); i++) {
    			Carta c = baraja.get(i);
    			if(!actual.contains(c)) {
    				actual.add(c);
    				posiblesManos(n-1, baraja, actual, soluciones);
        			actual.remove(c);
    			}
    		}
    	}
    };
    
    
    public static void filtroManos(int n, ArrayList<Carta> baraja, ArrayList<Carta> manoActual, ArrayList<ArrayList<Carta>> soluciones, Filtro filtro, long idFiltro) {
    	if (n == 0) {
    		if (cumpleFiltro(manoActual,filtro) && !contieneSolucion(soluciones, manoActual)) {    			
    			soluciones.add(new ArrayList<>(manoActual));
    			System.err.println(manoActual);
    			BD.subirMano(idFiltro, manoActual);
    		}
    	}else {
    		for (int i=0; i<baraja.size(); i++) {
    			Carta c = baraja.get(i);
    			if(!manoActual.contains(c)) {
    				manoActual.add(c);
    				filtroManos(n-1, baraja, manoActual, soluciones, filtro,idFiltro);
        			manoActual.remove(c);
    			}
    		}
    	}
    };
    
    private static boolean cumpleFiltro(ArrayList<Carta> cartas, Filtro filtro) {
        for (Condicion condicion : filtro.getCondiciones()) {
            if (!condicion.seCumple(cartas)) {
                return false;
            }
        }
        return true;
    }
    
    
    public static boolean contieneSolucion(ArrayList<ArrayList<Carta>> listaSoluciones, ArrayList<Carta> nuevaSolucion) {
    	for (ArrayList<Carta> solucion : listaSoluciones) {
    		if (sonEquivalentes(solucion, nuevaSolucion)) {
    			return true;
    		}
    	}
    	return false;
    };
    
    public static boolean sonEquivalentes(ArrayList<Carta> lista1, ArrayList<Carta> lista2) {
    	for (Carta carta : lista1) {
            if (!lista2.contains(carta)) {
                return false;
            }
        }

        return true;
    };
    
//  FUNCION PARA CREAR UNA BARAJA FRANCESA
    public static List<Carta> crearBaraja() {
        List<Carta> baraja = new ArrayList<>();

        String[] palos = {"Corazones", "Diamantes", "Tréboles", "Picas"};
        String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jota", "Reina", "Rey", "As"};

        for (String palo : palos) {
            for (String valor : valores) {
                Carta carta = new Carta(palo, valor);
                baraja.add(carta);
            }
        }

        return baraja;
    }
	 
	public static String invertirFrase(String frase, String resultado) {
		if (frase.length() == 0) {
			return resultado;
		} else {
			int index = frase.length()-1;
			resultado = resultado + frase.charAt(index);
			frase = frase.substring(0, index);
			return invertirFrase(frase, resultado);
		}
	}
   
   public static String invertirPalabras(String frase) {
   	String[] simbolos = {" ","\t","\n",".",";",",",":" };
   	ArrayList<String> simbolosArrayList = new ArrayList<>(Arrays.asList(simbolos));
   	if (frase.trim().length() == 0) {
   		return frase;
   	} else {
   		int indexSeparador = -1;
   		for (int i=0; i < frase.length(); i++) {
   			char character = frase.charAt(i);
   			if (simbolosArrayList.contains(String.valueOf(character))) {
   				indexSeparador = i;
   				break;
   			}
   		}
   		
   		if (indexSeparador == -1) {
   			return frase;
   		}
   		
   		return invertirPalabras(frase.substring(indexSeparador+1)) + frase.charAt(indexSeparador) + frase.substring(0,indexSeparador);
   	}
   	
   };
    
}
