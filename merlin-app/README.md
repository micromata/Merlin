# Building a new release
1. `cd merlin-app`
2. `mvn clean compile assembly:single`
3. Run Install4j
   1. General settings -> Application Info -> Version __current version__
   2. Files -> Define Distribution Tree -> Replace current `merlin-app-x.y-jar-with-dependencies.jar`
   3. Build release
   4. cp updates.xml from generated release to merlin-installer github dir.