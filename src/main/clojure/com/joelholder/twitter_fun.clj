(ns com.joelholder.twitter-fun
  (:import [com.joelholder.twitter Constants StreamReaderService]
          [com.joelholder.topology TwitterFunTopology])
  (:gen-class)
  )


(require 'clojure.pprint)
(use 'clojure.pprint)

(defn run-twitter []
  (let [service (StreamReaderService.)]
    (.readTwitterFeed service)
    (Thread/sleep 60000)
    ))

(defn run-topology []
  (. TwitterFunTopology main (make-array String 0)))

;; C-c M-n on this buffer
