
import org.apache.spark.SparkConf
// import org.apache.spark.SparkContext._
import org.elasticsearch.spark._
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.types._
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.functions._

val conf = new SparkConf().setMaster("local[2]").setAppName("DHiNidhi").set("es.write.operation", "upsert").set("es.read.metadata", "true")


val defectsRDD = sc.esRDD("defects") 
val defectsRDD = sc.esRDD("defects","?q=kiran.kumar") 
defectsRDD.first

def removeStopWords(sentence: String) = {
    val stopWords = Set("gpp", "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "youre", "youve", "youll",
      "youd", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "shes", "her", "hers",
      "herself", "it", "its", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom",
      "this", "that", "thatll", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
      "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with",
      "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on",
      "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few",
      "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don",
      "dont", "should", "shouldve", "now", "d", "ll", "m", "o", "re", "ve", "y", "ain", "aren", "arent", "couldn", "couldnt", "didn", "didnt", "doesn", "doesnt",
      "hadn", "hadnt", "hasn", "hasnt", "haven", "havent", "isn", "isnt", "ma", "mightn", "mightnt", "mustn", "mustnt", "needn", "neednt", "shan", "shant",
      "shouldn", "shouldnt", "wasn", "wasnt", "weren", "werent", "won", "wont", "wouldn", "wouldnt","payment","defect","retrofit")
    val trimmedSentance = sentence.filter(_ >= ' ').trim.replaceAll(" +", " ").replaceAll("[^A-Za-z0-9\\s]", "").toLowerCase
    val wordList = trimmedSentance.split(" ")
    val cleanList = wordList.filter(!stopWords.contains(_))
	cleanList.filter(_.nonEmpty)
    //cleanList.mkString(" ")
 }



case class Dhi(id:String,token:Array[String])
// val summaryRdd = defectsRDD.map((esdoc)=> removeStopWords(esdoc._2.getOrElse("Summary","").toString))
//
 val summaryRowRdd = defectsRDD.map((esdoc)=> ( Dhi(esdoc._2.getOrElse("Defect ID","").toString,removeStopWords(esdoc._2.getOrElse("Summary","").toString))))
 
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructType, StructField, StringType}

val hashTF = new HashingTF().setInputCol("token").setOutputCol("features").setNumFeatures(100) 

val features = hashTF.transform(summaryRowRdd.toDF)
features.select('id,'token, 'features).show(false)

val kmeans = new KMeans
kmeans.setK(5)
val kmModel = kmeans.fit(features.toDF)
//kmModel.clusterCenters.map(_.toSparse)
val result = kmModel.transform(features)
result.show
//result.select('id,col("Features").alias("Features(vocab_size,[index],[tf])")).show(false)
//result.select(size('token), 'Features._1).show
//result.select('id,'prediction).write.format("com.databricks.spark.csv").option("header", "true").save("output.csv")
//result.write.format("com.databricks.spark.csv").option("header", "true").save("output.csv")

val featuresSizes = result.map((r)=>{val features = r.getAs[org.apache.spark.ml.linalg.SparseVector]("features") ; println(features) ;val indexOfMax = if(!features.indices.isEmpty) features.indices.zipWithIndex.maxBy(_._1)._2 else 0; r.getAs[Seq[String]]("token")(indexOfMax) } )


val addCat : (Seq[String],org.apache.spark.ml.linalg.SparseVector)=>String=(token:Seq[String],features:org.apache.spark.ml.linalg.SparseVector)=>{val indexOfMax = if(!features.indices.isEmpty) features.indices.zipWithIndex.maxBy(_._1)._2 else 0;  token(indexOfMax)}
val addCatUDF = udf(addCat)
val resultWithSignificantTerms = result.withColumn("category", addCatUDF(result.col("token"),result.col("features")))

resultWithSignificantTerms.filter(resultWithSignificantTerms("prediction")==="0").show()
val resultWords = resultWithSignificantTerms.cache
val wordCountDF = resultWithSignificantTerms.groupBy("category").count.show
val wordCountDF = resultWithSignificantTerms.groupBy("id").count.show
featuresSizes.filter($"value".isNotNull).show()
val nonEmptyCats = resultWithSignificantTerms.filter($"category".isNotNull)
val wordCountDF = nonEmptyCats.groupBy("category").count
wordCountDF.show

val mostRepeatedWord = result.select('token).map(word => (word, 1))

val linesDF = sc.textFile("README.md").toDF("line")

val wordsDF = result.explode("token","word")((line: Seq[String]) => line).filter(result("prediction")==="0")
val mostRepeatedWord =  wordsDF.map(r => (r.getAs[String]("word"), 1))
  .reduceByKey((a, b) => a + b).reduce((a, b) => if (a._2 > b._2) a else b)

mostRepeatedWord.groupBy("word").sum().show()
wordsDF.select('word).mkString(" ")

//val df = Seq("John", "Jane", "Eve", "Joe", "Eve").toDF("name")
val wordsDF = result.explode("token","word")((line: Seq[String]) => line)
val grouping = Seq($"prediction",$"word")
val mostFrequentWord= wordsDF.groupBy(grouping: _*).count.agg(max(struct($"count" +: grouping: _*)))
// with Description
val defectsRDD = sc.esRDD("defects")
val summaryRowRdd = defectsRDD.map((esdoc)=> ( Dhi(esdoc._2.getOrElse("Defect ID","").toString,removeStopWords(esdoc._2.getOrElse("Summary","").toString+ esdoc._2.getOrElse("Description","").toString))))
val hashTF = new HashingTF().setInputCol("token").setOutputCol("features").setNumFeatures(1000)
val features = hashTF.transform(summaryRowRdd.toDF)
val kmeans = new KMeans
kmeans.setK(5)
val kmModel = kmeans.fit(features.toDF)
val result = kmModel.transform(features)
val wordsDF = result.explode("token","word")((line: Seq[String]) => line)
  //.filter(result("prediction")==="0")
val grouping = Seq($"prediction",$"word")
val mostFrequentWord= wordsDF.groupBy(grouping: _*).count.agg(max(struct($"count" +: grouping: _*)))
val mostFrequentWord0 = wordsDF.filter(result("prediction")==="0").groupBy(grouping: _*).count.agg(max(struct($"count" +: grouping: _*))).show
val mostFrequentWord1 = wordsDF.filter(result("prediction")==="1").groupBy(grouping: _*).count.agg(max(struct($"count" +: grouping: _*))).show
val mostFrequentWord2 = wordsDF.filter(result("prediction")==="2").groupBy(grouping: _*).count.agg(max(struct($"count" +: grouping: _*))).show
val mostFrequentWord3 = wordsDF.filter(result("prediction")==="3").groupBy(grouping: _*).count.agg(max(struct($"count" +: grouping: _*))).show
val mostFrequentWord4 = wordsDF.filter(result("prediction")==="4").groupBy(grouping: _*).count.agg(max(struct($"count" +: grouping: _*))).show


def addSomeProp(row:Tuple2[String,scala.collection.Map[String,AnyRef]]): Map[String,String] = {
  //add some new property to the existing document
  //note below should be [underscore]id, some sort of formatting is removing underscore in the post
  return Map("prediction" -> row._1, "studentTeacherRatio" -> "5")
}