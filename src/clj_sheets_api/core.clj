(ns clj-sheets-api.core
  (:require [clojure.pprint]
            [clojure.java.io :as io]
            [taoensso.nippy :as nippy]
            [tech.config.core :as config])
  (:import [java.util Base64]
           [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]
           [com.google.api.client.json.jackson2 JacksonFactory]
           [com.google.api.client.googleapis.auth.oauth2 GoogleCredential]
           [com.google.api.services.sheets.v4 Sheets Sheets$Builder SheetsScopes]
           [com.google.api.services.sheets.v4.model Spreadsheet ValueRange])
  (:gen-class))

(defn ->base64nippy
  [x]
  (->> (nippy/freeze x)
       (.encodeToString (Base64/getEncoder))))

(defn un-base64nippy
  [base64nippy]
  (->> base64nippy
       (.decode (Base64/getDecoder))
       (nippy/thaw)))

(defn- client
  []
  (-> (Sheets$Builder. (GoogleNetHttpTransport/newTrustedTransport)
                       (JacksonFactory/getDefaultInstance)
                       (with-open [is (io/input-stream (io/resource "creds.json"))]
                         (-> (GoogleCredential/fromStream is)
                             (.createScoped [SheetsScopes/SPREADSHEETS]))))
      (.setApplicationName "Google Sheets Client")
      (.build)))

(defn ->sheet-values
  []
  (-> (.spreadsheets (client))
      (.values)
      (.get (config/get-config :id) "Sheet1")
      (.execute)
      (.getValues)))

(defn ->index
  []
  (->> (->sheet-values)
       (map (comp un-base64nippy first))
       (reduce (fn [eax tx]
                 (->> (:tx tx)
                      (reduce (fn [eax [action rid attr v]]
                                (condp = action
                                  :add (assoc-in eax [rid attr] v)
                                  :rem (update eax attr dissoc)))
                              eax)))
               {})))

(defn append
  [tx]
  (-> (.spreadsheets (client))
      (.values)
      (.append (config/get-config :id) "Sheet1" (doto (ValueRange.)
                                                  (.setValues [[(->base64nippy tx)]])))
      (.setValueInputOption "RAW")
      (.execute))
  :ok)

(defn -main
  [& args]
  (clojure.pprint/pprint (->index)))
