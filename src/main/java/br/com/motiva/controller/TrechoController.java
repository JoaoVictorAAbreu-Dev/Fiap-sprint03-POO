package br.com.motiva.controller;
import br.com.motiva.dto.TrechoRequest;
import br.com.motiva.model.TrechoEntity;
import br.com.motiva.service.TrechoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/trechos")
public class TrechoController {
    private final TrechoService service;
    public TrechoController(TrechoService service) { this.service=service; }
    @GetMapping public List<TrechoEntity> listar() { return service.listar(); }
    @GetMapping("/{id}") public TrechoEntity buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<TrechoEntity> criar(@Valid @RequestBody TrechoRequest dados) { var salvo=service.criar(dados); return ResponseEntity.created(URI.create("/api/trechos/"+salvo.getId())).body(salvo); }
    @PutMapping("/{id}") public TrechoEntity atualizar(@PathVariable Long id, @Valid @RequestBody TrechoRequest dados) { return service.atualizar(id,dados); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
