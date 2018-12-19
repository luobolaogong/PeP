package pep.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import static pep.utilities.Driver.driver;

public class ScreenShot {
    private static Logger logger = Logger.getLogger(ScreenShot.class.getName());

    // I believe Puppetteer has better support for screenshot, but here's the way they usually do it with Selenium:
    //public static void shoot(By elementToShoot, String dirName, String fileName) {
    public static String shoot(String fileName) {
        String shootDirName = Arguments.shootDir;
        if (shootDirName == null || shootDirName.isEmpty()) {
            shootDirName = "./";
        }
        // Don't do the following unless there's something to write
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(fileName);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        String hhMmSs = simpleDateFormat.format(new Date());
        stringBuffer.append(hhMmSs);
        stringBuffer.append(".png");
        String shootFileName = stringBuffer.toString();

        File shootFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path shootFileOutputPath = Paths.get(shootDirName, shootFileName);
        try {
            Files.copy(shootFile.toPath(), shootFileOutputPath);
        } catch (Exception e) {
            logger.severe("ScreenShot.shoot(), exception caught while trying to do a Files.copy(): " + shootFile.toPath() + " and " + shootFileOutputPath + " e: " + e.getMessage());
        }
        return shootFileOutputPath.toString();
    }


}
