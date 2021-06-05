# HOF-OOP2-ReDiNet

## Own
Todo:
[] Model implementieren
[] Views (z.B. Settings oder History view) auslagern

## Officials

### Testat Checkliste
[x] a) Tabulierte Darstellung – Zur Anzeige einer Webseite kann der Benutzer ein neues Tab öffnen, worin diese dann dargestellt wird. (1 Punkt)
[x] b) Ein Texteingabefeld soll es dem Anwender ermöglichen eine URL einzugeben, welche durch drücken der Return-Taste im aktuellen Tab angezeigt werden soll. Alternativ soll eine Schaltfläche verwendet werden können, um das Laden der Internetseite zu starten. (2 Punkte)
[x] c) Eine Statuszeile, welche während des Ladens einer Webseite (natürlich passend zum angezeigten Tab!) einen Fortschrittsbalken anzeigt. Zusätzlich soll diese ToolBar auch verwendet werden, um Info- / Warn- und Fehlermeldungen auszuschreiben. (3 Punkte)
[x] d) Eine Schaltfläche, um ein neues Tab zu öffnen. Jedes neue Tab soll bereits eine initiale HTML-Seite anzeigen. (z.B. Startseite, o.ä.) (1 Punkt)
[x] e) Eine Schaltfläche, um per Mausklick direkt zur Startseite zu wechseln. (1 Punkt)
[x] f) Wird eine Seite nicht gefunden, oder konnte diese nicht geöffnet werden, so soll dem Benutzer eine Fehlerseite angezeigt werden. (1 Punkt)
[x] g) Die Browseranwendung soll einen einfachen Einstellungsdialog bekommen, worin der Benutzer eine Startseite festlegen kann. (2 Punkte)
[x] h) Das Browserprogramm erhält eine Menuleiste, mit folgenden Funktionen: (5 Punkte)
[x] 1. Datei → Neuer Tab - Öffnet ein neues Tab
[x] 2. Datei → Neues Fenster – Öffnet ein neues Fenster
[x] 3. Extras → Einstellungen – Festlegen von Programmeinstellungen (z.B. Startseite)
[x] 4. Hilfe → Über – Anzeige der Produktinformationen (Name der Software), sowie Ihr Name/MatrNr, „Testataufgabe zur Vorlesung Objektorientierte Programmierung 2“ + Studiengang und Studiensemester

### 1. Layout
[x] nothing to do

### 2. Programmierung 
[x] a) Schreiben Sie sich eine Klasse Browser, diese erbt z.B. von BorderPane und erstellt den Szenegraphen im Konstruktor. Mittels: primaryStage.setScene(new Browser()); ließe sich Ihr Browser dann anzeigen.
[x] b) Um die Ladeanzeige zu realisieren können sie die progressProperty() des LoadWorker der (Web)Engine einer WebView direkt an die progressProperty der Ladeanzeige binden. Beachten Sie: Wenn Sie in einen anderen Tab wechseln, so müssen Sie diese Bindung wieder aufheben! Außerdem empfiehlt es sich, die ProgressBar unsichtbar zu machen, wenn aktuell kein Ladevorgang statt findet.
[x] c) Mit den Statusmeldungen des LoadWorker verfahren Sie analog zu b).
[x] d) Alle Programmeinstellungen müssen natürlich auf einem Datenträger gespeichert werden und beim nächsten Start der Anwendung wieder eingelesen werden. Das Speichern und Laden soll automatisch vonstatten gehen, ohne dass der Anwender erst eube Datei auswählen muss!
[x] e) Die ENTER-Taste erzeugt beim Textfeld einen Action-Event. Das „http://“ können Sie automatisch voranstellen, wenn der Anwender dies nicht mit eintippt.
[x] f) Die Fehlerseite können Sie als HTML-Text (String) der loadContent(..)-Methode der Engine einer WebView übergeben.
[x] g) Damit Ihr Programm einem Browser wie Firefox oder Iceweasel in nichts nachsteht, sollten Sie auf Ihren Schaltflächen anstelle von Beschriftungen besser Icons anzeigen. (Kostenlose Icons finden Sie im Internet.)
[x] h) Wenn Sie Lust haben mehr zu machen, lassen Sie sich nicht bremsen. Allerdings sollte die Aufgabenstellung zunächst einmal komplett erfüllt sein.

### 3. Punktevergabe
[x] Nothing to do