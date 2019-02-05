package dev.mateusz.demo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dev.mateusz.demo.controller.MainController;
import dev.mateusz.demo.entity.Data;
import dev.mateusz.demo.entity.Drivers;
import dev.mateusz.demo.entity.Fixations;
import dev.mateusz.demo.entity.Rectangles;

public class ComparativeAnalysis {

	Logger logger = Logger.getLogger(MainController.class.getName());

	/**
	 * Metoda porównująca dane zebrane podczas analizy z danymi surowymi
	 * @param rectangles - lista obiektów Rectangles - prostokąty określone podczas analizy wideo
	 * @param coordinates - lista obiektów Data - dane surowe
	 * @param chooseDriver - obiekt Drivers - kierowca
	 * @return
	 */
	public List<Fixations> compareFramesWithRectangles(List<Rectangles> rectangles, List<Data> coordinates,
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

	

}
