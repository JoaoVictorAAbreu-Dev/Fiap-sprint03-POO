package br.com.motiva.controller;
import br.com.motiva.model.RelatorioEntity;
import br.com.motiva.service.RelatorioService;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/relatorios")
public class RelatorioController {
    private final RelatorioService service;
    public RelatorioController(RelatorioService service) { this.service=service; }
    @PostMapping public ResponseEntity<RelatorioEntity> gerar() { var salvo=service.gerar(); return ResponseEntity.created(URI.create("/api/relatorios/"+salvo.getId())).body(salvo); }
    @GetMapping("/{id}") public RelatorioEntity buscar(@PathVariable Long id) { return service.buscar(id); }
    @GetMapping public List<RelatorioEntity> listar() { return service.listar(); }
    @GetMapping("/periodo") public List<RelatorioEntity> periodo(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) { return service.periodo(inicio,fim); }
}
