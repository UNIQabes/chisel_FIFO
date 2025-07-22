import chisel3._
import chisel3.simulator.ChiselSim
import chisel3.simulator.HasSimulator
import chisel3.simulator.stimulus.{ResetProcedure, RunUntilFinished}
import chisel3.util.Counter




/*
object mainObj extends App {
	//ChiselStage.emitSystemVerilogFile(new BubbleFifo(8,1), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))
	//ChiselStage.emitSystemVerilogFile(new CircularFifo (8,4), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))
	//ChiselStage.emitSystemVerilogFile(new TestModule (4,2,58), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))
	//ChiselStage.emitSystemVerilogFile(new CircularFIFO_LooseIn_LooseOut (32,20), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))
	//ChiselStage.emitSystemVerilogFile(new CircularFIFO_LooseIn_StrictOut (32,20), firtoolOpts = Array("-strip-debug-info", "--disable-all-randomization","-default-layer-specialization=enable"))


	//simulator

}
*/

object Main extends App with ChiselSim {

  implicit val verilator: HasSimulator = HasSimulator.simulators
    .verilator(verilatorSettings =
      svsim.verilator.Backend.CompilationSettings(
        traceStyle = Some(
          svsim.verilator.Backend.CompilationSettings.TraceStyle
            .Vcd(traceUnderscore = true, "trace.vcd")
        )
      )
    )

  simulateRaw((new TestRbuf(32))) { dut =>
    enableWaves()
    ResetProcedure.module()(dut)
    //RunUntilFinished(1000)(dut)
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
}