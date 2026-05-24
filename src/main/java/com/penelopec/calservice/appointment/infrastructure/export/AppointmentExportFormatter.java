package com.penelopec.calservice.appointment.infrastructure.export;

import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class AppointmentExportFormatter {

  private static final String[] HEADERS = {
    "id",
    "status",
    "inicio",
    "fim",
    "nomeParticipante",
    "emailParticipante",
    "observacoes",
    "motivo",
    "criadoEm",
    "atualizadoEm"
  };

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  private AppointmentExportFormatter() {
  }

  public static String toCsv(List<AppointmentOutput> appointments) {
    StringBuilder csv = new StringBuilder();
    csv.append(String.join(",", HEADERS)).append('\n');

    for (AppointmentOutput appointment : appointments) {
      csv.append(toCsvLine(appointment)).append('\n');
    }

    return csv.toString();
  }

  public static byte[] toXlsx(List<AppointmentOutput> appointments) {
    try (var workbook = new XSSFWorkbook(); var output = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("appointments");
      CellStyle dateTimeStyle = workbook.createCellStyle();
      dateTimeStyle.setDataFormat(
        workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss")
      );
      int rowIndex = 0;

      Row headerRow = sheet.createRow(rowIndex++);
      for (int i = 0; i < HEADERS.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(HEADERS[i]);
      }

      for (AppointmentOutput appointment : appointments) {
        Row row = sheet.createRow(rowIndex++);
        writeRow(row, appointment, dateTimeStyle);
      }

      for (int i = 0; i < HEADERS.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(output);
      return output.toByteArray();
    } catch (IOException ex) {
      throw new IllegalStateException("Falha ao gerar arquivo xlsx", ex);
    }
  }

  private static void writeRow(Row row, AppointmentOutput appointment, CellStyle dateTimeStyle) {
    int col = 0;
    row.createCell(col++).setCellValue(textValue(appointment.id()));
    row.createCell(col++).setCellValue(textValue(appointment.status()));
    writeDateCell(row.createCell(col++), appointment.startDateTime(), dateTimeStyle);
    writeDateCell(row.createCell(col++), appointment.endDateTime(), dateTimeStyle);
    row.createCell(col++).setCellValue(textValue(appointment.attendeeName()));
    row.createCell(col++).setCellValue(textValue(appointment.attendeeEmail()));
    row.createCell(col++).setCellValue(textValue(appointment.notes()));
    row.createCell(col++).setCellValue(textValue(appointment.reason()));
    writeDateCell(row.createCell(col++), appointment.createdAt(), dateTimeStyle);
    writeDateCell(row.createCell(col++), appointment.updatedAt(), dateTimeStyle);
  }

  private static String toCsvLine(AppointmentOutput appointment) {
    return String.join(",",
      cell(appointment.id()),
      cell(appointment.status()),
      cell(appointment.startDateTime()),
      cell(appointment.endDateTime()),
      cell(appointment.attendeeName()),
      cell(appointment.attendeeEmail()),
      cell(appointment.notes()),
      cell(appointment.reason()),
      cell(appointment.createdAt()),
      cell(appointment.updatedAt())
    );
  }

  private static String cell(Object value) {
    if (value == null) {
      return "";
    }

    String text = value instanceof LocalDateTime dateTime
      ? DATE_TIME_FORMATTER.format(dateTime)
      : value.toString();

    String escaped = text.replace("\"", "\"\"");
    return "\"" + escaped + "\"";
  }

  private static void writeDateCell(Cell cell, LocalDateTime value, CellStyle dateTimeStyle) {
    if (value == null) {
      cell.setBlank();
      return;
    }

    cell.setCellValue(Timestamp.valueOf(value));
    cell.setCellStyle(dateTimeStyle);
  }

  private static String textValue(Object value) {
    if (value == null) {
      return "";
    }

    if (value instanceof LocalDateTime dateTime) {
      return DATE_TIME_FORMATTER.format(dateTime);
    }

    return value.toString();
  }
}
