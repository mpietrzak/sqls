(ns sqls.core
  (:gen-class)
  (:use seesaw.core)
  (:use sqls.ui)
  (:require [sqls.stor :as stor])
  (:require sqls.jdbc)
  (:require sqls.worksheet)
  )


(defn save-conn!
  "Save connection.

  Parameters:

  - conn-list-frame - connection list frame,
  - old-conn-data - old connection data if editing, nil if saving new connection,
  - new-conn-data - new connection data to be stored in settings and displayed in frame.
  "
  [conn-list-frame old-conn-data conn-data]
  (let [new-connections (stor/add-connection! conn-data)
        conn-list-table (select conn-list-frame [:#conn-list-table])]
    (config! conn-list-table :model (build-connection-list-model new-connections))))


(defn test-conn!
  "Test connection. Return a map of:

  - :ok - boolean, true if connection was successful,
  - :desc - optional message.
  "
  [conn-data]
  (try
    (let [conn (sqls.jdbc/connect! conn-data)]
      (sqls.jdbc/close! conn)
      {:ok true})
    (catch Exception e
      {:ok false :desc (str e)})))


(defn -sqls
  "Run sqls, but don't exit unless exit-on-close? is set. Useful for running in repl."
  ([] (-sqls false))
  (
    [exit-on-close?]
    (native!)
    (invoke-later
      (let [settings (stor/load-settings!)
            connections (stor/load-connections!)
            handlers {:create-worksheet sqls.worksheet/create-and-show-worksheet!
                      :save-conn save-conn!
                      :test-conn test-conn!}
            conn-list-frame (sqls.ui/create-login-ui exit-on-close? handlers settings connections)]
        (pack! conn-list-frame)
        (show! conn-list-frame)))))


(defn -main
  [& args]
  (-sqls true))


