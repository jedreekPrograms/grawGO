# Gra w GO

Prosty program napisany w technologii klient-serwer w języku Java. Obsługuje proste zasady gry Go takie jak kładzenie kamieni oraz obsługa łańcuchów i ich duszenie. Obecna rozgrywka odbywa sie na planszy 19 x 19

## Instrukcja gry

Gracze graja poprzez wpisywanie tekstowo komend, które wysyłane są na serwer.  
Obecnie serwer rozpoznaje następujące komendy:  
- **x y** - kładzie kamień na pozycji x,y na planszy jeśli to możliwe; koordynaty liczone sa od lewego górnego rogu planszy 
- **Pass** - pomija ture  
- **Resign** - poddaje gre  

## Działanie programu

Pozwala na wiele rozgrywek między dwoma klientami jednocześnie. Gracze są parowani w kolejności w jakiej połączą się z serwerem. Komunikacja odbywa się za pomocą protokołu TCP. Po wysłaniu komendy klienci otrzymuja adekwatną odpowiedź serwera wraz z zaktualizowanym stanem planszy gry.

## Uruchamianie

mvn compile

### Uruchamianie serwera
java -cp target/classes pl.edu.go.server.MatchmakingServer

### Uruchamianie klienta
java -cp target/classes pl.edu.go.client.GoClient

## Użyte technologie

- Java  
- Maven  
- Junit
