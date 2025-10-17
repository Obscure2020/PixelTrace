import java.io.*;
import java.nio.file.*;

public class ObscurePrint {
    private BufferedWriter bw;
    private final String indentStr;
    private int indentAmount = 0;
    private String indent = "";
    private boolean startOfLine = true;

    public ObscurePrint(Path output, String indent) throws IOException{
        bw = Files.newBufferedWriter(output); //UTF-8 implied by unspecified parameter.
        indentStr = indent;
    }

    private void updateIndent(){
        indent = indentStr.repeat(indentAmount);
    }

    public void moreIndent(){
        if(indentAmount == Integer.MAX_VALUE) return;
        indentAmount++;
        updateIndent();
    }

    public void lessIndent(){
        if(indentAmount == 0) return;
        indentAmount--;
        updateIndent();
    }

    private void checkClosed() throws IOException{
        if(bw == null) throw new IOException("Attempting to write to a closed ObscurePrint.");
    }

    public void print(String input) throws IOException{
        checkClosed();
        if(startOfLine) bw.write(indent);
        bw.write(input);
        startOfLine = input.charAt(input.length()-1) == '\n';
    }

    public void print(int input) throws IOException{
        print(Integer.toString(input));
    }

    public void println(String input) throws IOException{
        checkClosed();
        if(startOfLine) bw.write(indent);
        bw.write(input);
        bw.write('\n');
        startOfLine = true;
    }

    public void println(int input) throws IOException{
        println(Integer.toString(input));
    }

    public void close() throws IOException{
        bw.close();
        bw = null;
    }
}