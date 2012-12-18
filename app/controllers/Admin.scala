package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import play.api.i18n._
import anorm._

object Admin extends Controller {
  
  def admin = Action {
    Ok(views.html.admin())
  }
  
  def newCourse = Action { implicit request =>
  	courseForm.bindFromRequest.fold(
    errors => Ok("Errors" + errors), // BadRequest(views.html.index(searchForm)),
    course => {
      Course.create(course.code,course.name,course.starts,course.ends)
      Redirect(routes.Admin.courses)
    }
   )
  }	  
 
  def newStudent = Action { implicit request =>
 	studentForm.bindFromRequest.fold(
    errors => Ok("Error " + errors.toString()), // BadRequest(views.html.index(Language.all(), errors)),
    student => {
      Student.insert(student)
      Redirect(routes.Admin.students)
    }
   ) 
  }
  
   def newEnrolment = Action { 
    implicit request =>
  	 enrolmentForm.bindFromRequest.fold(
      errors => Ok("Error " + errors.toString()), // BadRequest(views.html.index(Trans.all(), errors)),
      vt => {
        Student.lookup(vt.dni) match {
          case None => Ok("Student " + vt.dni + " not found. Create student before")
          case Some(studentId) =>
            Course.lookup(vt.course) match {
              case None => Ok("Course " + vt.course + " not found. Create Course before")
              case Some(courseId) => 
                Enrolment.create(courseId,studentId,vt.grade)
                Redirect(routes.Admin.enrolments)
            }
        }
      }
   )
  }

  def deleteCourse(id: Long) = Action {
	  Course.delete(Id(id))
	  Redirect(routes.Admin.courses)
  }

 def deleteStudent(id: Long) = Action {
  Student.delete(Id(id))
  Redirect(routes.Admin.students)
}

 def deleteEnrolment(id: Long) = Action {
  Enrolment.delete(Id(id))
  Ok("Deleted enrolment " + id)
  Redirect(routes.Admin.enrolments)
}

  val courseForm : Form[Course] = Form(
      mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "starts" -> text,
      "ends" -> text
      )(Course.apply)(Course.unapply)
  )

  
  val studentForm : Form[Student] = Form(
     mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "dni" -> text,
      "firstName" -> text,
      "lastName" -> text
     )(Student.apply)(Student.unapply)
  )

  val enrolmentForm : Form[ViewEnrolment] = Form(
     mapping(
      "id" -> of[Long],
      "course" -> nonEmptyText,
      "dni" -> nonEmptyText,
      "grade" -> of[Double]
     )(ViewEnrolment.apply)(ViewEnrolment.unapply)
  )
  
   def courses = Action {
	  Ok(views.html.courses(Course.all(), courseForm))
  }

  def students = Action { 	  
    Ok(views.html.students(Student.all(), studentForm))
  }

  def viewEnrolments : List[ViewEnrolment] = {
    Enrolment.all().map(t => ViewEnrolment(t.id.get,
        Course.findCourseName(t.courseId).getOrElse("Not found"), 
        Student.findDNI(t.studentId).getOrElse("Not found"),
        t.grade.toDouble)
    )
  }

  def enrolments = Action { request =>
    // TODO select default language
    Ok(views.html.enrolments(viewEnrolments,enrolmentForm,Lang("es")))
  }

  

 
}
