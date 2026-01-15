# Gra w GO

Prosty program napisany w technologii klient-serwer w języku Java. Obsługuje większość zasady gry Go takich jak kładzenie kamieni,obsługa łańcuchów, duszenia, zapobiegania ko i samobójstw. Obecna rozgrywka odbywa sie na planszy 9 x 9. Obecna rekompensata za granie białymi(komi) wynosi 6.5 pkt.

## Instrukcja gry

Gracze graja do dyspozycji mają interfejs graficzny. W trakcie własnej tury mają możliwość położyć kamień poprzez nacieśnięcie na przecięcie na planszy. Mają również możliwość spasowania lub poddania się za pomocą przysków znajdujących się u góry interfejsu. Jeśli obydwaj gracze spasują jeden po drugim gra przechodzi w tryb uzgadniania wyniku partii. Gracze mogą wtedy klikać na obecne na planszy łańcuchy oznaczając je jako martwe. Jeśli gracze uzgodnią że określili wszystkie martwe łańcuchy i są usatysfakcjonowani stanem planszy to mogą kliknąć przycisk Accept. Jeśli obydwaj gracze zaakceptują stan planszy to wynik partii jest rozstrzygany automatycznie na podstawie terytoriów, martwych łańcuchów i jeńców. Jeśli jeden z graczy nie jest zadowolony ze stanu planszy i chciałby kontynuować gre to może to zrobić klikając przycisk Continue.

## Działanie programu

Pozwala na wiele rozgrywek między dwoma klientami jednocześnie. Gracze są parowani w kolejności w jakiej połączą się z serwerem. Komunikacja odbywa się za pomocą protokołu TCP. Po wysłaniu komendy klienci otrzymuja adekwatną odpowiedź serwera wraz z zaktualizowanym stanem planszy gry.

## Uruchamianie

mvn clean compile

### Uruchamianie serwera
java -cp target/classes pl.edu.go.server.MatchmakingServer

### Uruchamianie klienta
mvn javafx:run

## Użyte technologie

- Java  
- Maven  
- Junit
- JavaDoc
