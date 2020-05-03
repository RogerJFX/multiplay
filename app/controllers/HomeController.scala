package controllers

import javax.inject._
import play.api.mvc._

/**
 * Forget this one.
 * @param cc CC
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    Ok(views.html.index())
  }

}
