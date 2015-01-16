package com.example.followme;

public class Utente {
	
	private String id;
	private String numero;
	
	public Utente(String id, String numero)
	{
		this.id=id;
		this.numero=numero;
	}
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	

}
