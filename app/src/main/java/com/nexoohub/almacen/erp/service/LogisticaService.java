package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.*;
import com.nexoohub.almacen.erp.entity.*;
import com.nexoohub.almacen.erp.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LogisticaService {

    private static final Logger log = LoggerFactory.getLogger(LogisticaService.class);

    private final VehiculoRepository vehiculoRepo;
    private final ChoferRepository choferRepo;
    private final RutaEntregaRepository rutaRepo;
    private final RutaFacturaRepository rutaFacturaRepo;

    public LogisticaService(VehiculoRepository vehiculoRepo,
                            ChoferRepository choferRepo,
                            RutaEntregaRepository rutaRepo,
                            RutaFacturaRepository rutaFacturaRepo) {
        this.vehiculoRepo = vehiculoRepo;
        this.choferRepo = choferRepo;
        this.rutaRepo = rutaRepo;
        this.rutaFacturaRepo = rutaFacturaRepo;
    }

    // ==========================================
    // Catálogos: Vehículos y Choferes
    // ==========================================

    @Transactional(readOnly = true)
    public List<VehiculoDTO> listarVehiculos(Integer sucursalId) {
        return vehiculoRepo.findBySucursalIdAndEstatus(sucursalId, "ACTIVO")
                .stream().map(this::mapToVehiculoDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChoferDTO> listarChoferes(Integer sucursalId) {
        return choferRepo.findBySucursalIdAndEstatus(sucursalId, "ACTIVO")
                .stream().map(this::mapToChoferDTO).collect(Collectors.toList());
    }

    // ==========================================
    // Rutas de Entrega / Envíos de Paquetería
    // ==========================================

    @Transactional
    public RutaEntregaResponse crearRuta(RutaEntregaRequest req) {
        log.info("Creando ruta/embarque con código: {}", req.getCodigoRuta());

        if (rutaRepo.findByCodigoRuta(req.getCodigoRuta()).isPresent()) {
            throw new IllegalArgumentException("El código de ruta ya existe: " + req.getCodigoRuta());
        }

        RutaEntrega ruta = new RutaEntrega();
        ruta.setCodigoRuta(req.getCodigoRuta());
        ruta.setFechaProgramada(req.getFechaProgramada());
        ruta.setEsPaqueteria(req.getEsPaqueteria());
        ruta.setObservaciones(req.getObservaciones());
        ruta.setUsuarioId(req.getUsuarioId());

        if (Boolean.TRUE.equals(req.getEsPaqueteria())) {
            if (req.getProveedorEnvio() == null || req.getProveedorEnvio().isBlank()) {
                throw new IllegalArgumentException("Para envíos externos debe indicar el proveedor de paquetería");
            }
            ruta.setProveedorEnvio(req.getProveedorEnvio().toUpperCase());
        } else {
            if (req.getChoferId() == null || req.getVehiculoId() == null) {
                throw new IllegalArgumentException("Para flotilla propia debe indicar chofer y vehículo");
            }
            // Validar que chofer y vehículo existan
            choferRepo.findById(req.getChoferId()).orElseThrow(() -> new ResourceNotFoundException("Chofer no encontrado"));
            vehiculoRepo.findById(req.getVehiculoId()).orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));
            
            ruta.setChoferId(req.getChoferId());
            ruta.setVehiculoId(req.getVehiculoId());
        }

        return mapToRutaResponse(rutaRepo.save(ruta));
    }

    @Transactional(readOnly = true)
    public List<RutaEntregaResponse> listarRutas(LocalDate desde, LocalDate hasta) {
        return rutaRepo.findByFechaProgramadaBetweenOrderByFechaProgramadaDesc(desde, hasta)
                .stream().map(this::mapToRutaResponse).collect(Collectors.toList());
    }

    @Transactional
    public RutaEntregaResponse asignarFactura(Integer rutaId, RutaFacturaRequest req) {
        RutaEntrega ruta = rutaRepo.findById(rutaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada"));

        if (!"PENDIENTE".equals(ruta.getEstatus()) && !"EN_TRANSITO".equals(ruta.getEstatus())) {
            throw new IllegalStateException("Solo se pueden agregar facturas a rutas pendientes o en tránsito");
        }

        RutaFactura rf = new RutaFactura();
        rf.setFacturaClienteId(req.getFacturaClienteId());
        
        // Tracking y guía (Mercado Libre, DHL, etc.)
        if (req.getNumeroGuia() != null && !req.getNumeroGuia().isBlank()) {
            rf.setNumeroGuia(req.getNumeroGuia());
            rf.setUrlRastreo(req.getUrlRastreo());
        }

        ruta.addFactura(rf);
        return mapToRutaResponse(rutaRepo.save(ruta));
    }

    @Transactional
    public RutaEntregaResponse cambiarEstatusRuta(Integer rutaId, String estatus) {
        RutaEntrega ruta = rutaRepo.findById(rutaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada"));
        
        ruta.setEstatus(estatus.toUpperCase());
        return mapToRutaResponse(rutaRepo.save(ruta));
    }

    // ==========================================
    // Mappers
    // ==========================================

    private VehiculoDTO mapToVehiculoDTO(Vehiculo e) {
        VehiculoDTO d = new VehiculoDTO();
        d.setId(e.getId());
        d.setPlacas(e.getPlacas());
        d.setMarca(e.getMarca());
        d.setModelo(e.getModelo());
        d.setCapacidadKg(e.getCapacidadKg());
        d.setEstatus(e.getEstatus());
        d.setSucursalId(e.getSucursalId());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }

    private ChoferDTO mapToChoferDTO(Chofer e) {
        ChoferDTO d = new ChoferDTO();
        d.setId(e.getId());
        d.setNombreCompleto(e.getNombreCompleto());
        d.setLicencia(e.getLicencia());
        d.setVigenciaLicencia(e.getVigenciaLicencia());
        d.setTelefono(e.getTelefono());
        d.setEstatus(e.getEstatus());
        d.setSucursalId(e.getSucursalId());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }

    private RutaEntregaResponse mapToRutaResponse(RutaEntrega e) {
        RutaEntregaResponse d = new RutaEntregaResponse();
        d.setId(e.getId());
        d.setCodigoRuta(e.getCodigoRuta());
        d.setFechaProgramada(e.getFechaProgramada());
        d.setEsPaqueteria(e.getEsPaqueteria());
        d.setProveedorEnvio(e.getProveedorEnvio());
        d.setEstatus(e.getEstatus());
        d.setObservaciones(e.getObservaciones());

        if (e.getChoferId() != null) {
            choferRepo.findById(e.getChoferId()).ifPresent(ch -> d.setChofer(mapToChoferDTO(ch)));
        }
        if (e.getVehiculoId() != null) {
            vehiculoRepo.findById(e.getVehiculoId()).ifPresent(v -> d.setVehiculo(mapToVehiculoDTO(v)));
        }

        List<RutaFacturaDTO> facturasDTO = e.getFacturas().stream().map(rf -> {
            RutaFacturaDTO rfd = new RutaFacturaDTO();
            rfd.setId(rf.getId());
            rfd.setFacturaClienteId(rf.getFacturaClienteId());
            rfd.setNumeroGuia(rf.getNumeroGuia());
            rfd.setUrlRastreo(rf.getUrlRastreo());
            rfd.setEstatusEntrega(rf.getEstatusEntrega());
            rfd.setFechaEntrega(rf.getFechaEntrega());
            rfd.setFirmaRecibido(rf.getFirmaRecibido());
            return rfd;
        }).collect(Collectors.toList());

        d.setFacturasProgramadas(facturasDTO);
        return d;
    }
}
