import java.io.File;
import java.io.IOException;

public class Main {
    // Example args: 11000 "conflictMatrices/conflict-matrix-v0.6.xlsx"
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        File excelFile = new File(args[1]);

        IntersectionModel intersectionModel;

        // Read Excel file
        try {
            intersectionModel = ExcelParser.Parse(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Server server = new Server(port, intersectionModel);
        server.start();
    }
}
