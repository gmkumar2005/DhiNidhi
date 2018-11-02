/* Generated File */
package models.ddl

import java.time.LocalDateTime
import models.result.data.{DataField, DataFieldModel, DataSummary}
import util.JsonSerializers._

object FlywaySchemaHistory {
  implicit val jsonEncoder: Encoder[FlywaySchemaHistory] = deriveEncoder
  implicit val jsonDecoder: Decoder[FlywaySchemaHistory] = deriveDecoder

  def empty(installedRank: Long = 0L, version: Option[String] = None, description: String = "", typ: String = "", script: String = "", checksum: Option[Long] = None, installedBy: String = "", installedOn: LocalDateTime = util.DateUtils.now, executionTime: Long = 0L, success: Boolean = false) = {
    FlywaySchemaHistory(installedRank, version, description, typ, script, checksum, installedBy, installedOn, executionTime, success)
  }
}

final case class FlywaySchemaHistory(
    installedRank: Long,
    version: Option[String],
    description: String,
    typ: String,
    script: String,
    checksum: Option[Long],
    installedBy: String,
    installedOn: LocalDateTime,
    executionTime: Long,
    success: Boolean
) extends DataFieldModel {
  override def toDataFields = Seq(
    DataField("installedRank", Some(installedRank.toString)),
    DataField("version", version),
    DataField("description", Some(description)),
    DataField("typ", Some(typ)),
    DataField("script", Some(script)),
    DataField("checksum", checksum.map(_.toString)),
    DataField("installedBy", Some(installedBy)),
    DataField("installedOn", Some(installedOn.toString)),
    DataField("executionTime", Some(executionTime.toString)),
    DataField("success", Some(success.toString))
  )

  def toSummary = DataSummary(model = "flywaySchemaHistory", pk = Seq(installedRank.toString), title = s"$version / $typ / $script / $installedOn / $executionTime / $success ($installedRank)")
}
