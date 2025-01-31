package fr.efrei.pokemon_tcg.repositories;

import fr.efrei.pokemon_tcg.constants.TypePokemon;
import fr.efrei.pokemon_tcg.models.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, String> {

	List<Pokemon> findAllByEstTireFalse(); // ✅ Récupère uniquement les Pokémon non tirés
	List<Pokemon> findAllByType(TypePokemon type); // ✅ Ajout de la recherche par type
}
