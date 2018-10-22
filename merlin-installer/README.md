# Building a new release
1. 'mvn clean install'
2. `cd merlin-app`
3. `mvn clean compile assembly:single && cp target/merlin-app-?.?-jar-with-dependencies.jar target/merlin-app.jar`
4. `cd ../merlin-webapp`
5. `npm run build`
6. Run Install4j
   1. General settings -> Application Info -> Version __current version__
   2. Build project
   3. cp updates.xml from generated release to merlin-installer github dir.