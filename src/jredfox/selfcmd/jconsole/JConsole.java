package jredfox.selfcmd.jconsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.Document;

/**
 * @author jredfox
 * @credit micah_laster for making most of the gui
 */
public abstract class JConsole {
	
	public JFrame frame;
	public JTextArea console;
	public JTextField input;
	public Document doc;
	public JScrollPane ScrollPane;
	public PrintStream printStream;
	
	//jredfox edits
	public boolean osCmds;
	public Color backgroundColor = new Color(12, 12, 12);
	public Font textFont = new Font("Consolas", Font.PLAIN, 16);
	public Color textColor = new Color(204, 204, 204);
	public int width = 1115;//990
	public int height = 645;//525
	
	public JConsole()
	{
		this("JConsole");
	}
	
	public JConsole(String appName) 
	{
		this(appName, false);
	}
	
	public JConsole(String appName, boolean allowOsCmds) 
	{
		this.osCmds = allowOsCmds;
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		frame = new JFrame();
		console = new JTextArea();
		input = new JTextField();
		doc = console.getDocument();//TODO:unused????
		ScrollPane = new JScrollPane(console);
		printStream = new PrintStream(new Output(console));
		System.setOut(printStream);//moves the text that comes from system.out. to my stream
		System.setErr(printStream);//moves the text that comes from system.err. to my stream
		//System.setIn(in);//TODO: make input stream
		
		frame.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
		        input.requestFocusInWindow();
		    }
		});
		
		DropTargetListener dtl = new DropTargetAdapter() 
		{
			@Override
			public void drop(DropTargetDropEvent dtde) 
			{
				dtde.acceptDrop(DnDConstants.ACTION_LINK);
				try
				{
					//this should always result in a list of files even in the case of 1 file it is a list of 1
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					input.setText(input.getText() + files.get(0) );
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		this.frame.setDropTarget(new DropTarget(this.frame, dtl));
		this.frame.setDropTarget(new DropTarget(this.console, dtl));
		this.frame.setDropTarget(new DropTarget(this.input, dtl));
		this.frame.setDropTarget(new DropTarget(this.ScrollPane, dtl));
		
		KeyListener l = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					String command = input.getText();
					boolean isJava = isJavaCommand(JConsole.split(command,' ', '"', '"'));
					if(!isJava && hasOsCommands())
					{
						runConsoleCommand(command);
					}
					input.setText("");
				}
			}
		};
		
		input.addKeyListener(l);
		console.setEditable(false);
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		frame.setTitle(appName);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				try
				{
					if(shutdown())
					{
						System.gc();
						System.exit(0);
					}
				}
				catch(Throwable t)
				{
					t.printStackTrace();
					System.gc();
					System.exit(-1);
				}
			}
		});
		
		console.setOpaque(false);
		ScrollPane.setOpaque(false);
		ScrollPane.getViewport().setOpaque(false);
		ScrollPane.setBorder(null);
		input.setOpaque(false);
		input.setBorder(null);
		
		this.setBackgroundColor(this.backgroundColor);
		this.setFont(this.textFont);
		this.setTextColor(this.textColor);
		
		frame.add(ScrollPane, BorderLayout.CENTER);
		frame.add(input, BorderLayout.SOUTH);
		
		this.resize(this.width(), this.height());
		frame.setLocationRelativeTo(null);
		
		input.requestFocusInWindow();
	}
	
	public void resize(int w, int h)
	{
		frame.setSize(w, h);//the window size is here
	}

	public void setTextColor(Color c)
	{
		this.textColor = c;
		input.setCaretColor(this.textColor());//color of output caret
		input.setForeground(this.textColor());//color of output text
		console.setCaretColor(this.textColor());//color of input text caret
		console.setForeground(this.textColor());//color of input text
	}
	
	public void setBackgroundColor(Color c)
	{
		this.backgroundColor = c;
		frame.getContentPane().setBackground(this.backgroundColor());
	}
	
	public void setFont(Font font)
	{
		this.textFont = font;
		input.setFont(textFont());//font and font size of output
		console.setFont(this.textFont());//font and font size of input
	}
	
	public void setIcon(Image img)
	{
		this.frame.setIconImage(img);
	}
	
	public void start()
	{
		this.setEnabled(true);
	}
	
	public void hide()
	{
		this.frame.setVisible(false);
	}
	
	public void unhide()
	{
		this.frame.setVisible(true);
	}
	
	public void setEnabled(boolean enabled)
	{
		this.frame.setEnabled(enabled);
		this.frame.setVisible(enabled);
	}
	
    public boolean hasOsCommands()
    {
    	return this.osCmds;
    }
	
	public void runConsoleCommand(String command)
	{
		try
		{
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) 
			{
				System.out.println(line);
			}
		}
		catch(Exception ex)
		{
			System.out.println(command + " is not recognised as a internal or external command");
		}
	}
	

	
    /**
     * split with quote ignoring support @jredfox
     */
    public static String[] split(String str, char sep, char lquote, char rquote) 
    {
        if(str.isEmpty())
            return new String[]{str};
        List<String> list = new ArrayList<String>();
        boolean inside = false;
        for(int i = 0; i < str.length(); i += 1)
        {
            String a = str.substring(i, i + 1);
            String prev = i == 0 ? "a" : str.substring(i-1, i);
            boolean escape = prev.charAt(0) ==  '\\';
            if(a.equals("" + lquote) && !escape || a.equals("" + rquote) && !escape)
            {
                inside = !inside;
            }
            if(a.equals("" + sep) && !inside)
            {
                String section = str.substring(0, i);
                list.add(section);
                str = str.substring(i + ("" + sep).length(), str.length());
                i = -1;
            }
        }
        list.add(str);//add the rest of the string
        Object[] obj = list.toArray();
        String[] string = Arrays.copyOf(obj, obj.length,String[].class);
        return string;
    }
    
	public abstract boolean isJavaCommand(String[] command);
	/***
	 * return whether or not to shutdown
	 */
    public abstract boolean shutdown();
    
    public Color backgroundColor(){return this.backgroundColor;}
    public Font textFont(){return this.textFont;}
    public Color textColor(){return this.textColor;}
    public int width(){return this.width;}
    public int height(){return this.height;}
   
}
