import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Downloader{
	
    public static String extensions = "mp3";

    public static String DownlodFolder = "/Users/rarumalla/Telugu Songs/";

    private static final int BUFFER_SIZE = 4096;

    private static Set<String> finalMovieSet = new HashSet();

    private static Set<String> iSongsMovieLinks = new HashSet();
    
    
	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!");

		/*
		 * System.setProperty("webdriver.chrome.driver",
		 * "/Users/rarumalla/chromedriver"); WebDriver driver = new ChromeDriver();
		 */
		// getMovieLinksfromIdleBrain(driver,
		// "http://www.idlebrain.com/movie/archive/index.html");

		File file = new File("/Users/rarumalla/indexMoviesLink.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		int i = 0;
		int threads=0;
		String st;
		while ((st = br.readLine()) != null) {
			i++;
				threads=java.lang.Thread.activeCount();
				if(threads>100) {
					Thread.sleep(60000);
				}
				System.out.println("   "+i+"    "+st);
				getSongLinksFromMovie(st);
				
			}
		System.out.println("Completed...");
	}
	
	private static void getSongName(String songURL) throws Exception {
	        String strArray[] = songURL.split("/");
	        String movieName = strArray[strArray.length - 2].replaceAll("%20", " ").replaceAll("%29", "")
	                .replaceAll("%5B", "").replaceAll("%5D", "").replaceAll("%28", "");
	        String songName = strArray[strArray.length - 1].replaceAll("%20", " ").replaceAll("%29", "")
	                .replaceAll("%5B", "").replaceAll("%5D", "").replaceAll("iSongs.info", "")
	                .trim();
	      //  System.out.println(movieName + "    " + songName);
	        File f = new File(DownlodFolder + movieName + "/");
	        f.mkdir();
	        File checkFile=new File(DownlodFolder + movieName + "/"+songName);
	        if(!checkFile.exists()) {
	        	new DownloaderThread(songURL, DownlodFolder + movieName, songName).start();
	        }
	        

	    }
	
	public static void getSongLinksFromMovie(String url) throws Exception {

        String ps = getURLSource(url.replace(" ", ""));
        String regex = "<a href=(\"[^\"]*\")[^<]*</a>";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ps);
        String songURL;
        while (m.find()) {
            String finalURL = m.group().toString();
            if (finalURL.contains(extensions)) {
                // System.out.println(finalURL);
                songURL = finalURL.substring(finalURL.indexOf("=") + 2, finalURL.length()).substring(0,
                        finalURL.indexOf(".mp3") - 5);
              //  System.out.println(songURL);
                getSongName(songURL);
            }
        }
        // System.out.println(m.replaceAll("$1"));

    }
	
	
	public static void searchISongsWebsite(WebDriver driver, String movieName) throws Exception {
		
		// Storing the Application Url in the String variable
				String url = "https://isongs.info/search+te/?q="+movieName.replace(" ", "+");
		 
				//Launch the ToolsQA WebSite
				driver.get(url);
		 
				
				Thread.sleep(1000);
				
				List<WebElement> elements = driver.findElements(By.cssSelector("div[class*=gs-visibleUrl-long]"));
				
				for (int i = 0; i < elements.size(); i++) {
					WebElement element=elements.get(i);
					if(element.getText().contains("html") && element.getText().contains("naasongs.com")) {
						if(!iSongsMovieLinks.contains(element.getText())) {
							iSongsMovieLinks.add(element.getText());
							System.out.println(element.getText());
						}
						
					}
				}
				
				BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rarumalla/indexMoviesLink.txt"));
		        for(String mName : iSongsMovieLinks) {
		        	 writer.write(mName);
		        	 writer.write("\n");
		        }

		        writer.close();
				
				
				
	}
	
	
	public static void getMovieLinksfromIdleBrain(WebDriver driver,String url) throws Exception {

        String ps = getURLSource(url);
        String regex = "<a href=(\"[^\"]*\")[^<]*</a>";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ps);
        String songURL;
        while (m.find()) {
            String finalURL = m.group().toString();
            if (!finalMovieSet.contains(finalURL)) {
                // System.out.println(finalURL);
                if (finalURL.indexOf(".html") != -1 && finalURL.indexOf("</a>") != -1) {
                    // System.out.println(finalURL.indexOf(".html") + " " +
                    // finalURL.indexOf("</a>"));
                    songURL = finalURL.substring(finalURL.indexOf(".html") + 7, finalURL.indexOf("</a>")).trim();
                    finalMovieSet.add(songURL);
                    searchISongsWebsite(driver,songURL);
                }
            }
        }

        finalMovieSet.forEach(System.out::println);
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rarumalla/indexMovies.txt"));
        for(String mName : finalMovieSet) {
        	 writer.write(mName);
        	 writer.write("\n");
        }

        writer.close();
    }

    public static String getURLSource(String url) throws Exception {
        URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        return toString(urlConnection.getInputStream());
    }

    private static String toString(InputStream inputStream) throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }
    
    
}

class DownloaderThread implements Runnable {
    private Thread t;
    private String songURL;
    private String movieDir;
    private String songName;
    private static final int BUFFER_SIZE = 4096;

    DownloaderThread(String songURL, String movieDir, String songName) {
        this.songURL = songURL;
        this.movieDir = movieDir;
        this.songName = songName;
    }

    public void run() {
        try {
            downloadFile(songURL, movieDir, songName);
            t.stop();
        } catch (Exception e) {
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, songURL);
            t.start();
        }
    }

    public static void downloadFile(String fileURL, String saveDir, String songName) throws Exception {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        int responseCode = httpConn.getResponseCode();
    //    System.out.println(responseCode+"   "+saveDir);
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            // System.out.println("Content-Type = " + contentType);
            // System.out.println("Content-Disposition = " + disposition);
            // System.out.println("Content-Length = " + contentLength);
            // System.out.println("fileName = " + songName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + songName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("Song downloaded: " + songName + " from Movie: " + saveDir);
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
}