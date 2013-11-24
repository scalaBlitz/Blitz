package org.scala.optimized.test.par
package scalameter


import scala.collection.par.SequentialOptimizeBlock._
import org.scalameter.api._



class OptimizedBlockBench extends PerformanceTest.Regression with Serializable with Generators {

  /* config */

  def persistor = new SerializationPersistor

  val tiny = 300000
  val small = 3000000
  val large = 30000000

  val opts = Seq(
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 6,
    exec.jvmflags -> "-server -Xms3072m -Xmx3072m -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=64m -XX:+UseCondCardMark -XX:CompileThreshold=100 -Dscala.collection.parallel.range.manual_optimizations=true",
    reports.regression.noiseMagnitude -> 0.15)

  val pcopts = Seq(
    exec.minWarmupRuns -> 2,
    exec.maxWarmupRuns -> 4,
    exec.benchRuns -> 4,
    exec.independentSamples -> 1,
    reports.regression.noiseMagnitude -> 0.75)

  /* benchmarks */

  performance of "Optimized[Range]" config (opts: _*) in {

    measure method "reduce" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.reduce(_+_)
      }
      using(ranges(large)) curve ("optimized") in { x => optimize{x.reduce(_+_)} }
    }

    measure method "map" in {
      using(ranges(small)) curve ("collections") in { x =>
        x.map(x=>x)
      }
      using(ranges(small)) curve ("optimized") in { x => optimize{x.map(x=>x)} }
    }

    measure method "exists" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.exists(_<Int.MinValue)
      }
      using(ranges(large)) curve ("optimized") in { x => optimize{x.exists(_<Int.MinValue)} }
    }

    measure method "find" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.find(_<Int.MinValue)
      }
      using(ranges(large)) curve ("optimized") in { x => optimize{x.find(_<Int.MinValue)} }
    }

    measure method "filter" in {
      using(ranges(small)) curve ("collections") in { x =>
        x.find(_>0)
      }
      using(ranges(small)) curve ("optimized") in { x => optimize{x.filter(_>0)} }
    }

    measure method "forall" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.forall(_>0)
      }
      using(ranges(large)) curve ("optimized") in { x => optimize{x.forall(_>0)} }
    }

    measure method "flatMap" in {
      using(ranges(small)) curve ("collections") in { x =>
        x.flatMap(x=> List(x, x, x))
      }
      using(ranges(small)) curve ("optimized") in { x => optimize{x.flatMap(x=> List(x, x, x))} }
    }

    measure method "foreach" in {
      using(ranges(large)) curve ("collections") in { x =>
        var count = 0
        x.foreach(x=> count = count + 1)
      }
      using(ranges(large)) curve ("optimized") in { x => 
        var count = 0 
        optimize{x.foreach(x=> count = count + 1) }
      }
    }

    measure method "count" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.count(_>0)
      }
      using(ranges(large)) curve ("optimized") in { x => 
        optimize{x.count(_>0)}
      }
    }

    measure method "fold" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.fold(0)(_+_)
      }
      using(ranges(large)) curve ("optimized") in { x => 
        optimize{x.fold(0)(_+_)}
      }
    }
    measure method "min" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.min
      }
      using(ranges(large)) curve ("optimized") in { x => 
        optimize{x.min}
      }
    }
    measure method "product" in {
      using(ranges(large)) curve ("collections") in { x =>
        x.product
      }
      using(ranges(large)) curve ("optimized") in { x => 
        optimize{x.product}
      }
    }

  }
 

}
