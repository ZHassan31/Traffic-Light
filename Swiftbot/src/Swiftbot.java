import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import swiftbot.Button;
import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;
public class Swiftbot{

	static SwiftBotAPI API = new SwiftBotAPI();
	//while loop variables
	static boolean condition = true;
	static boolean valid = false;
	//RGB values variables
	static int red = 0;
	static int blue = 0;
	static int green = 0;
	// counter for each traffic light colour detected
	static int redCounters = 0;
	static int greenCounters = 0;
	static int blueCounters = 0;
	static int trafficCounters = 0;
	// total duration of program
	static long startTime = 0;
	static long endTime = 0;
	// default wheel velocity
	static int leftWheelVelocity = 25;
    static int rightWheelVelocity = 25;
    // default button light duration
    static int buttonLight = 1500;
    // time variables 
    static int minutes = 0;
    static int seconds = 0;
    // turbo mode counters
    static int turboModeOnCounters = 0;
    static int turboModeOffCounters = 0;
	
    public static void main(String[] args) throws InterruptedException {
    	startTime = System.nanoTime();
    	        String asciiArt = 
    	            "████████╗██████╗░░█████╗░███████╗███████╗██╗░█████╗░  ██╗░░░░░██╗░██████╗░██╗░░██╗████████╗\n" +
    	            "╚══██╔══╝██╔══██╗██╔══██╗██╔════╝██╔════╝██║██╔══██╗  ██║░░░░░██║██╔════╝░██║░░██║╚══██╔══╝\n" +
    	            "░░░██║░░░██████╔╝███████║█████╗░░█████╗░░██║██║░░╚═╝  ██║░░░░░██║██║░░██╗░███████║░░░██║░░░\n" +
    	            "░░░██║░░░██╔══██╗██╔══██║██╔══╝░░██╔══╝░░██║██║░░██╗  ██║░░░░░██║██║░░╚██╗██╔══██║░░░██║░░░\n" +
    	            "░░░██║░░░██║░░██║██║░░██║██║░░░░░██║░░░░░██║╚█████╔╝  ███████╗██║╚██████╔╝██║░░██║░░░██║░░░\n" +
    	            "░░░╚═╝░░░╚═╝░░╚═╝╚═╝░░╚═╝╚═╝░░░░░╚═╝░░░░░╚═╝░╚════╝░  ╚══════╝╚═╝░╚═════╝░╚═╝░░╚═╝░░░╚═╝░░░";
    	          
    	        System.out.println(asciiArt);

    	System.out.println("========================================================================");
    	System.out.println("========================================================================");
    	System.out.println("Hello and welcome to the traffic light task");
		System.out.println("Press A button to start the Swiftbot");
		System.out.println("Press X button at any time to terminate the Swiftbot");
		System.out.println("Note: you can press Y to enter Turbo Mode \n This increases the initial speed of the swiftbot");
		System.out.println("Note: you can press B to exit Turbo Mode \n This returnes the swiftbot to initial  speed");
			
		API.enableButton(Button.A, () -> {
			turnOnButtonLight(Button.A);	
			if (valid==false) {
				trafficLightDetection();
			} else {
				System.out.println("Invalid input: "
							+ "\nPress 'Y' Button for Yes or 'X' button for No");
				}
		});
		
		API.enableButton(Button.X, () -> {
			turnOnButtonLight(Button.X);	
	    	if(valid==false) {
		    	exitProgram();
			} else {
		    	try {
					writeToFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		API.enableButton(Button.Y, () -> {
			turnOnButtonLight(Button.Y);
			if(valid==false) {
				turboModeOn();
			} else {
		    	printInfoToScreen();
				System.exit(0);
			}
		});
		API.enableButton(Button.B, () -> {
			turnOnButtonLight(Button.B);
			if (valid==false) {
				turboModeOff();
			} else {
				System.out.println("Invalid input: "
						+ "\nPress 'Y' Button for Yes or 'X' button for No");
			}
		});
		

	}
    

    public static void turnOnButtonLight(Button button) {
        API.setButtonLight(button, true);

        try {
            Thread.sleep(buttonLight);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        API.setButtonLight(button, false);
    }
	// Method for traffic light detection
    public static void trafficLightDetection() {
		int[] LightUpYellow = { 255, 0, 255 };
		API.fillUnderlights(LightUpYellow);
		
		while (condition == true) {
		API.startMove(leftWheelVelocity, rightWheelVelocity);
		double distance = API.useUltrasound();
		
		// when the SwiftBot is within 20 cm of traffic light, it uses the camera to find RGB values
		if (distance < 20.00) {
			API.stopMove();
			BufferedImage img = API.takeStill(ImageSize.SQUARE_720x720);
            if(img == null){
                System.out.println("ERROR: Image is null");
                System.exit(5);
            }
					
         // Loop through each pixel in the image's width
            for (int x = 0; x < img.getWidth(); ++x) {
                // Loop through each pixel in the image's height
                for (int y = 0; y < img.getHeight(); ++y) {
                    // Get the RGB value of the pixel at coordinates (y, x)
                    int p = img.getRGB(y, x);
                    
                    // Create a Colour object using the RGB value
                    Color c = new Color(p);

                    // Extract the red, green, and blue values from the Colour object
                    int r = c.getRed();    // red value of the pixel
                    int g = c.getGreen();  // green value of the pixel
                    int b = c.getBlue();   // blue value of the pixel		
					
                    red += r;
					green += g;
					blue += b;

				}
			}

			// Calculate the average RGB value
			int average = img.getWidth() * img.getHeight();
			red /= average;
			blue /= average;
			green /= average;
			trafficDecision();

		 }
	  }

	}

    // Method to make a decision based on traffic light colours	
    public static void trafficDecision() {
		if ((red > blue) && (red > green)) {
			try {
				trafficLightRed();
				redCounters++;
				trafficCounters++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if ((blue > red) && (blue > green)) {
			try {
				trafficLightBlue();
				blueCounters++;
				trafficCounters++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if ((green > red) && (green > blue)) {
			try {
				trafficLightGreen();
				greenCounters++;
				trafficCounters++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
    // Method for handling green traffic light
	public static void trafficLightGreen() throws InterruptedException {
		try {
			int[] LightUpGreen = { 0, 0, 255 };
			API.fillUnderlights(LightUpGreen);
			API.move(100, 100, 1500); // requirement: pass the traffic in two seconds
			API.stopMove();
			Thread.sleep(500);
			int[] LightUpYellow = { 255, 0, 255 };
			API.fillUnderlights(LightUpYellow);
		} catch (Exception e) {
			System.out.println("ERROR: green traffic light");
			e.printStackTrace();
			System.exit(5);
		}

	}
	
	// Method for handling red traffic light
	public static void trafficLightRed() throws InterruptedException {
		try {
			int[] LightUpRed = { 255, 0, 0 };
			API.fillUnderlights(LightUpRed);
			API.stopMove(); // requirement: stop the SwiftBot for half a second
			Thread.sleep(500);
		} catch (Exception e) {
			System.out.println("ERROR: red traffic light");
			e.printStackTrace();
			System.exit(5);
		}

	}
	// Method for handling value traffic light
	public static void trafficLightBlue() throws InterruptedException {

		try {
			int[] LightUpBlue = { 0, 255, 0 }; // blink blue lights for 0.5 seconds
			API.fillUnderlights(LightUpBlue);
			Thread.sleep(500);
			API.disableUnderlights();
			API.move(10, 45, 1000);
			Thread.sleep(1000);
			API.move(25, 25, 750); // requirement: move SwiftBot move at a low speed for 1 second and stop
			Thread.sleep(1000);
			API.stopMove();
			Thread.sleep(500);
			API.move(-100, -20, 900);
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("ERROR: blue traffic light");
			e.printStackTrace();
			System.exit(5);
		}

	}
	// Method to exit the program
	public static void exitProgram() {		
    	endTime = System.nanoTime();
		//close while loop enabling SwiftBot movement
		condition = false;
		//Enables the else functionalities of the buttons
		valid = true;
		// disable all SwiftBot functionalities before terminating the program
		API.stopMove();
		API.disableUnderlights();
		
		System.out.println("========================================================================");
		System.out.println("========================================================================");
		System.out.println("Would you like to see the log of execution before terminating the program: "
				+ "\nPress 'Y' Button for Yes or 'X' button for No");
    }
    
	// Method to print information to the screen
	public static void printInfoToScreen() {
		System.out.println("====================================================");
		System.out.println("========================================================================");
		System.out.println("Number of times the Swiftbot encountered a traffic light: " +trafficCounters);
		System.out.println("Most frequent light colour encountered is: " +frequentTraffic());
		System.out.println("Number of times the most light traffic light encountered is: " +numberFrequentTraffic());
		System.out.println("Total duration of execution is: " +formatDuration());
		System.out.println("Number of times turbo mode was turn on: "+turboModeOnCounters);
		System.out.println("Number of times turbo mode was turn off: "+turboModeOffCounters);
		System.out.println("The Swiftbot has now been terminated");
		System.out.println("Thanks for your time and have a great Day :)");
	}
	// Method to write information to a file
	public static void writeToFile() throws IOException {
		   FileWriter writehandle = new FileWriter("file.txt");
		   BufferedWriter bw = new BufferedWriter(writehandle);
			bw.write("Number of times the Swiftbot encountered a traffic light: " +trafficCounters);
			bw.write("\nMost frequent light colour encountered is: " +frequentTraffic());
			bw.write("\nNumber of times the most light traffic light encountered is: " +numberFrequentTraffic());
			bw.write("\nTotal duration of execution is : " +formatDuration());
			bw.write("\nNumber of times turbo mode was turn on: "+turboModeOnCounters);
			bw.write("\nNumber of times turbo mode was turn off: "+turboModeOffCounters);
			bw.write("\nThe Swiftbot has now been termianted");
			bw.write("\nThanks for your time and have a great Day :)");
		   bw.close();
		   writehandle.close();
	}
	// Method to format the duration of execution
	public static String formatDuration(){
	    int seconds = (int) ((endTime - startTime) / 1000000000);

	    int minutes = seconds / 60;
	    seconds = seconds % 60;

	    return minutes + " minutes " + seconds + " sec";
	}
	// Method to get the number of the most frequent traffic light colour
	public static Integer numberFrequentTraffic() {
		if ((greenCounters>redCounters)&&(greenCounters>blueCounters)) {
			return greenCounters;
			
		}else if ((redCounters>greenCounters)&&(redCounters>blueCounters)) {
			return redCounters;
			
		}else if ((blueCounters>greenCounters)&&(blueCounters>redCounters)) {
			return blueCounters;
			
		//in the event some of the values are equal
		} else if ((greenCounters==blueCounters)&&((greenCounters>redCounters)&&(blueCounters>redCounters))) {
			return greenCounters;//only choose one instead of adding both
			
		}else if ((greenCounters==redCounters)&&((greenCounters>blueCounters)&&(redCounters>blueCounters))) {
				return greenCounters;
				
		}else if ((blueCounters==redCounters)&&((blueCounters>greenCounters)&&(redCounters>greenCounters))) {
			return blueCounters;
			
		}else if ((blueCounters==0) && (redCounters==0) && (greenCounters==0)) {
			return blueCounters + greenCounters + redCounters;
			
		} else {
			return blueCounters; //if all values are the same
		}
	}
	// Method to get the most frequent traffic light colour
	public static String frequentTraffic() {
		if ((greenCounters>redCounters)&&(greenCounters>blueCounters)) {
			return "green";
			
		}else if ((redCounters>greenCounters)&&(redCounters>blueCounters)) {
			return "red";
			
		}else if ((blueCounters>greenCounters)&&(blueCounters>redCounters)) {
			return "blue";
			
		//incase some of the values are equal
		} else if ((greenCounters==blueCounters)&&((greenCounters>redCounters)&&(blueCounters>redCounters))) {
			return "equal number of green & blue";
			
		}else if ((greenCounters==redCounters)&&((greenCounters>blueCounters)&&(redCounters>blueCounters))) {
				return "equal number of green & red";
				
		}else if ((blueCounters==redCounters)&&((blueCounters>greenCounters)&&(redCounters>greenCounters))) {
			return "equal number of blue & red";
			
		}else if ((blueCounters==0) && (redCounters==0) && (greenCounters==0)) {
			return "No Traffic light detected";
			
		}
		else {
			return "equal number of green & blue & red"; // if all values are the same
		}
	}
	// Method to turn off Turbo mode
	public static void turboModeOff() {
		turboModeOnCounters++;
		API.setButtonLight(Button.B,true);
		try {
			Thread.sleep(buttonLight);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		API.setButtonLight(Button.B,false);
		API.stopMove();
		
		System.out.println("Turbo mode off");
		leftWheelVelocity = 25;
		rightWheelVelocity = 25;
	}
	// Method to turn on Turbo mode
	public static void turboModeOn(){
		turboModeOffCounters++;
		API.setButtonLight(Button.Y,true);
		try {
			Thread.sleep(buttonLight);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		API.setButtonLight(Button.Y,false);
		API.stopMove();
		
		System.out.println("Turbo mode on");
		leftWheelVelocity = 75;
		rightWheelVelocity = 75;
		
	}
}
