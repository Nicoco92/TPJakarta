package fr.efrei.pokemon_tcg.services.implementations;

import fr.efrei.pokemon_tcg.constants.TypePokemon;
import fr.efrei.pokemon_tcg.dto.CapturePokemon;
import fr.efrei.pokemon_tcg.dto.DresseurDTO;
import fr.efrei.pokemon_tcg.models.Dresseur;
import fr.efrei.pokemon_tcg.models.Pokemon;
import fr.efrei.pokemon_tcg.repositories.DresseurRepository;
import fr.efrei.pokemon_tcg.services.IDresseurService;
import fr.efrei.pokemon_tcg.services.IPokemonService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DresseurServiceImpl implements IDresseurService {

	private final DresseurRepository repository;
	private final IPokemonService pokemonService;

	public DresseurServiceImpl(DresseurRepository repository, IPokemonService pokemonService) {
		this.repository = repository;
		this.pokemonService = pokemonService;
	}

	@Override
	public List<Dresseur> findAll() {
		return repository.findAllByDeletedAtNull();
	}

	@Override
	public Dresseur findById(String uuid) {
		return repository.findById(uuid).orElse(null);
	}

	@Override
	public void capturerPokemon(String uuid, CapturePokemon capturePokemon) {
		Dresseur dresseur = findById(uuid);
		Pokemon pokemon = pokemonService.findById(capturePokemon.getUuid());
		dresseur.getPokemonList().add(pokemon);
		repository.save(dresseur);
	}

	@Override
	public void create(DresseurDTO dresseurDTO) {
		Dresseur dresseur = new Dresseur();
		dresseur.setNom(dresseurDTO.getNom());
		dresseur.setPrenom(dresseurDTO.getPrenom());
		dresseur.setDeletedAt(null);
		repository.save(dresseur);
	}

	@Override
	public boolean update(String uuid, DresseurDTO dresseurDTO) {
		return false;
	}

	@Override
	public boolean delete(String uuid) {
		Dresseur dresseur = findById(uuid);
		dresseur.setDeletedAt(LocalDateTime.now());
		repository.save(dresseur);
		return true;
	}

	// 🆕 Méthode pour tirer des cartes aléatoires (ajoutée ici)
	@Override
	public List<Pokemon> tirerCartes(String dresseurUuid) {
		Dresseur dresseur = findById(dresseurUuid);
		if (dresseur == null) {
			throw new RuntimeException("Dresseur introuvable !");
		}

		// Vérifier si le dresseur a déjà tiré aujourd'hui
		LocalDateTime maintenant = LocalDateTime.now();
		if (dresseur.getDernierTirage() != null &&
				dresseur.getDernierTirage().toLocalDate().equals(maintenant.toLocalDate())) {
			throw new RuntimeException("Vous avez déjà tiré vos cartes aujourd'hui !");
		}

		List<Pokemon> nouveauxPokemons = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Pokemon pokemon = genererPokemonAleatoire();
			nouveauxPokemons.add(pokemon);
		}

		// Associer les nouveaux Pokémon au dresseur
		dresseur.getPokemonList().addAll(nouveauxPokemons);
		dresseur.setDernierTirage(maintenant);
		repository.save(dresseur);

		return nouveauxPokemons;
	}

	private Pokemon genererPokemonAleatoire() {
		String[] noms = {"Pikachu", "Bulbizarre", "Salamèche", "Carapuce", "Evoli"};
		String[] attaques = {"Charge", "Éclair", "Flammeche", "Lance-Soleil", "Hydrocanon"};

		Random random = new Random();
		Pokemon pokemon = new Pokemon();

		pokemon.setNom(noms[random.nextInt(noms.length)]);
		pokemon.setNiveau(random.nextInt(100) + 1);
		pokemon.setType(TypePokemon.values()[random.nextInt(TypePokemon.values().length)]);
		pokemon.setAttaque1(attaques[random.nextInt(attaques.length)]);
		pokemon.setAttaque2(attaques[random.nextInt(attaques.length)]);

		// Probabilités pour la rareté (5 étoiles plus rares)
		int chance = random.nextInt(100);
		if (chance < 50) {
			pokemon.setRarete(1); // 50% de chances
		} else if (chance < 80) {
			pokemon.setRarete(2); // 30% de chances
		} else if (chance < 95) {
			pokemon.setRarete(3); // 15% de chances
		} else if (chance < 99) {
			pokemon.setRarete(4); // 4% de chances
		} else {
			pokemon.setRarete(5); // 1% de chances
		}

		return pokemon;
	}
}
