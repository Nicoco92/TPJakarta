package fr.efrei.pokemon_tcg.models;

import fr.efrei.pokemon_tcg.constants.TypePokemon;
import jakarta.persistence.*;

@Entity
public class Pokemon {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String uuid;

	private String nom;
	private Integer niveau;

	@Enumerated(EnumType.STRING)
	private TypePokemon type;

	private String attaque1;
	private String attaque2;
	private Integer rarete;

	private boolean estTire = false;


	public String getUuid() {
		return uuid;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Integer getNiveau() {
		return niveau;
	}

	public void setNiveau(Integer niveau) {
		this.niveau = niveau;
	}

	public TypePokemon getType() {
		return type;
	}

	public void setType(TypePokemon type) {
		this.type = type;
	}

	public String getAttaque1() {
		return attaque1;
	}

	public void setAttaque1(String attaque1) {
		this.attaque1 = attaque1;
	}

	public String getAttaque2() {
		return attaque2;
	}

	public void setAttaque2(String attaque2) {
		this.attaque2 = attaque2;
	}

	public Integer getRarete() {
		return rarete;
	}

	public void setRarete(Integer rarete) {
		this.rarete = rarete;
	}

	public boolean isEstTire() {
		return estTire;
	}

	public void setEstTire(boolean estTire) {
		this.estTire = estTire;
	}
}
