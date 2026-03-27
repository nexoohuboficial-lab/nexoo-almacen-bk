package com.nexoohub.almacen.adquisiciones.service;

import com.nexoohub.almacen.adquisiciones.dto.OrdenCompraResponse;
import com.nexoohub.almacen.common.exception.BusinessException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExcelOrdenCompraGenerator {

    public byte[] generarExcel(OrdenCompraResponse oc) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Orden de Compra " + oc.getFolio());

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle amountStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            amountStyle.setDataFormat(dataFormat.getFormat("$#,##0.00"));

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("NEXOOHUB - ORDEN DE COMPRA: " + oc.getFolio());
            titleCell.setCellStyle(headerStyle);

            // Información General
            sheet.createRow(2).createCell(0).setCellValue("Proveedor: " + oc.getNombreProveedor());
            sheet.createRow(3).createCell(0).setCellValue("RFC: " + oc.getRfcProveedor());
            sheet.createRow(4).createCell(0).setCellValue("Sucursal Entrega: " + oc.getNombreSucursal());
            sheet.createRow(5).createCell(0).setCellValue("Fecha Creación: " + oc.getFechaCreacion().toString());
            sheet.createRow(6).createCell(0).setCellValue("Estado: " + oc.getEstado());

            // Headers de la Tabla
            Row headerRow = sheet.createRow(8);
            String[] headers = {"SKU Interno", "SKU Proveedor", "Descripción", "Cantidad", "Costo Unitario", "Subtotal"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIndex = 9;
            for (OrdenCompraResponse.DetalleResponse detalle : oc.getDetalles()) {
                Row row = sheet.createRow(rowIndex++);
                
                row.createCell(0).setCellValue(detalle.getSkuInterno());
                row.createCell(1).setCellValue(detalle.getSkuProveedor() != null ? detalle.getSkuProveedor() : "N/A");
                row.createCell(2).setCellValue(detalle.getNombreProducto());
                row.createCell(3).setCellValue(detalle.getCantidad());
                
                Cell costCell = row.createCell(4);
                costCell.setCellValue(detalle.getPrecioCostoUnitario().doubleValue());
                costCell.setCellStyle(amountStyle);
                
                Cell subCell = row.createCell(5);
                subCell.setCellValue(detalle.getSubtotal().doubleValue());
                subCell.setCellStyle(amountStyle);
            }

            // Total General
            Row totalRow = sheet.createRow(rowIndex + 1);
            Cell totalLabelCell = totalRow.createCell(4);
            totalLabelCell.setCellValue("TOTAL:");
            totalLabelCell.setCellStyle(headerStyle);
            
            Cell totalValueCell = totalRow.createCell(5);
            totalValueCell.setCellValue(oc.getTotalEstimado().doubleValue());
            totalValueCell.setCellStyle(amountStyle);

            // Ajustar Columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new BusinessException("Error al generar el archivo Excel de la Orden de Compra: " + e.getMessage());
        }
    }
}
