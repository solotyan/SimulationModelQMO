package org.solotyan.simulationmodelqmo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class MainController {
    public static Scene scene;
    private Stage stage;
    private Parent root;
    @FXML
    private ListView<Double> randomValuesList;
    @FXML
    private TextField randomValuesTextField;
    @FXML
    private TextField q1TextField;
    @FXML
    private TextField q2TextField;
    @FXML
    private TextField lambdaTextField;
    @FXML
    private TextField periodTextField;
    @FXML
    private Button buttonStart;

    private final Data data = new Data();

    @FXML
    public void initialize(){
        Platform.runLater(()->randomValuesTextField.requestFocus());
        randomValuesList.setEditable(true);
        randomValuesList.setCellFactory(lv -> new TextFieldListCell());
        randomValuesTextField.setOnAction(e -> setRandomValuesSpace(false));
        q1TextField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) q2TextField.requestFocus();
        });
        q2TextField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) lambdaTextField.requestFocus();
        });
        lambdaTextField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) periodTextField.requestFocus();
        });
        periodTextField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) buttonStart.requestFocus();
        });
        randomValuesTextField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) randomValuesList.requestFocus();
        });
    }

    @FXML
    private void onAutoFillButtonClick(){
        setRandomValuesSpace(true);
        q1TextField.requestFocus();
    }

    private void setRandomValuesSpace(boolean isRandom){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Пустое поле");
        if (randomValuesTextField.getText().isEmpty()){
            alert.setContentText("Поле \"Количество чисел\" не может быть пустым");
            alert.showAndWait();
            return;
        }
        int size;
        try {
            size = Integer.parseInt(randomValuesTextField.getText());
            if (size < 1 || size > 200) throw new IllegalArgumentException();
        } catch (NumberFormatException e){
            alert.setHeaderText("Неверные данные");
            alert.setContentText("Введите корректное целое число в поле \"Количество чисел\"");
            alert.showAndWait();
            return;
        } catch (IllegalArgumentException e){
            alert.setHeaderText("Неверные данные");
            alert.setContentText("Введите целое число в интервале [1, 200]\nв поле \"Количество чисел\"");
            alert.showAndWait();
            return;
        }
        ObservableList<Double> randomValues = FXCollections.observableArrayList();
        Random random = new Random();
        double randomValue;
        double scale = Math.pow(10, 4);
        for (int i = 0; i < size; i++) {
            if (isRandom){
                randomValue = random.nextDouble();
                randomValues.add(Math.ceil(randomValue * scale) / scale);
            } else {
                randomValues.add(0.0001);
            }
        }
        randomValuesList.setItems(randomValues);
    }

    @FXML
    private void onStartButtonClick(ActionEvent event) throws IOException {
        Alert alert = alertEmptyFields();
        if (!alert.getContentText().isEmpty()){
            alert.showAndWait();
            return;
        }
        PriorityQueue<Request> requestQueue = new PriorityQueue<>(new RequestTimeComparator()); // очередь заявок
        Queue<Double> randomValues = new LinkedList<>(randomValuesList.getItems()); // последовательность случайных чисел
        double q; // время обслуживания
        alert = alertIncorrectFieldValues();
        if (!alert.getContentText().isEmpty()){
            alert.showAndWait();
            return;
        }
        double q1 = Double.parseDouble(q1TextField.getText()); // время обслуживания если R<0.5
        double q2 = Double.parseDouble(q2TextField.getText()); // время обслуживания если R=>0.5
        double lambda = Double.parseDouble(lambdaTextField.getText()); // интенсивность входного потока
        double T = Double.parseDouble(periodTextField.getText()); // время моделирования
        double currentTime = 0.0; // счетчик текущего времени
        Request currentRequest; // текущая заявка(для цикла)
        Queue<Double> waitingTimeQueue = new PriorityQueue<>(new WaitingTimeComparator()); // время начала ожидания
        double waitingTime;
        double downtimeSummary = 0; // суммарное время простоя
        double waitingTimeSummary = 0; // суммарное время ожидания
        double lastTimeDowntime = 0; // время начала простоя
        int income = 0; // счетчик приходов
        int outcome = 0;
        int nextIncome = 0;
        int nextOutcome = 0;
        double nextTime1;
        double nextTime2;
        boolean downtime = true; // простой
        int queueLength = 0; // длина очереди

        // requestQueue.add(new Request(currentTime-Math.log(randomValues.poll())/T, TypeRequest.START_REQUEST));
        // - генерация времени начала завяки
        // requestQueue.add(new Request(currentTime+q, TypeRequest.END_REQUEST)); - генерация времени окончания заявки

        requestQueue.add(new Request(currentTime, TypeRequest.START_REQUEST));

        LinkedList<RowXlsx> rowsXlsx = new LinkedList<>();

        double scale = Math.pow(10, 4);

        while (Objects.requireNonNull(requestQueue.peek()).getTime() < T){
            alert = alertNullRandomValues(randomValues);
            if (!alert.getContentText().isEmpty()){
                alert.showAndWait();
                return;
            }

            RowXlsx rowXlsx = new RowXlsx();

            currentRequest = requestQueue.poll();
            assert currentRequest != null;
            currentTime = currentRequest.getTime();

            rowXlsx.setRequest(currentRequest);

            if (currentRequest.getType() == TypeRequest.START_REQUEST){
                if (currentTime != 0) {
                    if (downtime) {
                        rowXlsx.setR1(randomValues.peek());

                        q = randomValues.poll() < 0.5 ? q1 : q2;

                        nextTime1 = Math.ceil((currentTime + q) * scale) / scale;
                        rowXlsx.setNextTime1(nextTime1);

                        requestQueue.add(new Request(nextTime1, TypeRequest.END_REQUEST));
                        downtime = false;

                        downtimeSummary += currentTime - lastTimeDowntime;

                        nextOutcome++;
                        rowXlsx.setNextOutcome(nextOutcome);
                        rowXlsx.setDowntimeTime(currentTime - lastTimeDowntime);
                    } else {
                        queueLength++;
                        waitingTimeQueue.add(currentTime);

                        rowXlsx.setNextTime1(null);
                    }
                    income++;
                }
                alert = alertNullRandomValues(randomValues);
                if (!alert.getContentText().isEmpty()){
                    alert.showAndWait();
                    return;
                }
                rowXlsx.setR2(randomValues.peek());
                nextTime2 = Math.ceil((currentTime-Math.log(randomValues.poll())/lambda) * scale) / scale;
                rowXlsx.setNextTime2(nextTime2);

                requestQueue.add(new Request(nextTime2, TypeRequest.START_REQUEST));

                nextIncome++;
                rowXlsx.setNextIncome(nextIncome);
                rowXlsx.setIncome(income);
            } else {
                rowXlsx.setNextTime1(null);

                if (queueLength == 0){
                    downtime = true;
                    lastTimeDowntime = currentTime;

                    rowXlsx.setNextTime2(null);
                } else {
                    rowXlsx.setR2(randomValues.peek());

                    q = randomValues.poll() < 0.5 ? q1 : q2;

                    nextTime2 = Math.ceil((currentTime + q) * scale) / scale;

                    rowXlsx.setNextTime2(nextTime2);

                    requestQueue.add(new Request(nextTime2, TypeRequest.END_REQUEST));
                    queueLength--;
                    waitingTime = Math.ceil((currentTime - waitingTimeQueue.poll()) * scale) / scale;

                    nextOutcome++;
                    rowXlsx.setNextOutcome(nextOutcome);
                    rowXlsx.setWaitingTime(waitingTime);

                    waitingTimeSummary += waitingTime;
                }
                outcome++;
                rowXlsx.setOutcome(outcome);
            }
            rowXlsx.setQueueLength(queueLength);
            rowsXlsx.add(rowXlsx);
        }
        while (!(waitingTimeQueue.isEmpty())){
            waitingTimeSummary += T - waitingTimeQueue.poll();
        }
        Double W = Math.ceil((waitingTimeSummary / income) * scale) / scale;
        if (W.isNaN()){
            alertValueIsNaN();
            return;
        }
        data.setK(Math.ceil((downtimeSummary / T * 100) * scale) / scale);
        data.setW(W);
        data.setQ(Math.ceil((waitingTimeSummary / T) * scale) / scale);
        data.setRowsXlsx(rowsXlsx);
        switchToNextWindow(event);
    }

    private void alertValueIsNaN() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Невозможная модель");
        alert.setContentText("Моделирование с такими исходными данными невозможно");
        alert.showAndWait();
    }

    private Alert alertIncorrectFieldValues(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Неверные данные");
        alert.setContentText("");
        boolean q1 = false, q2 = false, lambda = false, period = false;
        try {
            double q1text = Double.parseDouble(q1TextField.getText());
            if (q1text < 0.01 || q1text > 200) throw new IllegalArgumentException();
        } catch (NumberFormatException e){
            q1 = true;
        } catch (IllegalArgumentException e){
            alert.setContentText("Поле q1 должно содержать число с плавающей точкой\n" +
                    "в интервале [0.01, 200]");
        }
        try {
            double q2text = Double.parseDouble(q2TextField.getText());
            if (q2text < 0.01 || q2text > 200) throw new IllegalArgumentException();
        } catch (NumberFormatException e){
            q2 = true;
        } catch (IllegalArgumentException e){
            alert.setContentText("Поле q2 должно содержать число с плавающей точкой\n" +
                    "в интервале [0.01, 200]");
        }
        try {
            double lambdatext = Double.parseDouble(lambdaTextField.getText());
            if (lambdatext < 1 || lambdatext > 10) throw new IllegalArgumentException();
        } catch (NumberFormatException e) {
            lambda = true;
        } catch (IllegalArgumentException e){
            alert.setContentText("Поле λ должно содержать число с плавающей точкой\n" +
                    "в интервале [1, 10]");
        }
        try {
            double periodtext = Double.parseDouble(periodTextField.getText());
            if (periodtext < 1 || periodtext > 10) throw new IllegalArgumentException();
        } catch (NumberFormatException e) {
            period = true;
        } catch (IllegalArgumentException e){
            alert.setContentText("Поле T должно содержать число с плавающей точкой\n" +
                    "в интервале [1, 10]");
        }
        String s1 = "Введите корректные данные в ";
        String s2 = "\nДанные должны быть в формате чисел с плавающей точкой";
        if (q1 && q2 && lambda && period) alert.setContentText(s1 + "поля q1, q2, λ, T" + s2);
        else if (q1 && q2 && lambda) alert.setContentText(s1 + "поля q1, q2, λ" + s2);
        else if (q1 && q2 && period) alert.setContentText(s1 + "поля q1, q2, T" + s2);
        else if (q1 && lambda && period) alert.setContentText(s1 + "поля q1, λ, T" + s2);
        else if (q2 && lambda && period) alert.setContentText(s1 + "поля q2, λ, T" + s2);
        else if (q1 && q2) alert.setContentText(s1 + "поля q1, q2" + s2);
        else if (q1 && lambda) alert.setContentText(s1 + "поля q1, λ" + s2);
        else if (q1 && period) alert.setContentText(s1 + "поля q1, T" + s2);
        else if (q2 && lambda) alert.setContentText(s1 + "поля q2, λ" + s2);
        else if (q2 && period) alert.setContentText(s1 + "поля q2, T" + s2);
        else if (lambda && period) alert.setContentText(s1 + "поля λ, T" + s2);
        else if (q1) alert.setContentText(s1 + "поле q1" + s2);
        else if (q2) alert.setContentText(s1 + "поле q2" + s2);
        else if (lambda) alert.setContentText(s1 + "поле λ" + s2);
        else if (period) alert.setContentText(s1 + "поле T" + s2);
        return alert;
    }

    private Alert alertNullRandomValues(Queue<Double> randomValues){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("");
        try {
            double test = randomValues.peek();
        } catch (NullPointerException e){
            alert.setTitle("Ошибка");
            alert.setHeaderText("Неверные данные");
            alert.setContentText("Недостаточно случайных значений для моделирования");
        }
        return alert;
    }

    private Alert alertEmptyFields() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Пустое поле");
        alert.setContentText("");
        String q1 = q1TextField.getText();
        String q2 = q2TextField.getText();
        String lambda = lambdaTextField.getText();
        String period = periodTextField.getText();
        String s1 = "\nВведите данные в формате чисел с плавающей точкой";
        if (q1.isEmpty() && q2.isEmpty() && lambda.isEmpty() && period.isEmpty())
            alert.setContentText("Поля q1, q2, λ, T не могут быть пустыми" + s1);
        else if (q1.isEmpty() && q2.isEmpty() && lambda.isEmpty())
            alert.setContentText("Поля q1, q2, λ не могут быть пустыми\n" + s1);
        else if (q1.isEmpty() && q2.isEmpty() && period.isEmpty())
            alert.setContentText("Поля q1, q2, T не могут быть пустыми\n" + s1);
        else if (q1.isEmpty() && lambda.isEmpty() && period.isEmpty())
            alert.setContentText("Поля q1, λ, T не могут быть пустыми\n" + s1);
        else if (q2.isEmpty() && lambda.isEmpty() && period.isEmpty())
            alert.setContentText("Поля q2, λ, T не могут быть пустыми\n" + s1);
        else if (q1.isEmpty() && q2.isEmpty())
            alert.setContentText("Поля q1, q2 не могут быть пустыми\n" + s1);
        else if (q1.isEmpty() && lambda.isEmpty())
            alert.setContentText("Поля q1, λ не могут быть пустыми\n" + s1);
        else if (q1.isEmpty() && period.isEmpty())
            alert.setContentText("Поля q1, T не могут быть пустыми\n" + s1);
        else if (q2.isEmpty() && lambda.isEmpty())
            alert.setContentText("Поля q2, λ не могут быть пустыми\n" + s1);
        else if (q2.isEmpty() && period.isEmpty())
            alert.setContentText("Поля q2, T не могут быть пустыми\n" + s1);
        else if (lambda.isEmpty() && period.isEmpty())
            alert.setContentText("Поля λ, T не могут быть пустыми\n" + s1);
        else if (q1.isEmpty()) alert.setContentText("Поле q1 не может быть пустым\n" + s1);
        else if (q2.isEmpty()) alert.setContentText("Поле q2 не может быть пустым\n" + s1);
        else if (lambda.isEmpty()) alert.setContentText("Поле λ не может быть пустым\n" + s1);
        else if (period.isEmpty()) alert.setContentText("Поле T не может быть пустым\n" + s1);
        return alert;
    }

    @FXML
    private void showInformation() throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Information.fxml")));
        stage = new Stage();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void switchToNextWindow(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("2ndWindow.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = stage.getScene();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.setUserData(data);
        stage.show();
    }
}

