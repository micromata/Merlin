# Building a new release
1. Increase version in `build.gradle`.
2. `gradle clean test dist`
3. Run Install4j
   1. General settings -> Application Info -> Version __current version__
   2. Installer -> Auto-Update Options -> Base url for installers: https://sourceforge.net/projects/pforge/files/Merlin/0.9/
   3. Build project
4. `gradle postDist`
5. Testing
   1. Testing update from previous installation (also with current installation but with 'faked' old version).
   2. install, test web and java-code.
   3. Check installation: Assure that no javafx*.jar is in the lib dir.
6. Creating Github release
   1. Create new tag, e. g. `v0.9`.
7. Upload to SourceForge
   1. Publish to new Folder 0.9
      1. `Install4j/Merlin/merlin_macos_v_9.dmg` (mark as default for MacOS)
      2. `Install4j/Merlin/merlin_unix_0_9.sh` (mark as default for Linux)
      3. `Install4j/Merlin/merlin_windows_x64_0_9.exe` (mark as default for Windows)
      4. `merlin-server/build/distributions/merlin-server-0.9.zip` (mark as default for all others)
   2. Testing
8. Enable update mechanism
   1. Commit and push.
9. Tag new Release.


# For preparing Install4j
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