

import chisel3._
import circt.stage.ChiselStage
import chisel3.util



class PeriodicReader(cycle:Int,burstLen:Int) extends Module{
	val size=8
	val io=IO(new Bundle{
		val deq = new ReaderIO_OnReader(size)
		val outForCheck=Output(UInt(size.W))
		val validRead = Output(Bool())
	})
	val cyclecnt = RegInit(0.U(util.log2Ceil(cycle+1).W))
	

	val outForCheck_Reg = RegInit(0.U(size.W))
	io.outForCheck := outForCheck_Reg
	val validRead_Reg=RegInit(false.B)
	io.validRead := validRead_Reg

	//デフォルトの動作
	when(cyclecnt===0.U){
		cyclecnt:=(cycle-1).U
	}.otherwise{
		cyclecnt:=cyclecnt-1.U
	}



	validRead_Reg := false.B
	//when(cyclecnt === (pseudoRandomNumber % cycle.U)){
	//when(readTiming === (pseudoRandomNumber % cycle.U)){
	when(cyclecnt < burstLen.U){
		io.deq.read:=true.B
		when(io.deq.empty){
			cyclecnt := cyclecnt
		}
		.otherwise{
			cyclecnt:=cyclecnt-1.U
			outForCheck_Reg := io.deq.dout
			validRead_Reg := true.B
		}
	}.otherwise{
		io.deq.read := false.B
	}
	
}