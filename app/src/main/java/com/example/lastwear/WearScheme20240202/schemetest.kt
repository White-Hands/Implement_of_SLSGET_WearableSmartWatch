package com.example.lastwear.WearScheme20240202

import Cfull
import Coff
import OutputReceiverParam
import OutputSenderParam
import ReadCfullFromFile
import ReadCoffFromFile
import WriteCfullToFile
import WriteCoffToFile
import WriteInitializationToFile
import android.os.Environment
import it.unisa.dia.gas.jpbc.Pairing
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.time.Instant
import kotlin.system.measureTimeMillis


fun PCReadCfullFromFile(gen : Pairing, dir:String, name:String): Cfull {


    val file = File(dir, name)

    var oos = ObjectInputStream(FileInputStream(file))

    val bytesArray = oos.readObject() as Array<ByteArray>

    oos.close()

    var Cfull : Cfull = Cfull(
        C1 = gen.g1.newElementFromBytes(bytesArray[0]),
        C2 = gen.g1.newElementFromBytes(bytesArray[1]),
        C3 = bytesArray[2],
        C4 = bytesArray[3],
        C5 = gen.zr.newElementFromBytes(bytesArray[4]),
    )
    return Cfull
}

fun PCReadInitializationFromFile( ): Initialization {

    //val file = File("C:\\Users\\struggler\\Desktop\\输出测试", "Initialization")
    //val file = File(Dir1, "Initialization")
    val file = File("C:\\Users\\SLY\\Desktop\\E20240625", "Initialization")


    var oos = ObjectInputStream(FileInputStream(file))

    val bytesArray = oos.readObject() as Array<ByteArray>

    oos.close()

    //val gen = PairingFactory.getPairing("C:\\Users\\SLY\\Desktop\\E20240625\\curve.txt")
    val gen = PairingFactory.getPairing("app/src/main/res/raw/curve")

    println(gen)

    var p : Initialization = Initialization(
        gen = gen,
        s = gen.zr.newElementFromBytes(bytesArray[0]),
        P = gen.g1.newElementFromBytes(bytesArray[1]),
        Ppub= gen.g1.newElementFromBytes(bytesArray[2]),
        skr= gen.zr.newElementFromBytes(bytesArray[3]),
        pkr= gen.g1.newElementFromBytes(bytesArray[4]),
        skG= gen.zr.newElementFromBytes(bytesArray[5]),
        hkG= gen.g1.newElementFromBytes(bytesArray[6]),
        w= gen.zr.newElementFromBytes(bytesArray[7]),
        Epk= gen.g1.newElementFromBytes(bytesArray[8]),
        ID= gen.zr.newElementFromBytes(bytesArray[9]),
        PID= bytesArray[10],
        ti= gen.zr.newElementFromBytes(bytesArray[11]),
        Ai= gen.g1.newElementFromBytes(bytesArray[12]),
        H1PIDAi= gen.zr.newElementFromBytes(bytesArray[13]),
        ppki= gen.zr.newElementFromBytes(bytesArray[14]),
        vi= gen.zr.newElementFromBytes(bytesArray[15]),
        VVi= gen.g1.newElementFromBytes(bytesArray[16]),
        Bi= gen.g1.newElementFromBytes(bytesArray[17]),
        Di= gen.g1.newElementFromBytes(bytesArray[18])
    )
    oos.close()

    return p
}

