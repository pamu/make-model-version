import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by pnagarjuna on 06/11/15.
  */
object Utils {
  val carWale = "http://www.carwale.com/ajaxpro/CarwaleAjax.AjaxValuation,Carwale.ashx"

  def getMakes(carYear: Int): Future[WSResponse] = {
    val req = WS.client.url(carWale).withHeaders(
      "X-AjaxPro-Method" -> "GetValuationMakes"
    )
    val payload = Json.obj(
      "carYear" -> carYear.toString
    )
    req.post(payload)
  }

  def getModels(carYear: Int, makeId: Int): Future[WSResponse] = {
    val req = WS.client.url(carWale).withHeaders(
      "X-AjaxPro-Method" -> "GetValuationModels"
    )
    val payload = Json.obj(
      "carYear" -> carYear.toString,
      "makeId" -> makeId.toString
    )
    req.post(payload)
  }

  def getVersions(carYear: Int, modelId: Int): Future[WSResponse] = {
    val req = WS.client.url(carWale).withHeaders(
      "X-AjaxPro-Method" -> "GetValuationVersions"
    )
    val payload = Json.obj(
      "carYear" -> carYear.toString,
      "modelId" -> modelId.toString
    )
    req.post(payload)
  }

  def parse(str: String): Option[List[Atomic]] = {
    Try {
      val parsedStr = Json.parse(str)
      val textValue = (parsedStr \ "value").as[String]
      val parsedTextValue = Json.parse(textValue)
      val table = (parsedTextValue \ "Table").asOpt[List[JsValue]]
      for(someList <- table) yield {
        for(item <- someList) yield Atomic((item \ "Value").as[String].trim.toInt, (item \ "Text").as[String])
      }
    } match {
      case Success(value) => value
      case Failure(th) =>
        th.printStackTrace()
        None
    }
  }
}
