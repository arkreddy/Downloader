package com.rarumalla.youtube;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.axet.vget.VGet;

public class Downloader {
	
	private static Set<String> finalMovieSet = new HashSet();
	
	private static String path = "/Users/rarumalla/videos";

    public static void main(String[] args) {
        try {
          
            getVideoLinksfromYoutubeChannel("sakshinews");
            
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
	public static void getVideoLinksfromYoutubeChannel(String channelName) throws Exception {

        String ps = getURLSource("https://www.youtube.com/user/"+channelName+"/videos");
        String regex = "<a href=\"[^\"]*?watch.*?\">.*?</a>";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ps);
        String songURL;
        while (m.find()) {
            String finalURL = m.group().toString();
            System.out.println(finalURL);
            if (!finalMovieSet.contains(finalURL) && !finalURL.contains("www.youtube.com")) {

                    songURL = finalURL.substring(finalURL.indexOf("watch?v=")+8, finalURL.indexOf(" class=")-1).trim();
                    finalMovieSet.add(songURL);
                    
            }
        }
        
        Iterator<String> it = finalMovieSet.iterator();
        while(it.hasNext()){
        	new DownloaderThread("https://www.youtube.com/watch?v="+it.next(),path).start();
        }

        finalMovieSet.forEach(System.out::println);
       
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
    private String url;
    private String path;

    DownloaderThread(String url, String path) {
        this.url = url;
        this.path = path;
    }

    public void run() {
        try {
            downloadFile(url, path);
        } catch (Exception e) {
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, url);
            t.start();
        }
    }

    public static void downloadFile(String url, String path) throws Exception {
        
        VGet v = new VGet(new URL(url), new File(path));
        v.download();
    	
    }

}
