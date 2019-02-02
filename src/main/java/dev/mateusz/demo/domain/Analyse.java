package dev.mateusz.demo.domain;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import dev.mateusz.demo.controller.MainController;
import dev.mateusz.demo.entity.Data;
import dev.mateusz.demo.entity.Drivers;
import dev.mateusz.demo.entity.Fixations;
import dev.mateusz.demo.entity.Rectangles;

public class Analyse {

	Logger logger = Logger.getLogger(MainController.class.getName());

	public Analyse() {

	}

	/**
	 * Metoda porównująca dane zebrane podczas analizy z danymi surowymi
	 * @param rectangles - lista obiektów Rectangles - prostokąty określone podczas analizy wideo
	 * @param coordinates - lista obiektów Data - dane surowe
	 * @param chooseDriver - obiekt Drivers - kierowca
	 * @return
	 */
	public List<Fixations> compareFramesWithRectanglesT2(List<Rectangles> rectangles, List<Data> coordinates,
			Drivers chooseDriver) {

		List<Fixations> listFixation = new ArrayList<Fixations>();

		System.out.println("rectangles: " + rectangles.size());
		int minX = rectangles.get(0).getStartX();
		int maxX = rectangles.get(0).getEndX();
		int minY = rectangles.get(0).getStartY();
		int maxY = rectangles.get(0).getEndY();
		for (int i = 1; i < rectangles.size(); i++) {
			if (rectangles.get(i).getStartX() < minX)
				minX = rectangles.get(i).getStartX();
			if (rectangles.get(i).getEndX() > maxX)
				maxX = rectangles.get(i).getEndX();
			if (rectangles.get(i).getStartY() < minY)
				minY = rectangles.get(i).getStartY();
			if (rectangles.get(i).getEndY() > maxY)
				maxY = rectangles.get(i).getEndY();
		}

		double x = 0;
		double y = 0;

		System.out.println("MAX_X = " + maxX);
		System.out.println("MAX_Y = " + maxY);
		System.out.println("MIN_X = " + minX);
		System.out.println("MIN_Y = " + minY);

		Data checkonedata;
		int id_driver = 0;
		Data onedata;
		long time;
		int id_start; // ID pierwszego i ostatniego rekordu w obrębie ruchów sakkadowych
		int to_skip; // liczba opuszczanych rekordów (rekordy te zostały zaliczone do jednej
						// fiksacji)
		System.out.println("coordinates: " + coordinates.size());

		for (int i = 0; i < coordinates.size();) {
			// System.out.println(onedata);
			to_skip = 0;
			onedata = coordinates.get(i);
			time = onedata.getTime();
			x = onedata.getbPorX();
			y = onedata.getbPorY();
			id_driver = chooseDriver.getIdDriver();
			// pobierz x i y z danych surowych z i-tego rekordu
			// sprawdź, czy punkt należy do dużego prostokąta
			if ((x > minX) && (x < maxX) && (y > minY) && (y < maxY)) {
				// ta pętla przechodzi po liście prostokątów
				System.out.println(onedata);
				for (int j = 0; j < rectangles.size(); j++) {
					Rectangles rect = rectangles.get(j); // pobierz prostokąt

					if (x > rect.getStartX() && x < rect.getEndX()) {
						if (y > rect.getStartY() && y < rect.getEndY()) {

							// System.out.println(x + "->" + rect.getStartX() + " " + rect.getEndX());
							// System.out.println(y + "->" + rect.getStartY() + " " + rect.getEndY());

							// punkt należy do prostokąta - zapisz w tabeli
							id_start = i;
							long time1 = 0;
							double x1, y1;

							id_start++;
							// System.out.println(id_start);
							checkonedata = coordinates.get(id_start);
							x1 = checkonedata.getbPorX();
							y1 = checkonedata.getbPorY();

							while ((x1 < x + 10) && (x1 > x - 10) && (y1 < y + 10) && (y1 > y - 10)) {
								System.out.println("=============>");
								time1 = checkonedata.getTime(); // wykryte ruchy sakkadowe - zapisz time do zmiennej
								id_start++; // przejdź do kolejnego rekordu

								checkonedata = coordinates.get(id_start); // pobierz współrzędne z kolejnego rekordu
								x1 = checkonedata.getbPorX();
								y1 = checkonedata.getbPorY();
							}

							long finaltime = time1 - time;
							to_skip = id_start - i;
							System.out.println("to_skip = " + to_skip);
							if (to_skip >= 2) {
								System.out.println("asdasdasdasd");
								Fixations fixations1 = new Fixations(time, x, y, Integer.parseInt("" + finaltime),
										chooseDriver);
								listFixation.add(fixations1);
							}
							break; // innych prostokątów już się nie sprawdza
						}
					}
				}
			}
			i = i + to_skip + 1;
		}
		return listFixation;
	}

