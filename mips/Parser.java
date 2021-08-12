package mips;
import java.io.File;
import java.util.Scanner;
import java.util.regex.*;

public class Parser 
{
     private File file;
    private Scanner scanner;
    private int lastReadLine;
    Memory memory = new Memory();
    int i=0;
    public Parser(File file) 
    {
        try 
        {
            this.file = file;
            lastReadLine = 0;
            scanner = new Scanner(file);
        } 
        catch (Exception e)
        {
            Simulator.exit("Parser Error : Constructor : " + e.getMessage());
        }
    }
    public String[] nextInstruction() 
    {
       try 
       {
            if (!scanner.hasNextLine())
                return null;
        } 
       catch (Exception e) 
       {
            return null;
        }
       lastReadLine += 1;
        String line = scanner.nextLine();
        if(line.equals(".data"))
        {
            lastReadLine += 1;
            line = scanner.nextLine();
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(line);
            while (m.find()) 
            {
                int n = Integer.parseInt(m.group());
                memory.data[i] = Integer.toString(n);
                i++;
            }
            return nextInstruction();
        }
        if(line.equals(".text")||line.equals("main:"))
        {
            lastReadLine += 1;
            line = scanner.nextLine();
            return nextInstruction();
        }
       if (line.isEmpty() || line.startsWith("#"))
            return nextInstruction();
        line = line.split("#")[0];
        String[] lineFormat = line.replaceAll(",", " ").split("\\s+");
        String[] instructionFormat = findInstruction(lineFormat);
       return new String[] 
    		   {
                assembleInstruction(lineFormat, instructionFormat),line
               };
        }
     private String[] findInstruction(String[] line)
     {
        try
        {
            Scanner stdin = new Scanner(new File("C:/Users/Gowthami/Desktop/Computer Organization/CO_Project/instructions.csv"));
            while (stdin.hasNextLine())
            {
                String[] instruction = stdin.nextLine().split(",");
                if (instruction[0].equals(line[0]))
                {
                    stdin.close();
                    return instruction;
                }
            }
          stdin.close();
        } 
        catch (Exception e) 
        {
            Simulator.exit("Parser Error : findInstruction : " + e.getMessage());
        }
        Simulator.exit("Parser Error : findInstruction : Invalid Instruction Found (" + line[0] + ")");
        return null;
    }
    private String assembleInstruction (String[] lineFormat, String[] instructionFormat) 
    {
        String[] instruction = new String[6];
        int opCode = Integer.parseInt(instructionFormat[2]);
         switch (instructionFormat[1])
         {
            case "R":
               instruction = new String[6];
                instruction[0] = toBitString(opCode, 6);
                instruction[1] = toBitString(Register.getRegister(lineFormat[2]), 5);
                instruction[2] = toBitString(Register.getRegister(lineFormat[3]), 5);
                instruction[3] = toBitString(Register.getRegister(lineFormat[1]), 5);
                instruction[4] = toBitString(0, 5);
                instruction[5] = toBitString(Integer.parseInt(instructionFormat[3]), 6);
                 break;
            case "I":
               instruction = new String[4];
                instruction[0] = toBitString(opCode, 6);
                instruction[1] = toBitString(Register.getRegister(lineFormat[2]), 5);
                instruction[2] = toBitString(Register.getRegister(lineFormat[1]), 5);
                if (opCode == 35 || opCode == 43) 
                {
                    if (lineFormat[2].indexOf('(') > -1) 
                    {
                        lineFormat[2] = lineFormat[2].replace('(', ' ');
                        String[] offsetFormat = lineFormat[2].split(" ");
                        offsetFormat[1] = offsetFormat[1].substring(0, offsetFormat[1].length() - 1);
                        instruction[1] = toBitString(Register.getRegister(offsetFormat[1]), 5);
                        instruction[3] = toBitString(Integer.parseInt(offsetFormat[0]), 16);
                    } 
                    else
                        instruction[3] = toBitString(0, 16);
                }
                else
                    instruction[3] = toBitString(Integer.parseInt(lineFormat[3]), 16);
                break;
            case "J":
                instruction = new String[2];
                instruction[0] = toBitString(opCode, 6);
                instruction[1] = toBitString(Integer.parseInt(lineFormat[1]), 26);
                 break;
        }
        String instructionStr = "";
        for (String str : instruction)
              instructionStr += str;
         return instructionStr;
    }
     public int[] decodeInstruction (String instructionRegister) 
     {
          int opCode = Integer.parseInt(instructionRegister.substring(0, 6), 2);
           if (opCode == 0) 
           {
            int rs = Integer.parseInt(instructionRegister.substring(6, 11), 2);
            int rt = Integer.parseInt(instructionRegister.substring(11, 16), 2);
            int rd = Integer.parseInt(instructionRegister.substring(16, 21), 2);
            int func = Integer.parseInt(instructionRegister.substring(26, 32), 2);
           return new int[] 
        		   {
                    opCode, rs, rt, rd, 0, func
                  };
        }
        else if (opCode == 2) 
        {
            int address = Integer.parseInt(instructionRegister.substring(6, 32), 2);
              return new int[] 
            		  {
                    opCode, address
            };
        } 
        else
        {
            int rs = Integer.parseInt(instructionRegister.substring(6, 11), 2);
            int rd = Integer.parseInt(instructionRegister.substring(11, 16), 2);
              int address = Integer.parseInt(instructionRegister.substring(16, 32), 2);
             return new int[] 
            		 {
                    opCode, rs, rd, address
            };
        }
    }
     public void jumpTo(int programCounter)
     {
        try 
        {
            if (programCounter <= lastReadLine) 
            {
                scanner = new Scanner(this.file);
                lastReadLine = 0;
            }
            while (scanner.hasNextLine() && lastReadLine < (programCounter - 1))
                this.nextInstruction();
        }
        catch (Exception e) 
        {
            Simulator.exit("Parser Error : jumpTo : " + e.getMessage());
        }
    }
    public int getProgramCounter() 
    {
        return lastReadLine;
    }
   public static String toBitString(int number, int size)
   {
         boolean isTwosComplement = false;
        if (number < 0)
            isTwosComplement = true;
        String binary = Integer.toBinaryString(number);
         if (binary.length() < size) 
         {
          while (binary.length() < size)
                binary = (isTwosComplement ? "1" : "0") + binary;
        } 
         else if (binary.length() > size)
            binary = binary.substring(binary.length() - 32, binary.length());
          return binary;
    }
    public String[] getData()
    {
        return memory.data;
    }
    public boolean disableCountStalls(int[] registerInstruction, int[] previousInstruction)
    {
      if (registerInstruction[0] == 2 || previousInstruction[0] == 2)
            return false;
        if(registerInstruction[0] == 0 && previousInstruction[0] == 0)
        {
            if(registerInstruction[1] == previousInstruction[3] || registerInstruction[2] == previousInstruction[3])
            {
                return true;
            }
        }
        else if(registerInstruction[0] == 0)
        {
            if(previousInstruction[0] == 8 || previousInstruction[0] == 9)
            {
                if(registerInstruction[1] == previousInstruction[2] || registerInstruction[2] == previousInstruction[2])
                {
                    return true;
                }
            }
        }
        else if(previousInstruction[0] == 0)
        {
            if(registerInstruction[0] == 8 || registerInstruction[0] == 9)
            {
                if(registerInstruction[1] == previousInstruction[3])
                {
                    return true;
                }
            }
        }
        else if(registerInstruction[0] == 8 || registerInstruction[0] == 9)
        {
            if(previousInstruction[0] == 8 || previousInstruction[0] == 9)
            {
                if(registerInstruction[1] == previousInstruction[2])
                {
                    return true;
                }
            }
        }
        else if(previousInstruction[0] == 35 )
        {
            if(registerInstruction[0] == 0)
            {
                if(previousInstruction[2] == registerInstruction[1] || previousInstruction[2] == registerInstruction[2])
                {
                    return true;
                }
            }
            else if(registerInstruction[0] == 8 || registerInstruction[0] == 9 || registerInstruction[0] == 43)
            {
                if(registerInstruction[1] == previousInstruction[2])
                {
                    return true;
                }
            }

        }
         return false;
    }
    public boolean enableCountStalls(int[] registerInstruction, int[] previousInstruction)
    {
        if(previousInstruction[0] == 35)
        {
            if(registerInstruction[0] == 0)
            {
                if(previousInstruction[2] == registerInstruction[1] || previousInstruction[2] == registerInstruction[2])
                {
                    return true;
                }
            }
            else if(registerInstruction[0] == 8 || registerInstruction[0] == 9 || registerInstruction[0] == 43)
            {
                if(registerInstruction[1] == previousInstruction[2])
                {
                    return true;
                }
            }
         }
        return false;
    }
}