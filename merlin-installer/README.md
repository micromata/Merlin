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
7. Testing
   1. Testing update from previous installation.
   2. install, test web and java-code.
8. Creating Github release
   1. Create new release
   2. Specify version, e. g. `v0.3`.
   3. Drag `merlin_macos_v0.3.dmg` to binary section (upload).
   4. Publish release button (after uploading is finished).
   5. Testing
9. Enable update mechanism
   1. `cd merlin`
   2. `cp ../Install4j/Merlin/updates.xml merlin-installer/`
   3. Commit and push.