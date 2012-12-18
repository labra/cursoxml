package models

import play.api.db._
import play.api.Logger
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Course(
	id: Pk[Long], 
	code: String,
	name: String,
	starts: String,
	ends: String
)

object Course {


  val course = {
	get[Pk[Long]]("id") ~ get[String]("code")~ get[String]("name")~ get[String]("starts")~ get[String]("ends") map {
  	  case id~code~name~starts~ends => Course(id, code,name,starts,ends)
  	}
  }
  
  def all(): List[Course] = DB.withConnection { implicit c =>
  	SQL("select * from course").as(course *)
  }
  
def create(code: String, name: String, starts: String, ends: String) {
  // Insert a course only if it did not exist
  if (lookup(code) == None) 
   DB.withConnection { implicit c =>
     SQL("insert into course (code,name,starts,ends) values ({code},{name},{starts},{ends})").on(
       'code -> code,
       'name -> name,
       'starts -> starts,
       'ends -> ends
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

 def lookup(code : String) : Option[Long] = {
    val ids = DB.withConnection { implicit c =>
    	SQL("select id from course where code = {code}").on(
    		'code -> code
    		).as(scalar[Long].*)
		}
    ids.length match {
      case 0 => None
      case 1 => Some(ids.head)
      case _ => {
        Logger.warn("Lookup course: " + code + ". More than one id (selected the first)")
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
    else Some(found.head.name)
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