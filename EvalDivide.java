/****************************
to compile
  javac Daily.java
to run
  java Daily sample.csv skiplines skipfields > outputfile
  java Daily24 33-304.csv | ./to8 33-304Dayly24.csv
  java Daily24 31-202.csv | .\to8.exe Daily24_31-202.csv
公田町データcsvファイルを読み取るためのテストプログラム
(c)2015-04-26 by Akio Tanaka

  Skip the first line, 
  and read six fields while skipping first two fields for each line
  
  The DateTime should be "yyyy/MM/dd h:mm".
  The DateTime also can be "yyyy/MM/dd h:mm AM".

2015-06-14
クラス化
 java EvalDivide ファイル 日数
xxxx.eval : 最後の日数分のデータ、評価用
xxxx.daily : 日数分を除いた、警告レベル算出用データ
xxxx.param : パラメータ記録用
****************************/
/*  Rの使い方
  http://cse.naro.affrc.go.jp/takezawa/r-tips/r.html
*/
import java.io.*;
//import java.time.LocalTime;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EvalDivide {
	static String filename;
	static String datafile;
	static String evalfile;
	static String paramfile;
	static String activity[][];
	static LocalDateTime At[];
	static double Entrance[];
	static Double Toilett[];
	static Double Living[];
	static Double Pass[];
	static Double Door[];
	
	static int skiplines = 1;  // 読み飛ばす先頭の行数
	static int skipfields = 2; // 読み飛ばすフィールドの数
	static int p = 6;					// 読み取るフィールドの数
	
	/* valid data  */
	static int valid_days = 0;
	static int eval_days;
	static int analyze_days;
	
	static LocalDateTime dayAt[];

	static Double dayEnergy[];
	static Double day_mean, day_sigma;
	static Double alert_level, adjusted_alert_level;
	
	static int nlines;
	
	private static final DateTimeFormatter 
			timeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d h:mm");
	private static final DateTimeFormatter 
			timeFormatterAMPM = DateTimeFormatter.ofPattern("yyyy/M/d h:mm a", Locale.US);
	
  public static LocalDateTime parseDateAndHour(String time) {
		LocalDateTime t= LocalDateTime.now();
		try {
    t = LocalDateTime.parse((time), timeFormatter);
    return (t);
    }
		catch (java.time.format.DateTimeParseException e)
		{
			try {
				t = LocalDateTime.parse((time), timeFormatterAMPM);
				return (t);
			}
			catch (java.time.format.DateTimeParseException e2)
			{
				System.err.println (e2);
				System.exit (-1);
			}
		}
		return (t);
	}
	
	public static Boolean beginningOfDay (LocalDateTime dt)
	{
			if (dt.getHour() == 0)
				return true;
			else
				return false;
	}
	
	public static Boolean endOfDay (LocalDateTime dt)
	{
			if (dt.getHour() == 23)
				return true;
			else
				return false;
	}
	
	EvalDivide (String filnam, int edays)
	{
		filename = filnam;
		eval_days = edays;
		init_setting();
		do_processing ();
	}
	
	static void init_setting()
	{
		System.err.println ("init_settin() filename" + filename);
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
		System.err.println ("Output file: " + fbody+".daily");
		datafile = fbody+"daily.csv";
		evalfile = fbody+"eval.csv";
		paramfile = fbody+"param.csv";
		System.err.println("Parameter file: " + paramfile);
	
		System.err.println("SkipLines="+skiplines+" SkipFields="+skipfields);
		System.out.println ("eval_days =" + eval_days);
		readData (filename, skiplines, skipfields, p);
	}
	static void do_processing ()
	{
		convertData();
		
		extractDaily ();
		
		analyze_days = valid_days - eval_days;
		
		System.out.println ("1st valid=" + valid_days + " analyze=" + analyze_days + " eval=" + eval_days);
		
		outputDailyActivity();
		System.out.println ("2nd valid=" + valid_days + " analyze=" + analyze_days + " eval=" + eval_days);
		
		calcAlertLevel ();
		System.out.println ("3rd valid=" + valid_days + " analyze=" + analyze_days + " eval=" + eval_days);
		adjustAlertLevel ();
		
		outputParameters ();
	}
	public static void main(String args[]) throws IOException
	{
				
		String filename = "";

		//int eval_days = 100;
		
		
		if (args.length == 1)
		{
			filename = args[0];
			eval_days = 100;
		}
		else if (args.length == 2)
		{
			filename = args[0];
			eval_days = Integer.parseInt(args[1]);;
		}
		
		else if (args.length == 4)
		{
			filename = args[0];
			eval_days = Integer.parseInt(args[1]);
		  skiplines = Integer.parseInt(args[2]);
		  skipfields = Integer.parseInt(args[3]);
		}
		else
		{
			System.err.println ("java Reader file days [skip1 skip2]");
			System.err.println ("DateTime format must be: yyyy/MM/dd HH:mm");
			System.exit (-1);
		}
		
		System.err.println ("filename=" + filename + " eval_days=" + eval_days);
		
		EvalDivide ed = new EvalDivide(filename, eval_days);
		
  }

  static void outputDailyActivity ()
  {// Shift_JISのファイルへ文字コード変換して書き込む
	 // 最初の解析用データの書き出し
	 System.out.println ("Daily 0th valid=" + valid_days + " analyze=" + analyze_days + " eval=" + eval_days);
		try {
			PrintWriter outWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(datafile), "Shift_JIS"));
			outWriter.println ("date, activity"); // Header
			System.err.println ("*** start daily writing ***");
			System.out.println ("Daily 1st valid=" + valid_days + " analyze=" + analyze_days + " eval=" + eval_days);
			for (int i=0; i<analyze_days; i++)
			{
				//System.err.println ("Writing result, i= "+i);
				int y = dayAt[i].getYear();
				int m = dayAt[i].getMonthValue();
				int d = dayAt[i].getDayOfMonth();
				outWriter.println (y+"/"+m+"/"+d+ ", "+  dayEnergy[i]);
			}
			outWriter.close();
		}
		catch (IOException e)
			{
				System.err.println(e);
				System.exit(-1);
			}
		// 後ろの評価用データを書き出す。
		try {
			PrintWriter outWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(evalfile), "Shift_JIS"));
			outWriter.println ("date, activity"); // Header
			System.err.println ("*** start eval writing ***");
			System.out.println ("Daily 2nd valid=" + valid_days + " analyze=" + analyze_days + " eval=" + eval_days);
			for (int i=analyze_days; i<analyze_days+eval_days; i++)
			{
				//System.err.println ("Writing result, i= "+i);
				int y = dayAt[i].getYear();
				int m = dayAt[i].getMonthValue();
				int d = dayAt[i].getDayOfMonth();
				outWriter.println (y+"/"+m+"/"+d+ ", "+  dayEnergy[i]);
			}
			outWriter.close();
		}
		catch (IOException e)
			{
				System.err.println(e);
				System.exit(-1);
			}
	}
	
	static void outputParameters ()
	{
		System.err.println ("paramFile: " + paramfile);
		File outFile = new File(paramfile);
    try {
			PrintWriter pw = new PrintWriter(
											new BufferedWriter(new FileWriter(outFile)));
			pw.println("mean, sigma");
			pw.println(String.format("%5.1f", day_mean) + ", " + String.format("%5.1f", day_sigma));
			pw.println(String.format("%5.1f",alert_level) + "," + String.format("%5.1f",adjusted_alert_level));
			pw.close();
		}catch(IOException e){
      System.err.println(e);
    }
	}

  static int count_UnderAlertLevel (Double level)
  {
		int count = 0;
		for (int i=0; i < valid_days; i++)
		{
			if (dayEnergy[i] < level)
					count++;
		}
		return count;
	}

  static void adjustAlertLevel ()
  {
		int c;
		Double l = alert_level;
		
		while ((c=count_UnderAlertLevel(l)) < valid_days/30)
		{
			System.err.println ("level= " + l + " Count= " + c);
			l = l + 10.0;
		}
		adjusted_alert_level = l;
	}
	
	static void calcAlertLevel ()
	{
		Double sum, sum2;
		System.err.println ("analyze_days: " + analyze_days + "dayEnergy[].length" + dayEnergy.length);
		
		sum=0.0; sum2=0.0;
		for (int i=0; i<analyze_days; i++)
		  sum += dayEnergy[i];
		day_mean = sum/analyze_days;
		
		for (int i=0; i<analyze_days; i++)
		  sum2 += Math.pow(dayEnergy[i] - day_mean, 2);
		day_sigma = Math.sqrt(sum2/analyze_days);
		System.err.println ("sum= " + sum + " days= " + analyze_days + " mean= " + day_mean);
		System.err.println ("sigma = " + day_sigma);
		System.err.println ("mean = " + day_mean + " sigma= " + day_sigma);
		alert_level = day_mean - 3*day_sigma;
		System.err.println ("alert = " + alert_level);
		/*
		sum=0.0; sum2=0.0;
		int i=0;
		for (i=0; i<valid_days; i++)
		{
			if ( dayEnergy[i] < day_mean + day_sigma)
		  	sum += dayEnergy[i];
		}
		day_mean = sum/i
		;
		i=0;
		for (i=0; i<valid_days; i++)
		{
			if ( dayEnergy[i] < day_mean + day_sigma &&  dayEnergy[i]>0)
		  	sum2 += Math.pow(dayEnergy[i] - day_mean, 2);
		}
		day_sigma = Math.sqrt(sum2/i);
		alert_level = day_mean - 3*day_sigma;
		
		System.err.println ("mean=" + day_mean + ", sigma=" + day_sigma + " alert="+alert_level);
		System.err.println ("*** before adjusting ***");
		//System.out.println ("date, activity"); // Header
		*/

	}
	
	static void extractDaily ()
	{
		int nhours = nlines-1;
		
    int h=0;
    valid_days = 0;
    dayEnergy = new Double[nhours/24];
    dayAt = new LocalDateTime[nhours/24];
    
    
		while (h<nhours)
		{
			double daily_energy = 0.0;
			boolean haserror = false;
			LocalDateTime nextday = At[h].plusDays(1L);
			//System.out.print (nextday.getYear() + "/" + nextday.getMonthValue()+"/"+nextday.getDayOfMonth() +":");
			// 次の日の零時
			LocalDateTime nextday00 = LocalDateTime.of(nextday.getYear(), nextday.getMonthValue(), nextday.getDayOfMonth(), 0, 0);

			int j;
			for (j=0; (h+j) < nhours && At[h+j].isBefore(nextday00); j++)
			// データが尽きるか、次の日の前のデータまで
			{
				if (At[h+j].getHour() != j)
				{
					if (!haserror)
					{
						System.err.println ("*** Some Gap " + At[h+j] +" *** j=" + j);
						haserror = true;
					}
				}
				daily_energy += Entrance[h+j]+Toilett[h+j]+ Pass[h+j];
			}


			if (j == 24) 
			{
				//System.err.println("valid_days=" + valid_days);
				dayEnergy[valid_days] = (daily_energy/j*24);
				dayAt[valid_days] = At[h];
				valid_days++;
			}
			h += j;
		}
	}
	
	static void convertData ()
	{
		int nhours = nlines-1;
		
		At = new LocalDateTime [nhours];
		Entrance = new double[nhours];
	  Toilett = new Double[nhours];
		Living = new Double[nhours];
		Pass = new Double[nhours];
	  Door = new Double[nhours];
	  
	  for (int i=0; i<nhours; i++)
	  {
			//System.out.println ("??? " + i + "?? " + activity[i][0]);
			At[i] = parseDateAndHour(activity[i][0]);
			//System.out.println ("At[" + i + "]" +At[i]);
			Entrance [i] = Double.parseDouble(activity[i][1]);
			Toilett [i] = Double.parseDouble(activity[i][2]);
			Living [i] = Double.parseDouble(activity[i][3]);
			Pass [i] = Double.parseDouble(activity[i][4]);
			Door [i] = Double.parseDouble(activity[i][5]);
		}
	}

  static void readData (String filename, int skiplines, int skipfields, int p)
  {
		String line;
		String str;
		Data dt;
		// 空読みしてデータの行数を数える。
		nlines = 0;
		try{
  		File file = new File(filename);
  		BufferedReader in = new BufferedReader(new FileReader(file));
		  line = in.readLine();
		  while (line != null)
		    {
					nlines++;
					//System.out.println (line);
					line = in.readLine();
		    }
		in.close();
		}catch(FileNotFoundException e){
  		System.err.println(e);
  		System.err.println ("File Not Found: "+ filename);
  		System.exit (-1);
		}catch(IOException e){
 			System.err.println(e);
 			System.exit (-1);
		}
		
		System.err.println ("Number of lines: " + nlines);

// ここから本当に読む

//int n = nlines;

		
		int i, ll, ff, sw;
		activity = new String [nlines][p];
		
		try{
  		File file = new File(filename);
  		BufferedReader in = new BufferedReader(new FileReader(file));
  		
			for (i = 0; i<skiplines; i++) // skip lines
				str = in.readLine();
			// データ
			for (ll = 0; ll < nlines - skiplines; ll++) {
				str = in.readLine();
				dt  = new Data(str);
				for (i=0; i<skipfields; i++)  // skip fields
					dt.next ();
				for (ff = 0; ff < p; ff++) {
					dt.next();
					activity[ll][ff] = dt.data;
					//System.out.println ("ll="+ll+"|ff="+ff+":"+dt.data);
				}
			}
			in.close();
			}catch(FileNotFoundException e){
  			System.out.println(e);
			}catch(IOException e){
 				System.out.println(e);
			}

			System.err.println 
			  ( "The first DateTime: "+ activity[0][0] +"=>" + parseDateAndHour (activity[0][0]));		
	}
}
