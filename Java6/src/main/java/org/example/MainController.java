package org.example;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainController {

    private final Stage stage;

    // operacje negatyw/progowanie/konturowanie są podzielone na tyle wątków
    private static final int THREAD_COUNT = 4;

    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private boolean operationsApplied = false;

    private ImageView originalImageView;
    private ImageView processedImageView;
    private ComboBox<String> operationComboBox;
    private Button executeButton;
    private Button saveButton;
    private Button scaleButton;
    private Button rotateLeftButton;
    private Button rotateRightButton;
    private StackPane rootPane;

    public MainController(Stage stage) {
        this.stage = stage;
        AppLogger.info("Aplikacja uruchomiona");
        buildUI();
    }

    private void buildUI() {
        rootPane = new StackPane();

        VBox mainLayout = new VBox(12);
        mainLayout.setPadding(new Insets(12));

        HBox header = buildHeader();
        HBox controls = buildControls();
        HBox imagePanel = buildImagePanel();
        VBox.setVgrow(imagePanel, Priority.ALWAYS);

        Label footer = new Label("Autor: Maksymilian Kuźniar 2880063");
        footer.getStyleClass().add("footer-label");

        mainLayout.getChildren().addAll(header, controls, imagePanel, footer);
        rootPane.getChildren().add(mainLayout);

        Scene scene = new Scene(rootPane, 1100, 750);
        scene.getStylesheets().add(getClass().getResource("/org/example/style.css").toExternalForm());
        stage.setTitle("Edytor Obrazów — Politechnika Wrocławska");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> AppLogger.info("Aplikacja zamknięta"));
        stage.show();
    }

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 20, 14, 20));
        header.getStyleClass().add("header-bar");

        ImageView logoView = new ImageView();
        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/org/example/pwr_logo.png"));
            logoView.setImage(logoImg);
        } catch (Exception ignored) {}
        logoView.setFitHeight(56);
        logoView.setPreserveRatio(true);

        // pionowa linia oddzielająca logo od tytułu
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 40;");

        VBox titleBox = new VBox(3);
        Label appName = new Label("Edytor Obrazów");
        appName.getStyleClass().add("app-title");
        Label subtitle = new Label("Politechnika Wrocławska");
        subtitle.getStyleClass().add("app-subtitle");
        titleBox.getChildren().addAll(appName, subtitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label welcome = new Label("Witaj! Wczytaj plik JPG, aby rozpocząć obróbkę obrazu.");
        welcome.getStyleClass().add("welcome-label");

        header.getChildren().addAll(logoView, sep, titleBox, spacer, welcome);
        return header;
    }

    private HBox buildImagePanel() {
        originalImageView = new ImageView();
        originalImageView.setFitWidth(490);
        originalImageView.setFitHeight(460);
        originalImageView.setPreserveRatio(true);

        processedImageView = new ImageView();
        processedImageView.setFitWidth(490);
        processedImageView.setFitHeight(460);
        processedImageView.setPreserveRatio(true);

        VBox leftBox = new VBox(8);
        leftBox.setAlignment(Pos.TOP_CENTER);
        leftBox.setPadding(new Insets(12));
        leftBox.getStyleClass().add("image-panel");
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        Label origLabel = new Label("Obraz oryginalny");
        origLabel.getStyleClass().add("panel-label");
        leftBox.getChildren().addAll(origLabel, originalImageView);

        VBox rightBox = new VBox(8);
        rightBox.setAlignment(Pos.TOP_CENTER);
        rightBox.setPadding(new Insets(12));
        rightBox.getStyleClass().add("image-panel");
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        Label procLabel = new Label("Obraz po operacjach");
        procLabel.getStyleClass().add("panel-label");
        rightBox.getChildren().addAll(procLabel, processedImageView);

        HBox panel = new HBox(12);
        panel.getChildren().addAll(leftBox, rightBox);
        return panel;
    }

    private HBox buildControls() {
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10, 16, 10, 16));
        controls.getStyleClass().add("toolbar");

        Button loadButton = new Button("📂  Wczytaj plik");
        loadButton.getStyleClass().add("btn-primary");
        loadButton.setOnAction(e -> loadImage());

        operationComboBox = new ComboBox<>();
        operationComboBox.setPromptText("Wybierz operację");
        operationComboBox.getItems().addAll("Negatyw", "Progowanie", "Konturowanie", "Rozmycie");
        operationComboBox.setDisable(true);
        operationComboBox.setPrefWidth(165);
        operationComboBox.getStyleClass().add("combo-box");

        executeButton = new Button("▶  Wykonaj");
        executeButton.getStyleClass().add("btn-primary");
        executeButton.setDisable(true);
        executeButton.setOnAction(e -> executeOperation());

        rotateLeftButton = new Button("↺  Obrót L");
        rotateLeftButton.getStyleClass().add("btn-secondary");
        rotateLeftButton.setDisable(true);
        rotateLeftButton.setOnAction(e -> rotateImage(-90));

        rotateRightButton = new Button("↻  Obrót P");
        rotateRightButton.getStyleClass().add("btn-secondary");
        rotateRightButton.setDisable(true);
        rotateRightButton.setOnAction(e -> rotateImage(90));

        scaleButton = new Button("⤡  Skaluj");
        scaleButton.getStyleClass().add("btn-secondary");
        scaleButton.setDisable(true);
        scaleButton.setOnAction(e -> showScaleDialog());

        saveButton = new Button("💾  Zapisz obraz");
        saveButton.getStyleClass().add("btn-secondary");
        saveButton.setDisable(true);
        saveButton.setOnAction(e -> showSaveDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        controls.getChildren().addAll(
                loadButton,
                makeSep(),
                operationComboBox, executeButton,
                makeSep(),
                rotateLeftButton, rotateRightButton,
                makeSep(),
                scaleButton,
                spacer,
                saveButton
        );
        return controls;
    }

    private Region makeSep() {
        Region sep = new Region();
        sep.getStyleClass().add("section-separator");
        sep.setPrefWidth(1);
        sep.setStyle("-fx-background-color: #dddddd; -fx-min-width: 1; -fx-max-width: 1; -fx-pref-height: 26;");
        HBox.setMargin(sep, new Insets(0, 4, 0, 4));
        return sep;
    }

    private void enableButtons() {
        operationComboBox.setDisable(false);
        executeButton.setDisable(false);
        rotateLeftButton.setDisable(false);
        rotateRightButton.setDisable(false);
        scaleButton.setDisable(false);
        saveButton.setDisable(false);
        // przycisk Zapisz wyróżnij jako primary gdy obraz załadowany
        saveButton.getStyleClass().remove("btn-secondary");
        saveButton.getStyleClass().add("btn-primary");
    }

    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik obrazka");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pliki JPG", "*.jpg", "*.JPG", "*.jpeg", "*.JPEG")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;

        String name = file.getName().toLowerCase();
        if (!name.endsWith(".jpg") && !name.endsWith(".jpeg")) {
            showToast("Niedozwolony format pliku", "error");
            return;
        }

        try {
            BufferedImage loaded = ImageIO.read(file);
            if (loaded == null) throw new Exception("Pusty plik");

            // Konwersja na TYPE_INT_RGB żeby uniknąć problemów przy przetwarzaniu
            BufferedImage converted = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = converted.createGraphics();
            g.drawImage(loaded, 0, 0, null);
            g.dispose();

            originalImage = converted;
            processedImage = copyImage(originalImage);
            operationsApplied = false;

            updateImageViews();
            enableButtons();
            AppLogger.info("Załadowano plik: " + file.getName());
            showToast("Pomyślnie załadowano plik", "success");
        } catch (Exception ex) {
            AppLogger.error("Nie udało się załadować pliku: " + ex.getMessage());
            showToast("Nie udało się załadować pliku", "error");
        }
    }

    private void executeOperation() {
        String selected = operationComboBox.getValue();
        if (selected == null) {
            AppLogger.warn("Kliknięto Wykonaj bez wybranej operacji");
            showToast("Nie wybrano operacji do wykonania", "warning");
            return;
        }

        try {
            switch (selected) {
                case "Negatyw" -> {
                    processedImage = applyNegative(copyImage(originalImage));
                    operationsApplied = true;
                    updateImageViews();
                    AppLogger.info("Wykonano operację: Negatyw");
                    showToast("Negatyw został wygenerowany pomyślnie!", "success");
                }
                case "Progowanie" -> showThresholdDialog();
                case "Konturowanie" -> {
                    processedImage = applyContour(copyImage(originalImage));
                    operationsApplied = true;
                    updateImageViews();
                    AppLogger.info("Wykonano operację: Konturowanie");
                    showToast("Konturowanie zostało przeprowadzone pomyślnie!", "success");
                }
                case "Rozmycie" -> {
                    processedImage = applyBlur(copyImage(originalImage));
                    operationsApplied = true;
                    updateImageViews();
                    AppLogger.info("Wykonano operację: Rozmycie");
                    showToast("Rozmycie zostało przeprowadzone pomyślnie!", "success");
                }
            }
        } catch (Exception ex) {
            switch (selected) {
                case "Negatyw" -> { AppLogger.error("Błąd negatywu: " + ex.getMessage()); showToast("Nie udało się wykonać negatywu.", "error"); }
                case "Konturowanie" -> { AppLogger.error("Błąd konturowania: " + ex.getMessage()); showToast("Nie udało się wykonać konturowania.", "error"); }
                case "Rozmycie" -> { AppLogger.error("Błąd rozmycia: " + ex.getMessage()); showToast("Nie udało się wykonać rozmycia.", "error"); }
            }
        }
    }

    private void showThresholdDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Progowanie");
        dialog.setResizable(false);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);

        Label info = new Label("Podaj wartość progu (0-255):");
        Spinner<Integer> spinner = new Spinner<>(0, 255, 128);
        spinner.setEditable(true);
        spinner.setPrefWidth(120);

        // blokada: tylko cyfry, max 3 znaki, wartość musi być 0-255
        spinner.getEditor().setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (!text.matches("\\d{0,3}")) return null;
            if (!text.isEmpty() && Integer.parseInt(text) > 255) return null;
            return change;
        }));

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button apply = new Button("Wykonaj progowanie");
        Button cancel = new Button("Anuluj");
        buttons.getChildren().addAll(apply, cancel);

        apply.setOnAction(e -> {
            try {
                int threshold = spinner.getValue();
                processedImage = applyThreshold(copyImage(originalImage), threshold);
                operationsApplied = true;
                updateImageViews();
                AppLogger.info("Wykonano operację: Progowanie (próg=" + threshold + ")");
                showToast("Progowanie zostało przeprowadzone pomyślnie!", "success");
            } catch (Exception ex) {
                AppLogger.error("Błąd progowania: " + ex.getMessage());
                showToast("Nie udało się wykonać progowania.", "error");
            }
            dialog.close();
        });
        cancel.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(info, spinner, buttons);
        dialog.setScene(new Scene(layout, 280, 150));
        dialog.showAndWait();
    }

    private void rotateImage(double degrees) {
        try {
            processedImage = doRotate(processedImage, degrees);
            operationsApplied = true;
            updateImageViews();
            AppLogger.info("Wykonano obrót: " + (int) degrees + "°");
        } catch (Exception ex) {
            AppLogger.error("Błąd obrotu: " + ex.getMessage());
            showToast("Nie udało się obrócić obrazu.", "error");
        }
    }

    private void showScaleDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Skalowanie obrazu");
        dialog.setResizable(false);

        VBox layout = new VBox(6);
        layout.setPadding(new Insets(20));

        Label widthLabel = new Label("Szerokość (px):");
        TextField widthField = new TextField();
        widthField.setTextFormatter(buildNumericFormatter());
        Label widthError = new Label();
        widthError.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        Label heightLabel = new Label("Wysokość (px):");
        TextField heightField = new TextField();
        heightField.setTextFormatter(buildNumericFormatter());
        Label heightError = new Label();
        heightError.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        Button restoreBtn = new Button("Przywróć oryginalne wymiary");
        restoreBtn.setOnAction(e -> {
            widthField.setText(String.valueOf(originalImage.getWidth()));
            heightField.setText(String.valueOf(originalImage.getHeight()));
        });

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button apply = new Button("Zmień rozmiar");
        Button cancel = new Button("Anuluj");
        buttons.getChildren().addAll(apply, cancel);

        apply.setOnAction(e -> {
            widthError.setText("");
            heightError.setText("");
            boolean valid = true;
            if (widthField.getText().isEmpty()) { widthError.setText("Pole jest wymagane"); valid = false; }
            if (heightField.getText().isEmpty()) { heightError.setText("Pole jest wymagane"); valid = false; }
            if (!valid) return;

            int w = Integer.parseInt(widthField.getText());
            int h = Integer.parseInt(heightField.getText());
            processedImage = doScale(processedImage, w, h);
            operationsApplied = true;
            updateImageViews();
            AppLogger.info("Wykonano skalowanie: " + w + "x" + h + " px");
            dialog.close();
        });

        cancel.setOnAction(e -> {
            widthField.clear();
            heightField.clear();
            dialog.close();
        });

        layout.getChildren().addAll(
                widthLabel, widthField, widthError,
                heightLabel, heightField, heightError,
                restoreBtn, buttons
        );
        dialog.setScene(new Scene(layout, 300, 270));
        dialog.showAndWait();
    }

    private void showSaveDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Zapisz obraz");
        dialog.setResizable(false);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        if (!operationsApplied) {
            Label warning = new Label("Na pliku nie zostały wykonane żadne operacje!");
            warning.setStyle("-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 12px;");
            layout.getChildren().add(warning);
        }

        Label nameLabel = new Label("Nazwa pliku (bez rozszerzenia):");
        TextField nameField = new TextField();
        // max 100 znaków
        nameField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 100 ? change : null
        ));
        Label nameError = new Label();
        nameError.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button save = new Button("Zapisz");
        Button cancel = new Button("Anuluj");
        buttons.getChildren().addAll(save, cancel);

        save.setOnAction(e -> {
            nameError.setText("");
            String fileName = nameField.getText().trim();
            if (fileName.length() < 3) {
                nameError.setText("Wpisz co najmniej 3 znaki");
                return;
            }

            // zapisuj obok app.log - w katalogu roboczym projektu
            File picturesDir = new File(System.getProperty("user.dir"));
            if (!picturesDir.exists()) picturesDir.mkdirs();

            File outputFile = new File(picturesDir, fileName + ".jpg");
            if (outputFile.exists()) {
                showToast("Plik " + fileName + ".jpg już istnieje w systemie. Podaj inną nazwę pliku!", "error");
                dialog.close();
                return;
            }

            try {
                ImageIO.write(processedImage, "jpg", outputFile);
                AppLogger.info("Zapisano plik: " + fileName + ".jpg");
                showToast("Zapisano obraz w pliku " + fileName + ".jpg", "success");
                dialog.close();
            } catch (Exception ex) {
                AppLogger.error("Nie udało się zapisać pliku " + fileName + ".jpg: " + ex.getMessage());
                showToast("Nie udało się zapisać pliku " + fileName + ".jpg", "error");
                dialog.close();
            }
        });

        cancel.setOnAction(e -> {
            nameField.clear();
            dialog.close();
        });

        layout.getChildren().addAll(nameLabel, nameField, nameError, buttons);
        dialog.setScene(new Scene(layout, 340, operationsApplied ? 160 : 210));
        dialog.showAndWait();
    }

    private void updateImageViews() {
        if (originalImage != null)
            originalImageView.setImage(SwingFXUtils.toFXImage(originalImage, null));
        if (processedImage != null)
            processedImageView.setImage(SwingFXUtils.toFXImage(processedImage, null));
    }

    private void showToast(String message, String type) {
        String color = switch (type) {
            case "success" -> "#27ae60";
            case "error" -> "#c0392b";
            case "warning" -> "#e67e22";
            default -> "#333333";
        };
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;" +
                " -fx-padding: 10 20; -fx-background-radius: 5; -fx-font-size: 13px;");
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 25, 0));
        rootPane.getChildren().add(toast);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(500), toast);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(ev -> rootPane.getChildren().remove(toast));
            fade.play();
        });
        pause.play();
    }

    private TextFormatter<String> buildNumericFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (!text.matches("\\d{0,4}")) return null;
            if (!text.isEmpty()) {
                try {
                    if (Integer.parseInt(text) > 3000) return null;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return change;
        });
    }

    // ---- Operacje na obrazie ----

    private BufferedImage copyImage(BufferedImage img) {
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return copy;
    }

    // Każda z trzech poniższych metod dzieli obraz na THREAD_COUNT pasków
    // i przetwarza każdy pasek w osobnym wątku (ExecutorService z pulą 4 wątków)

    private BufferedImage applyNegative(BufferedImage img) throws Exception {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> tasks = new ArrayList<>();
        int strip = h / THREAD_COUNT;

        for (int t = 0; t < THREAD_COUNT; t++) {
            int yStart = t * strip;
            int yEnd = (t == THREAD_COUNT - 1) ? h : yStart + strip;
            tasks.add(pool.submit(() -> {
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = 0; x < w; x++) {
                        int rgb = img.getRGB(x, y);
                        int r = 255 - ((rgb >> 16) & 0xFF);
                        int g = 255 - ((rgb >> 8) & 0xFF);
                        int b = 255 - (rgb & 0xFF);
                        result.setRGB(x, y, (r << 16) | (g << 8) | b);
                    }
                }
            }));
        }

        for (Future<?> f : tasks) f.get(); // czekamy aż wszystkie wątki skończą
        pool.shutdown();
        return result;
    }

    private BufferedImage applyThreshold(BufferedImage img, int threshold) throws Exception {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> tasks = new ArrayList<>();
        int strip = h / THREAD_COUNT;

        for (int t = 0; t < THREAD_COUNT; t++) {
            int yStart = t * strip;
            int yEnd = (t == THREAD_COUNT - 1) ? h : yStart + strip;
            tasks.add(pool.submit(() -> {
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = 0; x < w; x++) {
                        int rgb = img.getRGB(x, y);
                        int gray = ((rgb >> 16 & 0xFF) + (rgb >> 8 & 0xFF) + (rgb & 0xFF)) / 3;
                        int val = gray >= threshold ? 255 : 0;
                        result.setRGB(x, y, (val << 16) | (val << 8) | val);
                    }
                }
            }));
        }

        for (Future<?> f : tasks) f.get();
        pool.shutdown();
        return result;
    }

    private BufferedImage applyContour(BufferedImage img) throws Exception {
        int w = img.getWidth();
        int h = img.getHeight();

        // Konwersja do skali szarości - jeden przebieg, szybki
        int[][] gray = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                gray[y][x] = ((rgb >> 16 & 0xFF) + (rgb >> 8 & 0xFF) + (rgb & 0xFF)) / 3;
            }
        }

        // Detekcja krawędzi operatorem Sobela - zrównoleglona po paskach
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> tasks = new ArrayList<>();
        int strip = h / THREAD_COUNT;

        for (int t = 0; t < THREAD_COUNT; t++) {
            int yStart = Math.max(1, t * strip);
            int yEnd = (t == THREAD_COUNT - 1) ? h - 1 : (t + 1) * strip;
            tasks.add(pool.submit(() -> {
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = 1; x < w - 1; x++) {
                        int gx = 0, gy = 0;
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                gx += sobelX[i + 1][j + 1] * gray[y + i][x + j];
                                gy += sobelY[i + 1][j + 1] * gray[y + i][x + j];
                            }
                        }
                        int mag = (int) Math.min(255, Math.sqrt(gx * gx + gy * gy));
                        result.setRGB(x, y, (mag << 16) | (mag << 8) | mag);
                    }
                }
            }));
        }

        for (Future<?> f : tasks) f.get();
        pool.shutdown();
        return result;
    }

    private BufferedImage applyBlur(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int totalR = 0, totalG = 0, totalB = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = img.getRGB(x + j, y + i);
                        totalR += (rgb >> 16) & 0xFF;
                        totalG += (rgb >> 8) & 0xFF;
                        totalB += rgb & 0xFF;
                    }
                }
                result.setRGB(x, y, ((totalR / 9) << 16) | ((totalG / 9) << 8) | (totalB / 9));
            }
        }
        return result;
    }

    private BufferedImage doRotate(BufferedImage img, double degrees) {
        double rad = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(rad));
        double cos = Math.abs(Math.cos(rad));
        int newW = (int) Math.round(img.getWidth() * cos + img.getHeight() * sin);
        int newH = (int) Math.round(img.getHeight() * cos + img.getWidth() * sin);

        BufferedImage result = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = result.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, newW, newH);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        AffineTransform at = new AffineTransform();
        at.translate(newW / 2.0, newH / 2.0);
        at.rotate(rad);
        at.translate(-img.getWidth() / 2.0, -img.getHeight() / 2.0);
        g2.drawImage(img, at, null);
        g2.dispose();
        return result;
    }

    private BufferedImage doScale(BufferedImage img, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = result.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();
        return result;
    }
}
