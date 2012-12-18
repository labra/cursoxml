package models
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import anorm._

class StudentSpec extends Specification {

  "create Student" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val dni = "22"
      val firstName = "Jose"
      val lastName = "Jose"
      Student.create(dni,firstName,lastName)
      val id = Student.lookup(dni)
	  id must beSome  
    }
   }
  
  "create the same student several times" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val dni = "22"
      val firstName = "Jose"
      val lastName = "Jose"
      Student.create(dni,firstName,lastName)
      Student.create(dni,firstName,lastName)
      Student.create(dni,firstName,lastName)
      val id = Student.lookup(dni)
	  id must beSome  
    }
   }

    "delete Student" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      Student.create("22","Jose","Labra")
      Student.create("33", "Juan","Torre")

      val id = Student.lookup("22")
      id must beSome  
      Student.delete(Id(id.get))
      val id2 = Student.lookup("22")
      id2 must beNone
    }
   }

}