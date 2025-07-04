import chisel3._
import circt.stage.ChiselStage
import chisel3.util._


class WriterIO(size: Int) extends Bundle {
  val write = Input(Bool())
  val full = Output(Bool())
  val din = Input(UInt(size.W))
}

class ReaderIO(size: Int) extends Bundle {
  val read = Input(Bool())
  val empty = Output(Bool())
  val dout = Output(UInt(size.W))
}

class WriterIO_OnWriter(size: Int) extends Bundle {
  val write = Output(Bool())
  val full = Input(Bool())
  val din = Output(UInt(size.W))
}

class ReaderIO_OnReader(size: Int) extends Bundle {
  val read = Output(Bool())
  val empty = Input(Bool())
  val dout = Input(UInt(size.W))
}

abstract  class FIFO_Base(size: Int) extends Module {
  val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })
}

/**
 * A single register (=stage) to build the FIFO.
 */
class FifoRegister(size: Int) extends FIFO_Base(size) {


  val empty :: full :: Nil = Enum(2)
  val stateReg = RegInit(empty)
  val dataReg = RegInit(0.U(size.W))

  when(stateReg === empty) {
    when(io.enq.write) {
      stateReg := full
      dataReg := io.enq.din
    }
  }.elsewhen(stateReg === full) {
    when(io.deq.read) {
      stateReg := empty
      dataReg := 0.U // just to better see empty slots in the waveform
    }
  }.otherwise {
    // There should not be an otherwise state
  }

  io.enq.full := (stateReg === full)
  io.deq.empty := (stateReg === empty)
  io.deq.dout := dataReg
}
/**
 * This is a bubble FIFO.
 */
class BubbleFifo(size: Int, depth: Int) extends FIFO_Base(size) {


  val buffers = Array.fill(depth) { Module(new FifoRegister(size)) }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq.din := buffers(i).io.deq.dout
    buffers(i + 1).io.enq.write := ~buffers(i).io.deq.empty
    buffers(i).io.deq.read := ~buffers(i + 1).io.enq.full
  }
  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq
}




object mainObj extends App {
	ChiselStage.emitSystemVerilogFile(new BubbleFifo(8,1), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))
	ChiselStage.emitSystemVerilogFile(new CircularFifo (8,4), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))
	ChiselStage.emitSystemVerilogFile(new TestModule (4,2,58), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))
}