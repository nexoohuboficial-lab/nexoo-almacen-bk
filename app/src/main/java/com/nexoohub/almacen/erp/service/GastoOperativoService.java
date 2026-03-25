package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.erp.dto.GastoOperativoRequest;
import com.nexoohub.almacen.erp.dto.GastoOperativoResponse;
import com.nexoohub.almacen.erp.entity.GastoOperativo;
import com.nexoohub.almacen.erp.repository.GastoOperativoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GastoOperativoService {

    private static final Logger log = LoggerFactory.getLogger(GastoOperativoService.class);

    private final GastoOperativoRepository gastoRepo;

    public GastoOperativoService(GastoOperativoRepository gastoRepo) {
        this.gastoRepo = gastoRepo;
    }

    @Transactional
    public GastoOperativoResponse registrar(GastoOperativoRequest req) {
        log.info("Registrando gasto: {} - ${}", req.getConcepto(), req.getMonto());

        GastoOperativo gasto = new GastoOperativo();
        gasto.setConcepto(req.getConcepto());
        gasto.setCategoria(req.getCategoria().toUpperCase());
        gasto.setMonto(req.getMonto());
        gasto.setFechaGasto(req.getFechaGasto());
        gasto.setSucursalId(req.getSucursalId());
        gasto.setUsuarioId(req.getUsuarioId());
        gasto.setComprobanteRef(req.getComprobanteRef());
        gasto.setObservaciones(req.getObservaciones());

        return mapToResponse(gastoRepo.save(gasto));
    }

    @Transactional(readOnly = true)
    public List<GastoOperativoResponse> listar(LocalDate desde, LocalDate hasta, Integer sucursalId) {
        List<GastoOperativo> gastos;
        if (sucursalId != null) {
            gastos = gastoRepo.findBySucursalIdAndFechaGastoBetweenOrderByFechaGastoDesc(sucursalId, desde, hasta);
        } else {
            gastos = gastoRepo.findByFechaGastoBetweenOrderByFechaGastoDesc(desde, hasta);
        }
        return gastos.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private GastoOperativoResponse mapToResponse(GastoOperativo entity) {
        GastoOperativoResponse dto = new GastoOperativoResponse();
        dto.setId(entity.getId());
        dto.setConcepto(entity.getConcepto());
        dto.setCategoria(entity.getCategoria());
        dto.setMonto(entity.getMonto());
        dto.setFechaGasto(entity.getFechaGasto());
        dto.setSucursalId(entity.getSucursalId());
        dto.setUsuarioId(entity.getUsuarioId());
        dto.setComprobanteRef(entity.getComprobanteRef());
        dto.setObservaciones(entity.getObservaciones());
        return dto;
    }
}
