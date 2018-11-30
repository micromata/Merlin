# Merlin Desktop Installer

Contains resources and scripts for building new releases of Merlin's desktop application.

## Building a new release
1. Increase version in `build.gradle`.
2. In top directory of merlin: `gradle clean test dist`
3. Run Install4j
   1. General settings -> Application Info -> Version __0.1__ (old fake version) and test update from this fake version to the last published one.
   2. General settings -> Application Info -> Version __new version__
   3. Installer -> Auto-Update Options -> Base url for installers: https://sourceforge.net/projects/pforge/files/Merlin/0.9/
   4. Build project
4. `gradle postDist`
5. Testing
   1. install, test web and java-code.
   2. Check installation: Assure that no javafx*.jar is in the lib dir.
6. Upload to SourceForge
   1. Publish to new Folder 0.9
      1. `Install4j/Merlin/merlin-macos-v_9.dmg` (mark as default for MacOS)
      2. `Install4j/Merlin/merlin-unix-0_9.sh` (mark as default for Linux)
      3. `Install4j/Merlin/merlin-windows-0_9.exe` (mark as default for Windows)
      4. `merlin-server/build/distributions/merlin-server-0.9.zip` (mark as default for all others)
   2. Testing
7. Enable update mechanism
   1. Commit and push.
   2. Testing update from previous installation.
8. Tag new Release.
9. Creating Github release
   1. Create new tag, e. g. `v0.9`.


## For preparing Install4j
1. File structure:
   - merlin
     - merlin-app...
   - Install4j
     - Merlin
       - javafx
         - linux
           - *.jar
           - *.so
         - macos
           - *.dylib
           - *.jar
         - win
           - bin/*.dll
           - lib/*.jar
       
2. merlin base dir should be on same directory level as Install4j data dir (relative paths are used).
3. Download all JavaFX distributions and copy the lib (bin) dirs to the javafx os dirs.
4. Setup-icon (didn't work to customize in Install4j, ugly hack):
   Replace ```/Applications/install4j.app/Contents/Resources/app/resource/macos/updater.icns```