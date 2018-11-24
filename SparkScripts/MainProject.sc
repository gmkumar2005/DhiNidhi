
	import spark.implicits._
	import org.apache.spark.sql.functions.count
	import org.apache.spark.sql._
	import scala.util.matching.Regex
	import scala.util.matching.Regex

	println("Start Program")
	val errorMaster = spark.read.textFile("StandardErrorsGPP.tsv").toDF("key")
	val logFile = spark.read.textFile("gpp_trace.2018-11-22.0.log")
	errorMaster.count()
	errorMaster.first()
	val explodedDF = logFile.crossJoin(errorMaster)
	
	
	def matcher(row: Row): Boolean = {
	 val pattern = new Regex(row.getAs[String]("key"))
	// row.getAs[String]("value").contains(row.getAs[String]("key"))
	pattern.findFirstIn(row.getAs[String]("value")) match {
    case Some(i) => true
    case None => false
	}
	}
	val result = explodedDF.filter(matcher _).groupBy("key").agg(count("value").as("count"))