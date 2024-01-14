import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import swiftbot.Button;
import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;

public class Swiftbot {

	static SwiftBotAPI API = new SwiftBotAPI();
	
	public static void main(String[] args) throws InterruptedException {
		API.enableButton(Button.A, () -> {
			//System.out.println("Button A has been pressed");
			// set the swiftbot to yellow and start moving
			int[] LightUpYellow = {255,0,255};
			API.fillUnderlights(LightUpYellow);
			double distance = API.useUltrasound();
			API.startMove(50,50);
			
			// when the swiftbot is within 20 cm of traffic light, the loop begins and  uses the camera to find RGB values
			while (distance>20.00) {
				API.stopMove();
				//calculate the RGB values of each pixel
				BufferedImage img = API.takeStill(ImageSize.SQUARE_48x48);

				int red = 0;
				int blue = 0;
				int green = 0;

				for (int x = 0; x < img.getWidth(); ++x) {
					for (int y = 0; y < img.getHeight(); ++y) {
						int p = img.getRGB(y, x);
						Color c = new Color(p); // color from the pixels

						int r = c.getRed(); // red value of all the pixel
						int g = c.getGreen();// green value of all the pixel
						int b = c.getBlue();// blue value of all the pixel

						red += r;
						green += g;
						blue += b;

					}
				}
				
				//Calculate the average RGB value 
				int average = img.getWidth() * img.getHeight();
				red = red / average;
				blue = blue / average;
				green = green / average;
				
				// calling method to perfrom action at traffic light
				if ((red > blue) && (red>green)) {
					try {
						TrafficLightRed();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if ((blue > red)&&(blue > green)) {
					try {
						TrafficLightBlue();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if ((green > red)&&(green>blue)) {
					try {
						TrafficLightBlue();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// exit loop
				int breakcount = 0;
				API.enableButton(Button.X, () -> {
				System.out.println("Button x has been pressed");
				breakcount++;
				
				if (breakcount > 0) {
					break;
				}

			});
			}
		});
		

		

	}
	
	public static void TrafficLightGreen() throws InterruptedException {
		try {
		int[] LightUpGreen = {0,0,255};
		API.fillUnderlights(LightUpGreen);
		API.move(100,100,3000);
		API.stopMove();
		Thread.sleep(500);
		int[] LightUpYellow = {255,0,255};
		API.fillUnderlights(LightUpYellow);
		Thread.sleep(3000);
		API.disableUnderlights();
		API.startMove(100, 100);
		}catch(Exception e){
            System.out.println("ERROR:");
            e.printStackTrace();
            System.exit(5);
        }



		
	}
	
	public static void TrafficLightRed() throws InterruptedException {
		try {
		int[] LightUpRed = {255,0,0};
		API.fillUnderlights(LightUpRed);
		API.stopMove();
		Thread.sleep(500);
		//API.startMove(100,100);
		}catch(Exception e){
            System.out.println("ERROR:");
            e.printStackTrace();
            System.exit(5);
        }



		
	}
	
	public static void TrafficLightBlue() throws InterruptedException {
		try {
		int[] LightUpBlue = {0,255,0}; //blink blue lights for 0.5 seconds
		API.fillUnderlights(LightUpBlue);
		Thread.sleep(500);
		API.disableUnderlights();
		
		API.move(10,45,1000);
		Thread.sleep(1000);
		API.move(25,25,500);
		Thread.sleep(1000);
		API.move(-100,-20,900);
		Thread.sleep(1000);
		}catch(Exception e){
            System.out.println("ERROR:");
            e.printStackTrace();
            System.exit(5);
        }



		
	}

//	public static void TrafficLightDeteSystem() {
//		// find the RGB values
//		BufferedImage img = API.takeStill(ImageSize.SQUARE_48x48);
//
//		int red = 0;
//		int blue = 0;
//		int green = 0;
//
//		for (int x = 0; x < img.getWidth(); ++x) {
//			for (int y = 0; y < img.getHeight(); ++y) {
//				int p = img.getRGB(y, x);
//				Color c = new Color(p); // color from the pixels
//
//				int r = c.getRed(); // red value of the pixel
//				int g = c.getGreen();
//				int b = c.getBlue();
//
//				red += r;
//				green += g;
//				blue += b;
//
//			}
//		}
//		
//		//average
//		int average = img.getWidth() * img.getHeight();
//		red = red / average;
//		blue = blue / average;
//		green = green / average;
//
//		System.out.println("average rgb values " + red + " " + green + " " + blue);
//	}

}
