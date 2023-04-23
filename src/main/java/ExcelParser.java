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
    public static MyModel Parse(File excelFile) throws IOException {
        MyModel model = new MyModel();
        FileInputStream file = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(file);

        Sheet setupSheet = workbook.getSheet("Setup");
        for (Row row : setupSheet) {
            model.putLight(new MyModelItem(row.getCell(0).getNumericCellValue(), row.getCell(1).getNumericCellValue()));
        }

        Sheet conflictMatrixSheet = workbook.getSheet("ConflictMatrix");
        MyModelItem currentItem = null;
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
                            currentItem = model.getLight(cell.getNumericCellValue());
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return model;
    }
}
