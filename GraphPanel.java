import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
import java.util.Random;
import java.awt.Container;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import javax.swing.border.BevelBorder;
import javax.swing.border.*;

import java.awt.Dimension;
import java.awt.Font;

//import java.io.File;
//class GraphPanel extends JPanel implements ActionListener {
class GraphPanel extends JPanel  {
    //private final int DELAY = 150;
    Double data [];
    Boolean hasdata = false;
    private Timer timer;

		String date[];
		double activity[];
		String title ;
		
		double mean, sigma, alert;

    public GraphPanel() {
			System.out.println ("GraphPanel created.");
			setBorder(new EtchedBorder(EtchedBorder.RAISED));
			setBounds(240, 20, 600, 300);
		}
		
	  void setGraphData (String[]d, double[]a)
  {
		System.out.println ("GraphPanel.setGraphData:");
		date = d;
		activity = a;
		System.out.println ("A[0]=" + activity[0]);
		/*
		for (int i=0; i<date.length; i++)
			System.err.println ("Date["+ i + "]= " + date[i]);
			*/
		hasdata = true;
	}
	
	void setTitle (String ttl)
	{
		System.out.println ("GraphPanel.setTitle:");
		title = ttl;
	}
	
	String getTitle ()
	{
		System.out.println ("GraphPanel.getTitle:");
		return title;
	}

