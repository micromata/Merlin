# Merlin desktop app

This is a ready to run application including everything you need.

* Download from [Sourceforge](https://sourceforge.net/projects/merlinrunner/)

## For developers

Run ```gradle run```

#### IntelliJ

1. File -> Project Structure -> Modules -> Add lib directory of JavaFX: (```<path>/javafx-sdk-11/lib```).
2. Then in Run->Edit Configurations -> VM Options, put:
```--module-path="<path>/javafx-sdk-11/lib" --add-modules=javafx.controls```

