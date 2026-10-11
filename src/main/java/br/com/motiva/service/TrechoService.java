package br.com.motiva.service;
import br.com.motiva.dto.TrechoRequest;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.TrechoEntity;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.TrechoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class TrechoService {
    private final TrechoRepository trechos;
    private final IntervencaoRepository intervencoes;
    public TrechoService(TrechoRepository trechos, IntervencaoRepository intervencoes) { this.trechos=trechos; this.intervencoes=intervencoes; }
    @Transactional(readOnly=true) public List<TrechoEntity> listar() { return trechos.findAll(); }
    @Transactional(readOnly=true) public TrechoEntity buscar(Long id) { return trechos.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trecho", id)); }
    @Transactional public TrechoEntity criar(TrechoRequest dados) { validar(dados); return trechos.saveAndFlush(new TrechoEntity(dados.quilometro(),dados.sentido().trim(),dados.alturaVegetacaoCm(),dados.tipoCrescimento(),dados.areaSensivel(),dados.monitoradoIot(),dados.codigoSensor())); }
    @Transactional public TrechoEntity atualizar(Long id, TrechoRequest dados) { validar(dados); TrechoEntity trecho=buscar(id); trecho.atualizar(dados.quilometro(),dados.sentido().trim(),dados.alturaVegetacaoCm(),dados.tipoCrescimento(),dados.areaSensivel(),dados.monitoradoIot(),dados.codigoSensor()); return trechos.saveAndFlush(trecho); }
    @Transactional public void excluir(Long id) { buscar(id); if (intervencoes.existsByTrechoId(id)) throw new IllegalArgumentException("Trecho possui intervencoes; remova as referencias antes."); trechos.deleteById(id); trechos.flush(); }
    private void validar(TrechoRequest dados) {
        if (!Double.isFinite(dados.alturaVegetacaoCm()) || dados.alturaVegetacaoCm() < 0) throw new IllegalArgumentException("A altura da vegetacao deve ser finita e nao negativa.");
        if (dados.monitoradoIot() && (dados.codigoSensor()==null || dados.codigoSensor().isBlank())) throw new IllegalArgumentException("Trecho IoT exige codigo de sensor.");
        if (!dados.monitoradoIot() && dados.codigoSensor()!=null) throw new IllegalArgumentException("Trecho sem IoT nao pode ter codigo de sensor.");
    }
}