  void setStatis (double m, double s, double a)
  {
		System.out.println ("GraphPanel.setStatis:");
		mean = m;
		sigma = s;
		alert = a;
	}
		 //=============================================================================
 //paint()メソッド
 //=============================================================================
 public void doDraw(Graphics g)
 {
	System.out.println ("GraphPanel.doDraw");
	if (!hasdata)
		return;
	int left_margin = 40;
	int right_margin = 5;
	int bottom_margin = 80;
	int top_margin = 20;
  //領域のサイズ取得
  Dimension dimension = getSize();
  /*
  //軸の色は黒
  g.setColor(Color.black);
  //x軸
  g.drawLine(0,dimension.height/2,dimension.width-1,dimension.height/2);
  //y軸
  g.drawLine(dimension.width/2,0,dimension.width/2,dimension.height-1);
*/

  //グラフ線の色は青に設定
  g.setColor(Color.blue);

  //グラフ描画
  double gwidth = dimension.width - left_margin;
  double gheight = dimension.height - bottom_margin - top_margin;
  
  double ymax=-1000.0, ymin=1000.0;
  for(int i=0;i<date.length-1;i++)
  {
		ymax = Math.max(ymax, activity[i]);
		ymin = Math.min(ymin, activity[i]);
	}
	if (ymin > 0 ) ymin = 0.0; // to force to draw x-axis
	ymin = ymin-10.0;
	System.err.println ("ymax= "+ymax + " ymin= " + ymin);
	
	// Plot activity
	int i;
	for(i=0;i<date.length-1;i++)
  {
		double jy = gheight*(ymax - activity[i])/(ymax-ymin) + top_margin;
		double jx = gwidth*(i)/activity.length + left_margin - right_margin; 
		g.fillRect((int)jx-1, (int)jy-1, 3, 3);
	}
	System.err.println ("tail=" + activity[i-1]);
	// Draw Axes
		int axis_xleft = (int)(gwidth*(0)/activity.length + left_margin - right_margin);
		int axis_xright= (int)(gwidth*(activity.length)/activity.length + left_margin -right_margin);
		int axis_y0 = (int)(gheight*(ymax - 0.0)/(ymax-ymin)) + top_margin;
		int axis_ymax = (int)(gheight*(ymax - ymax)/(ymax-ymin)) + top_margin;
		int axis_ymin = (int)(gheight*(ymax - ymin)/(ymax-ymin)) + top_margin;
		//軸の色は黒
  	g.setColor(Color.black);
		g.drawLine (axis_xleft-10, axis_y0, axis_xright, axis_y0);
		g.drawLine (axis_xleft, axis_ymin+10, axis_xleft, axis_ymax);

    // mean: green
    g.setColor(Color.green);
    int axis_mean = (int)(gheight*(ymax - mean)/(ymax-ymin)) + top_margin;
    g.drawLine (axis_xleft, axis_mean, axis_xright, axis_mean);
    // +-sigma: yellow
    g.setColor(Color.yellow);
    int axis_sigma = (int)(gheight*(ymax - (mean+sigma))/(ymax-ymin)) + top_margin;
    g.drawLine (axis_xleft, axis_sigma, axis_xright, axis_sigma);
    // alert: red
		g.setColor(Color.red);
    int axis_alert = (int)(gheight*(ymax - (alert))/(ymax-ymin)) + top_margin;
    g.drawLine (axis_xleft, axis_alert, axis_xright, axis_alert);

	// Title 描画
	  int hpos = 10;
		g.setColor(Color.black);
		Font font = new Font("Arial", Font.BOLD, 14);
  	g.setFont(font);
    g.drawString ("file name: " + title, (int)(gwidth/2), hpos+=20);
    
    // mean, sigma, alert values
    g.drawString ("mean : " + mean, (int)(gwidth/2) + 200, hpos+=20);
    g.drawString ("sigma:  " + sigma, (int)(gwidth/2) + 200, hpos+=20);
    g.drawString ("alert:  " + alert, (int)(gwidth/2) + 200, hpos+=20);
  // y-ticks
    font = new Font("Arial", Font.BOLD, 10);
  	g.setFont(font);
   int ydelta = (int)ymax / 5;
   if (ydelta > 100) ydelta = 200;
   else ydelta = 100;
    for (int y=0;y<ymax;y+=ydelta)
    {
			double jy = gheight*(ymax - y)/(ymax-ymin) + top_margin;
			g.drawLine (axis_xleft-5, (int)jy, axis_xleft+5, (int)jy);
			g.drawString (Integer.toString((int)y), axis_xleft-30, (int)jy );
		}
// 縦書きテスト

		int daydelta = date.length / 10;
		System.err.println ("daydelta="+ daydelta);
		if (daydelta > 100) daydelta = 200;
		else if (daydelta > 50) daydelta = 100;
		else if (daydelta > 20) daydelta = 30;
		else daydelta = 10;
		System.err.println ("daydelta="+ daydelta);
		
		for (int day = 0; day < date.length; day+=daydelta)
		{
			double jx = gwidth*(day)/activity.length + left_margin - right_margin; 
			g.drawLine ((int)jx, axis_y0-5, (int)jx, axis_y0+5);
		
			Graphics2D g2 = (Graphics2D)g;
			g2.rotate(-Math.PI/2,(int)jx,axis_y0+60);
    	g2.drawString(date[day],(int)jx,axis_y0+60);
    	g2.rotate(Math.PI/2,(int)jx,axis_y0+60);
    }
    
	}
/*
    private void doDrawing(Graphics g) {
			System.out.println ("Hi! This is doDraw");
      Graphics2D g2d = (Graphics2D) g;
      g2d.setPaint(Color.blue);

      int w = getWidth();
      int h = getHeight();

      Random r = new Random();

      for (int i = 0; i < 2000; i++) {

        int x = Math.abs(r.nextInt()) % w;
        int y = Math.abs(r.nextInt()) % h;
        g2d.drawLine(x, y, x, y);
      }
		}
  
*/
    @Override
    public void paintComponent(Graphics g) {
			System.out.println ("This is paintComponent");
      super.paintComponent(g);
      //doDrawing(g);
      int left_margin = 40;
			int right_margin = 5;
			int bottom_margin = 80;
			int top_margin = 20;
  		//領域のサイズ取得
  		Dimension dimension = getSize();
  		
  		doDraw (g);
  		/*
  		//軸の色は黒
  		g.setColor(Color.black);
  		//x軸
  		g.drawLine(0,dimension.height/2,dimension.width-1,dimension.height/2);
  		//y軸
  		g.drawLine(dimension.width/2,0,dimension.width/2,dimension.height-1);
*/

  //グラフ線の色は青に設定
  		g.setColor(Color.red);

			if (!hasdata)
				return;
  }
  //グラフ描画
/*
	  System.out.println ("Canvas size: " + dimension);
    System.out.println ("data.length=" + data.length);
  	for(int i=0;i < dimension.width-2;i++)
  	{
   		g.drawLine( i, (int)( -data[i] + dimension.height/2 ), 
   							i+1, (int)( -data[i+1] + dimension.height/2 ) );
  	}

	System.out.println ("GraphCanvas: paint:" + g);
	}
	*/
/*
  void setData ()
    {
			System.out.println ("setData: dimension" + this.getSize());
			data = new Double[this.getWidth()];
			System.out.println ("SetData: data.length=" + data.length);
      for(int i=0;i<this.getWidth();i++)
      {
         data[i] = 100*Math.sin((i-this.getWidth()/2+1)*
            								 Math.PI/((this.getWidth())/2));
      }
      hasdata = true;
    }
*/
}
