package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Consumidor;

public class ConsumidorDAO {
    private Connection conexao;

    public ConsumidorDAO(){
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

    public boolean inserirElemento(Consumidor consumidor){
        boolean status = false;
		try {
			Statement st = conexao.createStatement();
			if(consumidor.getID() == 0){
				ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM Consumidor");
				if(rs.next()) consumidor.setID(rs.getInt("max") + 1);
			}
			st.executeUpdate("INSERT INTO Consumidor (ID, nome, email, senha, telefone, cartao, validade, cvv) " +
					"VALUES (" + consumidor.getID() + ", '" + consumidor.getNome() + "', '" + consumidor.getEmail() + "', '" + consumidor.getSenha() + "', '" + consumidor.getTelefone() + "', '" + consumidor.getCartao() + "', '" + consumidor.getValidade() + "', '" + consumidor.getCvv() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
    }

    public boolean atualizarElemento(Consumidor consumidor) {
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Consumidor SET nome = '" + consumidor.getNome() + "', email = '" + consumidor.getEmail() + "', senha = '" + consumidor.getSenha() + "', telefone = '" + consumidor.getTelefone() + "', cartao = '" + consumidor.getCartao() + "', validade = '" + consumidor.getValidade() + "', cvv = '" + consumidor.getCvv() + "' WHERE ID = " + consumidor.getID();
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
			st.executeUpdate("DELETE FROM Consumidor WHERE ID = " + ID);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

    public Consumidor[] getElementos() {
		Consumidor[] elementos = null;

		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Consumidor");
			if (rs.next()) {
				rs.last();
				elementos = new Consumidor[rs.getRow()];
				rs.beforeFirst();

				for (int i = 0; rs.next(); i++) {
					elementos[i] = new Consumidor(rs.getInt("ID"), rs.getString("nome"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getString("cartao"), rs.getString("validade"), rs.getString("cvv"));
				}
			}
			st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return elementos;
	}

    public Consumidor getElemento(int ID) {
		Consumidor elemento = null;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Consumidor WHERE ID = " + ID);
			if(rs.next()){
				elemento = new Consumidor(rs.getInt("ID"), rs.getString("nome"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getString("cartao"), rs.getString("validade"), rs.getString("cvv"));
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return elemento;
	}

	public String getSenhaFromId(int id){
		String senha = "";
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT senha FROM Consumidor WHERE ID = " + id);
			if(rs.next()){
				senha = rs.getString("senha");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return senha;
	}

	public boolean isThereElemento(String email) {
		boolean status = false;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Consumidor WHERE email = '" + email);
			if(rs.next()){
				status = true;
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return status;
	}

	public Consumidor getElemento(String email) {
		Consumidor elemento = null;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Consumidor WHERE email = '" + email + "'");
			if(rs.next()){
				elemento = new Consumidor(rs.getInt("ID"), rs.getString("nome"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getString("cartao"), rs.getString("validade"), rs.getString("cvv"));
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return elemento;
	}

    private List<Consumidor> get(String orderBy) {
		List<Consumidor> consumidores = new ArrayList<Consumidor>();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT * FROM Consumidor" + ((orderBy.trim().length() == 0) ? "" : (" ORDER BY " + orderBy));
			ResultSet rs = st.executeQuery(sql);	           
	        while(rs.next()) {	            	
	        	Consumidor c = new Consumidor(rs.getInt("ID"), rs.getString("nome"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getString("cartao"), rs.getString("validade"), rs.getString("cvv"));
	            consumidores.add(c);
	        }
	        st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return consumidores;
	}

    public List<Consumidor> get() {
		return get("");
	}

	
	public List<Consumidor> getOrderByID() {
		return get("id");		
	}
	
	
	public List<Consumidor> getOrderByNome() {
		return get("nome");		
	}
}
