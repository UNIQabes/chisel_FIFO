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