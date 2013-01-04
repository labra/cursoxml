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
import play.api.libs.json._
import play.api.libs.Jsonp
import play.api.libs.iteratee._

object Students extends Controller {
  
  val TextXML = Accepting("text/xml")
  val TextTurtle = Accepting("text/turtle")

  def getStudents = Action { implicit request =>
    request match {
      case Accepts.Html() => {
       Ok(views.html.students(Language.getLang(request,session),students,studentForm)) 
      }
      case Accepts.Json() => {
       val json = showJsonStudents(students)
       request.queryString.get("callback").flatMap(_.headOption) match {
        case Some(callback) => Ok(Jsonp(callback, json))
        case None => Ok(json)
       }
      }
      case TextXML() => Ok(showXMLStudents(students))
      case Accepts.Xml() => Ok(showXMLStudents(students))
      case TextTurtle() => {
       SimpleResult(
    		   header = ResponseHeader(200, Map(CONTENT_TYPE -> "text/turtle")), 
    		   body = Enumerator(showTurtleStudents(students))
       ) 
      }
      case _ => Ok("Cannot handle accept header: " + request.accept.mkString(",") )
    }
  }

  def showJsonStudents(students : List[Student]) = {
    Json.toJson (students.map( s => 
        Json.toJson(Map (
    			"dni" -> Json.toJson(s.dni),
                "firstName" -> Json.toJson (s.firstName),
                "lastName" -> Json.toJson (s.lastName),
                "email" -> Json.toJson (s.email),
                "lat" -> Json.toJson (s.lat),
                "long" -> Json.toJson (s.long)
                ))
   ))
  }
   
  def showTurtleStudents(students : List[Student]) = {
"""@prefix e: <http://example.org/> .
@prefix foaf: 	<http://xmlns.com/foaf/0.1/> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
""" + students.map( s => 
        "\n\ne:" + s.id + " e:dni \"" + s.dni + "\" ; \n" + 
                "  foaf:givenName \"" + s.firstName + "\"; \n" +
                "  foaf:familyName \"" + s.lastName + "\"; \n" +
                "  foaf:mbox \"" + s.email + "\"; \n" +
                "  geo:lat " + s.lat + "; \n" +
                "  geo:long " + s.long + " . "
                ).mkString
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


