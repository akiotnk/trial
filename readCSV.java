import java.io.*;

class readCSV {
	private String filename;
	private static int cols;
	private int rows;
	
	BufferedReader br = null;
	
	public int getRowLength() { return rows; }
	public readCSV (String fn, int c)
	{
		filename = fn;
		cols = c;
		
		br = null; 
		int i = 0; 
		try { 
      br = new BufferedReader(new FileReader(filename)); 
      
      //�t�@�C���̌�������ǂ݂���B�B 
      while (br.ready()) { 
        String line = br.readLine(); 
        //System.out.println (line);
        i++; 
      } 
    }
    catch (FileNotFoundException e){ 
      System.out.println("�t�@�C����������܂���B"); 
      e.printStackTrace(); 
    }
    catch (IOException e){ 
      System.out.println("���o�̓G���[�ł��B"); 
      e.printStackTrace(); 
    }
    finally{ 
      if(br != null){ 
         try{ 
           br.close(); 
         }
         catch(IOException e) { 
           System.out.println("���o�̓G���[�ł��B"); 
           e.printStackTrace(); 
         } 
      } 
    } // first try
    rows = i;
  } // readCSV
  
  public Boolean rewind ()
  {
		br = null;
		try { 
      br = new BufferedReader(new FileReader(filename)); 
    }
    catch (FileNotFoundException e){ 
      System.err.println("�t�@�C����������܂���B(rewind)"); 
      return false;
    }
    /*
    catch (IOException e){ 
      System.err.println("���o�̓G���[�ł��B(rewind)"); 
      return false;
    }
    */
    return true;
	}
	
  public String[] readNextRow ()
  {
		String [] columns = new String[cols];
		try {
		if (br.ready())
		{
			String line = br.readLine();
			columns = line.split(",");
			//System.out.println ("NextRow: [0]= " + columns[0] + " [1]= " + columns[1]);
		}
		else
		{
			br.close();
			System.err.println ("No rows");
			System.exit (-1);
		}
		}
		catch (IOException e)
		{
			System.err.println("�t�@�C����������܂���B(readNextRow)"); 
			System.exit (-1);
		}
		return columns;
	}
		
	public void close ()
	{
		try {
		  br.close();
		}
		catch (IOException e) {
			System.err.println("�t�@�C����������܂���B(close)"); 
			System.exit (-1);
		}
		return;
	}
	
	public static void main(String[] args) 
	{
		readCSV reader = new readCSV("test.csv", 2); // two columns
		String [] cols;
		reader.rewind();
		for (int i=0; i<reader.getRowLength(); i++)
		{
			cols = reader.readNextRow();
			Double x;
			if (i != 0)	
				x = Double.parseDouble (cols[1]);
			else
			  x = 0.0;
			System.out.println (i+"[0]= |" + cols[0] + "| [1]= |" + cols[1] + "|" + x);
			
		}
		reader.close();
		
		
	}
}
