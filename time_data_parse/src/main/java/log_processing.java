import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class log_processing {

    private int total_count;
    private long total_time_TJ;
    private long total_time_TS;
    private long avg_time_TJ;
    private long avg_time_TS;

    public log_processing(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            total_count = 0;
            total_time_TJ = 0;
            total_time_TS = 0;
            String line;
            Long currTJ, currTS;
            while ((line = reader.readLine()) != null) {
                if (!line.equals("")) {
                    total_count++;
                    String[] data = line.split(",");
                    currTJ = Long.parseLong(data[1]);
                    currTS = Long.parseLong(data[2]);
                    total_time_TJ += currTJ;
                    total_time_TS += currTS;
                }
            }
            avg_time_TJ = total_time_TJ /((long)total_count);
            avg_time_TS = total_time_TS /((long)total_count);
            reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCount() { return total_count; }

    public long getAvg_time_TJ() { return avg_time_TJ; }

    public long getAvg_time_TS() { return avg_time_TS; }

    public static void main(String [] args) {
        log_processing lp = new log_processing("time_data.txt");
        System.out.println("\nReport:\n\tTotal count: " + lp.getCount() + "\n\tAverage TJ: " + lp.getAvg_time_TJ() + "\n\tAverage TS: " + lp.getAvg_time_TS() + "\n");
    }

}
