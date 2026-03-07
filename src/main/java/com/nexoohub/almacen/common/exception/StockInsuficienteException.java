package com.nexoohub.almacen.common.exception;

/**
 * Excepción lanzada cuando no hay suficiente stock para completar una operación.
 * 
 * <p>Se utiliza en operaciones de:</p>
 * <ul>
 *   <li>Procesamiento de ventas</li>
 *   <li>Traspasos entre sucursales</li>
 *   <li>Reservas de inventario</li>
 * </ul>
 * 
 * <p><b>HTTP Status recomendado:</b> 409 CONFLICT</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class StockInsuficienteException extends BusinessException {

    private final String skuInterno;
    private final Integer stockDisponible;
    private final Integer cantidadSolicitada;
    private final Integer sucursalId;

    /**
     * Constructor con mensaje simple.
     * 
     * @param message Mensaje descriptivo del error de stock
     */
    public StockInsuficienteException(String message) {
        super(message, "STOCK_INSUFICIENTE");
        this.skuInterno = null;
        this.stockDisponible = null;
        this.cantidadSolicitada = null;
        this.sucursalId = null;
    }

    /**
     * Constructor completo con detalles del producto y stock.
     * 
     * @param skuInterno Código SKU del producto
     * @param stockDisponible Stock actual disponible
     * @param cantidadSolicitada Cantidad que se intentó tomar
     */
    public StockInsuficienteException(String skuInterno, Integer stockDisponible, Integer cantidadSolicitada) {
        super(String.format("Stock insuficiente para producto '%s'. Disponible: %d, Solicitado: %d", 
              skuInterno, stockDisponible, cantidadSolicitada), "STOCK_INSUFICIENTE");
        this.skuInterno = skuInterno;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
        this.sucursalId = null;
    }

    /**
     * Constructor completo con información de sucursal.
     * 
     * @param skuInterno Código SKU del producto
     * @param sucursalId ID de la sucursal donde falta stock
     * @param stockDisponible Stock actual disponible
     * @param cantidadSolicitada Cantidad que se intentó tomar
     */
    public StockInsuficienteException(String skuInterno, Integer sucursalId, Integer stockDisponible, Integer cantidadSolicitada) {
        super(String.format("Stock insuficiente en sucursal %d para producto '%s'. Disponible: %d, Solicitado: %d", 
              sucursalId, skuInterno, stockDisponible, cantidadSolicitada), "STOCK_INSUFICIENTE");
        this.skuInterno = skuInterno;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
        this.sucursalId = sucursalId;
    }

    public String getSkuInterno() {
        return skuInterno;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }
}
