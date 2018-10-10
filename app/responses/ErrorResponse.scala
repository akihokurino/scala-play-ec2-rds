package responses

import scala.util.parsing.json.JSONObject
import scala.collection.mutable
import play.api.data.FormError

case class ErrorResponse(message: String) {
  def toJson: JSONObject = {
    val body = Map("message" -> message)
    JSONObject(Map("errors" -> JSONObject(body)))
  }
}

object ErrorResponse {

  object Message {
    val invalidAuth = "認証エラーです"
    val invalidSignIn = "Emailまたはパスワードが不正です"
    val invalidStoreSignIn = "店舗IDが不正です"
    val duplicateEmail = "そのEmailはすでに使用されています"
    val notAuthorityUpdateAdminUser = "そのアカウントは更新権限がありません"
    val notAuthorityGetAgency = "そのアカウントは代理店情報を閲覧する権限がありません"
    val unExistUploadFile = "アップロードするファイルを選択して下さい"
    val invalidContractDate = "開始日と終了日が不正です"
    val notUpdateClosedContractStatus = "終了済みのプラン・オプションのステータス更新はできません"
    val invalidDateForCreatePlanContract = "作成するプランの開始日は現在最新のプランの終了日より未来を指定してください"
    val invalidDateForUpdatePlanContract = "更新するプランの開始日を早めることはできません"
    val alreadyExistRecruitment = "すでに求人が存在しています"
    val notOpenForCreateTmp = "特別編集をする求人が公開されていません"
    val notUpdateClosedRecruitmentStatus = "終了している求人のステータス更新はできません"
    val invalidStoreIdForConvertTmp = "特別編集している店舗と現在公開されている店舗のIDが一致しません"
    val alreadyExistStoreName = "すでに同じ名前の店舗が存在しています"
  }
}

case class FormErrorResponse(messages: Seq[FormError]) {

  def toJson: JSONObject = {
    val body: mutable.Map[String, Any] = mutable.Map("message" -> "入力に誤りがあります")
    val detail = mutable.Map.empty[String, String]
    messages.foreach({ e =>
      detail += (e.key -> convertMessage(e.message))
    })
    body += ("detail" -> JSONObject(detail.toMap))

    JSONObject(Map("errors" -> JSONObject(body.toMap)))
  }

  private def convertMessage(message: String): String = {
    message match {
      case "error.required" => "入力値は必須です"
      case "error.maxLength" => "入力値が長過ぎます"
      case "error.minLength" => "入力値が短過ぎます"
      case "error.number" => "数値で入力してください"
      case _ => "不明なエラーです"
    }
  }
}

object FormErrorResponse {

}

