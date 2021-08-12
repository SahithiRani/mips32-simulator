.data
array: .word 7 8 4 1 5 3 2
addi $t2,$t2,6
addi $a0,$a0,1
sub $t0,$t0,$a0
sub $t1,$0,$a0
beq $t0,$t2,25
addi $t0,$t0,1
j 10
addi $t1,$t1,1   
sub $t3,$t2,$t0  
beq $t1,$t3,6
addi $t4,$t1,1
lw $t5,0($t1)
lw $t6,0($t4)
slt $s0,$t5,$t6
beq $s0,$0,19
j 10
addi $t7,$t5,0     
addi $t5,$t6,0
addi $t6,$t7,0
sw $t5,0($t1)
sw $t6,0($t4)
j 10
#end