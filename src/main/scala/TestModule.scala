import chisel3._
import circt.stage.ChiselStage

abstract class ReaderOut(size:Int) extends Module{
	val io=IO(new Bundle {
		val outForCheck=Output(UInt(size.W))
		val validRead = Output(Bool())
	})
}



class TestModule(writeCycle:Int,readCycle:Int,bufferSize:Int) extends ReaderOut(8){
	val size=8

	val writer = Module(new RandomWriter(writeCycle))
	val reader =  Module(new RandomReader(readCycle))
	val fifo= Module(new CircularFifo(size,bufferSize))
	writer.io<>fifo.io.enq
	fifo.io.deq<>reader.io.deq
	io.outForCheck := reader.io.outForCheck
	io.validRead := reader.io.validRead
}

class Test_PR_Module(writeBurstLen:Int,readCycle:Int,bufferSize:Int) extends ReaderOut(8){
	val size=8

	val writer = Module(new PeriodicWriter(3*writeBurstLen,writeBurstLen))
	val reader =  Module(new RandomReader(readCycle))
	val fifo= Module(new CircularFifo(size,bufferSize))
	writer.io<>fifo.io.enq
	fifo.io.deq<>reader.io.deq
	io.outForCheck := reader.io.outForCheck
	io.validRead := reader.io.validRead
}
class Test_RP_Module(writeCycle:Int,readBurstLen:Int,bufferSize:Int) extends ReaderOut(8){
	val size=8

	val writer = Module(new RandomWriter(writeCycle))
	val reader =  Module(new PeriodicReader(readBurstLen*3,readBurstLen))
	val fifo= Module(new CircularFifo(size,bufferSize))
	writer.io<>fifo.io.enq
	fifo.io.deq<>reader.io.deq
	io.outForCheck := reader.io.outForCheck
	io.validRead := reader.io.validRead
}