fun main(){

    var p : Initialization

    /*
    var curve160  = TypeACurveGenerator(160,160).generate()
    println(curve160.toString())
    println( PairingFactory.getPairing(curve160))
    */

    /********************************
    val filetest = File("app/src/main/res/raw/curve")
    var oostest = ObjectInputStream(FileInputStream(filetest))
    val bytesArray = oostest.readObject() as String
    oostest.close()
    println(bytesArray)
    *********************************/

    val pFromRead = 0
    val CfullFromRead = 0


    if ( pFromRead == 0 ) {
        //val gen = PairingFactory.getPairing("C:\\Users\\SLY\\Desktop\\2024-6-25\\curve.txt")
        val gen = PairingFactory.getPairing("app/src/main/res/raw/curve")
        //val gen = PairingFactory.getPairing("res/raw/a.properties")
        //val gen = PairingFactory.getPairing("C:\\Users\\SLY\\Desktop\\2024-6-25\\curve.txt")
        //println(gen)
        p = Initialization(gen = gen)
        /********************/
        //println(p)

        WriteInitializationToFile(p, "PC")
        OutputSenderParam(p)
        OutputReceiverParam(p)
    }else {
        p = PCReadInitializationFromFile()
        println(p)
    }

    val CoffFromRead = 0
    var Coff: Coff
    if (CoffFromRead == 0) {
         Coff = OffSigncryption(p)
        println(Coff)
        WriteCoffToFile(Coff, "PC")
    }else{
        Coff = ReadCoffFromFile(p.gen,"PC")
    }

    var Cfull: Cfull
    if (CfullFromRead == 0) {
        Cfull = OnSigncryption(p, Coff, 64)
        println(Cfull)
        WriteCfullToFile(Cfull, "PC")
    }else{
        Cfull = ReadCfullFromFile(p.gen, "PC")
    }



    val M = UnSigncryption(
        skr = p.skr,
        Cfull = Cfull,
        PID = p.PID,
        VVi = p.VVi,
        gen = p.gen,
        P = p.P,
        Ppub = p.Ppub
    )
    println(M)
    println("Cfull.C3 = ,${Cfull.C3}")
    println("Cfull.C3.toString = ,${Cfull.C3.toString()}")
    println("Cfull.C3.zr, ${p.gen.zr.newElementFromBytes(Cfull.C3)}")

    println("Cfull.C4 = ,${Cfull.C4}")

    val p2 = PCReadInitializationFromFile()
    val Cfull2 = ReadCfullFromFile(p2.gen,"PC")
    val M2 = UnSigncryption(
        skr = p.skr,
        Cfull = Cfull,
        PID = p.PID,
        VVi = p.VVi,
        gen = p.gen,
        P = p.P,
        Ppub = p.Ppub
    )
    println(M2)


    val C2off = OffSigncryption(p)
    val C2 = OnSigncryption(p,C2off,64)
    //println(C2)
    println(Test(Cfull, C2, p.w, p.gen))




    val CAgg = mutableListOf<Cfull>()
    for (i in (1..100)){
        CAgg.add( OnSigncryption(p, OffSigncryption(p)))
    }


    var time4 = measureTimeMillis {
    TestAgg(CAgg, td = p.w , p.gen)
}
    println("TestAgg time: $time4")


    var time5 = measureTimeMillis {
    val M2list = UnSignAgg(
        skr = p.skr,
        CAgg = CAgg,
        PID = p.PID,
        VVi = p.VVi,
        gen = p.gen,
        P = p.P,
        Ppub = p.Ppub
    )
    println(M2list)
    }
    println("UnsignAgg time: $time5")

    var time6 = measureTimeMillis {
        for ( i in CAgg){
            val M2 = UnSigncryption(
                skr = p.skr,
                Cfull = i,
                PID = p.PID,
                VVi = p.VVi,
                gen = p.gen,
                P = p.P,
                Ppub = p.Ppub
            )
           // println(M2)
        }
    }
    println("Single unsign time: $time6")


    var time7 = measureTimeMillis {
        for ( i in CAgg){
            Test(CAgg[0],i,p.w,p.gen)
           // println(M2)
        }
    }
    println("Single TEST time: $time7")
}
    /*

    val dir1 = "C:\\Users\\struggler\\Desktop\\输出测试"
    val dir2 = "C:\\Users\\struggler\\Desktop\\输出测试\\curve.txt"
    //p = ReadInitializationFromFile(dir1,dir2)
    //println(p)

//    var curve160  = TypeACurveGenerator(160,160).generate()
//    println(curve160.toString())
//    var gen = PairingFactory.getPairing(curve160)

    var gen = PairingFactory.getPairing(dir2)
    p=Initialization(gen = gen)
//    p.gen = gen
//    println(q)

//    WriteInitializationToFile(p)
//    p = ReadInitializationFromFile(dir1,dir2)
    println(p)


    var Coff : Coff
    var time5 = Instant.now()
    Coff = OffSigncryption(p)
//    WriteCoffToFile(Coff)
//    Coff = ReadCoffFromFile(p.gen)
    /*
    for (i in 1..1000) {
        Coff = OffSigncryption(p)
    }
     */
    println("2024年2月1日 Offline : ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")
    println("Coff = $Coff")
    /*
    var time4 = measureTimeMillis {
        OnSigncryption(p, Coff)
    }
    println("Online time: $time4")



    time3 = measureTimeMillis {
        val time5 = Instant.now()
        for (i in 1..10) {
            Coff = OffSigncryption(p)
        }
        println("Offline: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")

    //   WriteCoffToFile(Coff )
    //  Coff = ReadCoffFromFile(p.gen)
    }
    println("Offline: $time3")

     */


    //Coff = ReadCoffFromFile(p.gen)
    //println(Coff)
    //Coff = ReadCoffFromFile(p.gen)
    //println(Coff)


    var Cfull : Cfull
    time5 = Instant.now()
    Cfull = OnSigncryption(p, Coff)
    /*
    for (i in 1..1000) {
        Cfull = OnSigncryption(p, Coff)
    }
     */
    println("2024年2月1日 Online times: ${Instant.now().toEpochMilli()-time5.toEpochMilli()}")
    println("Cfull = $Cfull")



    /*

    Cfull = OnSigncryption(p, Coff)

    println("Online 10 times: $time4")


    val time1 = measureTimeMillis {
        //    for ( i in 1..2) {
        Coff = OffSigncryption(p)
        //  }
    }
    println(time1)

    var Cfull2:Cfull
    val time2 = measureTimeMillis {
        for (i in 1..1) {
            //  Cfull = OnSigncryption(p, Coff)
            //  val time5 = Instant.now()
            Cfull2= OnSigncryption(p, Coff)
            println(Cfull2)
            // println(Instant.now().toEpochMilli()-time5.toEpochMilli())
        }
    }
    println(time2)
*/


    //println(Cfull)
    //println(Cfull2)
    var time3 = measureTimeMillis {

        val M = UnSigncryption(
            skr = p.skr,
            Cfull = Cfull,
            PID = p.PID,
            VVi = p.VVi,
            gen = p.gen,
            P = p.P,
            Ppub = p.Ppub,
            Ai = p.Ai

        )
        println(M)
    }

    println("Unsign: $time3")



    */


    /*
    p = PCReadInitializationFromFile()

    var C1 = PCReadCfullFromFile(p.gen,"C:\\Users\\struggler\\Desktop","FullCiphertext")
    var C2 = PCReadCfullFromFile(p.gen,"C:\\Users\\struggler\\Desktop","FullCiphertext")
    //var C2 = OnSigncryption(p, Coff = C1)
    //C
    var t1 = 0
    val time0 = measureTimeMillis {
        println(Test(C1, C2, p.w, p.gen))
        t1 = 1
    }
    println("Test : $time0")
    println("t1: $t1")

     */


   // println(UnSigncryption(p.skr,C2,p.PID,p.VVi,p.gen,p.P,p.Ppub,p.Ai))

    /*/!!!!!!!!!!!!!!!!!!1
    println((p.Ppub.duplicate().powZn( H1(p.PID,p.Ai,p.gen) )).duplicate().mul(p.Ai))
    println(p.P.duplicate().powZn(p.ppki))
    /
    /!!!!!!!!!!!!!!!
     */



