

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ThreadDetails {
    public static final Integer numberofthreads = 32;
    public static final Integer numberofpostsperthread = 1000;
    public static final AtomicInteger counter = new AtomicInteger(0);
    public static final Integer totalposts = 10000;
    public static final ArrayList<String[]> data = new ArrayList<>();
    public static final ArrayList<Post> list = new ArrayList<>();
    public static Long Walltime;
}

class Post extends Thread {
    @Override
    public void run() {
        for (int j = 1; j <= ThreadDetails.numberofpostsperthread; j++) {
            try {
                TestPost.testLiftRidePost();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class TestPost {
    public static void testLiftRidePost() throws Exception {
        int currentCount = ThreadDetails.counter.incrementAndGet();

        if (currentCount <= ThreadDetails.totalposts) {
            long begin = System.currentTimeMillis();
            CLient response = Client1.ClientTestPost(); 
            long end = System.currentTimeMillis();
            long duration = end - begin;

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date(begin);
            String strDate = formatter.format(date);

            String[] row = {Long.toString(duration), Integer.toString(response.responsecode), response.responsebody, strDate};

            synchronized (ThreadDetails.data) {
                ThreadDetails.data.add(row);
            }
        }
    }
}

public class Client2 {
    public static void main(String[] args) throws Exception {
        long beginTime = System.currentTimeMillis();

        // Header for the CSV file
        String[] heading = {"Duration (ms)", "Status Code", "Response Body", "Timestamp"};
        synchronized (ThreadDetails.data) {
            ThreadDetails.data.add(heading);
        }

        for (int i = 0; i < ThreadDetails.numberofthreads; i++) {
            Post thread = new Post();
            ThreadDetails.list.add(thread);
            thread.start();
        }

        for (Post thread : ThreadDetails.list) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        ThreadDetails.Walltime = endTime - beginTime;

        // Write all data to file at once to optimize I/O operations
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("data.csv"))) {
            synchronized (ThreadDetails.data) {
                for (String[] row : ThreadDetails.data) {
                    bufferedWriter.write(String.join(",", row));
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate statistics, starting from index 1 to skip header row
        long successfulRequests = 0;
        long unsuccessfulRequests = 0;
        for (int i = 1; i < ThreadDetails.data.size(); i++) {
            String[] dataRow = ThreadDetails.data.get(i);
            if (Integer.parseInt(dataRow[1]) >= 200 && Integer.parseInt(dataRow[1]) < 300) {
                successfulRequests++;
            } else {
                unsuccessfulRequests++;
            }
        }

        double totalRunTimeSeconds = ThreadDetails.Walltime / 1000.0;
        double throughput = (successfulRequests + unsuccessfulRequests) / totalRunTimeSeconds;

        // Print final statistics
        System.out.println("Number of successful requests: " + successfulRequests);
        System.out.println("Number of unsuccessful requests: " + unsuccessfulRequests);
        System.out.println("Total run time (wall time) in milliseconds: " + ThreadDetails.Walltime);
        System.out.println("Total throughput in requests per second: " + throughput);
        
        // 
        String csvFile = "data.csv";
        String line = "";
        String csvSeparator = ",";
        long sum = 0;
        List<Integer> durations = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            boolean firstRow = true;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            while ((line = br.readLine()) != null) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                String[] data = line.split(csvSeparator);

                sum += Integer.parseInt(data[0]);
                durations.add(Integer.parseInt(data[0]));

                LocalDateTime dateTime = LocalDateTime.parse(data[3], formatter);
                ZonedDateTime zonedDateTime = dateTime.atZone(ZoneOffset.UTC);
                timestamps.add(zonedDateTime.toInstant().toEpochMilli());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(durations);
        Collections.sort(timestamps);

        long startTimestamp = timestamps.get(0);
        long endTimestamp = timestamps.get(timestamps.size() - 1);
        long wallTime = endTimestamp - startTimestamp;
        System.out.println();
        System.out.println("Profiling Performance");
        System.out.println("Walltime: " + wallTime + "ms");
        float meanLatency = (float) sum / durations.size();
        System.out.println("Mean Latency: " + meanLatency + "ms");
        int medianLatency = durations.get(durations.size() / 2);
        System.out.println("Median Latency: " + medianLatency + "ms");
        float throughput1 = (float) durations.size() / (wallTime / 1000.0f);
        System.out.println("Throughput: " + throughput1 + " per sec");
        int p99Index = (int) Math.ceil(durations.size() * 0.99) - 1;
        int p99Latency = durations.get(p99Index);
        System.out.println("p99 Latency: " + p99Latency + "ms");
    }
}