package com.carvalhotechsolutions.mundoanimal.utils;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioManager {

    public void gerarRelatorioPDF(List<Agendamento> agendamentos, LocalDate dataInicio, LocalDate dataFim, File file) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(file));

        document.open();

        // Título
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Paragraph title = new Paragraph("Relatório de Agendamentos - Pet Shop Mundo Animal", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5f); // Espaço após o título
        document.add(title);

        // Período do relatório
        Font periodFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
        Paragraph period = new Paragraph("Período: " + formatarData(dataInicio) + " - " + formatarData(dataFim), periodFont);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(10f); // Espaço após o período
        document.add(period);

        // Tabela de agendamentos
        PdfPTable table = criarTabelaAgendamentos(agendamentos);
        document.add(table);

        // Seção de Resumo Financeiro com sublinhado
        Font sectionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD | Font.UNDERLINE);
        Paragraph resumoFinanceiro = new Paragraph("Resumo Financeiro", sectionFont);
        resumoFinanceiro.setSpacingBefore(30f);
        resumoFinanceiro.setSpacingAfter(15f);
        document.add(resumoFinanceiro);

        // Total arrecadado
        double total = calcularTotal(agendamentos);
        Font totalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Paragraph totalParagraph = new Paragraph("Total Arrecadado No Período: R$ " + String.format("%.2f", total), totalFont);
        totalParagraph.setAlignment(Element.ALIGN_LEFT);
        totalParagraph.setSpacingAfter(10f);
        document.add(totalParagraph);

        // Cliente mais frequente
        String clienteFrequente = calcularClienteMaisFrequente(agendamentos);
        Paragraph clienteFrequenteParagraph = new Paragraph("Cliente mais frequente: " + clienteFrequente, totalFont);
        clienteFrequenteParagraph.setSpacingAfter(20f);
        document.add(clienteFrequenteParagraph);

        // Valor arrecadado por serviço
        Map<String, Double> arrecadacaoPorServico = calcularArrecadacaoPorServico(agendamentos);
        Font subHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);
        Paragraph arrecadacaoParagraph = new Paragraph("Valor arrecadado por serviço:", subHeaderFont);
        arrecadacaoParagraph.setSpacingAfter(10f);
        document.add(arrecadacaoParagraph);

        for (Map.Entry<String, Double> entry : arrecadacaoPorServico.entrySet()) {
            Paragraph servicoValor = new Paragraph(entry.getKey() + ": R$ " + String.format("%.2f", entry.getValue()));
            servicoValor.setIndentationLeft(20f); // Indentação para melhor visualização
            document.add(servicoValor);
        }

        // Seção de Gráficos
        Paragraph graficosTitle = new Paragraph("Análise Gráfica", sectionFont);
        graficosTitle.setSpacingBefore(30f);
        graficosTitle.setSpacingAfter(20f);
        document.add(graficosTitle);

        // Gráfico de pizza com legenda descritiva
        JFreeChart pieChart = criarGraficoPizza(agendamentos);
        Image chartImage = converterGraficoParaImagem(pieChart);
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(450, 280); // Ajuste do tamanho
        document.add(chartImage);

        // Espaço entre os gráficos
        document.add(new Paragraph("\n"));

        // Gráfico de barras
        JFreeChart barChart = criarGraficoBarras(agendamentos);
        Image barChartImage = converterGraficoParaImagem(barChart);
        barChartImage.setAlignment(Element.ALIGN_CENTER);
        barChartImage.scaleToFit(450, 280); // Ajuste do tamanho
        document.add(barChartImage);

        document.close();
    }

    private PdfPTable criarTabelaAgendamentos(List<Agendamento> agendamentos) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f); // Aumentado o espaço após a tabela

        // Tente definir larguras relativas das colunas
        try {
            table.setWidths(new float[]{2.5f, 1.5f, 1.5f, 2f, 2f, 1.5f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        String[] headers = {"Serviço", "Data", "Pet", "Tutor", "Profissional", "Valor"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setPadding(5f); // Adiciona padding nas células
            table.addCell(cell);
        }

        Font contentFont = new Font(Font.FontFamily.HELVETICA, 10);
        for (Agendamento a : agendamentos) {
            // Adiciona células com a nova fonte e padding
            PdfPCell[] cells = {
                    new PdfPCell(new Phrase(a.getServico().getNomeServico(), contentFont)),
                    new PdfPCell(new Phrase(formatarData(a.getDataAgendamento()), contentFont)),
                    new PdfPCell(new Phrase(a.getAnimal().getNome(), contentFont)),
                    new PdfPCell(new Phrase(a.getCliente().getNome(), contentFont)),
                    new PdfPCell(new Phrase(a.getResponsavelAtendimento(), contentFont)),
                    new PdfPCell(new Phrase(String.format("R$ %.2f", a.getServico().getValorServico()), contentFont))
            };

            for (PdfPCell cell : cells) {
                cell.setPadding(5f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
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

    private JFreeChart criarGraficoBarras(List<Agendamento> agendamentos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> arrecadacaoPorServico = calcularArrecadacaoPorServico(agendamentos);

        for (Map.Entry<String, Double> entry : arrecadacaoPorServico.entrySet()) {
            dataset.addValue(entry.getValue(), "Faturamento", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Faturamento por Serviço",
                "Serviço",
                "Valor (R$)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return barChart;
    }


    private Image converterGraficoParaImagem(JFreeChart chart) throws IOException, BadElementException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsPNG(baos, chart, 500, 300);
        return Image.getInstance(baos.toByteArray());
    }

    private String calcularClienteMaisFrequente(List<Agendamento> agendamentos) {
        Map<String, Long> contagemClientes = agendamentos.stream()
                .collect(Collectors.groupingBy(a -> a.getCliente().getNome(), Collectors.counting()));
        return contagemClientes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private Map<String, Double> calcularArrecadacaoPorServico(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .collect(Collectors.groupingBy(a -> a.getServico().getNomeServico(),
                        Collectors.summingDouble(a -> a.getServico().getValorServico().doubleValue())));
    }

    private String formatarData(LocalDate data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}


