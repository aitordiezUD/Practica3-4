package main;

import java.sql.*;
import java.util.ArrayList;

public class BD {
	private static Connection conn = null;
	private static PreparedStatement prepStatement;
	private static Statement stmt;
	private static boolean run = false; //Poner a true si queremos usar la Base de Datos
	
	
	public static void setRun(boolean run) {
		BD.run = run;
	}

	public static void init() {
		if (run) {
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:practica34.db" );
				System.out.println("Base de datos inicializada");
			} catch (Exception e) {
				// TODO: handle exception
				System.exit(0);
			}
		}
		
	}
	
	public static void fin() {
		try {
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void delete() {
//		No asigno id a ninguna tabla ya que automaticamente se genera el rowid
		if (run) {
			String sql = 
					"drop table if exists CartaMano;\r\n"
					+ "drop table if exists ManoFiltro;\r\n"
					+ "drop table if exists carta;\r\n"
					+ "drop table if exists filtro;\r\n"
					+ "\r\n"
					+ "create table Carta (\r\n"
					+ "	id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n"
					+ "	palo varchar(30),\r\n"
					+ "	valor varchar(30)\r\n"
					+ ");\r\n"
					+ "\r\n"
					+ "\r\n"
					+ "create table Filtro(\r\n"
					+ "	id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n"
					+ "	nombre varchar(30),\r\n"
					+ "	descripcion mediumtext\r\n"
					+ ");\r\n"
					+ "\r\n"
					+ "create table ManoFiltro(\r\n"
					+ "	id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n"
					+ "	id_filtro int,\r\n"
					+ "	foreign key (id_filtro) references Filtro(id) ON DELETE CASCADE\r\n"
					+ ");\r\n"
					+ "\r\n"
					+ "create table CartaMano(\r\n"
					+ "	id_mano int,\r\n"
					+ "	id_carta int,\r\n"
					+ "	foreign key (id_mano) references ManoFiltro(id) ON DELETE CASCADE,\r\n"
					+ "	foreign key (id_carta) references Carta(id) \r\n"
					+ ");";
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public static void subirBaraja(ArrayList<Carta> baraja) {
		if (run) {
			for (Carta c : baraja) {
				subirCarta(c);
			}
		}
	};
	
	private static void subirCarta(Carta carta) {
		if (run) {
			try {
				prepStatement = conn.prepareStatement("select palo,valor from carta where palo = ? and valor = ?");
				prepStatement.setString(1, carta.getPalo());
				prepStatement.setString(2, carta.getValor());
				ResultSet rs = prepStatement.executeQuery();
				if (rs.next()) {
					rs.close();
					prepStatement.close();
				}else {
					rs.close();
					prepStatement.close();
					prepStatement = conn.prepareStatement("insert into carta(palo,valor) values(?,?)");
					prepStatement.setString(1, carta.getPalo());
					prepStatement.setString(2, carta.getValor());
					prepStatement.executeUpdate();
					prepStatement.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	public static long subirFiltro(Filtro filtro) {
		long clave;
		if (run) {
			try {
				prepStatement = conn.prepareStatement("select nombre, descripcion from filtro where nombre = ? and descripcion = ?");
				prepStatement.setString(1, filtro.getNombre());
				prepStatement.setString(2, filtro.getTexto());
				ResultSet rs = prepStatement.executeQuery();
				if (rs.next()) {
					rs.close();
					prepStatement.close();
					prepStatement = conn.prepareStatement("delete from filtro where nombre = ? and descripcion = ?");
					prepStatement.setString(1, filtro.getNombre());
					prepStatement.setString(2, filtro.getTexto());
					prepStatement.executeUpdate();
					prepStatement.close();
				}
				rs.close();
				prepStatement.close();
				prepStatement = conn.prepareStatement("insert into filtro(nombre,descripcion) values(?,?)",Statement.RETURN_GENERATED_KEYS);
				prepStatement.setString(1, filtro.getNombre());
				prepStatement.setString(2, filtro.getTexto());
				prepStatement.executeUpdate();
				ResultSet claveGenerada = prepStatement.getGeneratedKeys();
				if (claveGenerada.next()) {
					clave = claveGenerada.getLong(1);
				}else {
					claveGenerada.close();
					prepStatement.close();
					return -1;
				}
				claveGenerada.close();
				prepStatement.close();
				return clave;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return -1;
			}
		}
		return -1;
	};
	
	public static void guardaManos(int n, ArrayList<Carta> baraja, ArrayList<Carta> actual, ArrayList<ArrayList<Carta>> soluciones, Filtro filtro) {
		if (run) {
			subirBaraja(baraja);
			long idFiltro = subirFiltro(filtro);
			Recursividad.filtroManos(n, baraja, actual, soluciones, filtro, idFiltro);
		}else {
			Recursividad.filtroManos(n, baraja, actual, soluciones, filtro, -1);
		}
		
	};
	
	public static void subirMano(long idFiltro, ArrayList<Carta> mano) {
		long idMano;
		int idCarta;
		if (run) {
			try {
				prepStatement = conn.prepareStatement("insert into manofiltro(id_filtro) values(?)",Statement.RETURN_GENERATED_KEYS);
				prepStatement.setLong(1, idFiltro);
				prepStatement.executeUpdate();
				ResultSet claveGenerada = prepStatement.getGeneratedKeys();
				if (claveGenerada.next()) {
					idMano = claveGenerada.getLong(1);
				}else {
					return;
				}
				claveGenerada.close();
				prepStatement.close();
				for (Carta carta : mano) {
					prepStatement = conn.prepareStatement("select id from carta where palo = ? and valor = ?");
					prepStatement.setString(1, carta.getPalo());
					prepStatement.setString(2, carta.getValor());
					ResultSet rs = prepStatement.executeQuery();
					if (rs.next()) {
						idCarta = rs.getInt(1);
						rs.close();
						prepStatement.close();
					}else {
						rs.close();
						prepStatement.close();
						return;
					}
					prepStatement = conn.prepareStatement("insert into CartaMano values (?,?)");
					prepStatement.setLong(1, idMano);
					prepStatement.setInt(2, idCarta);
					prepStatement.executeUpdate();
					prepStatement.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		setRun(true);
		init();
	}
}
