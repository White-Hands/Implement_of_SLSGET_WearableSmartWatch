import android.os.Environment
import it.unisa.dia.gas.jpbc.Element
import it.unisa.dia.gas.jpbc.Pairing
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

data class Params(
    val pairing: Pairing,
    val g: Element,
    val a: Element,
    val b: Element,
    val time:Long
)

data class output(
    val ga: Element,
    val gb: Element,
    val gagb: Element,
    val ggab: Element,
    val time1:Long,
    val time2:Long,
    val time3:Long
)

fun Setup ():Params {
    //val pairing = PairingFactory.getPairing(TypeACurveGenerator(160, 512).generate())
    var time:Long = System.currentTimeMillis()
    val pairing = PairingFactory.getPairing("res/raw/a.properties")
    val g = pairing.g1.newRandomElement()
    val a = pairing.zr.newRandomElement()
    val b = pairing.zr.newRandomElement()
    time  = System.currentTimeMillis()-time
    return Params(pairing, g, a, b, time)
}

fun Runpair(params:Params):output{
    var time1 = System.currentTimeMillis()
    var ga = params.g.duplicate().powZn(params.a)
    var gb = params.g.duplicate().powZn(params.b)
    time1 = (System.currentTimeMillis()-time1)/2

    var time2 = System.currentTimeMillis()
    var gagb = params.pairing.pairing(ga,gb)// pairing = 3* powZn
    var ggab = params.pairing.pairing(params.g,params.g).duplicate().powZn(params.a.mul(params.b))
    time2 = (System.currentTimeMillis()-time2)/2

    /********Test*************/
    writeParamsToFile(ga,gb,gagb,ggab)


    val elementArray = readParamsFromFile(params,4)
    ga = elementArray[1]
    gb = elementArray[0]
    gagb = elementArray[2]
    ggab = elementArray[3]

    /********Test*************/

    return output(ga,gb,gagb,ggab,time1,time2,params.time)
}




fun writeParamsToFile(ga: Element, gb: Element, gagb: Element, ggab: Element){

    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

// 创建一个文件对象
    val file = File(downloadDir, "OfflineCiphertext.txt")

// 创建一个文件输出流对象,第二个参数true表示追加
    val fos = FileOutputStream(file)

// 创建一个对象输出流对象
    val oos = ObjectOutputStream(fos)

// 把配对结果作为一个值，序列化后写入文件
    oos.writeObject(  arrayOf( ga.toBytes(),
        gb.toBytes(),
        gagb.toBytes(),
        ggab.toBytes()
    )
    )
// 关闭输出流
    oos.close()
    fos.close()
}


// 定义一个函数，从文件中读取这四个参数，并转换为元素
fun readParamsFromFile(params: Params,size:Int=4): Array<Element> {

    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

// 创建一个文件对象
    val file = File(downloadDir, "OfflineCiphertext.txt")
    // 创建一个文件输入流对象
    val fis = FileInputStream(file)

    // 创建一个对象输入流对象
    val ois = ObjectInputStream(fis)

    // 从文件中读取一个对象，转换为字节数组数组
    val bytesArray = ois.readObject() as Array<ByteArray>

    // 关闭输入流
    ois.close()
    fis.close()

    // 创建一个元素数组
    val elements = arrayOfNulls<Element>(size)
    for (i in 0..1){
        elements[i]=params.pairing.getG1().newElementFromBytes(bytesArray[i])
    }
    for (i in 2..3){
        elements[i]=params.pairing.getGT().newElementFromBytes(bytesArray[i])
    }
    // 从字节数组数组中恢复元素
    /*
    elements[0] = params.pairing.getG1().newElementFromBytes(bytesArray[0])
    elements[1] = params.pairing.getZr().newElementFromBytes(bytesArray[1])
    elements[2] = params.pairing.getZr().newElementFromBytes(bytesArray[2])
    elements[3] = params.pairing.getZr().newElementFromBytes(bytesArray[3])

     */


    // 返回元素数组
    return elements as Array<Element>
}

