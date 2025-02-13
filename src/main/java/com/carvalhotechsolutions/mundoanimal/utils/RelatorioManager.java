package com.carvalhotechsolutions.mundoanimal.utils;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioManager {

    public void gerarRelatorioPDF(List<Agendamento> agendamentos, LocalDate dataInicio, LocalDate dataFim, File file) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(file));

        document.open();

        // Título
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Paragraph title = new Paragraph("Relatório de Agendamentos - Mundo Animal", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Período do relatório
        Font periodFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
        Paragraph period = new Paragraph("Período: " + formatarData(dataInicio) + " - " + formatarData(dataFim), periodFont);
        period.setAlignment(Element.ALIGN_CENTER);
        document.add(period);
        document.add(new Paragraph("\n"));

        // Tabela de agendamentos
        PdfPTable table = criarTabelaAgendamentos(agendamentos);
        document.add(table);

        // Total arrecadado
        double total = calcularTotal(agendamentos);
        Font totalFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Paragraph totalParagraph = new Paragraph("Total Arrecadado No Período: R$ " + String.format("%.2f", total), totalFont);
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        totalParagraph.setSpacingBefore(10f);
        document.add(totalParagraph);

        // Gerar gráfico de pizza
        JFreeChart chart = criarGraficoPizza(agendamentos);
        Image chartImage = converterGraficoParaImagem(chart);
        document.add(chartImage);

        document.close();
    }

    private PdfPTable criarTabelaAgendamentos(List<Agendamento> agendamentos) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        String[] headers = {"Serviço", "Data", "Pet", "Tutor", "Profissional", "Valor"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
        }

        for (Agendamento a : agendamentos) {
            table.addCell(a.getServico().getNomeServico());
            table.addCell(formatarData(a.getDataAgendamento()));
            table.addCell(a.getAnimal().getNome());
            table.addCell(a.getCliente().getNome());
            table.addCell(a.getResponsavelAtendimento());
            table.addCell(String.format("R$ %.2f", a.getServico().getValorServico()));
        }

        return table;
    }

    private double calcularTotal(List<Agendamento> agendamentos) {
        return agendamentos.stream().mapToDouble(a -> a.getServico().getValorServico().doubleValue()).sum();
    }

    private JFreeChart criarGraficoPizza(List<Agendamento> agendamentos) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> servicoContagem = new HashMap<>();

        // Contar quantos agendamentos tem por tipo de serviço
        for (Agendamento a : agendamentos) {
            servicoContagem.put(a.getServico().getNomeServico(), servicoContagem.getOrDefault(a.getServico().getNomeServico(), 0) + 1);
        }

        // Adicionar ao dataset do gráfico
        for (Map.Entry<String, Integer> entry : servicoContagem.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart(
                "Distribuição de Agendamentos por Serviço",
                dataset,
                true, // Legenda
                true, // Tooltips
                false // URLs
        );
    }

    private Image converterGraficoParaImagem(JFreeChart chart) throws IOException, BadElementException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsPNG(baos, chart, 500, 300);
        return Image.getInstance(baos.toByteArray());
    }

    private String formatarData(LocalDate data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}


