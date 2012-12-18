package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Enrolment(
	id: Pk[Long],
	courseId : Long, 
	studentId: Long,
    grade : Double
)

object Enrolment {

  val enrolment = {
	get[Pk[Long]]("id") ~ 
	get[Long]("courseId") ~ 
	get[Long]("studentId") ~ 
	get[Double]("grade") map {
  	  case id ~
  	  	   courseId ~
  	  	   studentId ~
  	  	   grade => {
  	  		 Enrolment(id, courseId, studentId, grade)
  	  	   }
  	}
  }
  
  
  def all(): List[Enrolment] = DB.withConnection { implicit c =>
  	SQL("select * from enrolment").as(enrolment *)
  }
  
  def create(courseId: Long, studentId: Long, grade: Double) {
    DB.withConnection { implicit c =>
      SQL("insert into enrolment (courseId,studentId,grade) values (%s, %s, %s)".
	  	     format(courseId,studentId,grade)).executeUpdate()
    }
  }

  def insert(enrolment: Enrolment) {
	  DB.withConnection { implicit c =>
	  	SQL("insert into enrolment (courseId,studentId,grade) values (%s, %s, %s)".
	  	     format(enrolment.courseId,
	  	            enrolment.studentId,
	  	            enrolment.grade)).executeUpdate()
	  }
  }
  def delete(id: Pk[Long]) {
		DB.withConnection { implicit c =>
    	SQL("delete from enrolment where id = {id}").on(
    		'id -> id
    		).executeUpdate()
		}
	}

  def lookupIds(courseId : Long, studentId: Long) : Option[Long] = {
        val query = "SELECT id FROM enrolment WHERE { courseId = %s and studentId = %s } ORDER BY grade DESC;".format(courseId,studentId)
        val ids : List[Long]= DB.withConnection { implicit c =>
    		SQL(query).as(scalar[Long].*)
		}
        if (ids.isEmpty) None
        else Some(ids.head)
  }
    
  def lookupEnrolment(courseStr : String) : List[(Student,Double)] = {
        
    List((new Student(Id(1),"12","Jose","Labra"),2.5),
         (new Student(Id(2),"13","Juan","Torre"),7.5))
  }
  
  def findById(id : Long) : Option[Enrolment] = {
   val enrols = DB.withConnection { implicit c =>
  		 SQL("select * from enrolment where id = {id}").on('id -> id).as(enrolment *)
       }
   if (enrols.isEmpty) None
   else Some(enrols.head)
  }

}