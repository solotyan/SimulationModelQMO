package org.solotyan.simulationmodelqmo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class ResultController {
    private Stage stage;
    @FXML
    private TextField textFieldResultW;
    @FXML
    private TextField textFieldResultK;
    @FXML
    private TextField textFieldResultQ;
    @FXML
    private Button buttonResult;
    private Data data;
    @FXML
    public void initialize(){
        Platform.runLater(()->{
            buttonResult.requestFocus();
            Stage stage = (Stage) textFieldResultK.getScene().getWindow();
            data = (Data) stage.getUserData();
            textFieldResultW.setText(String.valueOf(data.getW()));
            textFieldResultK.setText(data.getK() + "%");
            textFieldResultQ.setText(String.valueOf(data.getQ()));
        });
    }

    @FXML
    void getResult() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить как...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Открываем диалог для выбора файла
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx"); // Добавляем расширение .xlsx
            }
            XSSFWorkbook book = new XSSFWorkbook();
            FileOutputStream fileOut = new FileOutputStream(file);
            XSSFSheet sheet = book.createSheet("Результаты моделирования");
            int width = (int) (13 * 1.2) * 256;
            sheet.setColumnWidth(0, width);
            sheet.setColumnWidth(2, width);
            sheet.setColumnWidth(4, width);
            width = (int) (14 * 1.2) * 256;
            sheet.setColumnWidth(1, width);
            sheet.setColumnWidth(3, width);
            sheet.setColumnWidth(9, width);
            sheet.setColumnWidth(10, width);
            width = (int) (7 * 1.2) * 256;
            sheet.setColumnWidth(5, width);
            sheet.setColumnWidth(6, width);
            sheet.setColumnWidth(7, width);

            XSSFRow row = sheet.createRow((short)0);
            XSSFCell cell = createCell(book, row, (short) 9, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Расчеты характеристик");
            cell = createCell(book, row, (short) 10, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 10));

            row = sheet.createRow((short)1);

            cell = createCell(book, row, (short) 9, HorizontalAlignment.RIGHT);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("K=");

            cell = createCell(book, row, (short) 10, HorizontalAlignment.LEFT);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(data.getK());

            row = sheet.createRow((short)2);

            cell = createCell(book, row, (short) 9, HorizontalAlignment.RIGHT);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("W=");

            cell = createCell(book, row, (short) 10, HorizontalAlignment.LEFT);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(data.getW());

            row = sheet.createRow((short)3);

            cell = createCell(book, row, (short) 9, HorizontalAlignment.RIGHT);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Q=");

            cell = createCell(book, row, (short) 10, HorizontalAlignment.LEFT);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(data.getQ());

            row = sheet.createRow((short)5);

            cell = createCell(book, row, (short) 0, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Текущее время");

            cell = createCell(book, row, (short) 1, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Событие");

            cell = createCell(book, row, (short) 2, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Длина очереди");

            cell = createCell(book, row, (short) 3, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Время ожидания");

            cell = createCell(book, row, (short) 4, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Время простоя");

            cell = createCell(book, row, (short) 5, HorizontalAlignment.CENTER_SELECTION);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("Примечание");
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 5, 6));

            cell = createCell(book, row, (short) 7, HorizontalAlignment.CENTER);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("R");

            LinkedList<RowXlsx> rowsXlsx = data.getRowsXlsx();
            LinkedList<RowXlsx> backupRowsXlsx = new LinkedList<>();
            RowXlsx rowXlsx;
            int i = 5;
            XSSFRow row0;

            while (!(rowsXlsx.isEmpty())){
                rowXlsx = rowsXlsx.poll();
                backupRowsXlsx.add(rowXlsx);
                i++;

                if (rowXlsx.getRequest().getTime() == 0){
                    row = sheet.createRow((short) i);

                    cell = createCell(book, row, (short) 0, HorizontalAlignment.CENTER_SELECTION);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getRequest().getTime());

                    for (int j=1; j<=4; j++){
                        cell = createCell(book, row, (short) j, HorizontalAlignment.CENTER_SELECTION);
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue("");
                    }

                    cell = createCell(book, row, (short) 5, HorizontalAlignment.RIGHT);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("з_" + rowXlsx.getNextIncome() + "=");

                    cell = createCell(book, row, (short) 6, HorizontalAlignment.LEFT);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getNextTime2());

                    cell = createCell(book, row, (short) 7, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getR2() == null)){
                        cell.setCellValue(rowXlsx.getR2());
                    } else {
                        cell.setCellValue("");
                    }
                } else if (rowXlsx.getNextTime2() == null) {
                    row = sheet.createRow((short) i);
                    row.setHeight((short) (2 * 1.2 * 256));

                    cell = createCell(book, row, (short) 0, HorizontalAlignment.CENTER_SELECTION);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getRequest().getTime());

                    cell = createCell(book, row, (short) 1, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("уход з_" + rowXlsx.getOutcome().toString() + "\n(простой)");

                    for (int j=2; j<=7; j++){
                        cell = createCell(book, row, (short) j, HorizontalAlignment.CENTER_SELECTION);
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue("");
                    }
                } else if (rowXlsx.getNextTime1() == null){
                    row = sheet.createRow((short) i);

                    cell = createCell(book, row, (short) 0, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getRequest().getTime());

                    cell = createCell(book, row, (short) 1, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.STRING);
                    if (rowXlsx.getRequest().getType().equals(TypeRequest.START_REQUEST) && !(rowXlsx.getQueueLength() == 0)){
                        cell.setCellValue("приход з_" + rowXlsx.getIncome().toString());
                    } else if (rowXlsx.getRequest().getType().equals(TypeRequest.END_REQUEST)) {
                        cell.setCellValue("уход з_" + rowXlsx.getOutcome().toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = createCell(book, row, (short) 2, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getQueueLength() == null) || !(rowXlsx.getQueueLength() == 0)){
                        cell.setCellValue(rowXlsx.getQueueLength());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = createCell(book, row, (short) 3, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getWaitingTime() == null)){
                        cell.setCellValue(rowXlsx.getWaitingTime());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = createCell(book, row, (short) 4, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getDowntimeTime() == null)){
                        cell.setCellValue(rowXlsx.getDowntimeTime());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = createCell(book, row, (short) 5, HorizontalAlignment.RIGHT);
                    cell.setCellType(CellType.STRING);
                    if (rowXlsx.getRequest().getType() == TypeRequest.START_REQUEST){
                        cell.setCellValue("з_" + rowXlsx.getNextIncome() + "=");
                    } else {
                        cell.setCellValue("к_" + rowXlsx.getNextOutcome() + "=");
                    }

                    cell = createCell(book, row, (short) 6, HorizontalAlignment.LEFT);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getNextTime2() == null)){
                        cell.setCellValue(rowXlsx.getNextTime2());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = createCell(book, row, (short) 7, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getR2() == null)){
                        cell.setCellValue(rowXlsx.getR2());
                    } else {
                        cell.setCellValue("");
                    }
                } else {
                    row0 = sheet.createRow((short) i);
                    row = sheet.createRow((short) i+1);

                    cell = createCell(book, row0, (short) 0, HorizontalAlignment.CENTER_SELECTION);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getRequest().getTime());
                    sheet.addMergedRegion(new CellRangeAddress(i, i+1, 0, 0));

                    cell = createCell(book, row0, (short) 1, HorizontalAlignment.CENTER_SELECTION);
                    cell.setCellType(CellType.STRING);
                    if (rowXlsx.getRequest().getType().equals(TypeRequest.START_REQUEST) && !(rowXlsx.getIncome() == 0)){
                        cell.setCellValue("приход з_" + rowXlsx.getIncome().toString());
                    } else if (rowXlsx.getRequest().getType().equals(TypeRequest.END_REQUEST)) {
                        cell.setCellValue("уход з_" + rowXlsx.getOutcome().toString());
                    } else {
                        cell.setCellValue("");
                    }
                    sheet.addMergedRegion(new CellRangeAddress(i, i+1, 1, 1));

                    cell = createCell(book, row0, (short) 2, HorizontalAlignment.CENTER_SELECTION);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getQueueLength() == null) || !(rowXlsx.getQueueLength() == 0)){
                        cell.setCellValue(rowXlsx.getQueueLength());
                    } else {
                        cell.setCellValue("");
                    }
                    sheet.addMergedRegion(new CellRangeAddress(i, i+1, 2, 2));

                    cell = createCell(book, row0, (short) 3, HorizontalAlignment.CENTER_SELECTION);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getWaitingTime() == null)){
                        cell.setCellValue(rowXlsx.getWaitingTime());
                    } else {
                        cell.setCellValue("");
                    }
                    sheet.addMergedRegion(new CellRangeAddress(i, i+1, 3, 3));

                    cell = createCell(book, row0, (short) 4, HorizontalAlignment.CENTER_SELECTION);
                    cell.setCellType(CellType.NUMERIC);
                    if (!(rowXlsx.getDowntimeTime() == null)){
                        cell.setCellValue(rowXlsx.getDowntimeTime());
                    } else {
                        cell.setCellValue("");
                    }
                    sheet.addMergedRegion(new CellRangeAddress(i, i+1, 4, 4));

                    cell = createCell(book, row0, (short) 5, HorizontalAlignment.RIGHT);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("к_" + rowXlsx.getNextOutcome() + "=");

                    cell = createCell(book, row, (short) 5, HorizontalAlignment.RIGHT);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("з_" + rowXlsx.getNextIncome() + "=");

                    cell = createCell(book, row0, (short) 6, HorizontalAlignment.LEFT);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getNextTime1());

                    cell = createCell(book, row, (short) 6, HorizontalAlignment.LEFT);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getNextTime2());

                    cell = createCell(book, row0, (short) 7, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getR1());

                    cell = createCell(book, row, (short) 7, HorizontalAlignment.CENTER);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(rowXlsx.getR2());
                    i++;
                }
            }
            rowsXlsx.addAll(backupRowsXlsx);
            backupRowsXlsx.clear();
            book.write(fileOut);
            fileOut.close();
            String filePath = file.getAbsolutePath();
            alertSavedFile(filePath);
        }

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            File finalFile = file;
            new Thread(() -> {
                try {
                    desktop.open(finalFile);
                } catch (IOException e) {
                    alertErrorDesktop();
                }
            }).start();
        } else {
            alertErrorDesktop();
        }
    }

    private void alertSavedFile(String filePath){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Успех");
        alert.setHeaderText("Успешно сохранено");
        alert.setContentText("Файл сохранен по пути:\n" + filePath);
        alert.showAndWait();
    }

    private void alertErrorDesktop(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Не удалось открыть файл");
        alert.setContentText("Невозможно открыть файл. \nВозможно нет подходящего приложения");
        alert.showAndWait();
    }

    private XSSFCell createCell(Workbook book, Row row, short column,
                            HorizontalAlignment halign) {
        XSSFCell cell = (XSSFCell) row.createCell(column);
        cell.setCellValue("Значение");
        XSSFCellStyle cellStyle = (XSSFCellStyle) book.createCellStyle();
        cellStyle.setAlignment(halign);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    @FXML
    void switchToPreviousWindow(ActionEvent event) {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene mainScene = MainController.scene;
        stage.setScene(mainScene);
        stage.show();
    }

    @FXML
    void switchToNewWindow(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainWindow.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.show();
    }
}
