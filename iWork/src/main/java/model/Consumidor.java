package model;

public class Consumidor{
	private int ID;
	private String nome;
	private String email;
	private String senha;
	private String telefone;
	private String cartao;
	private String validade;
	private String cvv;

	public Consumidor(){
		this.ID = 0;
		this.nome = "";
		this.email = "";
		this.senha = "";
		this.telefone = "";
		this.cartao = "";
		this.validade = "";
		this.cvv = "";
	}

	public Consumidor(String nome, String email, String senha, String telefone, String cartao, String validade, String cvv){
		this.ID = 0;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.telefone = telefone;
		this.cartao = cartao;
		this.validade = validade;
		this.cvv = cvv;
	}

	public Consumidor(int ID, String nome, String email, String senha, String telefone, String cartao, String validade, String cvv){
		this.ID = ID;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.telefone = telefone;
		this.cartao = cartao;
		this.validade = validade;
		this.cvv = cvv;
	}

	public int getID(){
		return this.ID;
	}
	public void setID(int ID){
		this.ID = ID;
	}

	public String getNome(){
		return this.nome;
	}
	public void setNome(String nome){
		this.nome = nome;
	}

	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email = email;
	}

	public String getSenha(){
		return this.senha;
	}
	public void setSenha(String senha){
		this.senha = senha;
	}

	public String getTelefone(){
		return this.telefone;
	}
	public void setTelefone(String telefone){
		this.telefone = telefone;
	}

	public String getCartao(){
		return this.cartao;
	}
	public void setCartao(String cartao){
		this.cartao = cartao;
	}

	public String getValidade(){
		return this.validade;
	}
	public void setValidade(String validade){
		this.validade = validade;
	}

	public String getCvv(){
		return this.cvv;
	}
	public void setCvv(String cvv){
		this.cvv = cvv;
	}

	public String toString(){
		return "ID: " + this.ID + "\nNome: " + this.nome + "\nEmail: " + this.email + "\nSenha: " + this.senha + "\nTelefone: " + this.telefone + "\nCartao: " + this.cartao + "\nValidade: " + this.validade + "\nCVV: " + this.cvv;
	}
	public String toJson(){
		return "{\"ID\": " + this.ID + ", \"nome\": \"" + this.nome + "\", \"email\": \"" + this.email + "\", \"senha\": \"" + this.senha + "\", \"telefone\": \"" + this.telefone + "\", \"cartao\": \"" + this.cartao + "\", \"validade\": \"" + this.validade + "\", \"cvv\": \"" + this.cvv + "\"}";
	}

	public String toJsonSafe(){
		return "{\"ID\": " + this.ID + ", \"nome\": \"" + this.nome + "\", \"email\": \"" + this.email + "\", \"telefone\": \"" + this.telefone + "\"}";
	}
}