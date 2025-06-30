//wireはレジスタ値が確定した後にも計算を行うんだよな。
//回路も、Stateの遷移で記述できるところはプログラミングと同じ。
//その代わり、計算回数に限りがある。
//計算がwire+計算で、Regが各ループの開始時の状態にあたるが、
//値の更新はクロック立ち上がり時に行われ、そこから計算が走り、その結果が次のRegの入力まで届き、その結果の更新は、次のクロック立ち上がりに行われる。
//つまり、クロック立ち上がり->更新->計算 というループになり、微妙にプログラムとは考え方が違う。
//計算結果がそのまま出力となり、状態が出力になるわけではない。

//状態の更新と出力の計算は別ルートとして捉えた方が良いかも

import  chisel3._
import circt.stage.ChiselStage
import chisel3.util

class RandomWriter(cycle:Int) extends Module{
	val size=8
	val io=IO(new Bundle{
		val write = Output(Bool())
		val full = Input(Bool())
		val din = Output(UInt(size.W))
	})
	val pseudoRandomNumber = util.random.LFSR(size) 
	val cyclecnt=RegInit(0.U(util.log2Ceil(cycle+1).W))
	val cnt=RegInit(0.U(size.W))
	val writeTiming=RegInit(0.U(util.log2Ceil(cycle+1).W))

	
	
	//デフォルトの動作
	when(cyclecnt===0.U){
		cyclecnt:=(cycle-1).U
		writeTiming:= (pseudoRandomNumber % cycle.U)
	}.otherwise{
		cyclecnt:=cyclecnt-1.U
	}

	//when(cyclecnt === (pseudoRandomNumber % cycle.U)){
	//when(0.U === (pseudoRandomNumber % cycle.U)){
	when(cyclecnt === writeTiming){
		io.write:=true.B
		when(!io.full){
			cyclecnt:=cyclecnt
			cnt :=cnt+1.U
		}
	}.otherwise{
		io.write:=false.B
	}
	io.din := cnt	
}