package mips;
public class Register 
{
    private static Register register;
    public String[] registers;
    public Register ()
    {
        registers = new String[32];
        for (int i = 0; i < registers.length; i++)
            registers[i] = Parser.toBitString(0, 1);
    }
     public static synchronized Register getInstance() 
     {
        if (register == null)
            register = new Register();
        return register;
    }
   public int read(int registerNum) 
   {
        return Integer.parseInt(registers[registerNum]);
    }
     public void write(int registerNum, int value) 
     {
        String s = Integer.toString(value);
        registers[registerNum] = s;
    }
      public static int getRegister(String register) 
      {
          if (register.equals("$0"))
              return 0;
         String[] info = register.split("");
           if (info.length < 3)
        	    Simulator.exit("Register Error : getRegister : Invalid Register Name");
           int value;
          switch (info[1]) 
          {
            case "s":
                try 
                {
                   value = Integer.parseInt(info[2]);
                   if (value > 7)
                    	Simulator.exit("Register Error : getRegister : Register $s only accepts nums 0 -> 7");
                    else 
                    	return 16 + value;
                } 
                catch (Exception e) 
                { 
                	return 28; 
                 }
                  break;
            case "t":
                value = Integer.parseInt(info[2]);
                 if (value > 9)
                	Simulator.exit("Register Error : getRegister : Register $t only accepts nums 0 -> 9");
                else if (value < 8)
                    return 8 + value;
                else 
                	return 24 + (value - 8);
                break;
            case "a":
                value = Integer.parseInt(info[2]);
               if (value > 3)
                	Simulator.exit("Register Error : getRegister : Register $a only accepts nums 0 -> 3");
                else 
                	return 4 + value;
                break;
            case "v":
                value = Integer.parseInt(info[2]);
               if (value > 1)
                	Simulator.exit("Register Error : getRegister : Register $v only accepts nums 0 - 1");
                else
                	return 2 + value;
                break;
            case "g":
                return 28;
            case "f":
                return 30;
            case "r":
                return 31;
            default:
                return 0;
        }
      return 0;
    }
   public String toString () 
   {
        String str = "";
        for (int i = 0; i < registers.length; i++) 
        {
            if (i > 0)
                str += "\n";
             str += ((i < 10) ? "0" : "") + i + " : " + registers[i];
        }
         return str;
    }
 }