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
import play.api.Play.current

object Language {
  
  def getLang (request: Request[AnyContent], session : Session) = {
    val langsReq = request.acceptLanguages
    val defaultLang = Lang.preferred(langsReq.++(Lang.availables)).language
    val lang = session.get("prefLang").getOrElse(defaultLang)
    Lang(lang)
  }   

}
