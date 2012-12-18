package controllers
import anorm._

case class ViewEnrolment(
	id: Long,
	dni : String, 
	course: String,
    note : Double
)
