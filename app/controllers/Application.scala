package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import play.api.i18n._
import anorm._
import views.html.defaultpages.badRequest

object Application extends Controller {
  
  var languages : Seq[Lang] = Seq()
    
  implicit val flash = new play.api.mvc.Flash(Map(("message",""))) 

  def index = Action { implicit request =>
    
    request match {
      case Accepts.Html() => Ok(views.html.index(None, List(),searchForm))
//      case Accepts.Json() => Ok("JSON")
//      case Accepts.Xml() => Ok("XML")
    }
  }

  def about = Action { implicit request =>
    Ok(views.html.about())
  }
  
  def home(flash : Flash) = views.html.index(None,List(),searchForm)(flash)
  
  def searchEnrolment = Action { implicit request =>
    searchForm.bindFromRequest.fold(
    errors => BadRequest(views.html.index(None,List(),errors)),
    searchField => {
      val maybeCourse = Course.findCourse(searchField.course)
      maybeCourse match {
        case None => NotFound(Messages("CourseNotFound"))
        case Some(course) => {
           val enrols = Enrolment.lookupEnrolment(course.code)
           request match {
             case Accepts.Html() => Ok(views.html.index(Some(course), enrols, searchForm))
             case Accepts.Json() => Ok(prepareJson(course,enrols))
             case Accepts.Xml() => Ok(prepareXML(course,enrols))
           }        
        }
      }
    }
   ) 
  }

  def prepareJson(course: Course, enrols: List[(Student,Double)]) : String = {
    "JSON " + enrols
  }
  
  def prepareXML(course: Course, enrols: List[(Student,Double)]) : String = {
    val xmlOut = 
    <alumnos xmlns="http://www.uniovi.es/alumnos">
     <curso href={course.code}>{course.name}</curso> {
       enrols.map { e => 
       <alumno dni={e._1.dni}>
        <nombre>{e._1.firstName}</nombre>
        <apellidos> {e._1.lastName}</apellidos>
        <email>{e._1.email}</email>
        <lat>{e._1.lat}</lat>
        <long>{e._1.long}</long>
        <nota>{e._2}</nota>
     </alumno>}
      }
    </alumnos>
    xmlOut.toString
  }
  
  val searchForm : Form[SearchField] = Form (
     mapping(
      "course" -> nonEmptyText
     )(SearchField.apply)(SearchField.unapply)
  )

}