import chisel3._
import circt.stage.ChiselStage

class TestModule(writeCycle:Int,readCycle:Int,bufferSize:Int) extends Module{
	val size=8
	val io=IO(new Bundle {
		val outForCheck=Output(UInt(size.W))
		val validRead = Output(Bool())
	})
	
	val writer = Module(new RandomWriter(writeCycle))
	val reader =  Module(new RandomReader(readCycle))
	val fifo= Module(new CircularFifo(size,bufferSize))
	writer.io<>fifo.io.enq
	fifo.io.deq<>reader.io.deq
	io.outForCheck := reader.io.outForCheck
	io.validRead := reader.io.validRead
}