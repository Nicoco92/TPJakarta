package fr.efrei.pokemon_tcg.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
public class Dresseur {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String uuid;

	private String nom;
	private String prenom;
	private LocalDateTime deletedAt;
	private LocalDateTime dernierTirage;

	@OneToMany
	private List<Pokemon> pokemonList;

	@ElementCollection
	private Map<String, LocalDateTime> dernierEchangeAvec; // Stocke la date du dernier échange par dresseur UUID

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public List<Pokemon> getPokemonList() {
		return pokemonList;
	}

	public void setPokemonList(List<Pokemon> pokemonList) {
		this.pokemonList = pokemonList;
	}

	public LocalDateTime getDernierTirage() {
		return dernierTirage;
	}

	public void setDernierTirage(LocalDateTime dernierTirage) {
		this.dernierTirage = dernierTirage;
	}

	public Map<String, LocalDateTime> getDernierEchangeAvec() {
		return dernierEchangeAvec;
	}

	public void setDernierEchangeAvec(Map<String, LocalDateTime> dernierEchangeAvec) {
		this.dernierEchangeAvec = dernierEchangeAvec;
	}
}
