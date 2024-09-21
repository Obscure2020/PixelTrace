import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class PrintSVG {
    private BufferedWriter bw;

    private final String indentStr;
    private int indentAmount = 0;
    private String indent = "";

    private boolean startOfLine = true;


    public PrintSVG(File output, String indent) throws IOException{
        bw = Files.newBufferedWriter(output.toPath(), StandardCharsets.UTF_8);
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

    private void checkClosed() throws IOException{
        if(bw == null) throw new IOException("Attempting to write to a closed PrintSVG.");
    }

    public void println(int input) throws IOException{
        println(Integer.toString(input));
    }

    public void close() throws IOException{
        bw.close();
        bw = null;
    }
}