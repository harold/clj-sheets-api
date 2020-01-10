(defproject clj-sheets-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.google.api-client/google-api-client "1.30.4"]
                 [com.google.apis/google-api-services-sheets "v4-rev581-1.25.0"]
                 [com.taoensso/nippy "2.14.0"]
                 [techascent/tech.config "0.3.7"]]
  :main ^:skip-aot clj-sheets-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
