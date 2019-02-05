package dev.mateusz.demo.controller;

import static java.lang.Math.abs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jcodec.api.JCodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import dev.mateusz.demo.config.StageManager;
import dev.mateusz.demo.domain.VideoFrameExtracter;
import dev.mateusz.demo.entity.Data;
import dev.mateusz.demo.entity.Drivers;
import dev.mateusz.demo.entity.Rectangles;
import dev.mateusz.demo.repository.DriversRepository;
import dev.mateusz.demo.repository.RectanglesRepository;
import dev.mateusz.demo.repository.ResultsRepository;
import dev.mateusz.demo.view.FxmlView;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

@Component
public class AnalyseController {

	// zmienne FXML'owe
	@FXML
	private Button nextFrame;
	@FXML
	private Canvas canvasFrame;
	@FXML
	private VBox vboxAnalyse;

	// wstrzyknięte repozytoria i managera scen
	@Autowired
	private RectanglesRepository rectanglesRepository;
	@Autowired
	private DriversRepository driversRepository;
	@Autowired
	private ResultsRepository resultsService;
	@Lazy
	@Autowired
	StageManager stageManager;
	@Autowired
	VideoFrameExtracter videoFrameExtracter;

	// logger
	private static final Logger logger = Logger.getLogger(AnalyseController.class.getName());

	// reszta zmiennyech
	int correctRectangles;
	public static List<Rectangles> rectangles = new ArrayList<>();
	private GraphicsContext gc;
	private boolean dragging;
	private int prevX, prevY;
	String path;
	Rectangles rectangle;
	int i = 1;
	int g = 1;
	String p;
	String numerofad;

	List<Drivers> drivers = new LinkedList<>();
	Drivers chooseDriver = new Drivers();
	LinkedList<Data> coordinates = new LinkedList<Data>();

	/**
	 * Funkcja inicjalizująca
	 */
	@FXML
	public void initialize() {

		// pobranie wybranego kierowcy, w argumencie metody odwałano się do zmiennej
		// statycznej z głównego kontrollera
		chooseDriver = driversRepository.findByDriverName(MainController.USERNAME);

		// usunięcie danych w bazie danych w tabeli rectangles
		rectanglesRepository.deleteAll();

		// usunięcie resultatów dot. wybranego kierowcy
		resultsService.deleteDriverResults(chooseDriver);

		// przypisanie danych surowych ze zbioru do listy
		Set<Data> coordinatesFromData = chooseDriver.getData();
		for (Data data : coordinatesFromData) {
			coordinates.add(data);
		}

		// posortowanie listy obiektów Data
		Collections.sort(coordinates);
	}

	/**
	 * Metoda określająca czy kierowca patrzy na domyślne obszary
	 * 
	 * @param id      - id z listy
	 * @param b_por_x - zmienna x tam gdzie patrzy kierowca
	 * @param b_por_y - zmienna y tam gdzie patrzy kierowca
	 * @return id = 0 jeśli nie patrzy, jeśli patrzy to id poprawne
	 */
	public int firstAnalysisAreaOption(int id, double b_por_x, double b_por_y) {
		double templeft = abs((0.13 * b_por_x) - 340);
		double tempright = abs((-0.21 * b_por_x) - 150.2);
		int idchecked = 0;
		if ((0 < b_por_x) && (b_por_x < 490)) {
			if ((0 < b_por_y) && (b_por_y < templeft)) {
				idchecked = id;
			}
		} else if ((585 < b_por_x) && (b_por_x < 960)) {
			if ((0 < b_por_y) && (b_por_y < tempright)) {
				idchecked = id;
			}
		}
		return idchecked;
	}

	/**
	 * Metoda przelicza czas na sekundy
	 * @param time - czas "obecny"
	 * @param firsttime - czas "pierwszy"
	 * @return czas w sekundarz
	 */
	public static double getSecFromTime(long time, long firsttime) {
		double sec = 0;
		long ft = firsttime;
		long time1 = time / 10000;
		long ft1 = ft / 10000;
		double time2 = Double.parseDouble(time1 + "") / 100;
		double ft2 = Double.parseDouble(ft1 + "") / 100;
		double temp = time2 - ft2;
		sec = temp;
		return sec;
	}

