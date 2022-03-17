### Trivia Test Server

This is a simple test server for the Trivia games and Polls.
It is built using a Kotlin based Ktor server.

As currently configured this server runs at the development machine's IP address.
To test on the app, the websocket server address needs to be set to:

`ws://<dev-machine-ip>:8080/`

In the Android Display app, this address is set in the `HostProvider.kt` file, 
under the var `triviaHost`

To run the server, using Intellij or Android Studio, goto the `Applications.kt` file and click the run icon.

To verify that the server is running, using a browser on the dev machine, go to: `http://localhost:8080/test`. 
A page with "Hello" should be received.
