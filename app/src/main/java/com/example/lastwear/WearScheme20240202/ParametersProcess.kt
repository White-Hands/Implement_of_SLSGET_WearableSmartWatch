import android.content.Context
import android.os.Environment
import com.example.lastwear.WearScheme20240202.Initialization
import it.unisa.dia.gas.jpbc.Element
import it.unisa.dia.gas.jpbc.Pairing
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

data class Coff (
    var C1 : Element,
    val C2: Element,
    val T1: Element,
    val T2: Element,
    val r1: Element,
    val r2: Element
)

data class Cfull (
    val C1 : Element,
    val C2: Element,
    val C3 :ByteArray,
    val C4:ByteArray,
    val C5: Element
)


fun WriteInitializationToFile(p : Initialization, device : String = "watch"){

//    val file = File("C:\\Users\\struggler\\Desktop\\输出测试", "Initialization")

    var file : File
    if (device == "watch") {
        file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Initialization1"
        )
    }
    else{
        file = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "Initialization")
    }


    var oos = ObjectOutputStream(FileOutputStream(file))

    oos.writeObject(
        arrayOf(
            p.s.toBytes(),
            p.P.toBytes(),
            p.Ppub.toBytes(),
            p.skr.toBytes(),
            p.pkr.toBytes(),
            p.skG.toBytes(),
            p.hkG.toBytes(),
            p.w.toBytes(),
            p.Epk.toBytes(),
            p.ID.toBytes(),
            p.PID,
            p.ti.toBytes(),
            p.Ai.toBytes(),
            p.H1PIDAi.toBytes(),
            p.ppki.toBytes(),
            p.vi.toBytes(),
            p.VVi.toBytes(),
            p.Bi.toBytes(),
            p.Di.toBytes()
        )
    )
    oos.close()
}

fun OutputSenderParam( p : Initialization ){

    // output to a fixed dir
    // only pkr Epk ppki Ai vi VVi Di P and gen
    // by order of the former function
    // delete the other parameters
    // copy to the read function

    var file : File = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "SenderParam")

    var oos = ObjectOutputStream(FileOutputStream(file))
    oos.writeObject(
        arrayOf(
            p.P.toBytes(),
            p.pkr.toBytes(),
            p.Epk.toBytes(),
            p.Ai.toBytes(),
            p.ppki.toBytes(),
            p.vi.toBytes(),
            p.VVi.toBytes(),
            p.Di.toBytes()
        )
    )
    oos.close()
}

fun OutputReceiverParam( p : Initialization ){

    // output to a fixed dir
    // only skr PID Ppub VVi P and gen
    // by order of the former function
    // delete the other parameters
    // copy to the read function

    var file : File = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "ReceiverParam")

    var oos = ObjectOutputStream(FileOutputStream(file))
    oos.writeObject(
        arrayOf(
            p.P.toBytes(),
            p.Ppub.toBytes(),
            p.skr.toBytes(),
            p.PID,
            p.VVi.toBytes()
        )
    )
    oos.close()
}


fun ReadSenderParam( device : String = "watch"): Initialization {

    //val file = File("C:\\Users\\struggler\\Desktop\\输出测试", "Initialization")
    //val file = File(Dir1, "Initialization")
    var file : File
    if (device == "watch") {
        file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "SenderParam"
        )
    }
    else{
        file = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "SenderParam")
    }


    var oos = ObjectInputStream(FileInputStream(file))

    val bytesArray = oos.readObject() as Array<ByteArray>

    oos.close()

    val gen = PairingFactory.getPairing("app/src/main/res/raw/curve")
    //val gen = PairingFactory.getPairing("raw/curve.txt")
    var p : Initialization = Initialization(
        gen = gen,
        P = gen.g1.newElementFromBytes(bytesArray[0]),
        pkr= gen.g1.newElementFromBytes(bytesArray[1]),
        Epk= gen.g1.newElementFromBytes(bytesArray[2]),
        Ai= gen.g1.newElementFromBytes(bytesArray[3]),
        ppki= gen.zr.newElementFromBytes(bytesArray[4]),
        vi= gen.zr.newElementFromBytes(bytesArray[5]),
        VVi= gen.g1.newElementFromBytes(bytesArray[6]),
        Di= gen.g1.newElementFromBytes(bytesArray[7])
    )
    oos.close()
    return p
}

