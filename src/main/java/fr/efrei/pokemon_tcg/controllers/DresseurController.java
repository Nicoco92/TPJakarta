package fr.efrei.pokemon_tcg.controllers;

import fr.efrei.pokemon_tcg.dto.CapturePokemon;
import fr.efrei.pokemon_tcg.dto.DresseurDTO;
import fr.efrei.pokemon_tcg.models.Dresseur;
import fr.efrei.pokemon_tcg.models.Pokemon;
import fr.efrei.pokemon_tcg.services.IDresseurService;
import fr.efrei.pokemon_tcg.services.implementations.DresseurServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dresseurs")
public class DresseurController {

	private final IDresseurService dresseurService;

	public DresseurController(DresseurServiceImpl dresseurService) {
		this.dresseurService = dresseurService;
	}

	@GetMapping
	public ResponseEntity<List<Dresseur>> findAll() {
		return new ResponseEntity<>(dresseurService.findAll(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody DresseurDTO dresseurDTO) {
		dresseurService.create(dresseurDTO);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping("/{uuid1}/echanger/{uuid2}")
	public ResponseEntity<?> echangerCartes(
			@PathVariable String uuid1,
			@PathVariable String uuid2,
			@RequestParam String carte1Uuid,
			@RequestParam String carte2Uuid) {
		try {
			boolean success = dresseurService.echangerCartes(uuid1, uuid2, carte1Uuid, carte2Uuid);
			return ResponseEntity.ok(success ? "Échange réussi !" : "Échange échoué.");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PatchMapping("/{uuid}/tirer")
	public ResponseEntity<List<Pokemon>> tirerCartes(@PathVariable String uuid) {
		try {
			List<Pokemon> pokemons = dresseurService.tirerCartes(uuid);
			return new ResponseEntity<>(pokemons, HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}