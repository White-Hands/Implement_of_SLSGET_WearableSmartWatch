package com.example.lastwear.WearScheme20240202

import Cfull
import Coff
import ConcatenationForArray
import DisConcatenation
import H1
import H1_
import H2
import H3
import H4
import H5
import H5_
import XorForArray
import it.unisa.dia.gas.jpbc.Element
import it.unisa.dia.gas.jpbc.Pairing


/*
先把所有的元素全部按照顺序制造出来，在一个函数中表达
然后分出“加密者所得到的密钥”，开始制作加密Offline与Online，最好是两个按钮
再然后创造一个解密App，专门放在手机里
加密手表、解密手机、test笔记本，全部实现出来了
************/


data class Initialization(
    //var gen:Pairing = PairingFactory.getPairing(TypeACurveGenerator(160, 160).generate()),
    var gen: Pairing,

    val s: Element = gen.zr.newRandomElement(),
    val P: Element = gen.g1.newRandomElement(),
    val Ppub: Element = P.duplicate().powZn(s),

    val skr: Element = gen.zr.newRandomElement(),
    val pkr: Element = P.duplicate().powZn(skr),

    val skG: Element = gen.zr.newRandomElement(),
    val hkG: Element = Ppub.duplicate().powZn(skG),
    val w: Element = gen.zr.newRandomElement(),
    val Epk: Element = P.duplicate().powZn(w),

    //Test
    /*
    val A : ByteArray = H2( hkG.toBytes(), gen).toBytes(),
    val B : ByteArray =  ID.toBytes(),
    val C : ByteArray = XorForArray(A,B),
     */
    val ID: Element = gen.zr.newElement(12341234),
    val PID: ByteArray = XorForArray(H2(hkG), ID.toBytes()),
    val ti: Element = gen.zr.newRandomElement(),
    val Ai: Element = P.duplicate().powZn(ti),
    val H1PIDAi: Element = H1(PID, Ai, gen),
    val ppki: Element = (s.duplicate().mul(H1PIDAi)).duplicate().add(ti),

    val vi: Element = gen.zr.newRandomElement(),
    val VVi: Element = P.duplicate().powZn(vi),

    val Bi: Element = P.duplicate().powZn(ppki.duplicate().add(vi)),
    val Bi_: Element = Ppub.duplicate().powZn(H1PIDAi).duplicate().mul(Ai).duplicate().mul(VVi),
    val Di: Element = Bi.duplicate().powZn(skG.duplicate().add(w))
)


fun OffSigncryption(p: Initialization): Coff {
    ///////////
    //var time5 = Instant.now()

    val r1 = p.gen.zr.newRandomElement()
    val r2 = p.gen.zr.newRandomElement()

    val vir2 = p.vi.duplicate().mul(r2)
    val T1 = p.Epk.duplicate().powZn(vir2)
    val T2 = p.pkr.duplicate().powZn(vir2)

    val C1 = p.Di.duplicate().powZn(r1)
    val C2 = p.VVi.duplicate().powZn(r2)

    //////
    //println("Offline first: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")

    return Coff(
        C1 = C1,
        C2 = C2,
        T1 = T1,
        T2 = T2,
        r1 = r1,
        r2 = r2
    )
}


fun OnSigncryption(p: Initialization, Coff: Coff, M : Int = 63): Cfull {

    ///////////
    //var time5 = Instant.now()


    //val M: Int = 63
    val C3 = XorForArray(
        H2(Coff.T1),
        H3(M, p.gen).mul(Coff.r1).mul(p.ppki.duplicate().add(p.vi)).toBytes()
    )
    //println("Online C3: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")

    val C4 = XorForArray(H4(Coff.T2), ConcatenationForArray(H3(M, p.gen).toBytes(), p.Ai.toBytes()))
    //println("Online C4: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")
    //println(ConcatenationForArray(  H3(M, p.gen).toBytes(),  p.Ai.toBytes() ).size)
    //println(H4(Coff.T2).size)
    //println(C4.size)

    //time5 = Instant.now()
    var h1 = H5(Coff.C1, Coff.C2, C3, C4, M, Coff.T2, p.Ai, p.VVi, p.gen)
    /******************/
    //h1 = H5_(Coff.C1, Coff.C2, M, Coff.T2, p.Ai, p.VVi, p.gen)
    //println( "${Coff.C1}, ${Coff.C2}, ${C3}, ${C4}, ${M}, ${Coff.T2}, ${p.Ai}, ${p.VVi}, ${p.gen}")
    //println("H1 = $h1")
    //println("Online h1: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")

    //time5 = Instant.now()

    val C5 = (Coff.r2.duplicate().add(h1)).duplicate().mul(p.vi).duplicate()
        .add(p.ppki.duplicate().mul(h1))
    /*
    println("left = ${p.P.duplicate().powZn(C5)}")
    var right1 = p.VVi.duplicate().powZn(h1)
    println("right1 = $right1")
    var right2 = p.P.duplicate().powZn(  p.ppki.duplicate().mul(h1) )
    println("right2 = $right2")
    println("right = ${Coff.C2.duplicate().mul( right1 ).duplicate().mul( right2 )}")
*/
    //////
    // println("Online C5: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")
    return Cfull(Coff.C1, Coff.C2, C3, C4, C5)
}


