

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class CLient{

    int responsecode;
    String responsebody;
    public CLient(int responsecode, String responsebody) {
        this.responsecode = responsecode;
        this.responsebody = responsebody;
    }

}

public class Client1 {
    public static void main(String[] args) throws Exception {
        final int maxRetries = 5;
        int retries = 0;
        boolean requestSuccessful = false;
        CLient response = ClientTestPost();

        while (retries < maxRetries && !requestSuccessful) {
            if (response.responsecode / 100 == 5) { // Retry only for 5XX server errors
                retries++;
                System.out.println("Attempt " + retries + ": Server error, retrying...");
                // Exponential backoff (e.g., 2^retries * 100 milliseconds)
                Thread.sleep((long) Math.pow(2, retries) * 100);
                response = ClientTestPost();
                if (response.responsecode / 100 != 5) {
                    requestSuccessful = true;
                }
            } else if (response.responsecode / 100 == 4) {
                System.out.println("Client error, not retrying.");
                break; // Break on client error
            } else {
                System.out.println("Request successful.");
                requestSuccessful = true;
            }
        }

        if (!requestSuccessful) {
            System.out.println("Server might be down, all retries failed.");
        }
    }


    

	static CLient ClientTestPost() throws IOException, InterruptedException
    {

	    Integer minskierID=1;
	    Integer maxskierID=100000;   // Dummy data generation

	    Random random = new Random();
	    Integer skierID=random.nextInt(maxskierID-minskierID+1)+minskierID;

	    Integer minresortID=1;
	    Integer maxresortID=10;

	    
	    Integer resortID=random.nextInt(maxresortID-minresortID+1)+minresortID;
	    //Integer resortID=-1;
	    Integer minliftID=1;
	    Integer maxliftID=40;

	    
	    Integer liftID=random.nextInt(maxliftID-minliftID+1)+minliftID;

	   // Integer liftID=-1;
	            
	    Integer mintime=1;
	    Integer maxtime=360;

	    
	    Integer time=random.nextInt(maxtime-mintime+1)+mintime;


	    String jsonPayload = String.format("{\"time\": %d, \"liftID\": %d}", time,liftID);

	    HttpClient httpClient = HttpClient.newHttpClient();


	    String seasonID = "2024";
	    String dayID = "1";

	    URI uri = URI.create(String.format("http://54.152.66.110:8080/skiers/%d/seasons/%s/days/%s/skiers/%d", resortID,seasonID,dayID,skierID));

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(uri)
	            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
	            .header("Content-Type", "application/json")
	            .build();

	    HttpResponse<String> response;
        
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
	        String responseBody = response.body();
           // System.out.println("Status code: " + statusCode + "Response body: " + responseBody);
            CLient r1 = new CLient(statusCode, responseBody);
            return r1;

        
        
            
            
       
        
        	    
    }
        
    }