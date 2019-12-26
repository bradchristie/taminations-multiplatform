# Taminations-multiplatform
Taminations code for Android and Web

To set up this project on your computer

1.  Install Git and download this project<br/>
    <code>build</code>.
    
2.  Install JetBrains IntelliJ IDEA Community Edition and use it to open this project.  When a notification about Gradle pops up, click on `Import Gradle Project`.  On the next dialog, select `Use gradle 'wrapper' task configuration` and Ok.  Ignore any notifications about Python that pop up.

3.  Select `Run -> Edit Configurations`.  Click on the upper-left `+` in the next dialog and select Gradle.  Then fill in Name: `Build Taminations`, Gradle project: `taminations-multiplatform`, Tasks: `build` and then Ok.  

4.  Now run the Gradle target `Build Taminations`.  This builds both the web site and Android app.

5.  To view the web site, open ```Taminations/src/jsmain/web/index.html```.

6.  To run the Android app, select the `Taminations` target and run it.