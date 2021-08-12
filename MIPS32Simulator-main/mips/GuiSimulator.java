package mips;
import java.io.*;
import java.util.Scanner;
import javax.swing.JFileChooser;

public class GuiSimulator 
{
    @SuppressWarnings("resource")
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        int R[]=new int[32];
        int Mem[]=new int[1024];
        int l1cachesize =0, l1blocksize =0, l1associativity=0, l2cachesize=0, l2blocksize=0, l2associativity=0, l1hittime=0, l2hittime=0, memtime=0;
       for(int i=0;i<32;i++) 
       {
            R[i]=0;
        }
       for(int i=0;i<1024;i++) 
       {
            Mem[i]=0;
        }
        File file ;
        Scanner fileIn;
        int response;
        JFileChooser chooser=new JFileChooser(".");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        response=chooser.showOpenDialog(null);
         if(response==JFileChooser.APPROVE_OPTION) 
         {
               file=chooser.getSelectedFile();
              try 
              {
                fileIn=new Scanner(file);
                System.out.println("Enter the size of level 1 cache : ");
                l1cachesize = sc.nextInt();
                System.out.println("Enter the block size of level 1 cache : ");
                l1blocksize = sc.nextInt();
                System.out.println("Enter the associativity of level 1 cache : ");
                l1associativity = sc.nextInt();
                System.out.println("Enter the size of level 2 cache : ");
                l2cachesize = sc.nextInt();
                System.out.println("Enter the block size of level 2 cache : ");
                l2blocksize = sc.nextInt();
                System.out.println("Enter the associativity of level 2 cache : ");
                l2associativity = sc.nextInt();
                System.out.println("Enter the hit time of level 1 cache : ");
                l1hittime = sc.nextInt();
                System.out.println("Enter the hit time of level 2 cache : ");
                l2hittime = sc.nextInt();
                System.out.println("Enter the memory access time : ");
                memtime = sc.nextInt();
                 if(file.isFile()) 
                 {
                   Simulator sim = new Simulator(file,l1cachesize, l1blocksize, l1associativity, l2cachesize, l2blocksize, l2associativity, l1hittime, l2hittime, memtime);
                    sim.startSimulation();
                } 
                 else 
                 {
                        System.out.println("That was not a File :)");
                 }
                fileIn.close();
            } 
              catch (FileNotFoundException e)
              {
                e.printStackTrace();
             }
        }
      }
}