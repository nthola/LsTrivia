package com.mycompany.ls.triviaapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TriviaApp extends Application {
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;

    private static final String[] questions = {
            "What is the capital city of Lesotho?",
            "What is the highest point in Lesotho?",
            "What is the official language of Lesotho?",
            "Which famous landmark is located in Lesotho?",
            "What is the traditional Basotho blanket called?"
    };

    private static final String[][] options = {
            {"Maseru", "Johannesburg", "Cape Town", "Pretoria"},
            {"Mount Everest", "Kilimanjaro", "Thabana Ntlenyana", "Matterhorn"},
            {"English", "Sesotho", "French", "Zulu"},
            {"Victoria Falls", "Sani Pass", "Table Mountain", "Sahara Desert"},
            {"Motlatsi", "Seanamarena", "Mokorotlo", "Tsepiso"}
    };

    private static final String[] imagePaths = {
            "maseru.jpg",
            "thabana_ntlenyana.jpg",
            "lesotho_flag.jpg",
            "sani_pass.jpg",
            "basotho_blanket.jpg"
    };

    private Label questionLabel;
    private ImageView imageView;
    private VBox optionsBox;
    private Button nextButton;

    private StackPane mediaRoot;
    private CustomMediaPlayer mediaPlayer;
    private Timeline videoTimeline;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        questionLabel = new Label();
        imageView = new ImageView();
        optionsBox = new VBox(5);
        nextButton = new Button("Next");

        nextButton.setOnAction(e -> nextQuestion());

        root.getChildren().addAll(questionLabel, imageView, optionsBox, nextButton);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Lesotho Trivia Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize media components
        mediaRoot = new StackPane();
        mediaPlayer = new CustomMediaPlayer(getClass().getResource("/video.mp4").toExternalForm());
        mediaPlayer.play();

        displayQuestion();
    }

    private void displayQuestion() {
        questionLabel.setText(questions[currentQuestionIndex]);
        Image image = new Image(getClass().getResourceAsStream("/com/mycompany/ls/triviaapp/images/" + imagePaths[currentQuestionIndex]));
        imageView.setImage(image);

        // Show video before each question
        playVideo();
    }

    private void playVideo() {
        mediaRoot.getChildren().clear();
        mediaRoot.getChildren().add(new MediaView(mediaPlayer.getMedia()));
        mediaPlayer.seek(0);
        mediaPlayer.play();

        Scene mediaScene = new Scene(mediaRoot, 600, 400);
        Stage mediaStage = new Stage();
        mediaStage.setScene(mediaScene);
        mediaStage.setTitle("Video Player");
        mediaStage.show();

        // Set up timeline to close video after 10 seconds
        videoTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            mediaStage.close();
            mediaPlayer.stop();
            displayOptions();
        }));
        videoTimeline.setCycleCount(1);
        videoTimeline.play();
    }

    private void displayOptions() {
        optionsBox.getChildren().clear();
        for (String option : options[currentQuestionIndex]) {
            Button optionButton = new Button(option);
            optionButton.setOnAction(e -> handleAnswer(option));
            optionsBox.getChildren().add(optionButton);
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            displayQuestion();
        } else {
            endGame();
        }
    }

    private void handleAnswer(String selectedOption) {
        String correctAnswer = options[currentQuestionIndex][0]; // Assuming the first option is always correct
        if (selectedOption.equals(correctAnswer)) {
            correctAnswersCount++;
        }

        if (currentQuestionIndex < questions.length - 1) {
            nextQuestion();
        } else {
            endGame();
        }
    }

    private void endGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Your final score: " + correctAnswersCount + "/" + questions.length);
        alert.showAndWait();

        // Reset the game for replay
        currentQuestionIndex = 0;
        correctAnswersCount = 0;
        displayQuestion();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
