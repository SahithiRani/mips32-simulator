package mips;
public class Processor
{
    private static Processor processor;
    @SuppressWarnings("unused")
    private FileReader fileReader;
    private Register register;
    private Memory memory = new Memory();
    static int l1cachesize, l1blocksize, l1associativity;
    static int l2cachesize, l2blocksize, l2associativity;
    Cache l1cache;
    Cache l2cache;
     public Processor (int l1cachesize, int l1blocksize, int l1associativity, int l2cachesize, int l2blocksize, int l2associativity) 
     {
       fileReader   = FileReader.getInstance();
        register = Register.getInstance();
        Processor.l1cachesize = l1cachesize;
        Processor.l1blocksize = l1blocksize;
        Processor.l1associativity = l1associativity;
        this.l1cache = new Cache(l1cachesize, l1blocksize, l1associativity);
        Processor.l2cachesize = l2cachesize;
        Processor.l2blocksize = l2blocksize;
        Processor.l2associativity = l2associativity;
        this.l2cache = new Cache(l2cachesize, l2blocksize, l2associativity);
      }
    public static synchronized Processor getInstance(int l1cachesize, int l1blocksize, int l1associativity, int l2cachesize, int l2blocksize, int l2associativity) 
    {
        if (processor == null)
            processor = new Processor(l1cachesize, l1blocksize, l1associativity, l2cachesize, l2blocksize, l2associativity);
        return processor;
    }
    public int executeInstruction(int[] instruction, Memory data) 
    {
        memory = data;
        int opCode = instruction[0];
        if (opCode == 0) {
            int rs = instruction[1];
            int rt = instruction[2];
            int rd = instruction[3];

            int func = instruction[5];

            switch (func) {
                case 32:
                    processor.add(rd, rs, rt);
                    break;
                case 34:
                    processor.sub(rd, rs, rt);
                    break;
                case 36:
                    processor.and(rd, rs, rt);
                    break;
                case 37:
                    processor.or(rd, rs, rt);
                    break;
                case 39:
                    processor.mul(rd, rs, rt);
                    break;
                case 42:
                    processor.slt(rd, rs, rt);
                    break;
            }
            return -1;
        } 
        else if (opCode == 2) 
        {
            return instruction[1];
        } 
        else 
        {
            int rs = instruction[1];
            int rd = instruction[2];
            int address = instruction[3];
            switch (opCode) 
            {
                case 4:
                    return processor.beq(rd, rs, address);
                case 5:
                    return processor.bne(rd, rs, address);
                case 8:
                    processor.addi(rd, rs, address);
                    break;
                case 9:
                    processor.subi(rd, rs, address);
                    break;
                case 35:
                    processor.lw(rd, rs, address);
                    break;
                case 43:
                    processor.sw(rd, rs, address);
                    break;
            }
             return -1;
        }
    }
    private void add(int rd, int rs, int rt)
   {
        register.write(rd, register.read(rs) + register.read(rt));
    }
    private void sub(int rd, int rs, int rt)
    {
        register.write(rd, register.read(rs) - register.read(rt));
    }
   private void mul(int rd, int rs, int rt) 
   {
        register.write(rd, register.read(rs) * register.read(rt));
    }
    private void and(int rd, int rs, int rt) 
    {
        register.write(rd, register.read(rs) & register.read(rt));
    }
    private void or(int rd, int rs, int rt)
    {
        register.write(rd, register.read(rs) | register.read(rt));
    }
     private void slt(int rd, int rs, int rt)
     {
        boolean isSlt = register.read(rs) < register.read(rt);
        register.write(rd, (isSlt) ? 1 : 0);
    }

    private int beq(int rs, int rt, int address) {
        if (register.read(rs) == register.read(rt))
            return address;
        else return -1;
    }
    private int bne(int rs, int rt, int address) 
    {
        if (register.read(rs) != register.read(rt))
            return address;
        else return -1;
    }
   private void addi(int rd, int rs, int constant)
    {
        register.write(rd, register.read(rs) + constant);
    }
     private void subi(int rd, int rs, int constant)
     {
        register.write(rd, register.read(rs) - constant);
    }
    private void lw(int rd, int rs, int address) 
    {
       if(l1cache.cacheHit((register.read(rs)) + address) == true)
       {
             register.write(rd, l1cache.read((register.read(rs)) + address));
        } 
       else if(l2cache.cacheHit((register.read(rs)) + address) == true) 
       {
             register.write(rd, l2cache.read((register.read(rs)) + address));
        }
       else
       {
            int[] valuesl1 = new int[l1cache.noofvaluesinablock];
            int addl1 = l1cache.TagAddress(register.read(rs)+ address);
            for(int i=0; i<l1cache.noofvaluesinablock; i++) 
            {
                valuesl1[i] = memory.read(addl1+i);
            }
            l1cache.writeforLW(addl1, valuesl1);
             int[] valuesl2 = new int[l2cache.noofvaluesinablock];
            int addl2 = l2cache.TagAddress(register.read(rs)+ address);
            for(int i=0; i<l2cache.noofvaluesinablock; i++) 
            {
                valuesl2[i] = memory.read(addl2+i);
            }
            l2cache.writeforLW(addl2, valuesl2);
            register.write(rd, memory.read((register.read(rs)) + address));
        }
    }
   private void sw(int rd, int rs, int address)
   {
        if(l1cache.cacheHit(register.read(rs) +address) == true)
        {
            l1cache.writeforSW(register.read(rs) + address, register.read(rd));
        }
        if(l2cache.cacheHit(register.read(rs) +address)== true)
        {
            l2cache.writeforSW(register.read(rs) + address, register.read(rd));
        }
        memory.write(register.read(rs) + address, register.read(rd));
    }
    public boolean l1hit(int[] instruction)
    {
        int rs = instruction[1];
        int address = instruction[3];
         if(l1cache.cacheHit((register.read(rs)) + address) == true)
        {
            return true;
        }
        return false;
    }
    public boolean l2hit(int[] instruction)
    {
        int rs = instruction[1];
        int address = instruction[3];
         if(l2cache.cacheHit((register.read(rs)) + address) == true)
        {
            return true;
        }
        return false;
    }
}