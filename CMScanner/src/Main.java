import java.io.*;

/**
 * CMinus Scanner
 * Reads inputC- code and outputs what tokens it finds in the users input
 * based on cminus lexical conventions. Determines if the user wants to input
 * from cmd line or file. Outputs tokenized code line by line from cmd line
 * or as a block from file.
 * <p>
 * Created by Bobby on 2/14/2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        boolean inputFromCommand = true;
        //in case of input from file, filename
        String fileName = "FILE_NAME_NOT_SPECIFIED";
        //br input from command line
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //printwriter output to command line w/ autoflush
        PrintWriter out = new PrintWriter(System.out, true);
        out.println("Starting C- Scanner");
        out.println("For input from file use arg -i followed by the name of the desired input txt file.");
        //check for input from file argument
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-i")) {
                try {
                    br = new BufferedReader(new FileReader(fileName = args[i + 1]));
                    out.printf("FILE FOUND: %s\n", fileName);
                } catch (IndexOutOfBoundsException | FileNotFoundException e) {
                    e.printStackTrace();
                    out.printf("FILE NOT FOUND: %s\n", fileName);
                    System.exit(1);
                }
                inputFromCommand = false;
            }
        }
        if (!inputFromCommand) {
            out.printf("C- Scanner Started: Input from file \'%s\' \n", fileName);
        } else {
            out.println("\nC- Scanner Started: Input From Command Line");
            out.println("C- Scanner: Press CTRL + c To Exit From Command Line\n\n");
        }
        CMinusScanner CScanner = new CMinusScanner(br);
        //The Moment of Truth
        CMinusToken temp;
        while ((temp = CScanner.getTok()) != CMinusToken.ENDFILE) {
            out.printf("LINE-%d\t Token Type: %s \t Value: %s\n",CScanner.getLineno(),temp,CScanner.tokenString);
        }
    }
}