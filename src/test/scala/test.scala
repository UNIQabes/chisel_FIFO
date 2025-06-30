import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers



class SevenSegment_Spec extends AnyFreeSpec with Matchers with ChiselSim {
	val fifo_1to7StreamTest = { dut : FIFO_Base =>
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
	val fifo_RandomReadWriteTest ={ dut : ReaderOut =>
		var validReadCnt=0
		var clkCnt=0
		while(validReadCnt<100 & clkCnt<1000){
			clkCnt+=1
			if(dut.io.validRead.peekValue().asBigInt == 1){
				println("%d : %d".format(clkCnt,dut.io.outForCheck.peekValue().asBigInt))
				validReadCnt+=1
			}
			dut.clock.step()
		}	
	}
	
	
		
	"fifo_1to7StreamTest" - {
		"BubbleFIFO" - {
			"depth1" in {
				simulate(new BubbleFifo(8,1)){ dut =>
					fifo_1to7StreamTest.apply(dut)
				}
			}
			"depth2" in {
				simulate(new BubbleFifo(8,2)){ dut =>
					fifo_1to7StreamTest.apply(dut)
				} 
			}
			"depth10" in {
				simulate(new BubbleFifo(8,10)){ dut =>
					fifo_1to7StreamTest.apply(dut)
				} 
			}

		}
		"CircularFIFO" - {
			"depth2" in {
				
				simulate(new CircularFifo(8,2)){ dut =>
					println("------CircularFIFO-------")
					fifo_1to7StreamTest.apply(dut)
				} 
			}
			"depth10" in {
				simulate(new CircularFifo(8,10)){ dut =>
					fifo_1to7StreamTest.apply(dut)
				} 
			}

		}
	}




	"RandomTester" - {
		"SameWrite : SameRead" in {
			simulate(new TestModule(4,4,10)){ dut =>
				println("4:4")
				fifo_RandomReadWriteTest.apply(dut)
				println("Fin")
			} 
		}
		"SlowWrite : FastWrite" in {
			simulate(new TestModule(8,4,10)){ dut =>
				println("8:4")
				fifo_RandomReadWriteTest.apply(dut)
				println("Fin")
			} 
		}
		"FastWrite : SlowRead" in {
			simulate(new TestModule(4,8,10)){ dut =>
				println("4:8")
				fifo_RandomReadWriteTest.apply(dut)
				println("Fin")
			} 
		}

	}




	"PeriodicRandomCombination" - {
		"PeriodicWrite : RandomRead" in {
			simulate(new Test_PR_Module(100,4,128)){ dut =>
				println("4:4")
				fifo_RandomReadWriteTest.apply(dut)
				println("Fin")
			} 
		}
		"RandomWrite : PeriodicRead" in {
			simulate(new Test_RP_Module(4,100,128)){ dut =>
				println("8:4")
				fifo_RandomReadWriteTest.apply(dut)
				println("Fin")
			} 
		}
	}
	
}