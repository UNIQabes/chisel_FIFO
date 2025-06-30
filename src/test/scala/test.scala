import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers



class SevenSegment_Spec extends AnyFreeSpec with Matchers with ChiselSim {
	"BubbleFIFO" - {
		"depth1" in {
			simulate(new BubbleFifo(8,1)){ dut =>
				dut.reset.poke(true.B)
				dut.clock.step()
				dut.reset.poke(false.B)
				dut.clock.step()
				var inputAry_ptr = 0
				val inputAry  = Array(1,2,3,4,5,6,7)
				var outputcnt=0
				
				while(inputAry_ptr<inputAry.length || outputcnt<inputAry.length){
					if(inputAry_ptr<inputAry.length){
						dut.io.enq.din.poke( BigInt(inputAry(inputAry_ptr)) )
						dut.io.enq.write.poke( true )
					}
					if( dut.io.enq.full.peekValue().asBigInt == 0 ){
						println("in : %d".format(inputAry(inputAry_ptr)))
						inputAry_ptr+=1
					}
					dut.io.deq.read.poke(true)
					if(dut.io.deq.empty.peekValue().asBigInt==0){
						val outVal= dut.io.deq.dout.peekValue().asBigInt
						println("out: %d".format(outVal))
						outputcnt +=1  
					}
					dut.clock.step()
					println("------clock-------")
				}
			} 
			println("------FIN-------")
		}
		"depth2" in {
			simulate(new BubbleFifo(8,2)){ dut =>
				dut.reset.poke(true.B)
				dut.clock.step()
				dut.reset.poke(false.B)
				dut.clock.step()
				var inputAry_ptr = 0
				val inputAry  = Array(1,2,3,4,5,6,7)
				var outputcnt=0
				
				while(inputAry_ptr < inputAry.length || outputcnt<inputAry.length){
					if(inputAry_ptr<inputAry.length){
						dut.io.enq.din.poke( BigInt(inputAry(inputAry_ptr)) )
						dut.io.enq.write.poke( true )
						if( dut.io.enq.full.peekValue().asBigInt == 0 ){
							println("in : %d".format(inputAry(inputAry_ptr)))
							inputAry_ptr+=1
						}
					}
					
					dut.io.deq.read.poke(true)
					if(dut.io.deq.empty.peekValue().asBigInt==0){
						val outVal= dut.io.deq.dout.peekValue().asBigInt
						println("out: %d".format(outVal))
						outputcnt +=1  
					}
					dut.clock.step()
					println("------clock-------")
				}
			} 
		}
		"depth10" in {
			simulate(new BubbleFifo(8,10)){ dut =>
				dut.reset.poke(true.B)
				dut.clock.step()
				dut.reset.poke(false.B)
				dut.clock.step()
				var inputAry_ptr = 0
				val inputAry  = Array(1,2,3,4,5,6,7)
				var outputcnt=0
				
				while(inputAry_ptr<inputAry.length || outputcnt<inputAry.length){
					if(inputAry_ptr<inputAry.length){
						dut.io.enq.din.poke( BigInt(inputAry(inputAry_ptr)) )
						dut.io.enq.write.poke( true )
						if( dut.io.enq.full.peekValue().asBigInt == 0 ){
							println("in : %d".format(inputAry(inputAry_ptr)))
							inputAry_ptr+=1
						}
					}
					
					dut.io.deq.read.poke(true)
					if(dut.io.deq.empty.peekValue().asBigInt==0){
						val outVal= dut.io.deq.dout.peekValue().asBigInt
						println("out: %d".format(outVal))
						outputcnt +=1  
					}
					dut.clock.step()
					println("------clock-------")
				}
			} 
		}

	}
	"CircularFIFO" - {
		
		
		"depth2" in {
			println("------CircularFIFO-------")
			simulate(new CircularFifo(8,2)){ dut =>
				dut.reset.poke(true.B)
				dut.clock.step()
				dut.reset.poke(false.B)
				dut.clock.step()
				var inputAry_ptr = 0
				val inputAry  = Array(1,2,3,4,5,6,7)
				var outputcnt=0
				
				while(inputAry_ptr < inputAry.length || outputcnt<inputAry.length){
					if(inputAry_ptr<inputAry.length){
						dut.io.enq.din.poke( BigInt(inputAry(inputAry_ptr)) )
						dut.io.enq.write.poke( true )
						if( dut.io.enq.full.peekValue().asBigInt == 0 ){
							println("in : %d".format(inputAry(inputAry_ptr)))
							inputAry_ptr+=1
						}
					}
					
					dut.io.deq.read.poke(true)
					if(dut.io.deq.empty.peekValue().asBigInt==0){
						val outVal= dut.io.deq.dout.peekValue().asBigInt
						println("out: %d".format(outVal))
						outputcnt +=1  
					}
					dut.clock.step()
					println("------clock-------")
				}
			} 
			println("------FIN-------")
		}
		"depth10" in {
			simulate(new CircularFifo(8,10)){ dut =>
				dut.reset.poke(true.B)
				dut.clock.step()
				dut.reset.poke(false.B)
				dut.clock.step()
				var inputAry_ptr = 0
				val inputAry  = Array(1,2,3,4,5,6,7)
				var outputcnt=0
				
				while(inputAry_ptr<inputAry.length || outputcnt<inputAry.length){
					if(inputAry_ptr<inputAry.length){
						dut.io.enq.din.poke( BigInt(inputAry(inputAry_ptr)) )
						dut.io.enq.write.poke( true )
						if( dut.io.enq.full.peekValue().asBigInt == 0 ){
							println("in : %d".format(inputAry(inputAry_ptr)))
							inputAry_ptr+=1
						}
					}
					
					dut.io.deq.read.poke(true)
					if(dut.io.deq.empty.peekValue().asBigInt==0){
						val outVal= dut.io.deq.dout.peekValue().asBigInt
						println("out: %d".format(outVal))
						outputcnt +=1  
					}
					dut.clock.step()
					println("------clock-------")
				}
			} 
			println("------FIN-------")
		}

	}
}