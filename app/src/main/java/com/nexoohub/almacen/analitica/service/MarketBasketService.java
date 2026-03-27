package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.entity.ReglaAsociacionProductos;
import com.nexoohub.almacen.analitica.repository.ReglaAsociacionProductosRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketBasketService {

    private final VentaRepository ventaRepository;
    private final ReglaAsociacionProductosRepository reglaRepository;

    @Transactional
    public void calcularReglasAsociacion(double minSoporte, double minConfianza) {
        log.info("Iniciando cálculo de reglas de asociación (Market Basket Analysis)");
        
        List<Venta> ventas = ventaRepository.findAll();
        if (ventas.isEmpty()) {
            log.warn("No hay ventas para analizar.");
            return;
        }

        // 1. Extraer canastas únicas de SKUs (eliminando duplicados en la misma venta)
        List<Set<String>> canastas = ventas.stream()
                .map(Venta::getDetalles)
                .map(detalles -> detalles.stream()
                        .map(DetalleVenta::getSkuInterno)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()))
                .filter(canasta -> canasta.size() > 1) // Solo importan compras con > 1 artículo
                .collect(Collectors.toList());

        long totalTransacciones = canastas.size();
        if (totalTransacciones == 0) {
            log.info("No hay transacciones con más de 1 artículo válido.");
            return;
        }

        // 2. Calcular Frecuencias Individuales
        Map<String, Long> frecuenciaIndividual = new HashMap<>();
        for (Set<String> canasta : canastas) {
            for (String sku : canasta) {
                frecuenciaIndividual.put(sku, frecuenciaIndividual.getOrDefault(sku, 0L) + 1);
            }
        }

        // 3. Calcular Frecuencias de Pares
        Map<String, Long> frecuenciaPares = new HashMap<>(); // Key: "sku1|sku2" (ordenados)
        for (Set<String> canasta : canastas) {
            List<String> items = new ArrayList<>(canasta);
            Collections.sort(items); // Para asegurar uniformidad
            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    String par = items.get(i) + "|" + items.get(j);
                    frecuenciaPares.put(par, frecuenciaPares.getOrDefault(par, 0L) + 1);
                }
            }
        }

        // 4. Generar Reglas y Calcular Métricas (A -> B y B -> A)
        List<ReglaAsociacionProductos> nuevasReglas = new ArrayList<>();
        
        for (Map.Entry<String, Long> entry : frecuenciaPares.entrySet()) {
            String[] partes = entry.getKey().split("\\|");
            String skuA = partes[0];
            String skuB = partes[1];
            long freqAB = entry.getValue();

            // Regla A -> B
            crearRegla(skuA, skuB, freqAB, totalTransacciones, frecuenciaIndividual, minSoporte, minConfianza)
                    .ifPresent(nuevasReglas::add);
                    
            // Regla B -> A
            crearRegla(skuB, skuA, freqAB, totalTransacciones, frecuenciaIndividual, minSoporte, minConfianza)
                    .ifPresent(nuevasReglas::add);
        }

        // 5. Persistir
        log.info("Generadas {} reglas. Guardando en Base de Datos...", nuevasReglas.size());
        reglaRepository.deleteAll(); // Limpiar viejas
        reglaRepository.saveAll(nuevasReglas);
        log.info("Cálculo de Canasta de Compra finalizado.");
    }

    private Optional<ReglaAsociacionProductos> crearRegla(String origen, String destino, long freqInterseccion, 
                                                          long totalTransacciones, Map<String, Long> freqIndividual, 
                                                          double minSoporte, double minConfianza) {
        
        double soporte = (double) freqInterseccion / totalTransacciones;
        if (soporte < minSoporte) return Optional.empty();

        long freqOrigen = freqIndividual.getOrDefault(origen, 0L);
        if (freqOrigen == 0) return Optional.empty();

        double confianza = (double) freqInterseccion / freqOrigen;
        if (confianza < minConfianza) return Optional.empty();

        long freqDestino = freqIndividual.getOrDefault(destino, 0L);
        double soporteDestino = (double) freqDestino / totalTransacciones;
        
        double lift = soporteDestino > 0 ? confianza / soporteDestino : 0;

        ReglaAsociacionProductos regla = new ReglaAsociacionProductos();
        regla.setSkuOrigen(origen);
        regla.setSkuDestino(destino);
        regla.setSoporte(soporte);
        regla.setConfianza(confianza);
        regla.setLift(lift);

        return Optional.of(regla);
    }
}
