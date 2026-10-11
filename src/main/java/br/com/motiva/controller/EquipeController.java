package br.com.motiva.controller;
import br.com.motiva.dto.EquipeRequest;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.service.EquipeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/equipes")
public class EquipeController {
    private final EquipeService service;
    public EquipeController(EquipeService service) { this.service=service; }
    @GetMapping public List<EquipeManutencao> listar() { return service.listar(); }
    @GetMapping("/{id}") public EquipeManutencao buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<EquipeManutencao> criar(@Valid @RequestBody EquipeRequest dados) { var salvo=service.criar(dados); return ResponseEntity.created(URI.create("/api/equipes/"+salvo.getId())).body(salvo); }
    @PutMapping("/{id}") public EquipeManutencao atualizar(@PathVariable Long id, @Valid @RequestBody EquipeRequest dados) { return service.atualizar(id,dados); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
