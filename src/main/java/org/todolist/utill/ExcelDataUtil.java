package org.todolist.utill;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.util.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelDataUtil {

    public List<Map<String, Object>> readExcelData(FileInputStream fileInputStream) {

        // Prevent OOM on large files (POI default cap is ~100 MB)
        IOUtils.setByteArrayMaxOverride(300_000_000);

        long t0 = System.nanoTime();

        List<Map<String, Object>> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) return result;

            long t1 = System.nanoTime();

            // ── Read headers ──────────────────────────────────────────────
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) return result;

            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(normalizeHeader(getCellStringValue(cell)));
            }

            // Cache size — avoids repeated List.size() call on every row × every cell
            final int headerSize = headers.size();

            long t2 = System.nanoTime();

            // ── Collect raw rows (sequential — I/O bound) ─────────────────
            // Pre-size to avoid ArrayList internal array copies (10 → 20 → 40…)
            int estimatedRows = Math.max(sheet.getLastRowNum(), 16);
            List<Row> rawRows = new ArrayList<>(estimatedRows);
            rowIterator.forEachRemaining(rawRows::add);

            long t3 = System.nanoTime();

            // ── Convert rows in parallel (CPU bound, stateless per row) ───
            result = rawRows.parallelStream()
                    .map(row -> convertRow(row, headers, headerSize))
                    .filter(map -> !map.values().stream().allMatch(Objects::isNull))
                    .collect(Collectors.toList());

            long t4 = System.nanoTime();

            printTimingReport(rawRows.size(), result.size(), t0, t1, t2, t3, t4);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file", e);
        }

        return result;
    }

    // ── Row → Map ────────────────────────────────────────────────────────────

    private Map<String, Object> convertRow(Row row, List<String> headers, int headerSize) {
        // Pre-size: initial capacity = n / 0.75 to avoid rehashing
        Map<String, Object> map = new LinkedHashMap<>(headerSize * 2);
        for (int i = 0; i < headerSize; i++) {
            Cell cell = row.getCell(i, MissingCellPolicy.RETURN_BLANK_AS_NULL);
            map.put(headers.get(i), getCellValue(cell));
        }
        return map;
    }

    // ── Cell value (no FormulaEvaluator — uses POI's cached result) ─────────

    private static Object getCellValue(Cell cell) {
        if (cell == null) return null;

        try {
            CellType type = cell.getCellType();

            // For formula cells: read the cached result POI stored on load.
            // This avoids re-executing every formula — the dominant original overhead.
            if (type == CellType.FORMULA) {
                type = cell.getCachedFormulaResultType();
            }

            switch (type) {
                case STRING:
                    return cell.getStringCellValue().trim();

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue(); // java.util.Date
                    }
                    double d = cell.getNumericCellValue();
                    return (d == (long) d) ? (long) d : d;

                case BOOLEAN:
                    return cell.getBooleanCellValue();

                case BLANK:
                case ERROR:
                case _NONE:
                default:
                    return null;
            }
        } catch (Exception e) {
            return null; // degrade gracefully on corrupt/unsupported cells
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) type = cell.getCachedFormulaResultType();
        switch (type) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: {
                double d = cell.getNumericCellValue();
                return (d == (long) d) ? String.valueOf((long) d) : String.valueOf(d);
            }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return "";
        }
    }

    private static String normalizeHeader(String h) {
        return h == null ? "" : h.trim();
    }

    // ── Timing report ────────────────────────────────────────────────────────

    private void printTimingReport(int rawCount, int parsedCount,
                                   long t0, long t1, long t2, long t3, long t4) {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║            Excel Read — Timing Breakdown              ║");
        System.out.println("╠═══════════════════════════════════════════════════════╣");
        System.out.printf ("║  Workbook open          : %8.2f ms%n", ms(t1 - t0));
        System.out.printf ("║  Header parse           : %8.2f ms%n", ms(t2 - t1));
        System.out.printf ("║  Row collection (I/O)   : %8.2f ms%n", ms(t3 - t2));
        System.out.printf ("║  Parallel map convert   : %8.2f ms%n", ms(t4 - t3));
        System.out.println("║───────────────────────────────────────────────────────║");
        System.out.printf ("║  ⏱  TOTAL               : %8.2f ms%n", ms(t4 - t0));
        System.out.printf ("║  Rows read: %-6d   Rows returned: %-6d%n", rawCount, parsedCount);
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private double ms(long nanos) { return nanos / 1_000_000.0; }
}
