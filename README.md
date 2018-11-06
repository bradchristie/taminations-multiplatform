# Taminations-multiplatform
Taminations code for Android and Web

To set up this project on your computer

1.  Install Git and download this project<br/>
    <code>git clone https://github.com/bradchristie/taminations-multiplatform</code>
    
2.  Install JetBrains IntelliJ IDEA and use it to open this project.  Ignore any notifications about Gradle or Python that pop up.

3.  Now add this specific Gradle task:  Select Run -> Edit Configurations.  Click '+' on the upper left of the dialog, Add New Configuration, and choose Gradle.  Now enter ```syncAll``` for the name, ```taminations-multiplatform:Taminations``` for the Gradle project, and ```syncAll``` for the target.

4.  Now build the Gradle target ```syncAll```.  Then build Taminations.  This builds both the web site and Android app.

5.  To view the web site, open ```Taminations/build/classes/kotlin/js/main/index.html```.