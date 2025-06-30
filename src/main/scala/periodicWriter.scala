
import  chisel3._
import circt.stage.ChiselStage
import chisel3.util

class PeriodicWriter(cycle:Int,burstLen:Int) extends Module{
	val size=8
	val io=IO(new Bundle{
		val write = Output(Bool())
		val full = Input(Bool())
		val din = Output(UInt(size.W))
	})
	val cyclecnt=RegInit(0.U(util.log2Ceil(cycle+1).W))
	val cnt=RegInit(0.U(size.W))

	
	
	//デフォルトの動作
	when(cyclecnt===(cycle-1).U){
		cyclecnt:=0.U
	}.otherwise{
		cyclecnt:=cyclecnt+1.U
	}

	//when(cyclecnt === (pseudoRandomNumber % cycle.U)){
	//when(0.U === (pseudoRandomNumber % cycle.U)){
	when(cyclecnt < burstLen.U){
		io.write:=true.B
		when(!io.full){
			cyclecnt:=cyclecnt
			cnt :=cnt+1.U
		}
	}.otherwise{
		io.write:=false.B
	}
	io.din := cnt	
}