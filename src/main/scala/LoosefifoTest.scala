import chisel3._
import circt.stage.ChiselStage

class TestRbuf(size : Int) extends ReaderOut(size){
	val writer=Module(new LooseWriter(size))
	val reader=Module(new LooseReader(size))
	val fifo=Module(new CircularFIFO_LooseIn_LooseOut(size,20))

	writer.io.out <> fifo.io.in
	fifo.io.out <> reader.io.in

	io.outForCheck := reader.io.outd
	io.validRead := reader.io.outv
}

class BurstInOut(size :Int, burstclk_in : Int, intervalclk_in:Int, burstclk_out : Int, intervalclk_out:Int) extends ReaderOut(size){
	val writer=Module(new BurstLooseWriter(size,burstclk_in,intervalclk_in,0))
	val reader=Module(new BurstLooseReader(size,burstclk_out,intervalclk_out,burstclk_out))
	val fifo=Module(new CircularFIFO_LooseIn_LooseOut(size,20))

	writer.io.out <> fifo.io.in
	fifo.io.out <> reader.io.in

	io.outForCheck := reader.io.outd
	io.validRead := reader.io.outv
}

class BurstInOut_Rand(size :Int, burstclk_in : Int, intervalclk_in:Int, burstclk_out : Int, intervalclk_out:Int) extends ReaderOut(size){
	val writer=Module(new BurstLooseWriter_Rand(size,burstclk_in,intervalclk_in,0))
	val reader=Module(new BurstLooseReader_Rand(size,burstclk_out,intervalclk_out,burstclk_out))
	val fifo=Module(new CircularFIFO_LooseIn_LooseOut(size,20))

	writer.io.out <> fifo.io.in
	fifo.io.out <> reader.io.in

	io.outForCheck := reader.io.outd
	io.validRead := reader.io.outv
}
