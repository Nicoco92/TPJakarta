package fr.efrei.pokemon_tcg.services.implementations;

import fr.efrei.pokemon_tcg.dto.CapturePokemon;
import fr.efrei.pokemon_tcg.dto.DresseurDTO;
import fr.efrei.pokemon_tcg.models.Dresseur;
import fr.efrei.pokemon_tcg.models.Echange;
import fr.efrei.pokemon_tcg.models.Pokemon;
import fr.efrei.pokemon_tcg.repositories.DresseurRepository;
import fr.efrei.pokemon_tcg.repositories.PokemonRepository;
import fr.efrei.pokemon_tcg.repositories.EchangeRepository;
import fr.efrei.pokemon_tcg.services.IDresseurService;
import fr.efrei.pokemon_tcg.services.IPokemonService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DresseurServiceImpl implements IDresseurService {

	private final DresseurRepository dresseurRepository;
	private final PokemonRepository pokemonRepository;
	private final IPokemonService pokemonService;
	private final EchangeRepository echangeRepository;
	private final Random random = new Random();
	private LocalDate dateDerniereReinitialisation = LocalDate.now();

	public DresseurServiceImpl(DresseurRepository dresseurRepository, PokemonRepository pokemonRepository,
							   IPokemonService pokemonService, EchangeRepository echangeRepository) {
		this.dresseurRepository = dresseurRepository;
		this.pokemonRepository = pokemonRepository;
		this.pokemonService = pokemonService;
		this.echangeRepository = echangeRepository;
	}

	@Override
	public List<Dresseur> findAll() {
		return dresseurRepository.findAllByDeletedAtNull();
	}

	@Override
	public Dresseur findById(String uuid) {
		return dresseurRepository.findById(uuid).orElse(null);
	}

	@Override
	public void capturerPokemon(String uuid, CapturePokemon capturePokemon) {
		Dresseur dresseur = findById(uuid);
		if (dresseur == null) {
			throw new RuntimeException("Dresseur introuvable !");
		}
		Pokemon pokemon = pokemonService.findById(capturePokemon.getUuid());
		if (pokemon == null) {
			throw new RuntimeException("Pokemon introuvable !");
		}
		if (dresseur.getPokemonList() == null) {
			dresseur.setPokemonList(new ArrayList<>());
		}
		dresseur.getPokemonList().add(pokemon);
		dresseurRepository.save(dresseur);
	}

	@Override
	public void create(DresseurDTO dresseurDTO) {
		Dresseur dresseur = new Dresseur();
		dresseur.setNom(dresseurDTO.getNom());
		dresseur.setPrenom(dresseurDTO.getPrenom());
		dresseur.setDeletedAt(null);
		dresseur.setPokemonList(new ArrayList<>());
		dresseurRepository.save(dresseur);
	}

	@Override
	public boolean update(String uuid, DresseurDTO dresseurDTO) {
		Dresseur dresseur = findById(uuid);
		if (dresseur == null) {
			return false;
		}
		dresseur.setNom(dresseurDTO.getNom());
		dresseur.setPrenom(dresseurDTO.getPrenom());
		dresseurRepository.save(dresseur);
		return true;
	}

	@Override
	public boolean delete(String uuid) {
		Dresseur dresseur = findById(uuid);
		if (dresseur == null) {
			return false;
		}
		dresseur.setDeletedAt(LocalDateTime.now());
		dresseurRepository.save(dresseur);
		return true;
	}

	@Override
	public boolean echangerCartes(String dresseur1Uuid, String dresseur2Uuid, String carte1Uuid, String carte2Uuid) {
		Dresseur dresseur1 = findById(dresseur1Uuid);
		Dresseur dresseur2 = findById(dresseur2Uuid);
		if (dresseur1 == null || dresseur2 == null) {
			throw new RuntimeException("Dresseur introuvable !");
		}

		LocalDateTime debutJournee = LocalDate.now().atStartOfDay();
		LocalDateTime finJournee = LocalDate.now().atTime(LocalTime.MAX);

		if (echangeRepository.existsByDresseur1AndDresseur2AndDateEchangeBetween(dresseur1, dresseur2, debutJournee, finJournee)) {
			throw new RuntimeException("Échange limité à un par jour !");
		}

		Pokemon carte1 = dresseur1.getPokemonList().stream()
				.filter(p -> p.getUuid().equals(carte1Uuid))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Carte introuvable !"));

		Pokemon carte2 = dresseur2.getPokemonList().stream()
				.filter(p -> p.getUuid().equals(carte2Uuid))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Carte introuvable !"));

		dresseur1.getPokemonList().remove(carte1);
		dresseur2.getPokemonList().remove(carte2);
		dresseur1.getPokemonList().add(carte2);
		dresseur2.getPokemonList().add(carte1);

		dresseurRepository.save(dresseur1);
		dresseurRepository.save(dresseur2);

		Echange echange = new Echange();
		echange.setDresseur1(dresseur1);
		echange.setDresseur2(dresseur2);
		echange.setCarteDonnee(carte1);
		echange.setCarteReçue(carte2);
		echange.setDateEchange(LocalDateTime.now());

		echangeRepository.save(echange);

		return true;
	}

	@Override
	public List<Pokemon> tirerCartes(String dresseurUuid) {
		Dresseur dresseur = findById(dresseurUuid);

		if (dresseur == null) {
			throw new RuntimeException("Dresseur introuvable !");
		}

		if (dresseur.getDernierTirage() != null &&
				dresseur.getDernierTirage().toLocalDate().isEqual(LocalDate.now())) {
			throw new RuntimeException("Vous avez déjà tiré des cartes aujourd’hui !");
		}

		// ✅ Réinitialiser les Pokémon s'ils ont tous été tirés ou si la date a changé
		if (pokemonRepository.findAllByEstTireFalse().isEmpty() || !dateDerniereReinitialisation.equals(LocalDate.now())) {
			resetPokemonTires();
			dateDerniereReinitialisation = LocalDate.now();
		}

		List<Pokemon> pokemonsDisponibles = pokemonRepository.findAllByEstTireFalse();
		if (pokemonsDisponibles.isEmpty()) {
			throw new RuntimeException("Aucun Pokémon disponible pour le tirage !");
		}

		Set<Pokemon> nouvellesCartes = new HashSet<>();
		while (nouvellesCartes.size() < 5 && !pokemonsDisponibles.isEmpty()) {
			Pokemon pokemonAleatoire = pokemonsDisponibles.get(random.nextInt(pokemonsDisponibles.size()));

			// ✅ Marquer le Pokémon comme tiré et le sauvegarder
			pokemonAleatoire.setEstTire(true);
			pokemonRepository.save(pokemonAleatoire);

			nouvellesCartes.add(pokemonAleatoire);
			pokemonsDisponibles.remove(pokemonAleatoire);
		}

		if (dresseur.getPokemonList() == null) {
			dresseur.setPokemonList(new ArrayList<>());
		}

		dresseur.getPokemonList().addAll(nouvellesCartes);
		dresseur.setDernierTirage(LocalDateTime.now());
		dresseurRepository.save(dresseur);

		return new ArrayList<>(nouvellesCartes);
	}

	/**
	 * ✅ Réinitialise les Pokémon tirés à zéro chaque jour.
	 */
	private void resetPokemonTires() {
		List<Pokemon> allPokemons = pokemonRepository.findAll();
		for (Pokemon pokemon : allPokemons) {
			pokemon.setEstTire(false);
		}
		pokemonRepository.saveAll(allPokemons);
	}
}
