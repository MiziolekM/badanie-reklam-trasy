package dev.mateusz.demo.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import dev.mateusz.demo.config.StageManager;
import dev.mateusz.demo.domain.Analyse;
import dev.mateusz.demo.entity.Data;
import dev.mateusz.demo.entity.Drivers;
import dev.mateusz.demo.entity.Fixations;
import dev.mateusz.demo.entity.Rectangles;
import dev.mateusz.demo.entity.Results;
import dev.mateusz.demo.repository.DataRepository;
import dev.mateusz.demo.repository.DriversRepository;
import dev.mateusz.demo.repository.FixationsRepository;
import dev.mateusz.demo.repository.RectanglesRepository;
import dev.mateusz.demo.repository.ResultsRepository;
import dev.mateusz.demo.view.FxmlView;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

@Component
public class MainController {

	// zmienna @FMXLowe
	@FXML
	private TextField addDriverTextField;
	@FXML
	private Button addDriverButton;
	@FXML
	private ComboBox<String> chooseDriverCombobox;
	@FXML
	private Button searchFileButton;
	@FXML
	private Button addDataButton;
	@FXML
	private Label pathFileLabel;
	@FXML
	private ComboBox<String> chooseDriverForRemoveCombobox;
	@FXML
	private Button removeDriverButton;
	@FXML
	private ComboBox<String> chooseDriverForAnalize;
	@FXML
	private Button searchVideoFileButton;
	@FXML
	private Button startAnalizeButton;
	@FXML
	private ComboBox<Integer> chooseMinAdsForAnalize;
	@FXML
	private Button startLastAnalizeButton;
	@FXML
	private Button calculateResultsButton;
	@FXML
	private Label pathVideoFileLabel;
	@FXML
	private ComboBox<String> chooseDriverForResult;
	@FXML
	private Button showResultsButton;
	@FXML
	private Label lengthMovie;
	@FXML
	private Label timeDriverLookAds;
	@FXML
	private Label timeDriverLookAdsPercents;
	@FXML
	private Label timeDriverLookAdsAvg;

	// trzy zmienne statyczne, do których odwołuje się klasa AnalyseController
	public static String USERNAME;
	public static String VIDEOPATH;
	public static int MINADS;

	// wstrzykiwanie repositoriów oraz stageManagera do zmiany sceny
	@Lazy
	@Autowired
	StageManager stageManager;

	@Autowired
	DataRepository dataRepository;

	@Autowired
	FixationsRepository fixationsRepository;

	@Autowired
	RectanglesRepository rectanglesRepository;

	@Autowired
	ResultsRepository resultsRepository;

	@Autowired
	DriversRepository driversRepository;

	// logger
	//private static final Logger logger = Logger.getLogger(MainController.class.getName());

	/**
	 * Metoda czyszcząca i ustawiająca wszytkie trzy ComboBoxy wypełnia je nazwami
	 * kierowców
	 */
	private void prepInit() {

		startAnalizeButton.setDisable(true);
		addDataButton.setDisable(true);
		List<String> driversName = driversRepository.findDriversName();

		chooseDriverCombobox.getItems().clear();
		chooseDriverForRemoveCombobox.getItems().clear();
		chooseDriverForAnalize.getItems().clear();
		chooseDriverForResult.getItems().clear();

		for (String dName : driversName) {
			chooseDriverCombobox.getItems().add(dName);
			chooseDriverForRemoveCombobox.getItems().add(dName);
			chooseDriverForAnalize.getItems().add(dName);
			chooseDriverForResult.getItems().add(dName);
		}

		chooseDriverCombobox.getSelectionModel().select(0);
		chooseDriverForRemoveCombobox.getSelectionModel().select(0);
		chooseDriverForAnalize.getSelectionModel().select(0);
		chooseDriverForResult.getSelectionModel().select(0);
	}

	/**
	 * Metoda inicjalizująca. Wywołuje metodą prepInit() oraz dodatkowo ustawia trzy
	 * stałe wartości w comboboxie chooseMinAdsForAnalize jest to ilość reklam które
	 * uzytkownik bedzie musiał zlokalizować analizując video
	 */
	@FXML
	private void initialize() {

		prepInit();
		chooseMinAdsForAnalize.getItems().clear();
		chooseMinAdsForAnalize.getItems().addAll(5, 10, 15);
		chooseMinAdsForAnalize.getSelectionModel().select(0);
	}