class TextFieldListCell extends ListCell<Double> {
    private final TextField textField = new TextField();

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(String.valueOf(item));
            setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    editCell(item);
                }
            });
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        // Делаем активным текстовое поле при начале редактирования
        editCell(getItem());
    }

    private void editCell(Double item) {
        textField.setText(String.valueOf(item));
        textField.positionCaret(textField.getText().length());
        setGraphic(textField);
        textField.requestFocus();

        // Обработка нажатия Enter для активации текстового поля
        textField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                commitEditValue();
                int nextIndex = getIndex() + 1;
                if (nextIndex < getListView().getItems().size()) {
                    getListView().getSelectionModel().select(nextIndex);
                }
                event.consume();
            }
        });
    }

    private void commitEditValue() {
        try {
            double text = Double.parseDouble(textField.getText());
            if (text < 0.0001 || text > 1) throw new IllegalArgumentException();
            commitEdit(text);
        } catch (NumberFormatException ex) {
            showErrorAlert();
        } catch (IllegalArgumentException e){
            showError1Alert();
        }
        setGraphic(null);
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Неверные данные");
        alert.setContentText("Необходимо ввести корректные данные\n" +
                "Случайное значение должно быть в формате чисел с плавающей точкой");
        alert.showAndWait();
    }

    private void showError1Alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Неверные данные");
        alert.setContentText("Случайное значение должно быть в интервале [0.0001, 1]");
        alert.showAndWait();
    }
}