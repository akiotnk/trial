import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.awt.Container;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JToolTip;

import javax.swing.border.BevelBorder;
import javax.swing.border.*;

import java.awt.Dimension;
import java.io.*;
import java.io.File;

import javax.imageio.*;
import java.awt.image.*;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class Sokie extends JFrame implements ActionListener{

    JLabel label;
    JTextField field;
    JButton Button;
    JButton selectButton;
    JButton analyzeButton;
    JTextField textEvalSamples;
    JButton showButton;
    JButton evalButton;
    JButton pngButton;
    JLabel copyrightLabel;
    
    static GraphPanel surface;
    
    static String choosed_dir = ".";
    static String imagefile;
    static String filename;
    static String dailyfile;
    static String dailypngfile;
		static String evalfile;
		static String evalpngfile;
		static String paramfile;
    
    static double Activity[];
    static String Date[];
    static double mean;
    static double sigma;
    static double alert;

  static void readActivitydata (String file)
  {
		readCSV reader = new readCSV(file, 2); // two columns
		
		Activity = new double [reader.getRowLength() -1];
		Date = new String [reader.getRowLength() -1];
		String [] cols;
		reader.rewind();
		cols = reader.readNextRow(); // skip the first row
		for (int i=0; i<reader.getRowLength()-1; i++)
		{
			cols = reader.readNextRow();
			Double x = Double.parseDouble (cols[1]);
			
			Date[i] = cols[0];
			Activity[i] = x;
		}
		reader.close();
	}
	
	static void readParameter (String file)
	{
		readCSV reader = new readCSV(file, 2); // two columns
		
		String [] cols;
		reader.rewind();
		cols = reader.readNextRow(); // skip the first row
		
		cols = reader.readNextRow();
		mean = Double.parseDouble (cols[0]);
		sigma = Double.parseDouble (cols[1]);
		
		cols = reader.readNextRow();
		alert = Double.parseDouble (cols[1]);

		reader.close();
	}

	static void ShowTrainingData ()
	{
		System.out.println ("ShowTraining data: filename= " + filename);
		readActivitydata (dailyfile);
		surface.setTitle (new File(dailyfile).getName());
		surface.setGraphData(Date, Activity);
		readParameter (paramfile);
		surface.setStatis (mean, sigma, alert);
		surface.repaint();
		
		//saveComponentAsJPEG (surface, "mytest.jpg");
	}

public static void saveComponentAsJPEG(JPanel myComponent, String filename) {
        Dimension size = myComponent.getSize();
        BufferedImage myImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();
        myComponent.paint(g2);
        /*
        try {
            OutputStream out = new FileOutputStream(filename);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(myImage);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        */
        try {
        ImageIO.write(myImage, "png", new FileOutputStream(filename));
        } catch (Exception e) {
            System.out.println(e);
        }
        //g2.dispose ();
    }


	static void ShowEvaluatingData ()
	{
		System.out.println ("Show Evaluation data: filename= " + filename);
		readActivitydata (evalfile);
		surface.setTitle (new File(evalfile).getName());
		surface.setGraphData(Date, Activity);
		readParameter (paramfile);
		surface.setStatis (mean, sigma, alert);
		surface.repaint();
	}

	void makeFileNames ()
	{
		int index = filename.lastIndexOf('.');
		String fbody, fext;
		if (index >= 0) { //拡張子がある場合
			fbody = filename.substring(0, index);
			fext = filename.substring(index + 1);
		}
		else { //拡張子がない場合
			fbody = filename;
			fext = null;
		}
		
		System.err.println ("Output file: " + fbody+"daily.csv");
		dailyfile = fbody+"daily.csv";
		dailypngfile = fbody +"daily.png";
		evalfile = fbody+"eval.csv";
		evalpngfile = fbody + "eval.png";
		paramfile = fbody+"param.csv";
		dailypngfile = fbody +"daily.png";
		System.err.println("Parameter file: " + paramfile);
	}
    
    public Sokie() {

        initUI();
    }

//class ActionAdapter implements ActionListener {
    
    public void actionPerformed(ActionEvent e){
        String buttonclicked = e.getActionCommand();
        if (buttonclicked.equals("選択"))
        {
					JFileChooser filechooser = new JFileChooser(choosed_dir);
					
					int selected = filechooser.showOpenDialog(this);
    			if (selected == JFileChooser.APPROVE_OPTION){
      			File file = filechooser.getSelectedFile();
      			field.setText(filechooser.getName(file));
      			choosed_dir = file.getParent();
      			try {
      				filename = file.getCanonicalPath();
      			}
      			catch (IOException fe)
      			{
							System.err.println ("Some error in selected file name: " + fe);
						}
      			System.err.println("Selected file: "+file.getName());
    			}else if (selected == JFileChooser.CANCEL_OPTION){
      			System.err.println("キャンセルされました");
    			}else if (selected == JFileChooser.ERROR_OPTION){
      			System.err.println("エラー又は取消しがありました");
    			}
				}
				else if (buttonclicked.equals("解析"))
				{
					System.err.println("解析ボタンが押された。");
					int eval_days = Integer.parseInt(textEvalSamples.getText());
					makeFileNames ();
					EvalDivide ed = new EvalDivide(filename, eval_days);
				}
				else if (buttonclicked.equals("学習データ表示"))
				{
						
				
					System.err.println("表示ボタンが押された。");
					makeFileNames ();
					imagefile = dailypngfile;
					ShowTrainingData ();
					
					/*
					surface.setData();
					surface.repaint();
					*/
				}
				else if (buttonclicked.equals("評価データ表示"))
				{
					System.err.println("評価表示ボタンが押された。");
					makeFileNames ();
					imagefile = evalpngfile;
					ShowEvaluatingData ();
				}
				else if (buttonclicked.equals("画像保存"))
				{
					System.err.println("画像保存ボタンが押された。");
					makeFileNames ();
					System.out.println ("表示ファイル: " + surface.getTitle());
					// ShowEvaluatingData ();
					saveComponentAsJPEG (surface, imagefile);
				}
    }


    private void initUI() {

      Container contentPane = this.getContentPane();
      contentPane.setLayout(null);
      
      System.out.println ("init:");
      this.setTitle ("Sokie Watcher");
      this.setSize(1000,400);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
      //final GraphPanel surface = new GraphPanel();
      surface = new GraphPanel();
      add(surface);
      System.out.println ("GraphPanel added.");

      surface.setBounds(240, 20, 700, 300);

      System.out.println ("surface dimension: " + surface.getSize());

      setTitle("Sokie Watcher version 1.1");

	  // JLabelの組み込み
      label = new JLabel("対象ファイル選択");
      label.setSize(140,30);
      label.setLocation(20,20);
      this.add(label);
      // JTextFieldの組み込み
      field = new JTextField();
      field.setSize(120,20);
      field.setLocation(20,50);
      this.add(field);
      // JButtonの組み込み
      selectButton = new JButton("選択");
      selectButton.setSize(60,20);
      selectButton.setLocation(150,50);
      //selectButton.addActionListener(new ActionAdapter());
      selectButton.addActionListener(this);
      this.add(selectButton);
      analyzeButton = new JButton("解析");
      analyzeButton.setSize(100,25);
      analyzeButton.setLocation(20,80);
      
       // 評価用サンプル数
      textEvalSamples = new JTextField("100");
      textEvalSamples.setSize(40,20);
      textEvalSamples.setLocation(130,85);
      textEvalSamples.setToolTipText("評価に使うサンプルの個数");
      this.add(textEvalSamples);
      //analyzeButton.addActionListener(new ActionAdapter());
      analyzeButton.addActionListener(this);
      this.add(analyzeButton);
      showButton = new JButton("学習データ表示");
      showButton.setSize(140,25);
      showButton.setLocation(20,110);
      //showButton.addActionListener(new ActionAdapter());
      showButton.addActionListener(this);
      this.add(showButton);
      
      evalButton = new JButton("評価データ表示");
      evalButton.setSize(140,25);
      evalButton.setLocation(20,140);
      //pngButton.addActionListener(new ActionAdapter());
      evalButton.addActionListener(this);
      this.add(evalButton);
      
      pngButton = new JButton("画像保存");
      pngButton.setSize(140,25);
      pngButton.setLocation(20,170);
      //pngButton.addActionListener(new ActionAdapter());
      pngButton.addActionListener(this);
      this.add(pngButton);
        
      copyrightLabel = new JLabel("Copyright (c) Sokie Analysts Group 2015");
      copyrightLabel.setSize(240,30);
      copyrightLabel.setLocation(20,320);
      this.add(copyrightLabel);

//			surface.setBorder(new EtchedBorder(EtchedBorder.RAISED));
    }


    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                Sokie ex = new Sokie();
                ex.setVisible(true);
            }
        });
    }
}
