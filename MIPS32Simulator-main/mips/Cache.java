package mips;
import java.lang.Math;
import java.lang.String;

public class Cache 
{
   private static final int INT_MAX = 0;
    int cacheSize;
    int blockSize;
    int associativity;
    int noOfBlocks ;
    int noOfSets;
    int indexBits ;
    int offset;
    int tag;
    int noofvaluesinablock=blockSize/4;
    int tagArray[];
    int timeStamp[];
    int dataArray[];
    int time = 0;
    Cache(int cachesize,int blocksize,int associativity) 
    {
        this.cacheSize=cachesize;
        this.blockSize=blocksize;
        this.associativity=associativity;
        noOfBlocks = cacheSize/blockSize;
        noOfSets = noOfBlocks/associativity;
        noofvaluesinablock=blockSize/4;
        indexBits = (int)(Math.log(noOfSets)/Math.log(2));
        offset = (int)(Math.log(noofvaluesinablock)/Math.log(2));
        tag = 10-indexBits-offset;
        tagArray= new int[noOfBlocks];
        for(int i=0; i<noOfBlocks; i++)
        {
            tagArray[i]=-1;
        }
        timeStamp = new int[noOfBlocks];
        dataArray= new int[noOfBlocks*noofvaluesinablock];
    }
    public int TagAddress(int address)
    {
        return(address-(address%noofvaluesinablock));
    }
    public int StartBlockNo(int setNo,int associativity)
    {
         return (setNo*associativity);
    }
    public int StartIndex(int BlockNo,int n)
    {
       return (BlockNo*n);
    }
    public int BlockNo(int offset,int setNo,int associativity) 
    {
       return (offset+setNo*associativity);
    }
    public static String toBinary(int x) 
    {
        StringBuilder result = new StringBuilder();
        for (int i = 9; i >= 0 ; i--)
        {
            int mask = 1 << i;
            result.append((x & mask) != 0 ? 1 : 0);
        }
        return result.toString();
    }
     public int Returnint(String str,int a,int b) 
     {
        int n=b-a;
        int k=0;
        for(int i=n;i>=0;i--)
        {
        	k =(int)((k+(str.charAt(a++)-48)*Math.pow(2,i)));
        }
        return k;
    }
      public int getSmallest(int[] a, int start, int end)
      {
        int min = start;
        int i;
        for(i = start; i <= end; i++)
        {
            if(a[i] < a[min])
            {
                min = i;
            }
        }
        return min;
      }

    public boolean cacheHit(int address) 
    {
       String addressString=toBinary(address);
        int indexvalue=Returnint(addressString,10-offset-indexBits,9-offset);
        int setNo=indexvalue;
        int startBlockNo=StartBlockNo(setNo,associativity);
        int endBlockNo=startBlockNo+associativity-1;
        int tagaddress=TagAddress(address);
        for(int i=startBlockNo;i<=endBlockNo;i++)
        {
            if(tagArray[i]==tagaddress)
            {
                return true;
            }
        }
        return false;
    }
     public void writeforSW(int address,int value)
     {
        String addressString=toBinary(address);
        int offsetvalue=Returnint(addressString,10-offset,9);
        int indexvalue=Returnint(addressString,10-offset-indexBits,9-offset);
        int setNo=indexvalue;
        int startBlockNo=StartBlockNo(setNo,associativity);
        int endBlockNo=startBlockNo+associativity-1;
        int tagaddress=TagAddress(address);
        for(int i=startBlockNo;i<=endBlockNo;i++)
        {
            if(tagArray[i]==tagaddress)
            {
                int p=i*noofvaluesinablock+offsetvalue;
                dataArray[p]=value;
            }
        }
    }
     public int read(int address) 
     {
        String addressString=toBinary(address);
        int offsetvalue=Returnint(addressString,10-offset,9);
        int indexvalue=Returnint(addressString,10-offset-indexBits,9-offset);
        int setNo=indexvalue;
        int startBlockNo=StartBlockNo(setNo,associativity);
        int endBlockNo=startBlockNo+associativity-1;
        int tagaddress=TagAddress(address);
        time++;
         for(int i=startBlockNo;i<=endBlockNo;i++)
        {
            if(tagArray[i]==tagaddress)
            {
                timeStamp[i] = time;
                int add = i*noofvaluesinablock+offsetvalue;
                return dataArray[add];
            }
        }
        return INT_MAX;
    }
     public int blockAddrToReplace(int setNo)
     {
    	int start = StartBlockNo(setNo,associativity);
        int end = start+associativity-1;
        int flag =0;
        int i;
        for(i = start; i <= end; i++)
        {
            if(tagArray[i] == -1)
            {
                flag = 1;
                break;
            }
        }
        if(flag==1)
        {
            return i;
        }
        else
        {
            return getSmallest(timeStamp, start, end);
        }
    }
   public void writeforLW(int address,int[] value)
    {
        String addressString = toBinary(address);
        int indexvalue = Returnint(addressString,10-offset-indexBits,9-offset);
        int setNo = indexvalue;
        int addr = blockAddrToReplace(setNo);
        tagArray[addr] = address;
        time++;
        timeStamp[addr] = time;
        int startIndex = StartIndex(addr, noofvaluesinablock);
        for(int i=0; i<noofvaluesinablock; i++)
        {
            dataArray[startIndex+i] = value[i];
        }
    }
}