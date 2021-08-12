package mips;

public class Memory
{
    private static Memory memory;
    public String[] data;
     public Memory() 
     {
        data = new String[1024];
        for (int i = 0; i < data.length; i++)
            data[i] = Parser.toBitString(0,1);
    }
    public static synchronized Memory getInstance() 
    {
        if (memory == null)
            memory = new Memory();
        return memory;
    }
   public int read(int Num) 
   {
        return Integer.parseInt(data[Num]);
    }
     public void write(int Num, int value) 
     {
        data[Num] = Integer.toString(value);
    }
    public static int getdata(String data) 
    {
        return Integer.parseInt(data);
    }
  public String toString ()
  {
        String str = "";
        for (int i = 0; i < data.length; i++) 
        {
            if (i > 0)
                str += "\n";
            str += ((i < 10) ? "0" : "") + i + " : " + data[i];
        }
        return str;
    }
}

