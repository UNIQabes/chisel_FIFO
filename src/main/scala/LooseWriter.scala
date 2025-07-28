import chisel3._
import circt.stage.ChiselStage
import chisel3.util



//readyがfalseになった後も、2クロック書き込むようなwriter
//書き込む時以外、valid = false

class LooseWriter(size:Int) extends Module{

	val io = IO ( new Bundle{
		val out = new ValRed_out(size)
	})

	val out_valid_Reg=RegInit(false.B)
	val out_ready_Reg=RegInit(false.B)
	val out_data_Reg=RegInit(0.U(size.W))

	val cnt = RegInit( 0.U(size.W) );

	io.out.data:=out_data_Reg
	io.out.valid:=out_valid_Reg
	out_ready_Reg:=io.out.ready

	when( out_ready_Reg ){
		out_valid_Reg:=true.B
		out_data_Reg:=cnt
		cnt := (cnt+1.U)%100.U
	}
	.otherwise{
		out_valid_Reg:=false.B
	}
}



class LooseReader(size:Int) extends Module{
	val io = IO ( new Bundle{
		val in = new ValRed_in(size)
		val outd = Output(UInt(size.W))
		val outv = Output(Bool())
	})

	val in_valid_Reg=RegInit(false.B)
	val in_ready_Reg=RegInit(false.B)
	val in_data_Reg=RegInit(0.U(size.W))

	val cnt = RegInit( 0.U(size.W) );

	in_data_Reg:=io.in.data
	in_valid_Reg:=io.in.valid
	io.in.ready:=in_ready_Reg

	in_ready_Reg:=true.B

	io.outd:=in_data_Reg
	io.outv:=in_valid_Reg	
}

class BurstLooseWriter (size :Int, burstclk : Int, intervalclk:Int, offsetclk:Int) extends Module{
	val io = IO ( new Bundle{
		val out = new ValRed_out(size)
	})

	val out_valid_Reg=RegInit(false.B)
	val out_ready_Reg=RegInit(false.B)
	val out_data_Reg=RegInit(0.U(size.W))

	val cnt = RegInit( 0.U(size.W) );
	val periodicCnt=RegInit( (offsetclk%(burstclk+intervalclk)).U((util.log2Ceil(burstclk+intervalclk)).W)) ;

	io.out.data:=out_data_Reg
	io.out.valid:=out_valid_Reg
	out_ready_Reg:=io.out.ready

	out_valid_Reg:=false.B
	when( periodicCnt <  burstclk.U){
		when( out_ready_Reg ){
			out_valid_Reg:=true.B
			out_data_Reg:=cnt
			cnt := (cnt+1.U)%100.U
			periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
		}
	}.otherwise{
		periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
	}
}

class BurstLooseReader(size:Int, burstclk : Int, intervalclk:Int, offsetclk:Int) extends Module{
	val io = IO ( new Bundle{
		val in = new ValRed_in(size)
		val outd = Output(UInt(size.W))
		val outv = Output(Bool())
	})

	val in_valid_Reg=RegInit(false.B)
	val in_ready_Reg=RegInit(false.B)
	val in_data_Reg=RegInit(0.U(size.W))

	val cnt = RegInit( 0.U(size.W) );
	val periodicCnt=RegInit( (offsetclk%(burstclk+intervalclk)).U((util.log2Ceil(burstclk+intervalclk)).W)) ;
	
	when( periodicCnt <  burstclk.U ){
		in_ready_Reg:=true.B
		when( in_valid_Reg ){
			periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
		}
		
	}.otherwise{
		in_ready_Reg:=false.B
		periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
	}

	in_data_Reg:=io.in.data
	in_valid_Reg:=io.in.valid
	io.in.ready:=in_ready_Reg


	io.outd:=in_data_Reg
	io.outv:=in_valid_Reg	
}



class BurstLooseWriter_Rand (size :Int, burstclk : Int, intervalclk:Int, offsetclk:Int) extends Module{
	val io = IO ( new Bundle{
		val out = new ValRed_out(size)
	})

	val pseudoRandomNumber = util.random.LFSR(size) 

	val out_valid_Reg=RegInit(false.B)
	val out_ready_Reg=RegInit(false.B)
	val out_data_Reg=RegInit(0.U(size.W))

	val curCycleBurst=RegInit(burstclk.U(size.W))

	val cnt = RegInit( 0.U(size.W) );
	val periodicCnt=RegInit( (offsetclk%(burstclk+intervalclk)).U((util.log2Ceil(burstclk+intervalclk)).W)) ;

	io.out.data:=out_data_Reg
	io.out.valid:=out_valid_Reg
	out_ready_Reg:=io.out.ready

	out_valid_Reg:=false.B
	when(periodicCnt === (burstclk+intervalclk).U-1.U){
		curCycleBurst:=pseudoRandomNumber % (2*burstclk).U
	}
	when( periodicCnt <  curCycleBurst){
		when( out_ready_Reg ){
			out_valid_Reg:=true.B
			out_data_Reg:=cnt
			cnt := (cnt+1.U)%100.U
			periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
		}
	}.otherwise{
		
		periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
	}
}



class BurstLooseReader_Rand(size:Int, burstclk : Int, intervalclk:Int, offsetclk:Int) extends Module{
	val io = IO ( new Bundle{
		val in = new ValRed_in(size)
		val outd = Output(UInt(size.W))
		val outv = Output(Bool())
	})

	val pseudoRandomNumber = util.random.LFSR(size) 

	val in_valid_Reg=RegInit(false.B)
	val in_ready_Reg=RegInit(false.B)
	val in_data_Reg=RegInit(0.U(size.W))

	val curCycleBurst=RegInit(burstclk.U(size.W))

	val cnt = RegInit( 0.U(size.W) );
	val periodicCnt=RegInit( (offsetclk%(burstclk+intervalclk)).U((util.log2Ceil(burstclk+intervalclk)).W)) ;

	when(periodicCnt === (burstclk+intervalclk).U-1.U){
		curCycleBurst:=pseudoRandomNumber % (2*burstclk).U
	}
	
	when( periodicCnt <  curCycleBurst ){
		in_ready_Reg:=true.B
		when( in_valid_Reg ){
			periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
		}
		
	}.otherwise{
		in_ready_Reg:=false.B
		periodicCnt:=(periodicCnt+1.U)%(burstclk+intervalclk).U
	}

	in_data_Reg:=io.in.data
	in_valid_Reg:=io.in.valid
	io.in.ready:=in_ready_Reg


	io.outd:=in_data_Reg
	io.outv:=in_valid_Reg	
}