	/**
	 * Słuchacz przycisku "addDriverButton". Przycisk odpowiada za dodanie kierowcy
	 * do bazy danych. Sprawdza czy nazwa jest wprowadzona poprawnie i wyświetla
	 * odpowienie alerty
	 */
	@FXML
	private void addDriverButtonAction() {

		String driverName = addDriverTextField.getText().trim();

		Alert alert = new Alert(AlertType.INFORMATION);

		if (driverName.isEmpty()) {

			alert.setAlertType(AlertType.ERROR);
			alert.setTitle("Błąd");
			alert.setHeaderText("Nazwa kierowcy jest pusta!");

		} else {

			ObservableList<String> drivers = chooseDriverCombobox.getItems();

			if (drivers.contains(driverName)) {

				alert.setAlertType(AlertType.ERROR);
				alert.setTitle("Błąd");
				alert.setHeaderText("Taki kierowca już istnieje!");

			} else {

				Drivers theDriver = new Drivers();
				theDriver.setName(addDriverTextField.getText().trim());
				theDriver.setIdDriver(0);
				driversRepository.save(theDriver);

				alert.setAlertType(AlertType.INFORMATION);
				alert.setTitle("Potwierdzenie");
				alert.setHeaderText("Dodano kierowcę!");
			}
		}

		alert.showAndWait();
		prepInit();

	}

	/**
	 * Słuchacz przycisku "searchFileButton" Uruchamia fileChooser. Umożliwia wybór
	 * pliku z danymi "surowymi" Wymagany jest plik .txt W zależności od porażli
	 * wyświetlany jest alert W razie sukcesu ścieżka pliku stawiana jest na
	 * "pathFileLabel" oraz odblokowany zostaje przycisk "addDataButton"
	 */
	@FXML
	private void searchFileButtonAction() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Wybierz plik video");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
		File file = fileChooser.showOpenDialog(stageManager.primaryStage);

