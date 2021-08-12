package mips;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class FileReader
{
    private static FileReader filereader;
    private File file;
    private Scanner reader;
    private int lastReadLine;
    private int instructionCount;
   public FileReader(String filePath) 
   {
       file = new File(filePath);
        lastReadLine = 0;
        instructionCount = 0;
       try 
       {
            if (!file.exists())
                if (!file.createNewFile())
                    throw new Exception("File was not created due to insufficient permissions");
            reader = new Scanner(file);
        } 
       catch (Exception e) 
       {
            Simulator.exit("Memory Error : Constructor : " + e.getMessage());
        }
    }
    public static synchronized FileReader getInstance() 
    {
        if (filereader == null)
            filereader = new FileReader("C:/Users/Gowthami/Desktop/Computer Organization/CO_Project/memory.text");
        return filereader;
    }
    public String loadInstruction(String instruction) 
    {
        instructionCount += 1;
        this.write((instructionCount - 1) * 4, instruction);
        return instruction;
    }
    public int getInstructionCount () 
    {
        return instructionCount;
    }
     public String getInstruction (int programCounter)
     {
        try
        {

            if (!reader.hasNextLine() || programCounter <= lastReadLine)
            {
                reader = new Scanner(file);
                lastReadLine = 0;
            }
            while (reader.hasNextLine() && lastReadLine < (programCounter - 1)) 
            {
                reader.nextLine();
                lastReadLine += 1;
            }
            String instruction = "";
            for (int i = 0; i < 4; i++) 
            {
                instruction += reader.nextLine();
                lastReadLine += 1;
            }
            return instruction;
        }
        catch (Exception e) 
        {
            Simulator.exit("FileReader Error : getInstruction : " + e.getMessage());
        }
        return null;
    }
     public String read(int address) 
     {
        try
        {
            if (!reader.hasNextLine() || address <= lastReadLine)
            {
                reader = new Scanner(file);
                lastReadLine = 0;
            }
            while (reader.hasNextLine() && lastReadLine < (address - 1))
            {
                reader.nextLine();
                lastReadLine += 1;
            }
            lastReadLine += 1;
            return reader.nextLine();
        } 
        catch (Exception e)
        {
            Simulator.exit("FileReader Error : read : " + e.getMessage());
        }
        return null;
    }
     public void write(int address, String line) 
     {
         try 
         {
            Path path = Paths.get("C:/Users/Gowthami/Desktop/Computer Organization/CO_Project/memory.text");
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
             try 
             {
                  for (int i = 0; i < 4; i++)
                         lines.set((address + i) - 1, line.substring((8 * i), (8 * (i + 1))));
            }
             catch (Exception e) 
             {
                while (lines.size() < address)
                        lines.add(Parser.toBitString(0, 8));
                  for (int i = 0; i < 4; i++)
                         lines.add(line.substring((8 * i), (8 * (i + 1))));
            }
             finally 
             {
                Files.write(path, lines, StandardCharsets.UTF_8);
            }
        } 
         catch (Exception e) 
         {
            Simulator.exit("FileReader Error : write : " + e.getMessage());
        }
    }
     public String toString () 
     {
        String str = "";
        try
        {
            Scanner scanner = new Scanner(file);
           while (scanner.hasNextLine())
                 str += scanner.nextLine() + "\n";
             scanner.close();
        } 
        catch (Exception e) 
        {
            Simulator.exit("FileReader Error : toString : " + e.getMessage());
        }
        return str;
    }
}