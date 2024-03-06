file:///C:/Users/Pc/Desktop/MECELLONE/EMOFY/src/main/scala/com/emofy/models/User.scala
### java.lang.IndexOutOfBoundsException: 462

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 2.13.13
Classpath:
<WORKSPACE>\src\main\resources [exists ], <WORKSPACE>\src\main\resources [exists ], <WORKSPACE>\target\bloop-bsp-clients-classes\classes-Metals-Bwq9EN8LRXao4cpiQluyzg== [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\sourcegraph\semanticdb-javac\0.9.9\semanticdb-javac-0.9.9.jar [exists ], <WORKSPACE>\target\classes [exists ], <HOME>\.m2\repository\org\scala-lang\scala-library\2.13.13\scala-library-2.13.13.jar [exists ], <HOME>\.m2\repository\org\springframework\boot\spring-boot-starter\3.2.3\spring-boot-starter-3.2.3.jar [exists ], <HOME>\.m2\repository\org\springframework\boot\spring-boot\3.2.3\spring-boot-3.2.3.jar [exists ], <HOME>\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\3.2.3\spring-boot-autoconfigure-3.2.3.jar [exists ], <HOME>\.m2\repository\org\springframework\boot\spring-boot-starter-logging\3.2.3\spring-boot-starter-logging-3.2.3.jar [exists ], <HOME>\.m2\repository\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar [exists ], <HOME>\.m2\repository\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar [exists ], <HOME>\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.21.1\log4j-to-slf4j-2.21.1.jar [exists ], <HOME>\.m2\repository\org\apache\logging\log4j\log4j-api\2.21.1\log4j-api-2.21.1.jar [exists ], <HOME>\.m2\repository\org\slf4j\jul-to-slf4j\2.0.12\jul-to-slf4j-2.0.12.jar [exists ], <HOME>\.m2\repository\jakarta\annotation\jakarta.annotation-api\2.1.1\jakarta.annotation-api-2.1.1.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-core\6.1.4\spring-core-6.1.4.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-jcl\6.1.4\spring-jcl-6.1.4.jar [exists ], <HOME>\.m2\repository\org\yaml\snakeyaml\2.2\snakeyaml-2.2.jar [exists ], <HOME>\.m2\repository\net\bytebuddy\byte-buddy\1.14.12\byte-buddy-1.14.12.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-boot-starter\3.0.0\springfox-boot-starter-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-oas\3.0.0\springfox-oas-3.0.0.jar [exists ], <HOME>\.m2\repository\io\swagger\core\v3\swagger-annotations\2.1.2\swagger-annotations-2.1.2.jar [exists ], <HOME>\.m2\repository\io\swagger\core\v3\swagger-models\2.1.2\swagger-models-2.1.2.jar [exists ], <HOME>\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.4\jackson-annotations-2.15.4.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-spi\3.0.0\springfox-spi-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-schema\3.0.0\springfox-schema-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-core\3.0.0\springfox-core-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-spring-web\3.0.0\springfox-spring-web-3.0.0.jar [exists ], <HOME>\.m2\repository\io\github\classgraph\classgraph\4.8.83\classgraph-4.8.83.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-spring-webmvc\3.0.0\springfox-spring-webmvc-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-spring-webflux\3.0.0\springfox-spring-webflux-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-swagger-common\3.0.0\springfox-swagger-common-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-data-rest\3.0.0\springfox-data-rest-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-bean-validators\3.0.0\springfox-bean-validators-3.0.0.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-swagger2\3.0.0\springfox-swagger2-3.0.0.jar [exists ], <HOME>\.m2\repository\io\swagger\swagger-annotations\1.5.20\swagger-annotations-1.5.20.jar [exists ], <HOME>\.m2\repository\io\swagger\swagger-models\1.5.20\swagger-models-1.5.20.jar [exists ], <HOME>\.m2\repository\io\springfox\springfox-swagger-ui\3.0.0\springfox-swagger-ui-3.0.0.jar [exists ], <HOME>\.m2\repository\com\fasterxml\classmate\1.6.0\classmate-1.6.0.jar [exists ], <HOME>\.m2\repository\org\slf4j\slf4j-api\2.0.12\slf4j-api-2.0.12.jar [exists ], <HOME>\.m2\repository\org\springframework\plugin\spring-plugin-core\2.0.0.RELEASE\spring-plugin-core-2.0.0.RELEASE.jar [exists ], <HOME>\.m2\repository\org\springframework\plugin\spring-plugin-metadata\2.0.0.RELEASE\spring-plugin-metadata-2.0.0.RELEASE.jar [exists ], <HOME>\.m2\repository\org\springframework\security\spring-security-core\5.8.9\spring-security-core-5.8.9.jar [exists ], <HOME>\.m2\repository\org\springframework\security\spring-security-crypto\6.2.2\spring-security-crypto-6.2.2.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-aop\6.1.4\spring-aop-6.1.4.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-beans\6.1.4\spring-beans-6.1.4.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-context\6.1.4\spring-context-6.1.4.jar [exists ], <HOME>\.m2\repository\io\micrometer\micrometer-observation\1.12.3\micrometer-observation-1.12.3.jar [exists ], <HOME>\.m2\repository\io\micrometer\micrometer-commons\1.12.3\micrometer-commons-1.12.3.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-expression\6.1.4\spring-expression-6.1.4.jar [exists ], <HOME>\.m2\repository\org\springframework\security\spring-security-config\5.8.9\spring-security-config-5.8.9.jar [exists ], <HOME>\.m2\repository\org\springframework\security\spring-security-web\5.8.9\spring-security-web-5.8.9.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-web\6.1.4\spring-web-6.1.4.jar [exists ], <HOME>\.m2\repository\javax\persistence\javax.persistence-api\2.2\javax.persistence-api-2.2.jar [exists ], <HOME>\.m2\repository\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar [exists ], <HOME>\.m2\repository\org\springframework\boot\spring-boot-starter-data-jpa\3.2.3\spring-boot-starter-data-jpa-3.2.3.jar [exists ], <HOME>\.m2\repository\org\springframework\boot\spring-boot-starter-aop\3.2.3\spring-boot-starter-aop-3.2.3.jar [exists ], <HOME>\.m2\repository\org\aspectj\aspectjweaver\1.9.21\aspectjweaver-1.9.21.jar [exists ], <HOME>\.m2\repository\org\springframework\boot\spring-boot-starter-jdbc\3.2.3\spring-boot-starter-jdbc-3.2.3.jar [exists ], <HOME>\.m2\repository\com\zaxxer\HikariCP\5.0.1\HikariCP-5.0.1.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-jdbc\6.1.4\spring-jdbc-6.1.4.jar [exists ], <HOME>\.m2\repository\org\hibernate\orm\hibernate-core\6.4.4.Final\hibernate-core-6.4.4.Final.jar [exists ], <HOME>\.m2\repository\jakarta\persistence\jakarta.persistence-api\3.1.0\jakarta.persistence-api-3.1.0.jar [exists ], <HOME>\.m2\repository\jakarta\transaction\jakarta.transaction-api\2.0.1\jakarta.transaction-api-2.0.1.jar [exists ], <HOME>\.m2\repository\org\antlr\antlr4-runtime\4.13.0\antlr4-runtime-4.13.0.jar [exists ], <HOME>\.m2\repository\org\springframework\data\spring-data-jpa\3.2.3\spring-data-jpa-3.2.3.jar [exists ], <HOME>\.m2\repository\org\springframework\data\spring-data-commons\3.2.3\spring-data-commons-3.2.3.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-orm\6.1.4\spring-orm-6.1.4.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-tx\6.1.4\spring-tx-6.1.4.jar [exists ], <HOME>\.m2\repository\org\springframework\spring-aspects\6.1.4\spring-aspects-6.1.4.jar [exists ], <HOME>\.m2\repository\org\postgresql\postgresql\42.7.0\postgresql-42.7.0.jar [exists ], <HOME>\.m2\repository\org\scala-lang\scala-library\2.13.13\scala-library-2.13.13.jar [exists ]
Options:
-release 17 -Yrangepos -Xplugin-require:semanticdb


action parameters:
offset: 450
uri: file:///C:/Users/Pc/Desktop/MECELLONE/EMOFY/src/main/scala/com/emofy/models/User.scala
text:
```scala
package com.emofy.models

import javax.persistence._
import java.io.Serializable
import scala.beans.BeanProperty

@Entity
@Table(name = "users")
class User(
  @BeanProperty var username: String,
  @BeanProperty var email: String,
  @BeanProperty var password: String,
  @BeanProperty var role: String
) extends Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @BeanProperty
  var id: Long = _

  def thi@@s() 
}

```



#### Error stacktrace:

```
scala.reflect.internal.util.BatchSourceFile.offsetToLine(SourceFile.scala:213)
	scala.meta.internal.pc.MetalsGlobal$XtensionPositionMetals.toPos(MetalsGlobal.scala:677)
	scala.meta.internal.pc.MetalsGlobal$XtensionPositionMetals.toLsp(MetalsGlobal.scala:690)
	scala.meta.internal.pc.PcDocumentHighlightProvider.collect(PcDocumentHighlightProvider.scala:25)
	scala.meta.internal.pc.PcDocumentHighlightProvider.collect(PcDocumentHighlightProvider.scala:9)
	scala.meta.internal.pc.PcCollector.scala$meta$internal$pc$PcCollector$$collect$1(PcCollector.scala:315)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:348)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:311)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$11(PcCollector.scala:412)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:412)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:311)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$24(PcCollector.scala:518)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:518)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:311)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$8(PcCollector.scala:389)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:389)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:311)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$24(PcCollector.scala:518)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:518)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:311)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$15(PcCollector.scala:458)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:458)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$1(PcCollector.scala:311)
	scala.meta.internal.pc.PcCollector.$anonfun$traverseSought$15(PcCollector.scala:458)
	scala.collection.LinearSeqOps.foldLeft(LinearSeq.scala:183)
	scala.collection.LinearSeqOps.foldLeft$(LinearSeq.scala:179)
	scala.collection.immutable.List.foldLeft(List.scala:79)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:458)
	scala.meta.internal.pc.PcCollector.traverseSought(PcCollector.scala:521)
	scala.meta.internal.pc.PcCollector.resultWithSought(PcCollector.scala:289)
	scala.meta.internal.pc.PcCollector.result(PcCollector.scala:218)
	scala.meta.internal.pc.PcDocumentHighlightProvider.highlights(PcDocumentHighlightProvider.scala:30)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$documentHighlight$1(ScalaPresentationCompiler.scala:368)
```
#### Short summary: 

java.lang.IndexOutOfBoundsException: 462