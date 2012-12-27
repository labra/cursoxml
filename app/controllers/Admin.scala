package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import play.api.i18n._
import anorm._

object Admin extends Controller  with Secured {
  
  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Admin.admin).withSession("email" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Admin.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  
  def admin = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      Ok(views.html.admin())
    }.getOrElse(Forbidden)
  }
  
  
  def newCourse = IsAuthenticated { username => implicit request =>
  	courseForm.bindFromRequest.fold(
    errors => Ok("Errors" + errors), // BadRequest(views.html.index(searchForm)),
    course => {
      Course.create(course)
      Redirect(routes.Admin.courses)
    }
   )
  }	  
 
  def newStudent = IsAuthenticated { username => implicit request =>
 	studentForm.bindFromRequest.fold(
    errors => Ok("Error " + errors.toString()), // BadRequest(views.html.index(Language.all(), errors)),
    student => {
      Student.create(student)
      Redirect(routes.Admin.students)
    }
   ) 
  }
  
   def newEnrolment = IsAuthenticated { username =>
    implicit request =>
  	 enrolmentForm.bindFromRequest.fold(
      errors => Ok("Error " + errors.toString()), // BadRequest(views.html.index(Trans.all(), errors)),
      vt => createFromView(vt)
   )
  }

  def createFromView(vt: ViewEnrolment) : Result = {
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
  
   
  def deleteCourse(id: Long) = IsAuthenticated { username => implicit request =>
	  Course.delete(Id(id))
	  Redirect(routes.Admin.courses)
  }

 def deleteStudent(id: Long) = IsAuthenticated { username => implicit request =>
  Student.delete(Id(id))
  Redirect(routes.Admin.students)
}

 def deleteEnrolment(id: Long) = IsAuthenticated { username => implicit request =>
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


trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Admin.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

}
