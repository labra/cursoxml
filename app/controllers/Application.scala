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

    Ok(views.html.index(searchForm))
  }

  def about = Action { implicit request =>
    Ok(views.html.about())
  }
  
  def home(flash : Flash) = views.html.index(searchForm)(flash)
  def transResult(viewEnrol : ViewEnrolment) = Ok(views.html.result(viewEnrol))
  
/*  def format = Action { implicit request =>
    formatForm.bindFromRequest.fold(
        errors => BadRequest("Error: " + errors.toString()),
        formatField => {
          val viewTrans = ViewTranslation(formatField.id,formatField.iriName,formatField.langName,formatField.label)
          contentNegotiation(Some(formatField.format)) match {
            case HTML() => prepareHTML(viewTrans)
            case TURTLE() => prepareTurtle(viewTrans)
            case JSON() => prepareJson(viewTrans)
            case _ => prepareHTML(viewTrans)
          }
        }
    )
  }
*/
  def searchEnrolment = Action { implicit request =>

    searchForm.bindFromRequest.fold(
    errors => BadRequest(views.html.index(errors)),
    searchField => {
      val course = searchField.course
      val dni = searchField.dni
      val result = Enrolment.lookupEnrolment(course, dni)
      result match { 
        case None => NotFound("Not found")
        case Some(enrol) => 
              val viewEnrol = ViewEnrolment(enrol.id.get,course,dni,enrol.note)
              Ok("Result: " + viewEnrol)
              
      }
    }
   ) 
  }

  val searchForm : Form[SearchField] = Form (
     mapping(
      "course" -> nonEmptyText,
      "dni" -> nonEmptyText
     )(SearchField.apply)(SearchField.unapply)
  )

}