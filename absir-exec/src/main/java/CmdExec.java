import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by absir on 23/8/17.
 */
public class CmdExec {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("CmdExec => " + Arrays.toString(args));
        if (args.length > 0) {
            Process process = Runtime.getRuntime().exec(args);
            executeProcess(process);
        }

        System.out.println("CmdExec complete");
    }

    public static boolean executeProcess(Process process) {
        boolean success = true;
        BufferedReader input = null;
        BufferedReader inputError = null;
        try {
            String line;
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            inputError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = input.readLine()) != null) {
                System.err.println(inputError);
                success = false;
            }

            try {
                if (process.waitFor() != 0) {
                    success = false;
                }

            } catch (InterruptedException e) {
            }

        } catch (Exception e) {
            e.printStackTrace();
            success = false;

        } finally {
            if (process != null) {
                process.destroy();
            }

            if (input != null) {
                try {
                    input.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputError != null) {
                try {
                    inputError.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return success;
    }

}
