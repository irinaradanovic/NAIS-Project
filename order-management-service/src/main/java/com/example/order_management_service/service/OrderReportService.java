package com.example.order_management_service.service;

import com.example.order_management_service.dto.OrderReportRowDto;
import com.example.order_management_service.dto.PaymentDeliveryDistanceDto;
import com.example.order_management_service.dto.StatusAvgOrderValueDto;
import com.example.order_management_service.dto.TopCityRevenueDto;
import com.example.order_management_service.model.OrderMetric;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderReportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 13, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 8);

    private final Neo4jClient neo4jClient;
    private final OrderMetricService orderMetricService;

    public OrderReportService(Neo4jClient neo4jClient, OrderMetricService orderMetricService) {
        this.neo4jClient = neo4jClient;
        this.orderMetricService = orderMetricService;
    }

    public byte[] generate(LocalDateTime from, LocalDateTime to, String status, String restaurantId) {
        List<OrderReportRowDto> orders = findOrderRows(from, to, blankToNull(status), blankToNull(restaurantId));

        List<OrderMetric> metrics = orderMetricService.getAll();
        List<TopCityRevenueDto> topCities = orderMetricService.getTopCitiesByRevenue(5);
        List<StatusAvgOrderValueDto> avgByStatus = orderMetricService.getAvgOrderValueByStatus();
        List<PaymentDeliveryDistanceDto> avgDeliveryByPayment = orderMetricService.getAvgDeliveryDistanceByPayment();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(document, out);
            document.open();

            addTitle(document, from, to, status, restaurantId);
            addOrdersSection(document, orders);
            addMetricsSection(document, metrics);
            addTopCitiesSection(document, topCities);
            addStatusAverageSection(document, avgByStatus);
            addPaymentDistanceSection(document, avgDeliveryByPayment);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Failed to generate order report PDF", e);
        }
    }

    private void addTitle(Document document, LocalDateTime from, LocalDateTime to, String status, String restaurantId) throws DocumentException {
        Paragraph title = new Paragraph("Order Management Report", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        String filters = "Filters: from=" + value(from) +
                ", to=" + value(to) +
                ", status=" + value(status) +
                ", restaurantId=" + value(restaurantId);
        Paragraph filterParagraph = new Paragraph(filters, BODY_FONT);
        filterParagraph.setSpacingAfter(12);
        document.add(filterParagraph);
    }

    private List<OrderReportRowDto> findOrderRows(LocalDateTime from, LocalDateTime to, String status, String restaurantId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder cypher = new StringBuilder("""
                MATCH (o:Order)
                WHERE 1 = 1
                """);
        if (status != null) {
            cypher.append(" AND o.status = $status");
            params.put("status", status);
        }
        if (restaurantId != null) {
            cypher.append(" AND o.restaurantId = $restaurantId");
            params.put("restaurantId", restaurantId);
        }
        if (from != null) {
            cypher.append(" AND o.creationDate >= $from");
            params.put("from", from);
        }
        if (to != null) {
            cypher.append(" AND o.creationDate <= $to");
            params.put("to", to);
        }
        cypher.append("""
                
                OPTIONAL MATCH (o)-[r:HAS_MENU_ITEM]->(:OrderMenuItem)
                RETURN toString(o.id) AS id,
                       o.creationDate AS creationDate,
                       o.status AS status,
                       o.orderType AS orderType,
                       o.address AS address,
                       o.restaurantId AS restaurantId,
                       coalesce(sum(r.quantity), 0) AS itemCount
                ORDER BY o.creationDate DESC
                """);

        return neo4jClient.query(cypher.toString())
                .bindAll(params)
                .fetchAs(OrderReportRowDto.class)
                .mappedBy((typeSystem, record) -> new OrderReportRowDto(
                        stringValue(record, "id"),
                        localDateTimeValue(record, "creationDate"),
                        stringValue(record, "status"),
                        stringValue(record, "orderType"),
                        stringValue(record, "address"),
                        stringValue(record, "restaurantId"),
                        record.get("itemCount").asLong(0)
                ))
                .all()
                .stream()
                .collect(Collectors.toList());
    }

    private String stringValue(Record record, String key) {
        Value value = record.get(key);
        return value == null || value.isNull() ? null : value.asString();
    }

    private LocalDateTime localDateTimeValue(Record record, String key) {
        Value value = record.get(key);
        return value == null || value.isNull() ? null : value.asLocalDateTime();
    }

    private void addOrdersSection(Document document, List<OrderReportRowDto> orders) throws DocumentException {
        addSectionTitle(document, "Simple section 1: Neo4j orders");
        PdfPTable table = table(7, 2.1f, 1.6f, 1.4f, 1.3f, 2.5f, 1.8f, 0.8f);
        addHeaders(table, "Order ID", "Created", "Status", "Type", "Address", "Restaurant", "Items");
        if (orders.isEmpty()) {
            addEmptyRow(table, 7, "No Neo4j orders found for selected filters.");
        } else {
            orders.forEach(order -> addCells(table,
                    order.getId(),
                    value(order.getCreationDate()),
                    order.getStatus(),
                    order.getOrderType(),
                    order.getAddress(),
                    order.getRestaurantId(),
                    value(order.getItemCount())
            ));
        }
        document.add(table);
    }

    private void addMetricsSection(Document document, List<OrderMetric> metrics) throws DocumentException {
        addSectionTitle(document, "Simple section 2: InfluxDB order metrics");
        PdfPTable table = table(8, 1.8f, 1.3f, 1.2f, 1.2f, 1.6f, 1.1f, 0.9f, 1.0f);
        addHeaders(table, "Order ID", "Status", "Type", "City", "Payment", "Amount", "Items", "Distance");
        if (metrics.isEmpty()) {
            addEmptyRow(table, 8, "No InfluxDB order metrics found.");
        } else {
            metrics.stream().limit(30).forEach(metric -> addCells(table,
                    metric.getOrderId(),
                    metric.getStatus(),
                    metric.getOrderType(),
                    metric.getCity(),
                    metric.getPaymentMethod(),
                    money(metric.getTotalAmount()),
                    value(metric.getItemCount()),
                    decimal(metric.getDeliveryKm())
            ));
        }
        document.add(table);
    }

    private void addTopCitiesSection(Document document, List<TopCityRevenueDto> topCities) throws DocumentException {
        addSectionTitle(document, "Complex section: Top delivery cities by completed revenue");
        PdfPTable table = table(2, 2f, 1f);
        addHeaders(table, "City", "Total revenue");
        if (topCities.isEmpty()) {
            addEmptyRow(table, 2, "No revenue data found.");
        } else {
            topCities.forEach(city -> addCells(table, city.getCity(), money(city.getTotalRevenue())));
        }
        document.add(table);
        addTopCitiesChart(document, topCities);
    }

    private void addStatusAverageSection(Document document, List<StatusAvgOrderValueDto> averages) throws DocumentException {
        addSectionTitle(document, "Additional complex query: Average order value by status");
        PdfPTable table = table(2, 1f, 1f);
        addHeaders(table, "Status", "Average order value");
        if (averages.isEmpty()) {
            addEmptyRow(table, 2, "No status average data found.");
        } else {
            averages.forEach(avg -> addCells(table, avg.getStatus(), money(avg.getAvgOrderValue())));
        }
        document.add(table);
    }

    private void addPaymentDistanceSection(Document document, List<PaymentDeliveryDistanceDto> distances) throws DocumentException {
        addSectionTitle(document, "Additional complex query: Average delivery distance by payment");
        PdfPTable table = table(2, 1f, 1f);
        addHeaders(table, "Payment method", "Average delivery km");
        if (distances.isEmpty()) {
            addEmptyRow(table, 2, "No delivery distance data found.");
        } else {
            distances.forEach(distance -> addCells(table, distance.getPaymentMethod(), decimal(distance.getAvgDeliveryKm())));
        }
        document.add(table);
    }

    private void addTopCitiesChart(Document document, List<TopCityRevenueDto> topCities) throws DocumentException {
        addSectionTitle(document, "Chart: Revenue by city");
        PdfPTable chart = table(2, 1.4f, 4f);
        addHeaders(chart, "City", "Revenue bar");

        double max = topCities.stream()
                .map(TopCityRevenueDto::getTotalRevenue)
                .filter(value -> value != null && value > 0)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0);

        if (topCities.isEmpty() || max <= 0) {
            addEmptyRow(chart, 2, "No chart data found.");
        } else {
            topCities.forEach(city -> {
                chart.addCell(cell(city.getCity(), BODY_FONT, Color.WHITE, Element.ALIGN_LEFT));
                chart.addCell(barCell(city.getTotalRevenue(), max));
            });
        }
        document.add(chart);
    }

    private void addSectionTitle(Document document, String text) throws DocumentException {
        Paragraph title = new Paragraph(text, SECTION_FONT);
        title.setSpacingBefore(10);
        title.setSpacingAfter(5);
        document.add(title);
    }

    private PdfPTable table(int columns, float... widths) throws DocumentException {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingAfter(8);
        return table;
    }

    private void addHeaders(PdfPTable table, String... headers) {
        for (String header : headers) {
            table.addCell(cell(header, HEADER_FONT, new Color(64, 83, 105), Element.ALIGN_CENTER));
        }
    }

    private void addCells(PdfPTable table, String... values) {
        for (String value : values) {
            table.addCell(cell(value(value), BODY_FONT, Color.WHITE, Element.ALIGN_LEFT));
        }
    }

    private void addEmptyRow(PdfPTable table, int colspan, String text) {
        PdfPCell cell = cell(text, BODY_FONT, Color.WHITE, Element.ALIGN_LEFT);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    private PdfPCell cell(String value, Font font, Color background, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value(value), font));
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell barCell(Double value, double max) {
        String bar = "#".repeat(Math.max(1, (int) Math.round((value == null ? 0 : value) / max * 40)));
        PdfPCell cell = cell(bar + " " + money(value), BODY_FONT, Color.WHITE, Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String value(Object value) {
        if (value == null) {
            return "-";
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DATE_FORMAT);
        }
        return value.toString();
    }

    private String money(Double value) {
        return value == null ? "-" : String.format("%.2f", value);
    }

    private String decimal(Double value) {
        return value == null ? "-" : String.format("%.2f", value);
    }
}