	/**
	 * Metoda zmieniająca plik z danymi surowymi na zbiór danych
	 * @param pathFile - ścieżka pliku tekstowego z danymi surowymi
	 * @param chooseDriver - obiekt kierowcy do którego należą dane
	 * @return zbiór danych - obiektów klasy Data
	 */
	public Set<Data> addDataToDB(String pathFile, Drivers chooseDriver) {

		//zmienne przygotowujące
		Logger logger = Logger.getLogger(MainController.class.getName());
		Set<Data> tempDataSet = new LinkedHashSet<>();
		int wartosc = 0;
		FileReader fr = null;
		String all = null;
		String foo = null;
		String dodano[] = new String[100];
		int i = 0;

		// OTWIERANIE PLIKU:
		try {
			fr = new FileReader(pathFile);
		} catch (FileNotFoundException e) {
			logger.info("Błąd przy otwieraniu pliku!");
		}

		try {
			StreamTokenizer st = new StreamTokenizer(fr);

			// ODCZYT KOLEJNYCH "TOKENÓW" Z PLIKU:

			try {
				while ((wartosc = st.nextToken()) != StreamTokenizer.TT_EOF) {

					if (wartosc == StreamTokenizer.TT_WORD) {
						foo = st.sval;
					} else if (wartosc == StreamTokenizer.TT_NUMBER) {
						String temp = "" + st.nval;
						if (i == 0) {
							if (temp.length() == 4 && temp.substring(2, 3).equals("E")
									&& temp.substring(3, 4).equals("8")) {
								all = temp.substring(0, 1) + "00000000";
							} else if (temp.length() == 5 && temp.substring(3, 4).equals("E")
									&& temp.substring(4, 5).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 3) + "0000000";
							} else if (temp.length() == 6 && temp.substring(4, 5).equals("E")
									&& temp.substring(5, 6).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 4) + "000000";
							} else if (temp.length() == 7 && temp.substring(5, 6).equals("E")
									&& temp.substring(6, 7).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 5) + "00000";
							} else if (temp.length() == 8 && temp.substring(6, 7).equals("E")
									&& temp.substring(7, 8).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 6) + "0000";
							} else if (temp.length() == 9 && temp.substring(7, 8).equals("E")
									&& temp.substring(8, 9).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 7) + "000";
							} else if (temp.length() == 10 && temp.substring(8, 9).equals("E")
									&& temp.substring(9, 10).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 8) + "00";
							} else if (temp.length() == 11 && temp.substring(9, 10).equals("E")
									&& temp.substring(10, 11).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 9) + "0";
							} else if (temp.length() == 12 && temp.substring(10, 11).equals("E")
									&& temp.substring(11, 12).equals("8")) {
								all = temp.substring(0, 1) + temp.substring(2, 10);
							} else if (temp.length() == 13) {
								all = temp.substring(0, 1) + temp.substring(2, 11);
							}

							else {
								int toint = (int) st.nval;
								all = toint + "";
								dodano[0] = all;
							}
						} else {
							double toint = st.nval;
							foo = toint + "";
						}
					}
					dodano[i] = foo;
					if (i == 36) {
						String foo28, foo29, foo30, foo31;
						String d28 = dodano[29] + "";
						String d29 = dodano[31] + "";
						String d30 = dodano[33] + "";
						String d31 = dodano[34] + "";
						if (d28.length() == 3) {
							foo28 = "0" + d28.substring(0, 1);
						} else {
							foo28 = d28.substring(0, 2);
						}

						if (d29.length() == 3) {
							foo29 = "0" + d29.substring(0, 1);
						} else {
							foo29 = d29.substring(0, 2);
						}

						if (d30.length() == 3) {
							foo30 = "0" + d30.substring(0, 1);
						} else {
							foo30 = d30.substring(0, 2);
						}

						if (d31.length() == 3) {
							foo31 = "0" + d31.substring(0, 1);
						} else {
							foo31 = d31.substring(0, 2);
						}

						String frame = foo28 + ":" + foo29 + ":" + foo30 + ":" + foo31;

						if (dodano[9].equals("NaN") || dodano[10].equals("NaN")) {
							logger.info("========>" + dodano[9] + " " + dodano[10]);
						} else {
							
							//towrznie obiektu Data i ustawienie dla niego wartości wczesniej przygotowanych
							Data tempData = new Data();
							tempData.setIdData(0);
							tempData.setDriver(chooseDriver);
							tempData.setTime(Long.parseLong(all));
							tempData.setbPorX(Double.parseDouble(dodano[9]));
							tempData.setbPorY(Double.parseDouble(dodano[10]));
							tempData.setFrame(frame);
							
							//dodanie danych do zbioru
							tempDataSet.add(tempData);
						}

						i = -1;
					}
					i++;
				}

			} catch (IOException e) {
				logger.info("Błąd odczytu z pliku!");
			}
		} catch (Exception e) {
			logger.info("Nie mogę dodać danych!");
		}
		// ZAMYKANIE PLIKU:
		try {
			fr.close();
		} catch (IOException e) {
			logger.info("Błąd zamykania pliku!");
		}

		return tempDataSet;
	}

}
