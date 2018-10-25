# Building a new release
1. `cd merlin-installer`
2. `ant pre-dist`
3. Run Install4j
   1. General settings -> Application Info -> Version __current version__
   2. Installer -> Auto-Update Options -> Base url for installers: https://sourceforge.net/projects/pforge/files/Merlin/0.3/
   3. Build project
4. `ant post-dist`
5. Upload updates.xml to SourceForge download dir.
6. Testing
   1. Testing update from previous installation.
   2. install, test web and java-code.
7. Creating Github release
   1. Create new tag, e. g. `v0.3`.
8. Upload to SourceForge
   1. Publish `v0.3/merlin_macos_v0.3.dmg`.
   2. Testing
9. Enable update mechanism
   1. Commit and push.
