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

    // Perhaps this stuff should be in its own class/file called ScreenShot.
    // The idea is that any HTML element on a page should be able to be captured with a screenshot specified in the
    // encounters (input) file somehow.
    //
    // Already there are "objects", and arrays and elementsspecified in the "schema" for the input
    // files, like "registration", and "newPatietRegistration", and "demographics", and "firstName", and they'd have "addresses" or whatever
    // named "registration.newPatientRegistration.demographics.firstName".  This is the JSON equivalent in "properties" format, perhaps.
    // And yes, arrays work in that format.
    //
    // So, everything can be designated, elements, and objects, and arrays, if that's helpful for anything.
    //
    // The next question is therefore where you'd put this stuff in
    // the input file.  For objects, and arrays, or anything with an open brace or bracket, it could be the first thing, as in
    // {demographics { "screenshot": true}}, or perhaps {demographics { "screenshot": "firstName"}}, or whatever.
    // Seems that the easiest thing to do would be just add "screenshot": true after an open brace or bracket, and that would mean
    // "take a screenshot of this section just before saving the page, or just before leaving the section for the next section.
    // Or should it take the screenshot as soon as the page gets displayed?
    //
    // Or should it happen at the corrresponding point of processing
    // the page where the element is placed in the json file?  No, because it's not processed in a sequential order through the encounter
    // file.  Processing is done in the order of the elements on the page, pretty much.  And the JSON file is just a structure in memory
    // that is accessed as PeP runs through the page.  PeP is not directed by the JSON file.
    //
    // So, the JSON structure would have to include
    // a lot of new elements if we wanted to be able to specify a screenshot on any one element.
    //
    // On the other hand, maybe it could be done like "random" is done for each section, and maybe even each element could be augmented with
    // a "*, screenshot" value, like "firstName": "Fritz, screenshot".  Or perhaps each section could have a property "screenshot" with the
    // value of which elements to screenshot, as in "all", or "firstName, lastName, ssn"
    //
    // Being distracted as I am right now, it seems the simplest way to handle this would be to handle like "random" for a section, with
    // inheritance too, just like "random", and so for each section before leaving the processing for it a screenshot is taken.
    // Or perhaps instead of just "screenshot": true or "screenshot": false, have "screenshot": "start", or both start and end as with
    // "screenshot": "start, end", or "screenshot": "beforesave".
    //
    // The easiest would be "screenshot": true or false, just like "random": true.  And what that would mean, at least initially would be
    // "Take a screenshot of this section just before a save or leaving".
    //
    // Assuming we do that, can we actually take a screenshot of a section?  Maybe.  Looks like really there's only one kind of screenshot
    // available with Selenium, and that's for the entire page, so if you want a section of it you have to get the x/y of the center of
    // the element, like a section, and then gets it's length and width, and get it's bounding box and then cut it out of the whole page.
    // Not too pretty.
    //
    // An optional directory name to save the the screenshot image file in may be specified on command line or in properties file.
    // A default file name will be the class name plus timestamp, plus png.
    // An optional file name may be specified.
    // Do we want to make it full size first?
    // Should it always be full size when running headless?  Probably.
    // The default size should probably be full size too, and have an option on command line to resize.
    //
    // So this may be similar to -weps and -outdir maybe?  Or perhaps -logUrl


    // I believe Puppetteer has better support for screenshot, but here's the way they usually do it with Selenium:
    //public static void shoot(By elementToShoot, String dirName, String fileName) {
    public static String shoot(String fileName) {
        String shootDirName = Arguments.shootDir;
        if (shootDirName == null || shootDirName.isEmpty()) {
            shootDirName = "./";
        }
        //if (patient.registration.newPatientReg.demographics.shoot != null && patient.registration.newPatientReg.demographics.shoot.booleanValue() == true) {
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
//            if (!Arguments.quiet)  {
//                System.out.println("      Took screenshot: " + shootFileOutputPath.toString());
//            }
            logger.info("ScreenShot.shoot(), wrote file: " + shootFileOutputPath.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return shootFileOutputPath.toString();
    }

//    public void saveScreenshot(ITestResult result) throws IOException {
//        if (!result.isSuccess()) {
//            TakesScreenshot screenshot;
//            if (driver instanceof TakesScreenshot) {
//                screenshot = (TakesScreenshot) driver;
//            } else {
//                screenshot = (TakesScreenshot) new Augmenter().augment(driver);
//            }
//
//            File imageFile = screenshot.getScreenshotAs(OutputType.FILE);
//            String tName = result.getInstanceName();
//            String failureImageFileName =
//                    result.getMethod().getMethodName() + "-"
//                            + new SimpleDateFormat("MM-dd-yyyy_HH-ss").format(new GregorianCalendar().getTime())
//                            + ".png";
//            // imageFile.renameTo(new File(imageFile.getParentFile(), failureImageFileName));
//            FileUtils.moveFile(imageFile, new File("./report/screenshots/" + tName + "/"
//                    + failureImageFileName));
//        }
//    }
//
//
//
//
//    static void takeScreenShot(WebDriver driver) {
//        if (verbose) System.out.println("Taking screen shot...");
//        File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//        try {
//            FileUtils.copyFile(file, new File("C:\\temp\\headless_screenshot.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



//     @Test (priority = 0)
//    public void screenshotGetScreenShotAs() throws IOException {
//        //Take Screenshot of viewable area
//        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//        //Write Screenshot to a file
//        FileUtils.copyFile(scrFile, getSreenShotMethodImageFile);
//    }
//
//    /*@Test
//    public void screenshotWebElementAshot() throws IOException {
//        WebElement logo = driver.findElement(By.cssSelector(".nav-logo-link .nav-logo-base"));
//        //Take Screenshot
//        Screenshot elementScreenShot = new AShot().takeScreenshot(driver, logo);
//        //Write Screenshot to a file
//        ImageIO.write(elementScreenShot.getImage(),"PNG", webElementImageFile);
//    }*/
//
//    @Test (priority = 1)
//    public void screenshotWebElement2() throws IOException {
//        //Find the element
//        WebElement logo = driver.findElement(By.cssSelector(".nav-logo-link .nav-logo-base"));
//
//        // Get viewable area's screenshot
//        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//        BufferedImage fullImg = ImageIO.read(screenshot);
//
//        // Get the location of element on the page
//        Point point = logo.getLocation();
//
//        // Get width and height of the element
//        int eleWidth = logo.getSize().getWidth();
//        int eleHeight = logo.getSize().getHeight();
//
//        // Crop element from viewable area's screenshot to get element's screenshot
//        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(),
//                eleWidth, eleHeight);
//        ImageIO.write(eleScreenshot, "png", screenshot);
//
//        //Write Screenshot to a file
//        FileUtils.copyFile(screenshot, webElementImageFile);
//    }
//
//
//    @Test (priority = 2)
//    public void screenshotEntirePageAshot() throws IOException {
//        //Take Screenshot of entire page by AShot
//        Screenshot entirePageScreenShot = new AShot().
//                shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
//        //Write Screenshot to a file
//        ImageIO.write(entirePageScreenShot.getImage(),"PNG", entirePageImageFile);
//    }
//



}
