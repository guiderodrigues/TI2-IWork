package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Prestador;

public class PrestadorDAO {
    private Connection conexao;

    public PrestadorDAO(){
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

    public boolean inserirElemento(Prestador Prestador){
        boolean status = false;
		try {
			Statement st = conexao.createStatement();
			if(Prestador.getID() == 0){
				ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM Prestador");
				if(rs.next()) Prestador.setID(rs.getInt("max") + 1);
			}
			st.executeUpdate("INSERT INTO Prestador (ID, email, senha, nome, nomePrestador, categoria, preco, nota, nAvaliacoes, nPedidos, telefone, descricao, imagens) " +
					"VALUES (" + Prestador.getID() + ", '" + Prestador.getEmail() + "', '" + Prestador.getSenha() + "', '" + Prestador.getNome() + "', '" + Prestador.getNomePrestador() + "', '" + Prestador.getCategoria() + "', '" + Prestador.getPreco() + "', '" + Prestador.getNota() + "', '" + Prestador.getNAvaliacoes() + "', '" + Prestador.getNPedidos() + "', '" + Prestador.getTelefone() + "', '" + Prestador.getDescricao() + "', '" + Prestador.getImagens() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
    }

    public boolean atualizarElemento(Prestador Prestador) {
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Prestador SET ID = '" + Prestador.getID() + "', email = '" + Prestador.getEmail() + "', senha = '" + Prestador.getSenha() + "', nome = '" + Prestador.getNome() + "', nomePrestador = '" + Prestador.getNomePrestador() + "', categoria = '" + Prestador.getCategoria() + "', preco = '" + Prestador.getPreco() + "', nota = '" + Prestador.getNota() + "', nAvaliacoes = '" + Prestador.getNAvaliacoes() + "', nPedidos = '" + Prestador.getNPedidos() + "', telefone = '" + Prestador.getTelefone() + "', descricao = '" + Prestador.getDescricao() + "', imagens = '" + Prestador.getImagens() + "' WHERE ID = " + Prestador.getID() + ";";
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

	public boolean atualizarElemento(int id, String nome, String imagens, String descricao, float preco, int categoria) {
		boolean status = false;
		try {
			Statement st = conexao.createStatement();
			System.out.println(categoria);
			String sql = "UPDATE Prestador SET nome = '" + nome + "', imagens = '" + imagens + "', descricao = '" + descricao + "', preco = '" + preco + "', categoria = " + categoria + " WHERE ID = " + id + ";";
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
			st.executeUpdate("DELETE FROM Prestador WHERE ID = " + ID);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

    public Prestador[] getElementos() {
		Prestador[] elementos = null;

		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Prestador");
			if (rs.next()) {
				rs.last();
				elementos = new Prestador[rs.getRow()];
				rs.beforeFirst();

				for (int i = 0; rs.next(); i++) {
					elementos[i] = new Prestador(rs.getInt("ID"), rs.getString("nome"), rs.getString("nomePrestador"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getInt("categoria"), rs.getFloat("nota"), rs.getInt("nAvaliacoes"), rs.getInt("nPedidos"), rs.getString("descricao"), rs.getString("imagens"), rs.getFloat("preco"));
				}
			}
			st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return elementos;
	}

    public Prestador getElemento(int ID) {
		Prestador elemento = null;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM Prestador WHERE ID = " + ID);
			if(rs.next()){
				elemento = new Prestador(rs.getInt("ID"), rs.getString("nome"), rs.getString("nomePrestador"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getInt("categoria"), rs.getFloat("nota"), rs.getInt("nAvaliacoes"), rs.getInt("nPedidos"), rs.getString("descricao"), rs.getString("imagens"), rs.getFloat("preco"));
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return elemento;
	}

	public String getSenhaFromEmail(String email){
		try{
			String senha = null;
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT senha FROM Prestador WHERE email = '" + email + "'");
			if(rs.next()){
				senha = rs.getString("senha");
			}
			return senha;
		}catch(Exception e){
			System.err.println(e.getMessage());
			return null;
		}
	}

	public int getLargestID(){
		int id = 0;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM Prestador");
			if(rs.next()){
				id = rs.getInt("max");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return id;
	}

    public void updateNota(int prestador, float nota) {
        try {
            Statement st = conexao.createStatement();
            String sql = "UPDATE Prestador SET nota = '" + nota + "' WHERE ID = " + prestador + ";";
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
    }

    public void updateNAvaliacoes(int prestador, int nAvaliacoes) {
        try {
            Statement st = conexao.createStatement();
            String sql = "UPDATE Prestador SET nAvaliacoes = '" + nAvaliacoes + "' WHERE ID = " + prestador + ";";
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
    }

    public void updateNPedidos(int prestador, int nPedidos) {
        try {
            Statement st = conexao.createStatement();
            String sql = "UPDATE Prestador SET nPedidos = '" + nPedidos + "' WHERE ID = " + prestador + ";";
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
    }

    public void updateImagens(int prestador, String imagens) {
        try {
            Statement st = conexao.createStatement();
            String sql = "UPDATE Prestador SET imagens = '" + imagens + "' WHERE ID = " + prestador + ";";
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
    }

    public void updateDescricao(int prestador, String descricao) {
        try {
            Statement st = conexao.createStatement();
            String sql = "UPDATE Prestador SET descricao = '" + descricao + "' WHERE ID = " + prestador + ";";
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
    }

	public void updatePreco(int prestador, float preco) {
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Prestador SET preco = '" + preco + "' WHERE ID = " + prestador + ";";
			st.executeUpdate(sql);
			st.close();
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
	}

    private List<Prestador> get(String orderBy) {
		List<Prestador> Prestadores = new ArrayList<Prestador>();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT * FROM Prestador" + ((orderBy.trim().length() == 0) ? "" : (" ORDER BY " + orderBy));
			ResultSet rs = st.executeQuery(sql);	           
	        while(rs.next()) {	            	
	        	Prestador p = new Prestador(rs.getInt("ID"), rs.getString("nome"), rs.getString("nomePrestador"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getInt("categoria"), rs.getFloat("nota"), rs.getInt("nAvaliacoes"), rs.getInt("nPedidos"), rs.getString("descricao"), rs.getString("imagens"), rs.getFloat("preco"));
	            Prestadores.add(p);
	        }
	        st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return Prestadores;
	}

    public List<Prestador> get() {
		return get("");
	}

	
	public List<Prestador> getOrderByID() {
		return get("id");		
	}
	
	
	public List<Prestador> getOrderByNome() {
		return get("nome");		
	}

    public List<Prestador> getOrderByNota(){
        return get("nota");
    }

    public List<Prestador> getOrderByNPedidos(){
        return get("nPedidos");
    }

    public List<Prestador> getOrderByCategoria(){
        return get("categoria");
    }
    
    public List<Prestador> getOrderByPreco(){
        return get("preco");
    }

	public List<Prestador> get(int categoria, String search, int minPrice, int maxPrice, int order){
		List<Prestador> Prestadores = new ArrayList<Prestador>();
		String sql = "SELECT * FROM Prestador";
		boolean gotAProperty = false;
		if(categoria >= 0 && categoria <= 23){
			sql += " WHERE categoria = " + categoria;
			gotAProperty = true;	
		}
		if(minPrice >= 0){
			if(gotAProperty) sql += " AND ";
			else sql += " WHERE ";
			sql += "preco >= " + minPrice;
			gotAProperty = true;
		}
		if(maxPrice >= 0){
			if(gotAProperty) sql += " AND ";
			else sql += " WHERE ";
			sql += "preco <= " + maxPrice;
			gotAProperty = true;
		}
		
		if(search != null && !search.equals("")){
			if(gotAProperty) sql += " AND ";
			else sql += " WHERE ";
			sql += "nome LIKE '%" + search + "%'";
			gotAProperty = true;
		}

		switch(order){
			case 0:
				sql += " ORDER BY preco DESC";
				break;
			case 1:
				sql += " ORDER BY preco DESC";
				break;
			case 2:
				sql += " ORDER BY nota DESC";
				break;
			case 3:
				sql += " ORDER BY nPedidos DESC";
				break;
		}
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
				Prestador p = new Prestador(rs.getInt("ID"), rs.getString("nome"), rs.getString("nomePrestador"), rs.getString("email"), rs.getString("senha"), rs.getString("telefone"), rs.getInt("categoria"), rs.getFloat("nota"), rs.getInt("nAvaliacoes"), rs.getInt("nPedidos"), rs.getString("descricao"), rs.getString("imagens"), rs.getFloat("preco"));
				Prestadores.add(p);
			}
			st.close();
		}catch(Exception e){
			System.err.println(e.getMessage());
		}

		return Prestadores;
	}

	public int getIDFromEmail(String email){
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT ID FROM Prestador WHERE email = '" + email + "'");
			if(rs.next()){
				return rs.getInt("ID");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return -1;
	}

	public String getSenhaFromID(int ID){
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT senha FROM Prestador WHERE ID = " + ID);
			if(rs.next()){
				return rs.getString("senha");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return null;
	}

	public String toJson(List<Prestador> Prestadores){
		String json = "[";
		for(int i = 0; i < Prestadores.size(); i++){
			json += Prestadores.get(i).toJson();
			if(i < Prestadores.size() - 1) json += ",";
		}
		json += "]";
		return json;
	}

	public String toJsonSafe(List<Prestador> Prestadores){
		String json = "[";
		for(int i = 0; i < Prestadores.size(); i++){
			json += Prestadores.get(i).toJsonSafe();
			if(i < Prestadores.size() - 1) json += ",";
		}
		json += "]";
		return json;
	}

	public float getNota(int id){
		float nota = 0.0f;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT nota FROM Prestador WHERE ID = " + id);
			if(rs.next()){
				nota = rs.getFloat("nota");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return nota;
	}

	public int getNAvaliacoes(int id){
		int nAvaliacoes = 0;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT nAvaliacoes FROM Prestador WHERE ID = " + id);
			if(rs.next()){
				nAvaliacoes = rs.getInt("nAvaliacoes");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return nAvaliacoes;
	}

	public boolean rate(int id, int nota){
		boolean status = false;
		float notaAtual = getNota(id);
		int nAvaliacoes = getNAvaliacoes(id);
		if(notaAtual == -1.0f) notaAtual = 0.0f;
		float novaNota = (notaAtual * nAvaliacoes + nota) / (nAvaliacoes + 1);
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Prestador SET nota = " + novaNota + ", nAvaliacoes = " + (nAvaliacoes+1) + " WHERE ID = " + id + ";";
			System.out.println(sql);
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

	public boolean changeRating(int id, int notaAntiga, int notaNova){
		boolean status = false;
		float notaAtual = getNota(id);
		int nAvaliacoes = getNAvaliacoes(id);
		float novaNota = (notaAtual * nAvaliacoes - notaAntiga + notaNova) / (nAvaliacoes);
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Prestador SET nota = " + novaNota + " WHERE ID = " + id + ";";
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

	public boolean incrementNPedidos(int id){
		boolean status = false;
		int nPedidos = getNPedidos(id);
		try {
			Statement st = conexao.createStatement();
			String sql = "UPDATE Prestador SET nPedidos = " + (nPedidos+1) + " WHERE ID = " + id + ";";
			System.out.println(sql);
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

	public int getNPedidos(int id) {
		int nPedidos = 0;
		try{
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT nPedidos FROM Prestador WHERE ID = " + id);
			if(rs.next()){
				nPedidos = rs.getInt("nPedidos");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return nPedidos;
	}
}