	/**
	 * Słuchacz przycisku "nextFrame"
	 * 
	 * @throws IOException
	 */
	@FXML
	private void nextFrameAction() throws IOException {

		// sprawdzenie ile prostokątów zostało jeśli wypełniono listę odpowienią ilością
		// zmieniamy scena na domową
		// w porównaniu odwołuje się do statycznej zmiennej MINADS
		if (rectangles.size() >= MainController.MINADS) {
			// System.out.println("numer : " + MainController.MINADS);
			stageManager.switchScene(FxmlView.HOME);
		}

		// obiekt pliku z ścieżki
		// w argumencie odwołano się do zmiennej statycznej VIDEOPATH
		File file = Paths.get(MainController.VIDEOPATH).toFile();

		// pętla while dziła dopóki nie wypełnimy listy rectangles
		// w porównaniu odwołuje się do statycznej zmiennej MINADS
		while (rectangles.size() < MainController.MINADS) {

			// przypisanie odpowiednich zmiennych z obiektu DATA
			int id = coordinates.get(i).getIdData();
			double b_pox_x = coordinates.get(i).getbPorX();
			double b_pox_y = coordinates.get(i).getbPorY();
			long time = coordinates.get(i).getTime();

			// określanie pierwszych obszarów wyboru
			// sprawdzenie czy kierowca patrzy na domyślne obszary
			int idchecked = firstAnalysisAreaOption(id, b_pox_x, b_pox_y);
			System.out.println("id1 : " + idchecked);

			// jeśli patrzy - wynik metody firstAnalysisAreaOption
			if (idchecked != 0) {

				i = i + 1000;
				if (i > coordinates.getLast().getIdData()) {
					break;
				}

				// określenie sekundy w której patrzy
				long firsttime = coordinates.getFirst().getTime();
				double sec = getSecFromTime(time, firsttime);

				// wycięcie i narysowanie klatki z filmu na kanwie
				try {
					File imageFrame = videoFrameExtracter.createThumbnailFromVideo(file, sec);
					Image img = new Image("file:" + imageFrame.getAbsolutePath());
					gc = canvasFrame.getGraphicsContext2D();
					gc.drawImage(img, 0, 0);
					i++;
					break;
				} catch (JCodecException e) {
					e.printStackTrace();
				}
			}
			i++;
		}

	}

	/**
	 * Słuchacz kliknięcia myszki na kanwę
	 * @param e - współrzędne kliknięcia kursora myszki
	 */
	@FXML
	private void canvasFrameOnMousePressed(MouseEvent e) {

		// ignorowanie naciśnięcia myszy
		if (dragging == true)
			return;
		dragging = true;
		// Współrzędna x, w której użytkownik kliknął.
		int x = (int) e.getX(); 
		logger.info("===> " + x);
		prevX = x;
		// Współrzędna y, w której użytkownik kliknął.
		int y = (int) e.getY();
		logger.info("===> " + y);
		prevY = y;

		rectangle = new Rectangles(x, y, x, y);

	}

	/**
	 * Słuchacz przeciągnięcia kursora myszki
	 * @param e - współrzędne kursora myszki
	 */
	@FXML
	private void canvasFrameOnMouseDragged(MouseEvent e) {

		int x = (int) e.getX();
		System.out.println(x);
		int y = (int) e.getY();
		System.out.println(y);

		int startX, startY;
		int endX, endY;
		int rectWidth, rectHeight;

		if (dragging == false)
			return;

		startX = rectangle.getStartX();
		startY = rectangle.getStartY();
		endX = rectangle.getEndX();
		endY = rectangle.getEndY();
		/*
		 * zapisuj w obiekcie prostokąta maksymalne współrzędne (jeśli user cofnie
		 * kursor wgłąb prostokąta, w obiekcie i tak będą współrzędne narysowanego
		 * prostokąta)
		 */
		if (x > endX)
			rectangle.setEndX(x);
		if (y > endY)
			rectangle.setEndY(y);

		rectWidth = Math.abs(x - startX);
		rectHeight = Math.abs(y - startY);

		// wypełnienie rysowanego prostokąta
		if (x > startX && y > startY && x >= endX && y >= endY)
			gc.fillRect(startX, startY, rectWidth, rectHeight);
	}

	/**
	 * Słuchacz gdy kursor muszyki zostanie zwolniony
	 * @param e - współrzędne kursora myszki
	 */
	@FXML
	private void canvasFrameOnMouseReleased(MouseEvent e) {

		int x = (int) e.getX();
		int y = (int) e.getY();

		 // pomijaj zapisywanie prostokątów nienarysowanych
		if (!(rectangle.getStartX() == rectangle.getEndX())) {
			rectangles.add(rectangle);
			rectanglesRepository.save(rectangle);
		}

		if (dragging == false)
			return; // nic nie robić - użytkownik nie rysuje
		dragging = false;
		gc = null;

	}

}
