bin/spark-shell --conf  spark.es.nodes="10.91.10.13" --conf spark.es.port="9200"  --conf spark.es.nodes.discovery="false"     --conf es.port="9200"  --conf es.nodes="10.91.10.13:9200" 

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.elasticsearch.spark._
val conf = new SparkConf().setMaster("local[2]").setAppName("DHiNidhi")
val defectsRDD = sc.esRDD("defects","?kiran") 
defectsRDD.first




conf.set("es.nodes", "10.91.10.13")
conf.set("es.port", "9200")

defectsRDD.take(10).foreach(println)
//conf.set("es.index.auto.create", "true")


bin/spark-shell --conf spark.es.nodes="10.91.10.13" --conf spark.es.port="9200"

conf.set("spark.es.nodes", "10.91.10.13")
conf.set("spark.es.nodes", "10.91.10.13")
conf.set("spark.es.port", "100")
conf.set("spark.port", "200")


defectsRDD.first._2("Comments")

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.types._

def stoDouble (s : String): Double = {
return s.map(_.toByte.doubleValue()).reduceLeft( (x,y) => x + y)
}

// org.apache.spark.rdd.RDD[(String, scala.collection.Map[String,AnyRef])]
// val documents: RDD[Seq[String]] = sc.textFile("data/mllib/kmeans_data.txt").map(_.split(" ").toSeq)
val hashingTF = new HashingTF()
val tf: RDD[Vector] = hashingTF.transform(defectsRDD)

val summaryRdd = defectsRDD.map((esdoc)=> esdoc._2("Summary").toString.split(" ").toSeq)
val summaryRdd = defectsRDD.map((esdoc)=> removeStopWords(esdoc._2.getOrElse("Summary","").toString).toSeq) 
val tf: RDD[Vector] = hashingTF.transform(summaryRdd)
tf.cache()
val idf = new IDF().fit(tf)
val tfidf: RDD[Vector] = idf.transform(tf)

val numClusters = 5
val numIterations = 20
val cachedtfidf = tfidf.cache
val clusters = KMeans.train(cachedtfidf, numClusters, numIterations)

val WSSSE = clusters.computeCost(cachedtfidf)


val conf = new SparkConf().setMaster("local[2]").setAppName("DHiNidhi").set("spark.es.nodes", "10.91.10.13:9200")

	.set("es.resource", "defects")
	.set("es.write.operation", "upsert")
	.set("es.mapping.id", "id")
	.set("es.mapping.date.rich", "false")
	.set("es.query", {query})
val esIndex = "index/type"
val sc = new SparkContext(conf)
val esRDD = sc.esRDD()
sc.getConf.setMaster("local[2]").setAppName("DHiNidhi").set("spark.es.nodes", "10.91.10.13:9200").set("spark.es.port","9200")