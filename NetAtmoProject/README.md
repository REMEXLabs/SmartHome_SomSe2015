# Universal Remote Console

Dieses Respository enthält zwei Web Applikationen, die während der Vorlesung Universal Remote Console an der Hochschule der Medien in Stuttgart im Sommersemester 2015 programmiert wurden. Für die Vorlesung sollten zwei Anwendungen erstellt werden, eine für eine fiktive Persona und eine die man sie selber verwenden wollen würde.

Als Grundlage der Anwendungen wurde die [netatmo Wetterstation](https://www.netatmo.com/de-DE/produkt/wetterstation) verwendet, welche verschiedene Umgebungsvariablen wie Temperatur, Lautstärke oder Luftfeuchtigkeit messen kann. Diese Messdaten und weitere Daten aus anderen Schnittstellen werden in unseren Anwednungen aufbereitet und dem Nutzer dargestellt.

### Sabine App
Die Sabine App ist die Implementierung des Interfaces für die fiktive Persona Sabine Wohlfahrt (71). In einem [Video](https://www.youtube.com/watch?v=XvkGSJwmDAU) wird das Interface näher dargestellt. Das Interface wurde speziell an ihre Bedrüfnisse und Wünsche angepasst und enthält folgende Ausgaben:
- Temperatur 
- Lebensmittelverfügarkeit
- Wettervorhersage 
- Stresslevel
- Lärm
- Luftqualität

### Universal App
Die Universal App ist die zweite Implementierung nach eigenen Wünschen und Vorlieben. In dieser Anwednung kann auf alle verfügbaren Daten zugegriffen:
- CO2-Gehalt
- Geräte-Informationen
- Lärm
- Luftdruck
- Luftfeuchtigkeit
- Luftqualität 
- Lebensmittel
- Pollen
- Stresslevel
- Temperatur
- Wettervorhersage
 
## Verwendete Technologien
### Frontent
- HTML5
- JS (vanilla)
- CSS (SCSS)
- [Hightcharts](http://www.highcharts.com/)

### Backend
- NodeJS
- [netatmo](https://dev.netatmo.com/)
- [openHab](http://www.openhab.org/)
- [OpenWeaterMap](http://openweathermap.org/)
