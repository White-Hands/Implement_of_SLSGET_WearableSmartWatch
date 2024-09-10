import it.unisa.dia.gas.jpbc.Element
import it.unisa.dia.gas.jpbc.Pairing
import kotlin.experimental.xor

fun H1 (e1 : ByteArray, e2 : Element, pairing: Pairing) : Element {

    //var time5 = Instant.now()
    var Bytearray = pairing.zr.newElementFromBytes(e1).toString().hashCode() + e2.toString().hashCode()
    //var Bytearray =e1.hashCode() + e2.toString().hashCode()
    //Bytearray=e2.toString().hashCode()
    //println("PID = $e1")
    //println("PID.zr = ${pairing.zr.newElementFromBytes(e1)}")
    //println("PID.zr.hashcode = ${pairing.zr.newElementFromBytes(e1).hashCode()}")


    //println("Online H1: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")

    //return pairing.zr.newElement(   Bytearray  ).add(pairing.zr.newElementFromBytes(e1))
    return pairing.zr.newElement(   Bytearray  )

}

fun H1_ (e1 : ByteArray, e2 : Element, pairing: Pairing) : Element {
    var Bytearray =  e2.toString().hashCode()
    return pairing.zr.newElement(   Bytearray  )
}

fun H2(e : Element): ByteArray {
    return   e.toBytes()
}

fun H3 ( M: Int, pairing: Pairing): Element {
    return pairing.zr.newElement(M)
}

fun H4 ( e : Element):ByteArray {
    var Bit320 = e.toBytes()
    return ConcatenationForArray(Bit320,Bit320)
}

fun H5 (C1 : Element,
        C2 : Element,
        C3 : ByteArray,
        C4 : ByteArray,
        M : Int,
        T2 : Element,
        Ai : Element,
        VVi : Element,
        pairing: Pairing
): Element {

    //var time5 = Instant.now()


    //var BytesFromCs =  (C1.toString() + C2.toString() + C3.toString() + C4.toString() + M.toString() + T2.toString() + Ai.toString() + VVi.toString()).hashCode()
    //var BytesFromCs =  C1.hashCode() + C2.hashCode() + C3.hashCode() + C4.hashCode() + M.hashCode() + T2.hashCode() + Ai.hashCode() + VVi.hashCode()
    var BytesFromCs =  C1.toString().hashCode()+
            C2.toString().hashCode() +
            pairing.zr.newElementFromBytes(C3).toString().hashCode() +
            pairing.zr.newElementFromBytes(C4).toString().hashCode() +
            M+
            T2.toString().hashCode() +
            Ai.toString().hashCode()+
            VVi.toString().hashCode()
    /*
    println(BytesFromCs)
    println(C1.hashCode())
    println(C1)
    println(C2.hashCode())
    println(C3.hashCode())
    println(C4.hashCode())
    println(M.hashCode())
    println(T2.hashCode() )
    println(T2)
    println( Ai.hashCode() )
    println(Ai)
    println(VVi.hashCode())

     */
    //println("H5: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")

    //time5 = Instant.now()
    //pairing.zr.newElement( BytesFromCs )
    //println("ByteArrayToZr: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")

    return pairing.zr.newElement( BytesFromCs )
//    return pairing.zr.newElementFromBytes( C1.toBytes() + C2.toBytes() + C3 + C4 + M.toByte() + T2.toBytes() + Ai.toBytes() + VVi.toBytes() )
}


fun H5_ (C1 : Element,
        C2 : Element,
        M : Int,
        T2 : Element,
        Ai : Element,
        VVi : Element,
        pairing: Pairing
): Element {

    var BytesFromCs =  C1.toString().hashCode() + C2.toString().hashCode() + M.hashCode() +T2.toString().hashCode() + Ai.toString().hashCode()+  VVi.toString().hashCode()
    return pairing.zr.newElement( BytesFromCs )
}

fun XorForArray ( X : ByteArray , Y :  ByteArray ) : ByteArray {
    var testtest = ByteArray(Y.size)
    //println(testtest.size)
    for (i in testtest.indices) {
        //  println(i)
        testtest[i] = (X[i] xor  Y[i])
    }
    return testtest
}

fun ConcatenationForArray ( X:ByteArray, Y:ByteArray ):ByteArray{
    var testtest = ByteArray(Y.size+X.size)
    for (i in X.indices) {
        testtest[i] = X[i]
    }
    for (i in Y.indices) {
        testtest[i+X.size] = Y[i]
    }
    return testtest
}

fun DisConcatenation ( X : ByteArray, Start: Int, End : Int ) : ByteArray{

    var result = ByteArray(End - Start)

    for ( i  in  result.indices){
        result[i] = X[i+Start]
    }

    return result
}
