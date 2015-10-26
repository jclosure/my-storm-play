(defproject my-twitter-play "0.0.1-SNAPSHOT"
  :description "My Storm and Twitter Playground"
  :url "http://joelholder.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main com.joelholder.word-count
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :java-source-paths ["src/main/java"]
  :java-test-paths ["src/test/java"] 
  :resource-paths ["src/main/resources" "multilang"]
  :aot :all
  :dependencies [
                 ;; clojure lang
                 [org.clojure/clojure "1.7.0"]

                 ;; storm stuff
                 [org.apache.storm/storm-core "0.10.0-beta1"]
                 [org.apache.storm/multilang-javascript "0.10.0-beta1"]
                 [org.apache.storm/multilang-ruby "0.10.0-beta1"]
                 [org.apache.storm/multilang-python "0.10.0-beta1"]
                 [org.apache.storm/storm-kafka "0.10.0-beta1"]

                 ;; other
                 [commons-collections/commons-collections "3.2.1"]
                 [com.google.guava/guava "19.0-rc2"]

                 ;; twitter4j stuff
                 [org.twitter4j/twitter4j-core "4.0.4"]
                 [org.twitter4j/twitter4j-stream "4.0.4"]
                 [org.twitter4j/twitter4j-media-support "4.0.4"]
                 [org.twitter4j/twitter4j-async "4.0.4"]
                 [org.twitter4j/twitter4j-http2-support "4.0.4"]
                 ;; ver
                 [org.twitter4j/twitter4j-spdy-support "4.0.2"]
                 [org.twitter4j/twitter4j-httpclient-support "2.2.6"]
                 

                 ;; kafka stuff
                 [org.apache.kafka/kafka_2.10 "0.8.2.2"]
                 [org.apache.kafka/kafka-clients "0.8.2.2"]
                 
                 ]

  :profiles {:dev
             {:dependencies [
                             [org.testng/testng "6.8.5"]
                             [org.easytesting/fest-assert-core "2.0M8"]
                             [org.mockito/mockito-all "2.0.2-beta"]
                             [org.jmock/jmock "2.6.0"]
                             
                             ]}}

  )
