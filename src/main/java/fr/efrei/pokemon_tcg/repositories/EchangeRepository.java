package fr.efrei.pokemon_tcg.repositories;

import fr.efrei.pokemon_tcg.models.Dresseur;
import fr.efrei.pokemon_tcg.models.Echange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface EchangeRepository extends JpaRepository<Echange, String> {
    boolean existsByDresseur1AndDresseur2AndDateEchangeBetween(
            Dresseur dresseur1, Dresseur dresseur2, LocalDateTime start, LocalDateTime end
    );
}