fun ReadReceiverParam( device : String = "watch"): Initialization {

    //val file = File("C:\\Users\\struggler\\Desktop\\输出测试", "Initialization")
    //val file = File(Dir1, "Initialization")
    var file : File
    if (device == "watch") {
        file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "ReadReceiverParam"
        )
    }
    else{
        file = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "ReadReceiverParam")
    }


    var oos = ObjectInputStream(FileInputStream(file))

    val bytesArray = oos.readObject() as Array<ByteArray>

    oos.close()

    val gen = PairingFactory.getPairing("app/src/main/res/raw/curve")
    //val gen = PairingFactory.getPairing("raw/curve.txt")
    var p : Initialization = Initialization(
        gen = gen,
        P = gen.g1.newElementFromBytes(bytesArray[0]),
        Ppub= gen.g1.newElementFromBytes(bytesArray[1]),
        skr= gen.zr.newElementFromBytes(bytesArray[2]),
        PID= bytesArray[3],
        VVi= gen.g1.newElementFromBytes(bytesArray[4]),
     )
    oos.close()
    return p
}

fun WriteCoffToFile(Coff: Coff,  device : String = "watch"){
    //val file = File("C:\\Users\\struggler\\Desktop\\输出测试", "OfflineCiphertext")
    var file : File
    if (device == "watch") {
        file = File(
            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Environment.getExternalStorageDirectory(),
            "OfflineCiphertext"
        )
    }
    else{
        file = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "OfflineCiphertext")
    }
    var oos = ObjectOutputStream(FileOutputStream(file))

    oos.writeObject(
        arrayOf(
            Coff.C1.toBytes(),
            Coff.C2.toBytes(),
            Coff.T1.toBytes(),
            Coff.T2.toBytes(),
            Coff.r1.toBytes(),
            Coff.r2.toBytes()
        )
    )
    oos.close()
}

fun ReadCoffFromFile(gen : Pairing ,  device : String = "watch"): Coff {
//    val file = File("C:\\Users\\struggler\\Desktop\\输出测试", "OfflineCiphertext")
    var file : File
    if (device == "watch") {
        file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "OfflineCiphertext"
        )

    }else{
        file = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "OfflineCiphertext")
    }


    var oos = ObjectInputStream(FileInputStream(file))

    val bytesArray = oos.readObject() as Array<ByteArray>

    oos.close()

    var Coff : Coff = Coff(
        C1 = gen.g1.newElementFromBytes(bytesArray[0]),
        C2 = gen.g1.newElementFromBytes(bytesArray[1]),
        T1 = gen.g1.newElementFromBytes(bytesArray[2]),
        T2 = gen.g1.newElementFromBytes(bytesArray[3]),
        r1 = gen.zr.newElementFromBytes(bytesArray[4]),
        r2 = gen.zr.newElementFromBytes(bytesArray[5])
    )
    return Coff

    /*
    for (i in 0..3){
        elements[i]=gen.g1.newElementFromBytes(bytesArray[i])
    }
    for (i in 4..5){
        elements[i]=gen.zr.newElementFromBytes(bytesArray[i])
    }
    // 从字节数组数组中恢复元素

    elements[0] = params.pairing.getG1().newElementFromBytes(bytesArray[0])
    elements[1] = params.pairing.getZr().newElementFromBytes(bytesArray[1])
    elements[2] = params.pairing.getZr().newElementFromBytes(bytesArray[2])
    elements[3] = params.pairing.getZr().newElementFromBytes(bytesArray[3])

     */


    // 返回元素数组

}

fun WriteCfullToFile( Cfull : Cfull  ,  device : String = "watch" ){

    var file : File
    if (device == "watch") {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        file = File(dir, "FullCiphertext")
    }else{
        file = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "FullCiphertext")
    }

    var oos = ObjectOutputStream(FileOutputStream(file))

    oos.writeObject(
        arrayOf(
            Cfull.C1.toBytes(),
            Cfull.C2.toBytes(),
            Cfull.C3,
            Cfull.C4,
            Cfull.C5.toBytes()
        )
    )
    oos.close()
}


fun ReadCfullFromFile(gen : Pairing ,  device : String = "watch"): Cfull {
    var file : File
    if (device == "watch") {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

         file = File(dir, "FullCiphertext")
    }else{
        file = File("C:\\Users\\SLY\\Desktop\\E20240625\\", "FullCiphertext")
    }

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