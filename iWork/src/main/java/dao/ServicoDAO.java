package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.SysexMessage;

import model.Servico;

public class ServicoDAO {
    private Connection conexao;

    public ServicoDAO(){
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

    public boolean inserirElemento(Servico Servico){
        boolean status = false;
		try {
			Statement st = conexao.createStatement();
			if(Servico.getID() == 0){
				ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM Servico");
				if(rs.next()) Servico.setID(rs.getInt("max") + 1);
			}
			st.executeUpdate("INSERT INTO Servico (ID, data, hora, avaliacao, idConsumidor, idPrestador, comentario, endereco) " +
					"VALUES (" + Servico.getID() + ", '" + Servico.getData() + "', '" + Servico.getHora() + "', " + Servico.getAvaliacao() + ", '" + Servico.getIdConsumidor() + "', '" + Servico.getIdPrestador() + "', '" + Servico.getComentario() + "', '" + Servico.getEndereco() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
    }

    public boolean atualizarElemento(Servico Servico) {
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Servico SET ID = '" + Servico.getID() + "', data = '" + Servico.getData() + "', hora = '" + Servico.getHora() + "', avaliacao = " + Servico.getAvaliacao() + ", idConsumidor = '" + Servico.getIdConsumidor() + "', idPrestador = '" + Servico.getIdPrestador() + "', comentario = '" + Servico.getComentario() + "', endereco = '" + Servico.getEndereco() + "' WHERE ID = " + Servico.getID() + ";";
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
			st.executeUpdate("DELETE FROM Servico WHERE ID = " + ID);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

    public Servico[] getElementos() {
		Servico[] elementos = null;

		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Servico");
			if (rs.next()) {
				rs.last();
				elementos = new Servico[rs.getRow()];
				rs.beforeFirst();

				for (int i = 0; rs.next(); i++) {
					elementos[i] = new Servico(rs.getInt("ID"), rs.getString("data"), rs.getString("hora"), rs.getInt("avaliacao"), rs.getInt("idConsumidor"), rs.getInt("idPrestador"), rs.getString("comentario"), rs.getString("endereco"));
				}
			}
			st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return elementos;
	}

    public Servico getElemento(int ID) {
		Servico elemento = null;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Servico WHERE ID = " + ID);
			if(rs.next()){
				elemento = new Servico(rs.getInt("ID"), rs.getString("data"), rs.getString("hora"), rs.getInt("avaliacao"), rs.getInt("idConsumidor"), rs.getInt("idPrestador"), rs.getString("comentario"), rs.getString("endereco"));
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return elemento;
	}

    private List<Servico> get(String orderBy) {
		List<Servico> Servicoes = new ArrayList<Servico>();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT * FROM Servico" + ((orderBy.trim().length() == 0) ? "" : (" ORDER BY " + orderBy));
			ResultSet rs = st.executeQuery(sql);	           
	        while(rs.next()) {	            	
	        	Servico c = new Servico(rs.getInt("ID"), rs.getString("data"), rs.getString("hora"), rs.getInt("avaliacao"), rs.getInt("idConsumidor"), rs.getInt("idPrestador"), rs.getString("comentario"), rs.getString("endereco"));
	            Servicoes.add(c);
	        }
	        st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return Servicoes;
	}

    public List<Servico> get() {
		return get("");
	}

	
	public List<Servico> getOrderByData() {
		return get("data");		
	}
	
	
	public List<Servico> getOrderByIdConsumidor() {
		return get("idConsumidor");		
	}

    public List<Servico> getOrderByIdPrestador() {
        return get("idPrestador");
    }

	public List<Servico> get(int idPrestador, int idConsumidor, String minData, String maxData){
		List<Servico> servicos = new ArrayList<Servico>();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT * FROM Servico";
			if(idPrestador>=0) sql += " WHERE idPrestador = " + idPrestador;
			if(idConsumidor>=0) sql += " WHERE idConsumidor = " + idConsumidor;
			if(minData != null && minData.length()>0 && !minData.equals("undefined")) sql += " AND data >= '" + minData + "'";
			if(maxData != null && maxData.length()>0 && !maxData.equals("undefined")) sql += " AND data <= '" + maxData + "'";
			sql += " ORDER BY data";
			ResultSet rs = st.executeQuery(sql);	           
	        while(rs.next()) {	            	
	        	Servico c = new Servico(rs.getInt("ID"), rs.getString("data"), rs.getString("hora"), rs.getInt("avaliacao"), rs.getInt("idConsumidor"), rs.getInt("idPrestador"), rs.getString("comentario"), rs.getString("endereco"));
	            servicos.add(c);
	        }
	        st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return servicos;
	}

	public boolean inserirElemento(int idPrestador, int idCliente, String data, String hora, String endereco){
		System.out.println(idPrestador + " " + idCliente + " " + data + " " + hora + " " + endereco);
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM Servico");
			int id = 0;
			if(rs.next()) id = rs.getInt("max") + 1;
			System.out.println("INSERT INTO Servico (ID, data, hora, avaliacao, idConsumidor, idPrestador, comentario, endereco) " +
					"VALUES (" + id + ", '" + data + "', '" + hora + "', " + -1 + ", '" + idCliente + "', '" + idPrestador + "', '" + "" + "', '" + endereco + "');");
			st.executeUpdate("INSERT INTO Servico (ID, data, hora, avaliacao, idConsumidor, idPrestador, comentario, endereco) " +
					"VALUES (" + id + ", '" + data + "', '" + hora + "', " + -1 + ", '" + idCliente + "', '" + idPrestador + "', '" + "" + "', '" + endereco + "');");
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

	public String toJson(List<Servico> servicos){
		String json = "[";
		for(int i=0;i<servicos.size();i++){
			json += servicos.get(i).toJson();
			if(i<servicos.size()-1) json += ",";
		}
		json += "]";
		return json;
	}

	public boolean rate(int id, int nota, String comentario){
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Servico SET";
			if(nota > 0) sql += " avaliacao = " + nota;
			if(nota > 0 && comentario != null && comentario.length() > 0) sql += ",";
			if(comentario != null && comentario.length() > 0) sql += " comentario = '" + (!comentario.equals("undefined")?comentario:"") + "'";
			sql += " WHERE ID = " + id;
			System.out.println(sql);
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}
}
