package models
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._


class EnrolmentSpec extends Specification {

  "create an enrolment" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val dni = "22"
      Student.create(dni,"Jose","Labra")
      val idStudent = Student.lookup(dni)
      
      val code = "xml"
      Course.create(code,"XML Course", "", "")
      val idCourse = Course.lookup(code)
      
      Enrolment.create(idCourse.get,idStudent.get,4.5)

      val idt = Enrolment.lookupIds(idCourse.get,idStudent.get)
	  idt must beSome
	  val t = Enrolment.findById(idt.get)
	  t must beSome
	  t.get.note must beEqualTo(4.5)
    }
   }


}