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

object XML extends Controller {
  
  def getStudents = Action { implicit request =>
    Ok("getStudents")
  }

  def getStudent(id: Long) = Action { implicit request =>
    Ok("getStudent " + id)
  }

  // Modify student with a given id
  def putStudent(id :Long) = Action { implicit request =>
    Ok("putStudent " + id)
  }

  // Create new alumno...return id
  def putStudents = Action { implicit request =>
    Ok("postStudents")
  }

  def deleteStudent(id :Long) = Action { implicit request =>
    Ok("deleteStudent " + id)
  }
}


