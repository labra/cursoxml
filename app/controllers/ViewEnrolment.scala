package controllers
import anorm._

case class ViewEnrolment(
	id: Long,
	course: String,
    dni : String, 
	grade : Double
)
