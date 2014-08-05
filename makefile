all:
	javac -g:none -Xlint:all,-serial -d class sim/Sim.java

run:
	java -cp class sim.Sim asmFiles/life.asm
