package models

import play.api.db._
import play.api.Logger
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Course(
	id: Pk[Long], 
	courseName: String
)

object Course {


  val course = {
	get[Pk[Long]]("id") ~ get[String]("courseName") map {
  	  case id~courseName => Course(id, courseName)
  	}
  }
  
  def all(): List[Course] = DB.withConnection { implicit c =>
  	SQL("select * from course").as(course *)
  }
  
def create(courseName: String) {
  // Insert a course only if it did not exist
  if (lookup(courseName) == None) 
   DB.withConnection { implicit c =>
     SQL("insert into course (courseName) values ({courseName})").on(
       'courseName -> courseName
     ).executeUpdate()
   }
}

 def delete(id: Pk[Long]) {
  DB.withConnection { implicit c =>
    SQL("delete from course where id = {id}").on(
    		'id -> id
    		).executeUpdate()
		}
	}

 def lookup(courseName : String) : Option[Long] = {
    val ids = DB.withConnection { implicit c =>
    	SQL("select id from course where courseName = {courseName}").on(
    		'courseName -> courseName
    		).as(scalar[Long].*)
		}
    ids.length match {
      case 0 => None
      case 1 => Some(ids.head)
      case _ => {
        Logger.warn("Lookup course: " + courseName + ". More than one id (selected the first)")
        Some(ids.head)
      }
    }
  }
 
   def findCourseName(id : Long) : Option[String] = {
    val found = DB.withConnection { implicit c =>
    SQL("select * from course where id = {id}").on(
    		'id -> id
    	).as(course *)
	}
    if (found.isEmpty) None
    else Some(found.head.courseName)
  }

   def find(id : Long) : Option[Course] = {
    val found = DB.withConnection { implicit c =>
    SQL("select * from course where id = {id}").on(
    		'id -> id
    	).as(course *)
	}
    if (found.isEmpty) None
    else Some(found.head)
  }

}