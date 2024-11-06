package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import model.HorariosDia;

public class HorariosDiaDAO {
    private Connection conexao;

    public HorariosDiaDAO(){
        conexao = null;
    }

    public boolean conectar(){
        String driverName = "org.postgresql.Driver";                    
		String serverName = "localhost";
		String mydatabase = "iWork";
		int porta = 5432;
		String url = "jdbc:postgresql://" + serverName + ":" + porta +"/" + mydatabase;
		String username = "postgres";
		String password = "Gabri8448";
		boolean status = false;

		try {
			Class.forName(driverName);
			conexao = DriverManager.getConnection(url, username, password);
			status = (conexao == null);
			System.out.println("Conexão efetuada com o postgres!");
		} catch (ClassNotFoundException e) { 
			System.err.println("Conexão NÃO efetuada com o postgres -- Driver não encontrado -- " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Conexão NÃO efetuada com o postgres -- " + e.getMessage());
		}

		return status;
    }

    public boolean close() {
		boolean status = false;
		try {
			conexao.close();
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}

    public boolean inserirElemento(HorariosDia HorariosDia){
        boolean status = false;
		try {
			Statement st = conexao.createStatement();
			if(HorariosDia.getID() == 0){
				ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM HorariosDia");
				if(rs.next()) HorariosDia.setID(rs.getInt("max") + 1);
			}
			st.executeUpdate("INSERT INTO HorariosDia (ID, idPrestador, diaDaSemana, horas) " +
					"VALUES (" + HorariosDia.getID() + ", '" + HorariosDia.getIdPrestador() + "', " + HorariosDia.getDiaDaSemana() + ", '" + HorariosDia.getHoras() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
    }

	public boolean inserirElemento(int idPrestador, int diaDaSemana, String horas){
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM HorariosDia");
			int id = 0;
			if(rs.next()) id = rs.getInt("max") + 1;
			st.executeUpdate("INSERT INTO HorariosDia (ID, idPrestador, diaDaSemana, horas) " +
					"VALUES (" + id + ", '" + idPrestador + "', " + diaDaSemana + ", '" + horas + "');");
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

    public boolean atualizarElemento(HorariosDia HorariosDia) {
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE HorariosDia SET ID = '" + HorariosDia.getID() + "', idPrestador = '" + HorariosDia.getIdPrestador() + "', diaDaSemana = " + HorariosDia.getDiaDaSemana() + ", horas = '" + HorariosDia.getHoras() + "' WHERE ID = " + HorariosDia.getID() + ";";
            st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

    public boolean excluirElemento(int ID) {
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			st.executeUpdate("DELETE FROM HorariosDia WHERE ID = " + ID);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

    public HorariosDia[] getElementos() {
		HorariosDia[] elementos = null;

		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM HorariosDia");
			if (rs.next()) {
				rs.last();
				elementos = new HorariosDia[rs.getRow()];
				rs.beforeFirst();

				for (int i = 0; rs.next(); i++) {
					elementos[i] = new HorariosDia(rs.getInt("ID"), rs.getInt("idPrestador"), rs.getInt("diaDaSemana"), rs.getString("horas"));
				}
			}
			st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return elementos;
	}

    public HorariosDia getElemento(int ID) {
		HorariosDia elemento = null;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM HorariosDia WHERE ID = " + ID);
			if(rs.next()){
				elemento = new HorariosDia(rs.getInt("ID"), rs.getInt("idPrestador"), rs.getInt("diaDaSemana"), rs.getString("horas"));
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return elemento;
	}

    private List<HorariosDia> get(String orderBy) {
		List<HorariosDia> HorariosDiaes = new ArrayList<HorariosDia>();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT * FROM HorariosDia" + ((orderBy.trim().length() == 0) ? "" : (" ORDER BY " + orderBy));
			ResultSet rs = st.executeQuery(sql);	           
	        while(rs.next()) {	            	
	        	HorariosDia hd = new HorariosDia(rs.getInt("ID"), rs.getInt("idPrestador"), rs.getInt("diaDaSemana"), rs.getString("horas"));
	            HorariosDiaes.add(hd);
	        }
	        st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return HorariosDiaes;
	}

    public List<HorariosDia> get() {
		return get("");
	}

	
	public List<HorariosDia> getOrderByID() {
		return get("id");		
	}
	
	
	public List<HorariosDia> getOrderByNome() {
		return get("nome");		
	}

	public List<HorariosDia> getHorariosPrestador(int id){
		List<HorariosDia> horarios = new ArrayList<HorariosDia>();
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM HorariosDia WHERE idPrestador = " + id);
			while(rs.next()){
				HorariosDia hd = new HorariosDia(rs.getInt("ID"), rs.getInt("idPrestador"), rs.getInt("diaDaSemana"), rs.getString("horas"));
				horarios.add(hd);
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return horarios;
	}

	public HorariosDia getHorariosPrestadorDia(int id, int dia){
		HorariosDia horario = null;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM HorariosDia WHERE idPrestador = " + id + " AND diadasemana = " + dia);
			if(rs.next()){
				horario = new HorariosDia(rs.getInt("id"), rs.getInt("idprestador"), rs.getInt("diaDaSemana"), rs.getString("horas"));
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return horario;
	}

	public String toJson(HorariosDia horarios){
		return horarios.toJson();
	}

	public String toJson(List<HorariosDia> horarios){
		String json = "[";
		for(int i = 0; i < horarios.size(); i++){
			json += horarios.get(i).toJson();
			if(i < horarios.size() - 1){
				json += ",";
			}
		}
		json += "]";
		return json;
	}

	public boolean updateHorarios(int id, String[] horas){
		List<HorariosDia> horarios = new ArrayList<HorariosDia>();
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for(int i=0;i<7;i++){
				st.executeUpdate("UPDATE HorariosDia SET horas = '" + horas[i] + "' WHERE idPrestador = " + id + " AND diaDaSemana = " + i);
			}
			return true;
		}catch(Exception e){
			System.err.println(e.getMessage());
			return false;
		}
	}
}
