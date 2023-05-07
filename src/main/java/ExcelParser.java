import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelParser {
    public static IntersectionModel Parse(File excelFile) throws IOException {
        IntersectionModel intersectionModel = new IntersectionModel();
        FileInputStream file = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(file);

        Sheet setupSheet = workbook.getSheet("Setup");
        for (Row row : setupSheet) {
            intersectionModel.putLight(new TrafficLightModel(row.getCell(0).getNumericCellValue(), row.getCell(1).getNumericCellValue()));
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
