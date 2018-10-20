# Building a new release
1. `cd merlin-app`
2. `mvn clean compile assembly:single && cp target/merlin-app-?.?-jar-with-dependencies.jar target/merlin-app.jar`
3. `cd ../merlin-webapp`
4. `npm run build`
5. Run Install4j
   1. General settings -> Application Info -> Version __current version__
   2. Build project
   3. cp updates.xml from generated release to merlin-installer github dir.