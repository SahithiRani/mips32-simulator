package mips;
import java.io.File;

public class Simulator 
{
    private Processor processor;
    private FileReader filereader;
    private Register register;
    private Parser parser;
    private String instructionRegister;
    private String str=" ";
    private String stallInsEnabled = "";
    private String stallInsDisabled = "";
    int stallsEnabled = 0;
    int clocksEnabled= 4;
    int stallsDisabled=0;
    int clocksDisabled=4;
    boolean enable = true;
    boolean disable=true;
    int instr = 0;
    int l1cachesize, l1blocksize, l1associativity, l2cachesize, l2blocksize, l2associativity, l1hittime, l2hittime, memtime, l1cachemisses = 0,l2cachemisses = 0,memoryaccesses = 0;
     public Simulator (File inputFile, int l1cachesize, int l1blocksize, int l1associativity, int l2cachesize, int l2blocksize, int l2associativity, int l1hittime, int l2hittime, int memtime) 
     {
       filereader = FileReader.getInstance();
        register = Register.getInstance();
        parser = new Parser(inputFile);
        instructionRegister = "";
        this.l1cachesize = l1cachesize;
        this.l1blocksize = l1blocksize;
        this.l1associativity = l1associativity;
        this.l2cachesize = l2cachesize;
        this.l2blocksize = l2blocksize;
        this.l2associativity = l2associativity;
        this.l1hittime = l1hittime;
        this.l2hittime = l2hittime;
        this.memtime = memtime;
        processor = Processor.getInstance(l1cachesize, l1blocksize, l1associativity, l2cachesize, l2blocksize, l2associativity);
      }
     public static void display(Register r,Parser p,String str,int instr,int stallsEnabled,int stallsDisabled, int clocksEnabled,int clocksDisabled,String stallInsEnabled, String stallInsDisabled,int l1cachemisses,int l2cachemisses,int memoryaccesses) 
     {
        GUI gui= new GUI(r.registers,p.getData(),32,1024,str,instr,stallsEnabled,stallsDisabled, clocksEnabled,clocksDisabled,stallInsEnabled, stallInsDisabled,l1cachemisses,l2cachemisses,memoryaccesses);
        gui.display(r.registers,p.getData(),32,1024,str,instr,stallsEnabled,stallsDisabled, clocksEnabled,clocksDisabled,stallInsEnabled, stallInsDisabled,l1cachemisses,l2cachemisses,memoryaccesses);
    }
   @SuppressWarnings("unused")
    public void startSimulation () 
   {
        String[] instruction;
        int[] registerInstruction;
        int[] previousInstruction = null;
        int[] prevprevInstruction = null;
        boolean isStallDisabled = false;
        boolean isStallEnabled=false;
        while ((instruction = parser.nextInstruction()) != null)
        {
            isStallDisabled = false;
            isStallEnabled=false;
            instr++;
            instructionRegister = filereader.loadInstruction(instruction[0]);
            registerInstruction = parser.decodeInstruction(instructionRegister);
            final String[] instructionTemp = instruction;
            final int[] registerInstructionTemp = registerInstruction;
             if (registerInstruction[0] == 2) 
             {
                parser.jumpTo(processor.executeInstruction(registerInstruction, parser.memory));
                previousInstruction = null;
                prevprevInstruction = null;
                continue;
            }
            if(registerInstruction[0] == 35)
            {
                memoryaccesses++;
                isStallEnabled = true;
                isStallDisabled = true;
                if(processor.l1hit(registerInstructionTemp) == true) 
                {
                   stallsEnabled += l1hittime;
                    stallsDisabled += l1hittime;
                } 
                else if(processor.l2hit(registerInstructionTemp) == true) 
                {
                   l1cachemisses++;
                    stallsEnabled += l2hittime+l1hittime;
                    stallsDisabled += l2hittime+l1hittime;
                } 
                else 
                {

                    l1cachemisses++;
                    l2cachemisses++;
                    stallsEnabled += memtime+l1hittime+l2hittime;
                    stallsDisabled += memtime+l1hittime+l2hittime;
                }
            }
             if (registerInstruction[0] == 4 || registerInstruction[0] == 5) 
             {
                 isStallDisabled = true;
                isStallEnabled = true;
                stallsEnabled++;
                stallsDisabled++;
            }
             else 
             {
               if(disable == true) 
               {
                    if (previousInstruction != null && parser.disableCountStalls(registerInstruction, previousInstruction)) 
                    {
                        isStallDisabled= true;
                        stallsDisabled+= 2;
                    }
                    if (prevprevInstruction != null && parser.disableCountStalls(registerInstruction, prevprevInstruction))
                    {
                        isStallDisabled = true;
                        stallsDisabled++;
                    }
                }
               else
               {
                    if (previousInstruction != null && parser.enableCountStalls(registerInstruction, previousInstruction))
                    {
                        isStallEnabled= true;
                        stallsEnabled++;
                    }
                }
            }
            prevprevInstruction = previousInstruction;
            previousInstruction = registerInstruction;
             int address = processor.executeInstruction(registerInstructionTemp, parser.memory);
             if (address != -1) 
             {
                if(registerInstructionTemp[0]== 4||registerInstructionTemp[0]== 5)
                {
                    parser.jumpTo(address);
                }
                else
                {
                    parser.jumpTo(parser.getProgramCounter() + address);
                }
             }
            if(isStallEnabled == true) 
            {
                stallInsEnabled =  stallInsEnabled + instruction[1]+"\n";
            }
            if(isStallDisabled == true) 
            {
                stallInsDisabled = stallInsDisabled  + instruction[1]+"\n";
            }
            str=str+ "\n"+instruction[1]+"\n"+instruction[0]+"\n"+register+"\n"+"-----------------------------------------------------------\n";
         }
        clocksEnabled =clocksEnabled+ instr  + stallsEnabled ;
        clocksDisabled = clocksDisabled+instr  + stallsDisabled ;
        display(register, parser, str,instr,stallsEnabled,stallsDisabled, clocksEnabled,clocksDisabled,stallInsEnabled, stallInsDisabled,l1cachemisses,l2cachemisses,memoryaccesses);
 }
  public static void exit(String message)
  {
        System.out.println(message);
        System.exit(-1);
    }
}