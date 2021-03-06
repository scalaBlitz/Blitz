package org.scala.optimized.test.par
package scalameter


import scala.collection.par._
import org.scalameter.api._
import Scheduler.Config
import scala.collection._
import scala.collection.parallel.ForkJoinTaskSupport



trait Generators {

  def sizes(from: Int) = Gen.enumeration("size")(from, from * 3, from * 5)
  def ranges(from: Int) = for (size <- sizes(from)) yield 0 until size
  def ranges(sizes: Gen[Int]) = for (size <- sizes) yield 0 until size
  def arrays(from: Int) = for (size <- sizes(from)) yield (0 until size).toArray

  def lists(from: Int) = for (size <- sizes(from)) yield (0 until size).toList

  def arraysBoxed(from: Int) = for (size <- sizes(from)) yield {
    val r = new Array[java.lang.Integer](size)
    (0 until size).foreach { x => r(x) = x: java.lang.Integer }
    r
  }

  def concs(from: Int) = for (size <- sizes(from)) yield {
    var conc: Conc[Int] = Conc.Zero
    for (i <- 0 until size) conc = conc <> i
    conc
  }

  def normalizedConcs(from: Int) = for (conc <- concs(from)) yield conc.normalized

  def bufferConcs(from: Int) = for (size <- sizes(from)) yield {
    var cb = new Conc.Buffer[Int]
    for (i <- 0 until size) cb += i
    cb.result.normalized
  }

  def hashMaps(from: Int) = for (size <- sizes(from)) yield {
    val hm = new mutable.HashMap[Int, Int]
    for (i <- 0 until size) hm += ((i, i))
    hm
  }

  def hashTrieSets(from: Int) = for (size <- sizes(from)) yield {
    var hs = immutable.HashSet[Int]()
    for (i <- 0 until size) hs += i
    hs
  }

  def hashSets(from: Int) = for (size <- sizes(from)) yield {
    val hs = new mutable.HashSet[Int]
    for (i <- 0 until size) hs += i
    hs
  }

  def immutableTreeSets(from: Int) = for (size <- sizes(from)) yield {
    var ts = immutable.TreeSet[String]()
    for (i <- 0 until size) ts += i.toString
    ts
  }

  def withArrays[Repr <% TraversableOnce[_]](gen: Gen[Repr]) = for (coll <- gen) yield (coll, new Array[Int](coll.size))

  val parallelismLevels = Gen.exponential("par")(1, Runtime.getRuntime.availableProcessors, 2)

  val schedulers = {
    val ss = for (par <- parallelismLevels) yield new Scheduler.ForkJoin(new Config.Default(par) { override val maximumStep = 1024 })
    ss.cached
  }

  val tasksupports = {
    val ss = for (par <- parallelismLevels) yield new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(par))
    ss.cached
  }

  def withTaskSupports[Repr](colls: Gen[Repr]): Gen[(Repr, ForkJoinTaskSupport)] = for {
    c <- colls
    s <- tasksupports
  } yield (c, s)

  def withSchedulers[Repr](colls: Gen[Repr]): Gen[(Repr, Scheduler)] = for {
    c <- colls
    s <- schedulers
  } yield (c, s)

}
