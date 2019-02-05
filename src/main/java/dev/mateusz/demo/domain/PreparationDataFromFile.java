package dev.mateusz.demo.domain;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import dev.mateusz.demo.controller.MainController;
import dev.mateusz.demo.entity.Data;
import dev.mateusz.demo.entity.Drivers;

public class PreparationDataFromFile {
	
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
