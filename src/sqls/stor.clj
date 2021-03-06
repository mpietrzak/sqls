(ns sqls.stor
  "Storage support.
  Saving and loading of preferences, connection data, worksheet state etc."
  (:require [clojure.tools.logging :refer [debug info]]
            [clojure.data.json :as json])
  (:require [sqls.model :as model]
            [sqls.util :as util]
            [sqls.util :refer [assert-not-nil debugf]])
  (:import [java.io File IOException]
           [sqls.model Conn]))

(defn load-settings!
  [conf-dir]
  (try
    (json/read-str (slurp (util/path-join [conf-dir "settings.json"])))
    (catch Exception _ (hash-map))))

(defn write-settings!
  [^String conf-dir
   settings]
  (spit (util/path-join [conf-dir "settings.json"]) (json/write-str settings)))

(defn load-connections!
  "Load connections from connections.json.
  Returns ordered collection of sqls.model.Conn instances."
  [^String conf-dir]
  {:pre [(not (nil? conf-dir))]
   :post [(sequential? %)
          (every? (partial instance? Conn) %)]}
  (debugf "loading connections…")
  (try
    (->> (util/path-join [conf-dir "connections.json"])
         slurp
         json/read-str
         (map
           (fn [j]
             (try
               (model/->conn (j "name")
                             (j "desc")
                             (j "class")
                             (j "jar")
                             (or (j "conn")
                                 (j "jdbc-conn-str")))
               (catch Throwable e
                 (println (format "exception while parsing connections: %s" e))))))
         (filter (complement nil?)))
    (catch Exception e
      (println (format "exception while loading connections: %s" e))
      [])))

(defn save-connections!
  "Write connections to connections.json"
  [^String conf-dir
   connections]
  (spit (util/path-join [conf-dir "connections.json"]) (json/write-str connections)))

(defn add-connection!
  "Add connection to connection list in conf-dir, return new list.

  Parameters:

  - conn-data is sqls.model.Conn instance.

  Returns new connection list."
  [^String conf-dir
   conn-data]
  {:pre [(not (nil? conn-data))
         (not (nil? (:name conn-data)))
         (string? (:name conn-data))]}
  (let [connections (load-connections! conf-dir)
        connections-without-new (remove #(= (:name %) (:name conn-data)) connections)
        keyfn (fn [conn-data] [(:name conn-data)
                               (:class conn-data)
                               (:desc conn-data)])
        connections-with-new (sort-by keyfn (cons conn-data connections-without-new))]
    (save-connections! conf-dir connections-with-new)
    connections-with-new))


(defn delete-connection!
  "Delete connection by name."
  [^String conf-dir
   ^String name]
  (assert (not (nil? conf-dir)))
  (assert (not= name nil))
  (let [connections (load-connections! conf-dir)
        connections-without-deleted (remove #(= (:name %) name) connections)]
    (save-connections! conf-dir connections-without-deleted)
    connections-without-deleted))

(defn load-worksheet-data!
  "Read worksheet data like worksheet position and contents.
  Takes connection name.
  Returns a map with worksheet data.
  Data file is called \"worksheetdata.json\" and it's located in conf-dir.
  "
  [^String conf-dir
   ^String conn-name]
  {:pre [(not (nil? conf-dir))
         (not (nil? conn-name))]}
  (let [worksheet-data-path (util/path-join [conf-dir "worksheetdata.json"])
        datas (try
                (-> (slurp worksheet-data-path)
                    (json/read-str :key-fn keyword))
                (catch IOException _ nil))]
    (if datas
      (let [wd (datas (keyword conn-name))]
        (assert (or (nil? wd) (map? wd)))
        wd))))

(defn save-worksheet-data!
  "Save worksheet state."
  [^String conf-dir
   ^String conn-name
   worksheet-data]
  (assert (not= conn-name nil))
  (assert (map? worksheet-data))
  (let [worksheet-data-path (util/path-join [conf-dir "worksheetdata.json"])
        current-data (try
                       (-> (slurp worksheet-data-path)
                           (json/read-str :key-fn keyword))
                       (catch IOException _ {}))
        new-data (assoc current-data (keyword conn-name) worksheet-data)
        new-data-str (json/write-str new-data)]
    (spit worksheet-data-path new-data-str)))

