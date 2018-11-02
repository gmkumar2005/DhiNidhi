package models

import util.JsonSerializers._

object BgbugSummary {
  implicit val jsonEncoder: Encoder[BgbugSummary] = deriveEncoder
  implicit val jsonDecoder: Decoder[BgbugSummary] = deriveDecoder
}
case class BgbugSummary(totalHits: Long, seq: Seq[Bgbug], _id: String)

object Bgbug {
  implicit val jsonEncoder: Encoder[Bgbug] = deriveEncoder
  implicit val jsonDecoder: Decoder[Bgbug] = deriveDecoder
}
case class Bgbug(
    `_id`: String,
    `Detected on Date`: String,
    `Modif_to_Reopen`: String,
    `Bucket`: String,
    `Time_Stamp_New`: String,
    `Affected Fields`: String,
    `Actual Fix Time`: String,
    `Closing Date`: String,
    `Prior_Fixed`: String,
    `FRSD Name`: String,
    `Main Project`: String,
    `Subject`: String,
    `Comments`: String,
    `Original Detected in Project`: String,
    `Delivery Date`: String,
    `Detected By`: String,
    `Close Counter Internal`: String,
    `Sub Status`: String,
    `QA (last)`: String,
    `Previous Group`: String,
    `Description`: String,
    `Technical Component`: String,
    `Product/Project`: String,
    `Testing Phase`: String,
    `ReTest Failed Counter`: String,
    `FRSD Type`: String,
    `Detected in time`: String,
    `Defect ID`: Int,
    `View`: String,
    `Open_date`: String,
    `Number of 'ReOpen'`: String,
    `Detected in Project`: String,
    `Group`: String,
    `Customer`: String,
    `Priority`: String,
    `Is_Old`: String,
    `Prior Defect Status`: String,
    `Prevent Copy`: String,
    `Time_Stamp_Old`: String,
    `All Relevant Info Supplied`: String,
    `Check Field`: String,
    `Detected in Release`: String,
    `NFR  Rejected by Fund`: String,
    `Last Leg`: String,
    `Product`: String,
    `Assigned To`: String,
    `Summary`: String,
    `Close Counter External`: String,
    `Developer (last)`: String,
    `Severity`: String,
    `Modified`: String,
    `Status`: String,
    `Type`: String,
    `Raised By`: String
)

object SearchResult {
  implicit val jsonEncoder: Encoder[SearchResult] = deriveEncoder
  implicit val jsonDecoder: Decoder[SearchResult] = deriveDecoder
}
case class SearchResult(totalHits: Long, bgBugs: Seq[Bgbug])

object ESQuery {
  implicit val jsonEncoder: Encoder[ESQuery] = deriveEncoder
  implicit val jsonDecoder: Decoder[ESQuery] = deriveDecoder
}
case class ESQuery(query: String, start: Int, limit: Int)
