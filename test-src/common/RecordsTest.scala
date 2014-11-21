package scala.virtualization.lms
package common
import scala.virtualization.lms.epfl._

import scala.virtualization.lms.epfl.test2._
import scala.virtualization.lms.epfl.test3._

import org.scala_lang.virtualized.virtualize
import org.scala_lang.virtualized.Struct
import common._

@virtualize
trait BasicProg extends TestOps {
  def f(s: Rep[String]): Rep[String] = {
    val res = Record(name = s, lastName = s)
    res.lastName
  }
}

@virtualize
trait NestedProg extends TestOps {
  def f(s: Rep[String]): Rep[String] = {
    val res = Record(name = s, lastName = Record(fathers = s, last = s))

    res.lastName.fathers
  }
}

@virtualize
trait AsArgumentsProg extends TestOps {
  def f(s: Rep[String]): Rep[String] =
    (if(unit(true)) Record(name = s) else Record(name = s)).name
}

@virtualize
trait MixedTypesProg extends TestOps {
  implicit def lift[T: Manifest](t: T): Rep[T]= unit(t)
  def f(s: Rep[String]): Rep[String] =
    Record(name = s, lastName = "last").lastName
}

trait TestOps extends Functions with Equal with IfThenElse with RecordOps with StructOps
trait TestExp extends FunctionsExp with EqualExp with IfThenElseExp with StructExp
trait TestGen extends ScalaGenFunctions with ScalaGenEqual with ScalaGenIfThenElse with ScalaGenStruct {val IR: TestExp}

class TestBasic extends FileDiffSuite {

  val prefix = home + "test-out/common/records-"

  def testRecordsBasic = {
    withOutFile(prefix+"basic") {
      object BasicProgExp extends BasicProg with TestExp
      import BasicProgExp._

      val p = new TestGen { val IR: BasicProgExp.type = BasicProgExp }
      val stream = new java.io.PrintWriter(System.out)
      p.emitSource(f, "RecordsBasic", stream)
      p.emitDataStructures(stream)
    }
    assertFileEqualsCheck(prefix+"basic")
  }

  def testRecordsNested = {
    withOutFile(prefix+"nested") {
      object NestedProgExp extends NestedProg with TestExp
      import NestedProgExp._

      val p = new TestGen { val IR: NestedProgExp.type = NestedProgExp }
      val stream = new java.io.PrintWriter(System.out)
      p.emitSource(f, "RecordsNested", stream)
      p.emitDataStructures(stream)
    }
    assertFileEqualsCheck(prefix+"nested")
  }

  def testAsArguments = {
    withOutFile(prefix+"as-arguments") {
      object AsArgumentsProgExp extends AsArgumentsProg with TestExp
      import AsArgumentsProgExp._

      val p = new TestGen { val IR: AsArgumentsProgExp.type = AsArgumentsProgExp }
      val stream = new java.io.PrintWriter(System.out)
      p.emitSource(f, "AsArguments", stream)
      p.emitDataStructures(stream)
    }
    assertFileEqualsCheck(prefix+"as-arguments")
  }

  def testMixedTypes = {
    withOutFile(prefix+"mixed-types") {
      object MixedTypesProgExp extends MixedTypesProg with TestExp
      import MixedTypesProgExp._

      val p = new TestGen { val IR: MixedTypesProgExp.type = MixedTypesProgExp }
      val stream = new java.io.PrintWriter(System.out)
      p.emitSource(f, "MixedTypes", stream)
      p.emitDataStructures(stream)
    }
    assertFileEqualsCheck(prefix+"mixed-types")
  }
}