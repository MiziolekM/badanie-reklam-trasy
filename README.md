
### Badanie Zageszczenia Reklam

## Spis treści

* [Informacje ogólne](#informacje-ogólne)
* [Technologie](#technologie)
* [Funkcjonalności](#funkcjonalności)

## Informacje ogólne

Aplikacja desktopowa badająca zagęszczenie/ilość reklam na które patrzy kierowca podczas jazdy samochodem. Wymagane są pliki z systemu EyeTracker. Aby zrozumieć podejście działania aplikacji trzeba pamiętać, że jeden zestaw danych (dane z pliku tekstowego oraz plik wideo) to jeden kierowca.

## Technologie

* Spring Boot
* Hibernate
* MySql
* JavaFX
* JCodec 

## Funkcjonalności

* Stworzenie kierowcy.
* Dodanie danych surowych (dane tekstowe pochodzące z eyetrackera) dla wybranego kierowcy.
* Przeprowadznie wstępnego badania, czyli badania pliku video.
* Przeprowadzenie badania porównawczego. Porównanie badania wstępnego z danymi tekstowymi.
* Obliczenie wyników.
* Wyświetlenie wyników.
* Usunięcie danych związanych z wybranym kierowcą.