fun UnSigncryption(
    skr: Element,
    Cfull: Cfull,
    PID: ByteArray,
    VVi: Element,
    gen: Pairing,
    P: Element,
    Ppub: Element
): Element? {

    val T2_ = Cfull.C2.duplicate().powZn(skr)
    val MandA = XorForArray(H4(T2_), Cfull.C4)

    val Mbytes = DisConcatenation(MandA, 0, 160 / 8)
    val M_ = gen.zr.newElementFromBytes(Mbytes)
    val M = M_.toBigInteger().toInt()
    val Abytes = DisConcatenation(MandA, 160 / 8, MandA.size)
    val A = gen.g1.newElementFromBytes(Abytes)

/*
    println("A = $A")
    println("A.hashCode, ${A.toString().hashCode()}")
    println("Ai= $Ai")
    println("Ai.hashCode, ${Ai.toString().hashCode()}")

    println(A.isEqual(Ai))
    */

    /********/
    var h1 = H5(Cfull.C1, Cfull.C2, Cfull.C3, Cfull.C4, M, T2_, A, VVi, gen)
    //println( "${Cfull.C3}, ${Cfull.C4}, ${gen}")
    /**********/
    //h1 = H5_(Cfull.C1, Cfull.C2, M, T2_, A, VVi, gen)

   // println( "${Cfull.C1}, ${Cfull.C2},$M,  $T2_, $A, $VVi, ${gen}")

    //val h1 = H5(Cfull.C1, Cfull.C2, Cfull.C3, Cfull.C4, M, T2_, Ai, VVi, gen)

    //println("H1 = $h1")

    val left = P.duplicate().powZn(Cfull.C5)
    var right1 = VVi.duplicate().powZn(h1)
    var right2 =
        ((Ppub.duplicate().powZn(H1(PID, A, gen))).duplicate().mul(A)).duplicate().powZn(h1)
    var right = Cfull.C2.duplicate().mul(right1)
    //println(right)

    right = right.duplicate().mul(right2)
    /*
    println("unsigncryption left = $left")
    //println(P)
    //println(Cfull.C5)
    println("right1 = $right1")
    println("right2 = $right2")
    println("unsigncryption right= $right")

    //return M_
    */
    if (left.isEqual(right)) {
        return M_
    } else {
        return null
    }
    //println( "${Cfull.C1}, ${Cfull.C2}, ${Cfull.C3}, ${Cfull.C4}, ${M}, ${T2_}, ${A}, ${VVi}")
}


