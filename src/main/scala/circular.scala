/*
 * Author: Martin Schoeberl (martin@jopdesign.com)
 *
 * Play with FIFO buffers.
 *
 */


import chisel3._
import chisel3.util._

/*
 * On signal naming:
 * 
 * Alter's FIFO component:
 * 
 * data - data in, q - data out, wrreq and rdreq
 * state: full and empty
 * 
 * Xilinx's FIFO component:
 * din and dout, wr_en, rd_en
 * state: full and empty
 * 
 */


/**
 * A single register (=stage) to build the FIFO.
 */

class CircularFifo(size: Int, depth: Int) extends Module {
  val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })
  val empty :: full :: mid :: Nil = Enum(3)

  
  val buffer=Reg(Vec(depth,UInt(size.W)))
  val state = RegInit(empty)
  val outPtr = RegInit(0.U(log2Ceil(depth).W))
  val inPtr = RegInit(0.U(log2Ceil(depth).W))

  
  val isPopping=WireInit(false.B)
  val isPushed=WireInit(false.B)

  io.enq.full := (state === full)
  io.deq.empty := (state === empty)

  
  io.deq.dout := buffer(outPtr) 
  // read && not empty -> actually read
  when ( state =/= empty && io.deq.read ){
	isPopping := true.B
	io.deq.dout := buffer(outPtr) 
	outPtr := (outPtr+1.U) % size.U
  }
  
  // read && not empty -> actually write
  when(state =/= full && io.enq.write){
	isPushed:=true.B
	buffer(inPtr) := io.enq.din
	inPtr := (inPtr+1.U) % size.U
  }
  

  // | inptr | outptr |    --nextClock-->    | | inptr outptr |  の場合はnextclockでfull
  when ( !isPopping && isPushed && ((size.U + outPtr - inPtr) % size.U === 1.U) ){
	state:=full
  }
  // | outptr | inptr |    --nextClock-->    | | outptr inptr | の場合はnextclockでempty
  .elsewhen( isPopping && !isPushed && ((size.U + inPtr - outPtr) % size.U === 1.U) ){
	state:=empty
  }
  .elsewhen(isPushed || isPopping){
	state:=mid
  }
  

}