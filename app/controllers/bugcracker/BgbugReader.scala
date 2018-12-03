package controllers.bugcracker

import com.sksamuel.elastic4s.{Hit, HitReader}
import models.Bgbug

object BgbugReader {
  implicit object BgbugReader extends HitReader[Bgbug] {
    override def read(hit: Hit): Either[Throwable, Bgbug] = {
      Right(Bgbug(
        hit.id,
        hit.sourceAsMap.getOrElse("Detected on Date", "Empty").toString,
        hit.sourceAsMap.getOrElse("Modif_to_Reopen", "Empty").toString,
        hit.sourceAsMap.getOrElse("Bucket", "Empty").toString,
        hit.sourceAsMap.getOrElse("Time_Stamp_New", "Empty").toString,
        hit.sourceAsMap.getOrElse("Affected Fields", "Empty").toString,
        hit.sourceAsMap.getOrElse("Actual Fix Time", "Empty").toString,
        hit.sourceAsMap.getOrElse("Closing Date", "Empty").toString,
        hit.sourceAsMap.getOrElse("Prior_Fixed", "Empty").toString,
        hit.sourceAsMap.getOrElse("FRSD Name", "Empty").toString,
        hit.sourceAsMap.getOrElse("Main Project", "Empty").toString,
        hit.sourceAsMap.getOrElse("Subject", "Empty").toString,
        hit.sourceAsMap.getOrElse("Comments", "Empty").toString,
        hit.sourceAsMap.getOrElse("Original Detected in Project", "Empty").toString,
        hit.sourceAsMap.getOrElse("Delivery Date", "Empty").toString,
        hit.sourceAsMap.getOrElse("Detected By", "Empty").toString,
        hit.sourceAsMap.getOrElse("Close Counter Internal", "Empty").toString,
        hit.sourceAsMap.getOrElse("Sub Status", "Empty").toString,
        hit.sourceAsMap.getOrElse("QA (last)", "Empty").toString,
        hit.sourceAsMap.getOrElse("Previous Group", "Empty").toString,
        hit.sourceAsMap.getOrElse("Description", "Empty").toString,
        hit.sourceAsMap.getOrElse("Technical Component", "Empty").toString,
        hit.sourceAsMap.getOrElse("Product/Project", "Empty").toString,
        hit.sourceAsMap.getOrElse("Testing Phase", "Empty").toString,
        hit.sourceAsMap.getOrElse("ReTest Failed Counter", "Empty").toString,
        hit.sourceAsMap.getOrElse("FRSD Type", "Empty").toString,
        hit.sourceAsMap.getOrElse("Detected in time", "Empty").toString,
        hit.sourceAsMap.getOrElse("Defect ID", "Empty").asInstanceOf[Int],
        hit.sourceAsMap.getOrElse("View", "Empty").toString,
        hit.sourceAsMap.getOrElse("Open_date", "Empty").toString,
        hit.sourceAsMap.getOrElse("Number of 'ReOpen'", "Empty").toString,
        hit.sourceAsMap.getOrElse("Detected in Project", "Empty").toString,
        hit.sourceAsMap.getOrElse("Group", "Empty").toString,
        hit.sourceAsMap.getOrElse("Customer", "Empty").toString,
        hit.sourceAsMap.getOrElse("Priority", "Empty").toString,
        hit.sourceAsMap.getOrElse("Is_Old", "Empty").toString,
        hit.sourceAsMap.getOrElse("Prior Defect Status", "Empty").toString,
        hit.sourceAsMap.getOrElse("Prevent Copy", "Empty").toString,
        hit.sourceAsMap.getOrElse("Time_Stamp_Old", "Empty").toString,
        hit.sourceAsMap.getOrElse("All Relevant Info Supplied", "Empty").toString,
        hit.sourceAsMap.getOrElse("Check Field", "Empty").toString,
        hit.sourceAsMap.getOrElse("Detected in Release", "Empty").toString,
        hit.sourceAsMap.getOrElse("NFR  Rejected by Fund", "Empty").toString,
        hit.sourceAsMap.getOrElse("Last Leg", "Empty").toString,
        hit.sourceAsMap.getOrElse("Product", "Empty").toString,
        hit.sourceAsMap.getOrElse("Assigned To", "Empty").toString,
        hit.sourceAsMap.getOrElse("Summary", "Empty").toString,
        hit.sourceAsMap.getOrElse("Close Counter External", "Empty").toString,
        hit.sourceAsMap.getOrElse("Developer (last)", "Empty").toString,
        hit.sourceAsMap.getOrElse("Severity", "Empty").toString,
        hit.sourceAsMap.getOrElse("Modified", "Empty").toString,
        hit.sourceAsMap.getOrElse("Status", "Empty").toString,
        hit.sourceAsMap.getOrElse("Type", "Empty").toString,
        hit.sourceAsMap.getOrElse("Raised By", "Empty").toString,
        hit.sourceAsMap.getOrElse("Suggested_Resolution", "Empty").toString
      )
      )
    }
  }
}
