(require '[clojure.edn :as edn] '[clojure.java.io :as io]
         '[clojure.string :as str] '[clojure.test :as t]
         'shiori.murakumo-test 'shiori.methods.test-datom-emit
         'shiori.tests.test-analyze 'shiori.tests.test-coverage
         'shiori.tests.test-gap-drivers 'shiori.tests.test-kotoba)
(def contracts (edn/read-string (slurp "repository-contracts.edn")))
(doseq [p (:required contracts)] (assert (.isFile (io/file p)) (str "missing " p)))
(doseq [f (file-seq (io/file ".")) :when (and (.isFile f) (str/ends-with? (.getName f) ".edn")
                                               (not (str/includes? (.getPath f) "/.git/")))]
  (edn/read-string (slurp f)))
(let [bad (for [f (file-seq (io/file ".")) :when (and (.isFile f)
                 (not (str/includes? (.getPath f) "/.git/"))
                 (some #(str/ends-with? (.getName f) %) (:forbidden-extensions contracts)))] f)]
  (assert (empty? bad) (str "forbidden artifacts " bad)))
(def nss '[shiori.murakumo-test shiori.methods.test-datom-emit
           shiori.tests.test-analyze shiori.tests.test-coverage
           shiori.tests.test-gap-drivers shiori.tests.test-kotoba])
(let [r (apply t/run-tests nss)] (System/exit (if (zero? (+ (:fail r) (:error r))) 0 1)))