		if (file != null) {

			if (file.toString().endsWith(".txt")) {

				pathFileLabel.setText(file.toString());
				addDataButton.setDisable(false);

			} else {

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Błąd");
				alert.setHeaderText("Musisz wybrać plik o rozszerzeniu \".txt\"!");
				alert.showAndWait();
			}

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Błąd");
			alert.setHeaderText("Wybierz Plik!");
			alert.showAndWait();
		}
	}

	/**
	 * Słuchacz przycisku "addDataButton" - do dodawania danych surowych
	 */
	@FXML
	private void addDataButtonAction() {

		// przygotowanie zmiennych i alertu
		AtomicInteger taskExecution = new AtomicInteger(0);

		ButtonType buttonOK = new ButtonType("OK!");
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Może to potrwać kilka minut!", buttonOK);
		ProgressIndicator progressIndicator = new ProgressIndicator();

		alert.getDialogPane().lookupButton(buttonOK).setDisable(true);
		alert.setTitle("Operacja w toku");
		alert.setHeaderText("Proszę czekać... ");
		alert.setGraphic(progressIndicator);

		// stworzenie obiektu Task
		Task<Void> task = new Task<Void>() {

			{
				setOnFailed(a -> {
					alert.close();
					updateMessage("Błąd");
				});
				setOnCancelled(a -> {
					alert.close();
					updateMessage("Anulowane");
				});
			}

			@Override
			protected Void call() throws Exception {

				// przygotowanie zmiennych
				String pathFile = pathFileLabel.getText();
				String driverName = chooseDriverCombobox.getSelectionModel().getSelectedItem();

				// stworzenie kierowcy
				Drivers chooseDriver = new Drivers(driverName);

				// pobranie id_kierowcy z bazy danych
				int id_driver = driversRepository.findIdByDriversName(driverName);

				// ustawienie id kierowcy, a reszte na null
				chooseDriver.setIdDriver(id_driver);
				chooseDriver.setData(null);
				chooseDriver.setFixation(null);
				chooseDriver.setResult(null);

				// wyczyszczenie danych dla określonego kierowcy
				dataRepository.deleteDriverData(chooseDriver);
				fixationsRepository.deleteDriverFixations(chooseDriver);
				resultsRepository.deleteDriverResults(chooseDriver);

				// utworzenie obiektu i wywołanie metody zwracającej zbrób danych surowych
				Analyse analyse = new Analyse();
				Set<Data> tempDataSet = analyse.addDataToDB(pathFile, chooseDriver);

				// ustawienei danych dla kierowcy
				chooseDriver.setData(tempDataSet);

				// wywołanie metody zapisującej kierowcę -> w efekcie dane surowe
				driversRepository.save(chooseDriver);

				// ustawienie prograsu na 100%
				updateProgress(1.0, 1.0);

				// odblokowanie przycisku w alercie
				alert.getDialogPane().lookupButton(buttonOK).setDisable(false);

				// zablokowanie przycisku dodającego dane
				addDataButton.setDisable(true);

				// zwrócenie nulla -> w tasku chodzi o to aby zakończył prace
				return null;
			}
		};

		// ustawienie progresu
		progressIndicator.progressProperty().bind(task.progressProperty());

		// stworzenie nowego wątku na podstawie task'a
		Thread taskThread = new Thread(task, "task-thread-" + taskExecution.getAndIncrement());

		// uruchomienie wątku
		taskThread.start();

		// określenie właściciela alertu "stageManager.primaryStage" - aktualna scena
		alert.initOwner(stageManager.primaryStage);

		// pokazanie alertu
		alert.showAndWait();

	}

	/**
	 * Słuchacz przycisku "removeDriverButton" - do usuwania kierowcy oraz danych z
	 * nim związanych
	 */
	@FXML
	private void removeDriverButtonAction() {

		// przygotowanie zmiennych i alertu
		AtomicInteger taskExecution = new AtomicInteger(0);

		ButtonType buttonOK = new ButtonType("OK!");
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Może to potrwać kilka minut!", buttonOK);
		ProgressIndicator progressIndicator = new ProgressIndicator();

		alert.getDialogPane().lookupButton(buttonOK).setDisable(true);
		alert.setTitle("Operacja w toku");
		alert.setHeaderText("Proszę czekać... ");
		alert.setGraphic(progressIndicator);

		Task<Void> task = new Task<Void>() {

			{
				setOnFailed(a -> {
					alert.close();
					updateMessage("Failed");
				});
				setOnCancelled(a -> {
					alert.close();
					updateMessage("Cancelled");
				});
			}

			@Override
			protected Void call() throws Exception {

				String driverName = chooseDriverForRemoveCombobox.getSelectionModel().getSelectedItem();

				// pobranie id kierowcy
				int id = driversRepository.findIdByDriversName(driverName);

				// usunięcie kierowcy wg id
				driversRepository.deleteById(id);

				updateProgress(1.0, 1.0);
				alert.getDialogPane().lookupButton(buttonOK).setDisable(false);
				return null;
			}
		};

		progressIndicator.progressProperty().bind(task.progressProperty());
		Thread taskThread = new Thread(task, "task-thread-" + taskExecution.getAndIncrement());
		taskThread.start();
		alert.initOwner(stageManager.primaryStage);
		alert.showAndWait();

		// wywołanie metody prepInit -> ponowne wypełnienie comboboxów
		prepInit();

	}

	/**
	 * Słuchacz przycisku "searchVideoFileButton" -> do znalezienia pliku video
	 * Przycisk tworzy i uruchamia FileChooser Plik musi być .mp4 W razie błedów
	 * zostanie wyświetlony odpowiedni alert Sukces oznacza odblokowanie przycisku
	 * "startAnalizeButton" i ustawienie "pathVideoFileLabel" jako ścieżka pliku
	 * video
	 */
	@FXML
	private void searchVideoFileButtonAction() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Wybierz plik video");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.mp4)", "*.mp4"));
		File file = fileChooser.showOpenDialog(stageManager.primaryStage);

		if (file != null) {

			if (file.toString().endsWith(".mp4")) {

				pathVideoFileLabel.setText(file.toString());
				startAnalizeButton.setDisable(false);

			} else {

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Błąd");
				alert.setHeaderText("Musisz wybrać plik o rozszerzeniu \".mp4\"!");
				alert.showAndWait();
			}

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Błąd");
			alert.setHeaderText("Wybierz Plik!");
			alert.showAndWait();
		}
	}

	/**
	 * Słuchacz przycisku "startAnalizeButton"
	 */
	@FXML
	private void startAnalizeButtonAction() {

		// przypisanie zmiennych statycznych do których odwoływać się będzie
		// AnalyseController podczas
		// badania pliku video
		MINADS = chooseMinAdsForAnalize.getSelectionModel().getSelectedItem();
		USERNAME = chooseDriverForAnalize.getSelectionModel().getSelectedItem();
		VIDEOPATH = pathVideoFileLabel.getText();

		// zmiena sceny
		stageManager.switchScene(FxmlView.ANALYSE);

	}

	/**
	 * Słuchacz przycisku "startLastAnalizeButton"
	 */
	@FXML
	private void startLastAnalizeButtonAction() {

		AtomicInteger taskExecution = new AtomicInteger(0);

		ButtonType buttonOK = new ButtonType("OK!");
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Może to potrwać kilka minut!", buttonOK);
		ProgressIndicator progressIndicator = new ProgressIndicator();

		alert.getDialogPane().lookupButton(buttonOK).setDisable(true);
		alert.setTitle("Operacja w toku");
		alert.setHeaderText("Proszę czekać... ");
		alert.setGraphic(progressIndicator);

		Task<Void> task = new Task<Void>() {

			{
				setOnFailed(a -> {
					alert.close();
					updateMessage("Błąd");
				});
				setOnCancelled(a -> {
					alert.close();
					updateMessage("Anulowane");
				});
			}

			@Override
			protected Void call() throws Exception {

				// stworzenie listy obiektów Rectangles
				List<Rectangles> rectangles = new ArrayList<>();

				// nazwa wybranego kierowcy
				String chooseDriverName = chooseDriverForAnalize.getSelectionModel().getSelectedItem();

				// nowy obiekt kierowcy
				Drivers chooseDriver = new Drivers();

				// pobranie obiektu kierowcy z bazy danych wg nazwy wybranego kierowcy
				chooseDriver = driversRepository.findByDriverName(chooseDriverName);

				// usunięcie fixacji oraz rezultatów przypisanych dla konkretnego kierowcy
				fixationsRepository.deleteDriverFixations(chooseDriver);
				resultsRepository.deleteDriverResults(chooseDriver);

				// przypisanie obiektów Data do osobnego seta
				Set<Data> coordinatesFromData = chooseDriver.getData();

				// stworzenie nowej listy koordynatów - obiektów Data
				List<Data> coordinates = new LinkedList<Data>();

				// przepisanie danych do nowej listy
				for (Data data : coordinatesFromData) {
					coordinates.add(data);
				}

				// posortowanie listy z obiektymi Data
				Collections.sort(coordinates);

				// pobranie listy Rectangles
				rectangles = rectanglesRepository.findAll();

				// stworzenie obiektu klasy Analyse i wywołanie metody
				// "compareFramesWithRectanglesT2" analizujące podane dane
				Analyse analyse = new Analyse();
				List<Fixations> listFix = analyse.compareFramesWithRectanglesT2(rectangles, coordinates, chooseDriver);

				// stworzenie zbioru fixacji z listy
				Set<Fixations> fixationsSet = new LinkedHashSet<Fixations>(listFix);

				// ustawienie zbioru obiektów Fixations do kierowcy
				chooseDriver.setFixation(fixationsSet);

				// zapisanie kierowcy -> w efekcie Fixacji
				driversRepository.save(chooseDriver);

				updateProgress(1.0, 1.0);
				alert.getDialogPane().lookupButton(buttonOK).setDisable(false);
				return null;

			}
		};

		progressIndicator.progressProperty().bind(task.progressProperty());
		Thread taskThread = new Thread(task, "task-thread-" + taskExecution.getAndIncrement());
		taskThread.start();
		alert.initOwner(stageManager.primaryStage);
		alert.showAndWait();

	}

	/**
	 * Słuchacz przycisku "calculateResultsButton" -> obliczającego rezultaty
	 */
	@FXML
	private void calculateResultsButtonAction() {

		AtomicInteger taskExecution = new AtomicInteger(0);

		ButtonType buttonOK = new ButtonType("OK!");
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Może to potrwać kilka minut!", buttonOK);
		ProgressIndicator progressIndicator = new ProgressIndicator();

		alert.getDialogPane().lookupButton(buttonOK).setDisable(true);
		alert.setTitle("Operacja w toku");
		alert.setHeaderText("Proszę czekać... ");
		alert.setGraphic(progressIndicator);

		Task<Void> task = new Task<Void>() {

			{
				setOnFailed(a -> {
					alert.close();
					updateMessage("Błąd");
				});
				setOnCancelled(a -> {
					alert.close();
					updateMessage("Anulowane");
				});
			}

			@Override
			protected Void call() throws Exception {

				String chooseDriverName = chooseDriverForAnalize.getSelectionModel().getSelectedItem();
				Drivers chooseDriver = new Drivers();
				chooseDriver = driversRepository.findByDriverName(chooseDriverName);

				resultsRepository.deleteDriverResults(chooseDriver);

				Set<Data> coordinatesFromData = chooseDriver.getData();

				LinkedList<Data> coordinates = new LinkedList<Data>();
				for (Data data : coordinatesFromData) {
					coordinates.add(data);
				}

				Collections.sort(coordinates);

				// cały czas filmu
				double firstTime = coordinates.getFirst().getTime();
				double lastTime = coordinates.getLast().getTime();
				double full_time = lastTime - firstTime;

				double sec_full_time = (full_time / 1000000);

				// czas przez, który patrzy na reklamy
				Set<Fixations> fixations = chooseDriver.getFixation();

				double time_look_ads_all = 0;
				for (Iterator<Fixations> iterator = fixations.iterator(); iterator.hasNext();) {
					Fixations onefix = (Fixations) iterator.next();
					time_look_ads_all = time_look_ads_all + onefix.getTimelooking();
				}

				double sec_time_look_ads_all = time_look_ads_all / 1000000;
				double avg_time_advertising = sec_time_look_ads_all / fixations.size();
				double time_look_percent = (sec_time_look_ads_all / sec_full_time) * 100;

				// ustawienie rezultatu do obiektu kierowcy (towrzenie obiektu rezultatu w
				// argumencie)
				chooseDriver.setResult(new Results(sec_full_time, sec_time_look_ads_all, time_look_percent,
						avg_time_advertising, chooseDriver));

				// zapisanie danych
				driversRepository.save(chooseDriver);

				// usunięcie danych zbędnych
				dataRepository.deleteDriverData(chooseDriver);
				fixationsRepository.deleteDriverFixations(chooseDriver);

				updateProgress(1.0, 1.0);
				alert.getDialogPane().lookupButton(buttonOK).setDisable(false);
				return null;

			}
		};

		progressIndicator.progressProperty().bind(task.progressProperty());
		Thread taskThread = new Thread(task, "task-thread-" + taskExecution.getAndIncrement());
		taskThread.start();

		alert.initOwner(stageManager.primaryStage);
		alert.showAndWait();
	}

	/**
	 * Słuchacz przycisku showResultsButton -> do pokazywania rezultatów
	 */
	@FXML
	private void showResultsButtonAction() {

		// stworznienie obiektów i pobranie danych z bazy
		Results res = new Results();
		List<Results> results = resultsRepository.findAll();
		for (Results result : results) {
			if (result.getDriver().getName().equals(chooseDriverForResult.getSelectionModel().getSelectedItem())) {
				res = result;
			}
		}

		// przygotowanie zmiennych wynikowych
		float fulltime = (float) (res.getFullTime() / 60);
		double sec = (fulltime - Math.floor(fulltime)) * 60;

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(0);

		// ustawienie wyniku do label "lengthMovie"
		lengthMovie.setText(df.format(fulltime) + " minut " + " i " + df.format(sec) + " sekund");

		df.setMaximumFractionDigits(2);

		// ustawienie reszty wyników
		timeDriverLookAds.setText(df.format(res.getTimeLookForAdvertising()) + " sekund");
		timeDriverLookAdsPercents.setText(df.format(res.getTimeFromTotalInPercent()) + "%");
		timeDriverLookAdsAvg.setText(df.format(res.getAvgTimeForOneAdvertising()) + " sekund");
	}

}
