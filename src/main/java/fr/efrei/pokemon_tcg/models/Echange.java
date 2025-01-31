package fr.efrei.pokemon_tcg.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Echange {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @ManyToOne
    private Dresseur dresseur1;

    @ManyToOne
    private Dresseur dresseur2;

    @OneToOne
    private Pokemon carteDonnee;

    @OneToOne
    private Pokemon carteReçue;

    private LocalDateTime dateEchange;

    // Getters et Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Dresseur getDresseur1() {
        return dresseur1;
    }

    public void setDresseur1(Dresseur dresseur1) {
        this.dresseur1 = dresseur1;
    }

    public Dresseur getDresseur2() {
        return dresseur2;
    }

    public void setDresseur2(Dresseur dresseur2) {
        this.dresseur2 = dresseur2;
    }

    public Pokemon getCarteDonnee() {
        return carteDonnee;
    }

    public void setCarteDonnee(Pokemon carteDonnee) {
        this.carteDonnee = carteDonnee;
    }

    public Pokemon getCarteReçue() {
        return carteReçue;
    }

    public void setCarteReçue(Pokemon carteReçue) {
        this.carteReçue = carteReçue;
    }

    public LocalDateTime getDateEchange() {
        return dateEchange;
    }

    public void setDateEchange(LocalDateTime dateEchange) {
        this.dateEchange = dateEchange;
    }
}
