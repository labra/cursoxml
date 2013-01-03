package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import play.api.i18n._
import anorm._
import play.api.Play.current

object Students extends Controller {
  
  def getStudents = Action { implicit request =>
    Ok(showXMLStudents(students))
  }
  
  def showXMLStudents(students : List[Student]) = 
    <students>
      { students.map( s => showXMLStudent(s)) }
     </students>
 

  def getStudent(id: Long) = Action { implicit request =>
    Student.find(id) match {
      case Some(student) => Ok(showXMLStudent(student))
      case None => NotFound("Student with id " + id + " not found")
    }
  }

  def showXMLStudent(s : Student) = 
    <student dni={s.dni}>
            <id>{s.id}</id>
            <firstName>{s.firstName}</firstName>
            <lastName>{s.lastName}</lastName>
            <email>{s.email}</email>
            <lat>{s.email}</lat>
            <long>{s.email}</long>
      </student>
    
  // Modify student with a given id
  def putStudent(id : Long) = Action { implicit request =>
    studentForm.bindFromRequest.fold(
    errors => NotFound("Bad request: " + errors),
    student => {
      Student.update(id, student)
      Redirect(routes.Students.getStudents)    }
   )
  }

  // Create new alumno...return id
  def postStudents = Action { implicit request =>
    studentForm.bindFromRequest.fold(
    errors => NotFound("Bad request: " + errors),
    student => {
      Student.create(student)
      Redirect(routes.Students.getStudents)
    }
   ) 
  }

  def deleteStudent(id :Long) = Action { implicit request =>
    Student.find(id) match {
      case Some(student) => {
        Student.delete(student.id)
        Redirect(routes.Students.getStudents)
      }
      case None => NotFound("Cannot delete student with id " + id)
    }
  }
  
  def students = Student.all
  
  val studentForm : Form[Student] = Form(
     mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "dni" -> text,
      "firstName" -> text,
      "lastName" -> text,
      "email" -> text,
      "lat" -> of[Double],
      "long" -> of[Double]
     )(Student.apply)(Student.unapply)
  )

}


