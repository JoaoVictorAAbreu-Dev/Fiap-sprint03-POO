package br.com.motiva.controller;
import br.com.motiva.dto.IntervencaoRequest;
import br.com.motiva.dto.IntervencaoResponse;
import br.com.motiva.service.IntervencaoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/intervencoes")
public class IntervencaoController {
    private final IntervencaoService service;
    public IntervencaoController(IntervencaoService service) { this.service=service; }
    @GetMapping public List<IntervencaoResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public IntervencaoResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<IntervencaoResponse> criar(@Valid @RequestBody IntervencaoRequest dados) { var salvo=service.criar(dados); return ResponseEntity.created(URI.create("/api/intervencoes/"+salvo.id())).body(salvo); }
    @PutMapping("/{id}") public IntervencaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody IntervencaoRequest dados) { return service.atualizar(id,dados); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
