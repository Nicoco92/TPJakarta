package fr.efrei.pokemon_tcg.services.implementations;

import fr.efrei.pokemon_tcg.dto.CapturePokemon;
import fr.efrei.pokemon_tcg.dto.DresseurDTO;
import fr.efrei.pokemon_tcg.models.Dresseur;
import fr.efrei.pokemon_tcg.models.Pokemon;
import fr.efrei.pokemon_tcg.repositories.DresseurRepository;
import fr.efrei.pokemon_tcg.repositories.PokemonRepository;
import fr.efrei.pokemon_tcg.services.IDresseurService;
import fr.efrei.pokemon_tcg.services.IPokemonService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DresseurServiceImpl implements IDresseurService {

	private final DresseurRepository dresseurRepository;
	private final PokemonRepository pokemonRepository;
	private final IPokemonService pokemonService;
	private final Random random = new Random();

	public DresseurServiceImpl(DresseurRepository dresseurRepository, PokemonRepository pokemonRepository, IPokemonService pokemonService) {
		this.dresseurRepository = dresseurRepository;
		this.pokemonRepository = pokemonRepository;
		this.pokemonService = pokemonService;
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
		dresseur.setPokemonList(new ArrayList<>()); // Initialisation de la liste
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

	// 🔹 Méthode pour tirer 5 cartes Pokémon (corrigée pour éviter les doublons)
	@Override
	public List<Pokemon> tirerCartes(String dresseurUuid) {
		Dresseur dresseur = findById(dresseurUuid);

		if (dresseur == null) {
			System.out.println("⚠️ Dresseur non trouvé avec UUID : " + dresseurUuid);
			throw new RuntimeException("Dresseur introuvable !");
		}

		// Vérification du dernier tirage
		if (dresseur.getDernierTirage() != null &&
				dresseur.getDernierTirage().toLocalDate().isEqual(LocalDate.now())) {
			throw new RuntimeException("Vous avez déjà tiré des cartes aujourd’hui !");
		}

		// Récupérer toutes les cartes disponibles
		List<Pokemon> toutesLesCartes = pokemonRepository.findAll();
		if (toutesLesCartes.isEmpty()) {
			throw new RuntimeException("Aucune carte disponible en base !");
		}

		// Tirer 5 cartes uniques
		Set<Pokemon> nouvellesCartes = new HashSet<>();
		while (nouvellesCartes.size() < 5) {
			Pokemon pokemonAleatoire = toutesLesCartes.get(random.nextInt(toutesLesCartes.size()));
			nouvellesCartes.add(pokemonAleatoire); // Un Set empêche les doublons
		}

		// Vérifier si la liste est null
		if (dresseur.getPokemonList() == null) {
			dresseur.setPokemonList(new ArrayList<>());
		}

		// Ajout des cartes et mise à jour du tirage
		dresseur.getPokemonList().addAll(nouvellesCartes);
		dresseur.setDernierTirage(LocalDateTime.now());
		dresseurRepository.save(dresseur);

		return new ArrayList<>(nouvellesCartes);
	}

	// 🔹 Génération d'un Pokémon aléatoire (corrigée)
	private Pokemon genererCarteAleatoire() {
		List<Pokemon> toutesLesCartes = pokemonRepository.findAll();
		if (toutesLesCartes.isEmpty()) {
			throw new RuntimeException("Aucune carte disponible en base !");
		}
		return toutesLesCartes.get(random.nextInt(toutesLesCartes.size()));
	}
}
