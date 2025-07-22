


import chisel3._
import chisel3.util._

class ValRed_out(size: Int) extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val data = Output(UInt(size.W))
}
class ValRed_in(size: Int) extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val data = Input(UInt(size.W))
}


//厳密にはvalid readyのプロトコルではない。
//validがついているデータは基本受け入れる
//not readyは下流からのストール命令

/*  
FIFOへの書き込み側の仕様
1, readyがTrueの時以外ではデータを保存するとは限らない
2, ただし、readyをdisableしてから2回分の入力は保存することを保証する
例)
disableしたクロック+0 : ready :false din : 受け入れる
disableしたクロック+1 : ready :false din : 受け入れる
disableしたクロック+2 : ready :false din : 受け入れるとは限らない

FIFOから読み込む側の仕様
1, readyがenableされてから2クロック後に有効なデータを出力し始める
2, readyをdisableされてから2回分は有効なデータを出力し続ける
disableされたクロック-1 : ready :true
disableされたクロック+0 : ready :false dout・valid : 有効なデータを出力
disableされたクロック+1 : ready :false dout・valid : 有効なデータを出力
disableされたクロック+2 : ready :false dout・valid : 有効なデータを出力しない
*/
class CircularFIFO_LooseIn_LooseOut(size: Int, depth: Int) extends Module {
	val io = IO(new Bundle{
		val in = new ValRed_in(size)
		val out = new ValRed_out(size)
	})

	val in_valid_Reg=RegInit(false.B)
	val in_ready_Reg=RegInit(false.B)
	val in_data_Reg=RegInit(0.U(size.W))

	val out_valid_Reg=RegInit(false.B)
	val out_ready_Reg=RegInit(false.B)
	val out_data_Reg=RegInit(0.U(size.W))

	in_data_Reg:=io.in.data
	in_valid_Reg:=io.in.valid
	io.in.ready:=in_ready_Reg

	io.out.data:=out_data_Reg
	io.out.valid:=out_valid_Reg
	out_ready_Reg:=io.out.ready

	val buffer=Reg(Vec((depth+1),UInt(size.W)))
	val outPtr = RegInit(0.U(log2Ceil(depth+1).W))
	val inPtr = RegInit(0.U(log2Ceil(depth+1).W))

	//in側(FIFOへの書き込み)の処理
	val restSpaceNum=Wire(UInt(size.W))
	restSpaceNum := ((outPtr+depth.U+1.U)-inPtr) % (depth+1).U 
	in_ready_Reg:= ~(restSpaceNum <= 4.U)
	val isFull=Wire(Bool())
	isFull := restSpaceNum===1.U
	when( isFull & in_valid_Reg){
		buffer(inPtr):=in_data_Reg
		inPtr:=(inPtr+1.U)%(depth+1).U
	}

	//out側(FIFO読み込み)の処理
	val isEmpty=Wire(Bool())
	isEmpty:=(outPtr === inPtr)
	when( ~isEmpty & out_ready_Reg ){
		out_valid_Reg := true.B
		out_data_Reg := buffer(outPtr)
		outPtr := (outPtr+1.U)%(depth+1).U
	}
	.otherwise{
		out_valid_Reg := false.B
	}
}



/*  
FIFOへの書き込み側の仕様(上と同じ)
1, readyがTrueの時以外ではデータを保存するとは限らない
2, ただし、readyをdisableしてから2回分の入力は保存することを保証する
例)
disableしたクロック+0 : ready :false din : 受け入れる
disableしたクロック+1 : ready :false din : 受け入れる
disableしたクロック+2 : ready :false din : 受け入れるとは限らない

FIFOから読み込む側の仕様
1, readyがenableされてから2クロック後に有効なデータを出力し始める(1クロックおきにトグルしている場合などは有効なデータを出力しない)
2, readyがdisableされてからは有効なデータを出力しない(=内部的にはポインタのロールバックが行われる)
*/
class CircularFIFO_LooseIn_StrictOut(size: Int, depth: Int) {
	val io = IO(new Bundle{
		val in = new ValRed_in(size)
		val out = new ValRed_out(size)
	})

	val empty :: full :: mid :: Nil = Enum(3)
	
	val buffer=Reg(Vec(depth,UInt(size.W)))

	val outPtr = RegInit(0.U(log2Ceil(depth).W))
	val inPtr = RegInit(0.U(log2Ceil(depth).W))
}

