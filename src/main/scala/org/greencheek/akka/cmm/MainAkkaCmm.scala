package org.greencheek.akka.cmm

import akka.util.{ConcurrentMultiMap2, ConcurrentMultiMap}
import java.util.concurrent.{CountDownLatch, ExecutorService, Executors}
import java.util.Comparator
import scala.None

/**
 * Created by dominictootell on 13/01/2014.
 */
object MainAkkaCmm {
  def main(args: Array[String]) {
    val loops = 1000
    val cmm = new ConcurrentMultiMap[String, Int](1,
      new Comparator[Int] {
        def compare(a: Int, b: Int): Int = a compareTo  b
      })

    @volatile var count = 0
    val countDown = new CountDownLatch(2);

    class Producer(map: ConcurrentMultiMap[String,Int]) extends Runnable {

      def run() {
        for (i <- 1 to loops) {
          map.put("1", i)
        }
        countDown.countDown();
      }
    }

    class Consumer(map: ConcurrentMultiMap[String,Int]) extends Runnable {

      def run() {
        for (i <- 1 to loops) {
          val x = map.remove("1")
          x match {
            case Some(y) => count+=y.size
            case None =>

          }
        }
        countDown.countDown();

      }
    }


    val pool: ExecutorService = Executors.newFixedThreadPool(2)

    pool.execute(new Consumer(cmm))
    pool.execute(new Producer(cmm))

    countDown.await()

    val x = cmm.remove("1")
    x match {
      case Some(y) => count += y.size
      case None =>

    }

    println()
    println("========")
    println(count)
    println("========")






    pool.shutdown()
  }
}
