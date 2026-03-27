package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.entity.PrediccionChurnCliente;
import com.nexoohub.almacen.analitica.repository.PrediccionChurnClienteRepository;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChurnPredictionService {

    private final PrediccionChurnClienteRepository churnRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    @Transactional
    public void calcularRiesgoChurnGlobal() {
        log.info("Iniciando cálculo global de riesgo de fuga (Churn) para todos los clientes.");
        List<Cliente> clientes = clienteRepository.findAll();
        
        int calculados = 0;
        for (Cliente cliente : clientes) {
            List<Venta> ventas = ventaRepository.findByClienteId(cliente.getId());
            
            // Si el cliente no tiene ventas, no se puede predecir
            if (ventas == null || ventas.isEmpty()) {
                continue;
            }

            // Ordenar por fechaVenta para identificar la primera y la última
            ventas.sort(Comparator.comparing(Venta::getFechaVenta));
            
            Venta primeraVenta = ventas.get(0);
            Venta ultimaVenta = ventas.get(ventas.size() - 1);
            
            long diasSinComprar = ChronoUnit.DAYS.between(ultimaVenta.getFechaVenta(), LocalDateTime.now());
            
            int frecuenciaPromedio = 0;
            if (ventas.size() > 1) {
                long totalDiasDesdePrimeraVenta = ChronoUnit.DAYS.between(primeraVenta.getFechaVenta(), ultimaVenta.getFechaVenta());
                // Por ejemplo, si pasaron 30 días entre su primera y última venta, y tuvo 4 ventas,
                // la frecuencia promedio son 30 / (4 - 1) = 10 días
                frecuenciaPromedio = (int) (totalDiasDesdePrimeraVenta / (ventas.size() - 1));
            }

            // Si es un cliente con 1 sola compra, asignaremos una frecuencia promedio de 30 días (estimado inicial)
            if (frecuenciaPromedio == 0) {
                frecuenciaPromedio = 30;
            }

            int score = 0;
            StringBuilder factores = new StringBuilder();

            // Matemática básica de Churn
            // 1. Ausencia prolongada extrema
            if (diasSinComprar >= 90) {
                score = 99;
                factores.append("Más de 90 días sin comprar (Abandonado). ");
            } else {
                // 2. Disminución de frecuencia
                if (diasSinComprar > (frecuenciaPromedio * 2)) {
                    score += 50;
                    factores.append("Superó el doble de su frecuencia esperada de compra (").append(frecuenciaPromedio).append(" días). ");
                }

                // 3. Disminución de monto en última compra vs Promedio Histórico
                if (ventas.size() > 1) {
                    BigDecimal sumaTotal = ventas.stream()
                            .map(Venta::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal promedioHistorico = sumaTotal.divide(BigDecimal.valueOf(ventas.size()), 2, RoundingMode.HALF_UP);
                    
                    BigDecimal ultimaCompra = ultimaVenta.getTotal();
                    
                    // Si la última compra es 30% menor al promedio histórico
                    if (ultimaCompra.compareTo(promedioHistorico.multiply(BigDecimal.valueOf(0.70))) < 0) {
                        score += 25;
                        factores.append("Su última compra fue un ").append(
                                BigDecimal.ONE.subtract(ultimaCompra.divide(promedioHistorico, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100)).intValue()
                        ).append("% menor a su promedio histórico. ");
                    }
                }

                if (score == 0) {
                    factores.append("Cliente activo y saludable.");
                }
            }
            
            // Asegurar límites del score (0 a 100)
            score = Math.min(score, 100);
            
            // Persistencia
            Optional<PrediccionChurnCliente> optChurn = churnRepository.findByClienteId(cliente.getId());
            PrediccionChurnCliente churnRecord = optChurn.orElseGet(() -> {
                PrediccionChurnCliente newChurn = new PrediccionChurnCliente();
                newChurn.setClienteId(cliente.getId());
                return newChurn;
            });

            churnRecord.setScoreRiesgo(score);
            churnRecord.setDiasSinComprar((int) diasSinComprar);
            churnRecord.setFrecuenciaPromedioDias(frecuenciaPromedio);
            // Limitar longitud del texto
            String factorStr = factores.toString();
            churnRecord.setFactoresRiesgo(factorStr.length() > 500 ? factorStr.substring(0, 497) + "..." : factorStr);
            
            churnRepository.save(churnRecord);
            calculados++;
        }
        
        log.info("Cálculo de churn finalizado exitosamente. Se evaluaron {} clientes.", calculados);
    }
}
