package jredfox.selfcmd.jconsole;

import java.io.IOException;
import java.io.OutputStream;
 
import javax.swing.JTextArea;
 
public class Output extends OutputStream {
    private JTextArea txt;
     
    public Output(JTextArea textArea) 
    {
        this.txt = textArea;
    }
     
    @Override
    public void write(int b) throws IOException 
    {
        // redirects data to the text area
    	txt.append(String.valueOf((char)b));
        if(b == '\n')
        {
        	txt.setCaretPosition(txt.getDocument().getLength());
        	txt.update(txt.getGraphics());
        }
    }
}
