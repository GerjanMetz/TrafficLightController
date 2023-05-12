import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ExcelParser {
    public static IntersectionModel Parse(File excelFile) throws IOException {
        IntersectionModel intersectionModel = new IntersectionModel();
        FileInputStream file = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(file);

        Sheet setupSheet = workbook.getSheet("Setup");
        for (Row row : setupSheet) {
            XSSFColor color = (XSSFColor) row.getCell(0).getCellStyle().getFillForegroundColorColor();

            switch (color.getARGBHex()) {
                case "FFFFEB9C":
                    intersectionModel.putLight(new TrafficLightModel(row.getCell(0).getNumericCellValue(), row.getCell(1).getNumericCellValue()));
                    break;
                case "FFFFFFCC":
                    intersectionModel.setTimer(new Timer(
                            row.getCell(0).getNumericCellValue(),
                            row.getCell(1).getNumericCellValue(),
                            intersectionModel.getLight(0.0),
                            intersectionModel.getLights(List.of(86.1, 26.1)),
                            intersectionModel.getLights(List.of(42.0))));
                    break;
            }
        }

        Sheet conflictMatrixSheet = workbook.getSheet("ConflictMatrix");
        TrafficLightModel currentItem = null;
        for (Row row : conflictMatrixSheet) {
            for (Cell cell : row) {
                try {
                    XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
                    switch (color.getARGBHex()) {
                        case "FFA5A5A5":
                            break;
                        case "FFC6EFCE":
                            currentItem.addPossibility(cell.getNumericCellValue());
                            break;
                        case "FFFFC7CE":
                            currentItem.addConflict(cell.getNumericCellValue());
                            break;
                        case "FFFFEB9C":
                            currentItem = intersectionModel.getLight(cell.getNumericCellValue());
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return intersectionModel;
    }
}
