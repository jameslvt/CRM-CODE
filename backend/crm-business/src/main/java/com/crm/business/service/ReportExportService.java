package com.crm.business.service;

import com.crm.business.dto.DashboardDTO;
import com.crm.business.dto.PerformanceTrendDTO;
import com.crm.business.dto.SalesFunnelDTO;
import com.crm.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报表导出服务
 * 提供仪表盘数据、销售漏斗、业绩趋势等报表的导出功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final DashboardService dashboardService;

    /**
     * 导出仪表盘报表
     *
     * @param response HTTP响应
     * @param ownerId 负责人ID（可选）
     */
    public void exportDashboardReport(HttpServletResponse response, Long ownerId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 获取数据
            DashboardDTO dashboard = dashboardService.getDashboardData(ownerId);
            SalesFunnelDTO funnel = dashboardService.getSalesFunnel(ownerId);
            PerformanceTrendDTO trend = dashboardService.getPerformanceTrend(ownerId, "month", 6);

            // 创建汇总数据Sheet
            createSummarySheet(workbook, dashboard);

            // 创建销售漏斗Sheet
            createFunnelSheet(workbook, funnel);

            // 创建业绩趋势Sheet
            createTrendSheet(workbook, trend);

            // 设置响应头
            String fileName = "销售报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            setExcelResponse(response, fileName);

            // 写入响应流
            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }

            log.info("导出仪表盘报表成功");

        } catch (Exception e) {
            log.error("导出仪表盘报表失败", e);
            throw new BusinessException("导出报表失败: " + e.getMessage());
        }
    }

    /**
     * 导出销售漏斗报表
     */
    public void exportFunnelReport(HttpServletResponse response, Long ownerId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            SalesFunnelDTO funnel = dashboardService.getSalesFunnel(ownerId);
            createFunnelSheet(workbook, funnel);

            String fileName = "销售漏斗_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            setExcelResponse(response, fileName);

            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }

        } catch (Exception e) {
            log.error("导出销售漏斗报表失败", e);
            throw new BusinessException("导出报表失败: " + e.getMessage());
        }
    }

    /**
     * 导出业绩趋势报表
     */
    public void exportTrendReport(HttpServletResponse response, Long ownerId, int months) {
        try (Workbook workbook = new XSSFWorkbook()) {
            PerformanceTrendDTO trend = dashboardService.getPerformanceTrend(ownerId, "month", months);
            createTrendSheet(workbook, trend);

            String fileName = "业绩趋势_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            setExcelResponse(response, fileName);

            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }

        } catch (Exception e) {
            log.error("导出业绩趋势报表失败", e);
            throw new BusinessException("导出报表失败: " + e.getMessage());
        }
    }

    /**
     * 创建汇总数据Sheet
     */
    private void createSummarySheet(Workbook workbook, DashboardDTO dashboard) {
        Sheet sheet = workbook.createSheet("数据汇总");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        int rowNum = 0;

        // 标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("CRM 销售数据汇总报表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // 生成日期
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("生成日期: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        rowNum++;

        // 线索统计
        if (dashboard.getLeadStats() != null) {
            rowNum = createStatsSection(sheet, headerStyle, rowNum, "线索统计",
                    new String[]{"总线索数", "本月新增", "待跟进", "已转化", "转化率"},
                    new Object[]{
                            dashboard.getLeadStats().getTotalCount(),
                            dashboard.getLeadStats().getMonthNewCount(),
                            dashboard.getLeadStats().getPendingCount(),
                            dashboard.getLeadStats().getConvertedCount(),
                            formatPercent(dashboard.getLeadStats().getConversionRate())
                    });
        }

        // 客户统计
        if (dashboard.getCustomerStats() != null) {
            rowNum = createStatsSection(sheet, headerStyle, rowNum, "客户统计",
                    new String[]{"总客户数", "本月新增", "公海池", "活跃客户"},
                    new Object[]{
                            dashboard.getCustomerStats().getTotalCount(),
                            dashboard.getCustomerStats().getMonthNewCount(),
                            dashboard.getCustomerStats().getPoolCount(),
                            dashboard.getCustomerStats().getActiveCount()
                    });
        }

        // 商机统计
        if (dashboard.getOpportunityStats() != null) {
            rowNum = createStatsSection(sheet, headerStyle, rowNum, "商机统计",
                    new String[]{"总商机数", "本月新增", "进行中", "赢单数", "输单数", "赢单率", "总金额", "赢单金额"},
                    new Object[]{
                            dashboard.getOpportunityStats().getTotalCount(),
                            dashboard.getOpportunityStats().getMonthNewCount(),
                            dashboard.getOpportunityStats().getOngoingCount(),
                            dashboard.getOpportunityStats().getWonCount(),
                            dashboard.getOpportunityStats().getLostCount(),
                            formatPercent(dashboard.getOpportunityStats().getWinRate()),
                            formatMoney(dashboard.getOpportunityStats().getTotalAmount()),
                            formatMoney(dashboard.getOpportunityStats().getWonAmount())
                    });
        }

        // 合同统计
        if (dashboard.getContractStats() != null) {
            rowNum = createStatsSection(sheet, headerStyle, rowNum, "合同统计",
                    new String[]{"总合同数", "本月新增", "执行中", "总金额", "本月签约金额"},
                    new Object[]{
                            dashboard.getContractStats().getTotalCount(),
                            dashboard.getContractStats().getMonthNewCount(),
                            dashboard.getContractStats().getExecutingCount(),
                            formatMoney(dashboard.getContractStats().getTotalAmount()),
                            formatMoney(dashboard.getContractStats().getMonthSignedAmount())
                    });
        }

        // 回款统计
        if (dashboard.getPaymentStats() != null) {
            createStatsSection(sheet, headerStyle, rowNum, "回款统计",
                    new String[]{"计划回款总额", "实际回款总额", "本月回款", "待回款", "逾期金额", "完成率"},
                    new Object[]{
                            formatMoney(dashboard.getPaymentStats().getTotalPlanAmount()),
                            formatMoney(dashboard.getPaymentStats().getTotalActualAmount()),
                            formatMoney(dashboard.getPaymentStats().getMonthActualAmount()),
                            formatMoney(dashboard.getPaymentStats().getPendingAmount()),
                            formatMoney(dashboard.getPaymentStats().getOverdueAmount()),
                            formatPercent(dashboard.getPaymentStats().getCompletionRate())
                    });
        }

        // 自动调整列宽
        for (int i = 0; i < 4; i++) {
            sheet.setColumnWidth(i, 5000);
        }
    }

    /**
     * 创建统计区块
     */
    private int createStatsSection(Sheet sheet, CellStyle headerStyle, int startRow,
                                   String sectionTitle, String[] labels, Object[] values) {
        int rowNum = startRow;

        // 区块标题
        Row sectionRow = sheet.createRow(rowNum++);
        Cell sectionCell = sectionRow.createCell(0);
        sectionCell.setCellValue(sectionTitle);
        sectionCell.setCellStyle(headerStyle);

        // 数据行
        for (int i = 0; i < labels.length; i++) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(labels[i]);
            dataRow.createCell(1).setCellValue(values[i] != null ? values[i].toString() : "-");
        }

        rowNum++; // 空行
        return rowNum;
    }

    /**
     * 创建销售漏斗Sheet
     */
    private void createFunnelSheet(Workbook workbook, SalesFunnelDTO funnel) {
        Sheet sheet = workbook.createSheet("销售漏斗");
        CellStyle headerStyle = createHeaderStyle(workbook);
        int rowNum = 0;

        // 表头
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"阶段", "商机数", "金额", "数量占比", "金额占比", "转化率", "赢单概率"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 数据行
        if (funnel.getStages() != null) {
            for (SalesFunnelDTO.FunnelStage stage : funnel.getStages()) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(stage.getStageName());
                dataRow.createCell(1).setCellValue(stage.getCount());
                dataRow.createCell(2).setCellValue(formatMoney(stage.getAmount()));
                dataRow.createCell(3).setCellValue(formatPercent(stage.getCountRatio()));
                dataRow.createCell(4).setCellValue(formatPercent(stage.getAmountRatio()));
                dataRow.createCell(5).setCellValue(formatPercent(stage.getConversionRate()));
                dataRow.createCell(6).setCellValue(stage.getProbability() + "%");
            }
        }

        // 汇总行
        rowNum++;
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("合计");
        totalRow.createCell(1).setCellValue(funnel.getTotalCount());
        totalRow.createCell(2).setCellValue(formatMoney(funnel.getTotalAmount()));

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 4000);
        }
    }

    /**
     * 创建业绩趋势Sheet
     */
    private void createTrendSheet(Workbook workbook, PerformanceTrendDTO trend) {
        Sheet sheet = workbook.createSheet("业绩趋势");
        CellStyle headerStyle = createHeaderStyle(workbook);
        int rowNum = 0;

        // 表头
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"月份", "新增线索", "新增客户", "新增商机", "商机金额", "赢单数", "赢单金额", "签约合同", "签约金额", "回款金额"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 数据行
        List<PerformanceTrendDTO.TrendPoint> points = trend.getTrendPoints();
        if (points != null) {
            for (PerformanceTrendDTO.TrendPoint point : points) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(point.getPeriod());
                dataRow.createCell(1).setCellValue(point.getNewLeadCount());
                dataRow.createCell(2).setCellValue(point.getNewCustomerCount());
                dataRow.createCell(3).setCellValue(point.getNewOpportunityCount());
                dataRow.createCell(4).setCellValue(formatMoney(point.getOpportunityAmount()));
                dataRow.createCell(5).setCellValue(point.getWonCount());
                dataRow.createCell(6).setCellValue(formatMoney(point.getWonAmount()));
                dataRow.createCell(7).setCellValue(point.getSignedContractCount());
                dataRow.createCell(8).setCellValue(formatMoney(point.getSignedAmount()));
                dataRow.createCell(9).setCellValue(formatMoney(point.getPaymentAmount()));
            }
        }

        // 汇总行
        PerformanceTrendDTO.TrendSummary summary = trend.getSummary();
        if (summary != null) {
            rowNum++;
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("合计");
            totalRow.createCell(1).setCellValue(summary.getTotalNewLeadCount());
            totalRow.createCell(2).setCellValue(summary.getTotalNewCustomerCount());
            totalRow.createCell(3).setCellValue(summary.getTotalNewOpportunityCount());
            totalRow.createCell(4).setCellValue(formatMoney(summary.getTotalOpportunityAmount()));
            totalRow.createCell(5).setCellValue(summary.getTotalWonCount());
            totalRow.createCell(6).setCellValue(formatMoney(summary.getTotalWonAmount()));
            totalRow.createCell(7).setCellValue(summary.getTotalSignedContractCount());
            totalRow.createCell(8).setCellValue(formatMoney(summary.getTotalSignedAmount()));
            totalRow.createCell(9).setCellValue(formatMoney(summary.getTotalPaymentAmount()));
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 4000);
        }
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        return style;
    }

    /**
     * 创建标题样式
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);

        return style;
    }

    /**
     * 设置Excel响应头
     */
    private void setExcelResponse(HttpServletResponse response, String fileName) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");
    }

    /**
     * 格式化金额
     */
    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f", amount);
    }

    /**
     * 格式化百分比
     */
    private String formatPercent(BigDecimal percent) {
        if (percent == null) {
            return "0.00%";
        }
        return String.format("%.2f%%", percent);
    }
}