fun UnSignAgg(
    skr: Element,
    CAgg: MutableList<Cfull>,
    PID: ByteArray,
    VVi: Element,
    gen: Pairing,
    P: Element,
    Ppub: Element,
) : MutableList<Int> ? {

    val hList = mutableListOf<Element>()
    val AList = mutableListOf<Element>()
    val MList = mutableListOf<Int>()

    var T2_ : Element
    var MandA : ByteArray
    var Mbytes : ByteArray
    var M_ : Element
    var M : Int
    var Abytes : ByteArray
    var A  : Element

    //var h1 = H5(Cfull.C1, Cfull.C2, Cfull.C3, Cfull.C4, M, T2_, A, VVi, gen)

    // calculate the aggregated h
    for (C in CAgg){
        T2_ = C.C2.duplicate().powZn(skr)
        MandA = XorForArray(H4(T2_), C.C4)

        Mbytes = DisConcatenation(MandA, 0, 160 / 8)
        M_ = gen.zr.newElementFromBytes(Mbytes)
        M = M_.toBigInteger().toInt()
        MList.add(M)

        Abytes = DisConcatenation(MandA, 160 / 8, MandA.size)
        A = gen.g1.newElementFromBytes(Abytes)
        AList.add(A)

        hList.add(H5(C.C1, C.C2, C.C3, C.C4, M, T2_, A, VVi, gen))
    }

    var h1 : Element = hList[0]
    for (i in hList){
        if(i == hList[0]) continue
        else {
            h1 = h1.duplicate().add(i)
        }
    }

    // compute C2 and C5
    var C2Agg : Element = CAgg[0].C2
    var C5Agg : Element = CAgg[0].C5
    for (C in CAgg){
        if(C == CAgg[0]) continue
        else {
            C2Agg = C2Agg.duplicate().mul(C.C2)
            C5Agg = C5Agg.duplicate().add(C.C5)
        }
    }
    //println( "${Cfull.C3}, ${Cfull.C4}, ${gen}")
    /**********/
    //h1 = H5_(Cfull.C1, Cfull.C2, M, T2_, A, VVi, gen)

    //println( "${Cfull.C1}, ${Cfull.C2},$M,  $T2_, $A, $VVi, ${gen}")

    //val h1 = H5(Cfull.C1, Cfull.C2, Cfull.C3, Cfull.C4, M, T2_, Ai, VVi, gen)
    val left = P.duplicate().powZn(C5Agg)
    var right1 = VVi.duplicate().powZn(h1)
    var right2 =
        ((Ppub.duplicate().powZn(H1(PID, AList[0], gen))).duplicate().mul(AList[0])).duplicate().powZn(h1)
    var right = C2Agg.duplicate().mul(right1)
    right = right.duplicate().mul(right2)
    /*
    println("unsigncryption left = $left")

    println("right1 = $right1")
    println("right2 = $right2")
    println("unsigncryption right= $right")
*/
    if (left.isEqual(right)) {
        return MList
    } else {
        return null
    }
}

fun Test(C1: Cfull, C2: Cfull, td: Element, gen: Pairing): Boolean {

    var T1 = C1.C2.duplicate().powZn(td)
    var T1_ = C2.C2.duplicate().powZn(td)

    val I = XorForArray(H2(T1), C1.C3)
    val Izr = gen.zr.newElementFromBytes(I)
    val I_ = XorForArray(H2(T1_), C2.C3)
    val I_zr = gen.zr.newElementFromBytes(I_)

    // I should be accumulated after generating a zr element.

    val left = C2.C1.duplicate().powZn(Izr)
    val right = C1.C1.duplicate().powZn(I_zr)
    //println("左边= $left")
    //println("右边= $right")
    if (left.isEqual(right)) return true
    else return false
}


fun TestAgg(CAgg: MutableList<Cfull>, td: Element, gen: Pairing): Boolean {

    var T : Element
    var I : Element

    var CSum : Element = CAgg[0].C1
    var ISum : Element
    val Ilist = mutableListOf<Element>()
    //val Clist = mutableListOf<Element>()


    // two lists receive the temp result of I and C sum
    // then uses these two lists to sum 1..end and save it to sum
    // use Ilist[0] and Clist[0] to realize the generation of left and right!

    for (i in CAgg) {
        T = i.C2.duplicate().powZn(td)
        I = gen.zr.newElementFromBytes( XorForArray(H2(T), i.C3) )
        //println("I = $I")
        Ilist.add(I)

    //    println("C1 = ${i.C1}")
        if(i == CAgg[0]) continue
        else {
            CSum = CSum.duplicate().mul(i.C1)
        }
    }

    ISum = Ilist[0]
    for (i in Ilist) {
    //   println("I = $i")

        if(i == Ilist[0]) continue
        else ISum = ISum.duplicate().add(i)
    }


    val left = CSum.duplicate().powZn(Ilist[0])
    val right = CAgg[0].C1.duplicate().powZn(ISum)

/*
    val left1 = CAgg[1].C1.duplicate().powZn(Ilist[0])
    val right1 = CAgg[0].C1.duplicate().powZn(Ilist[1])
    println("左边= $left1")
    println("右边= $right1")
*/
    if (left.isEqual(right)) return true
    else return false

}




