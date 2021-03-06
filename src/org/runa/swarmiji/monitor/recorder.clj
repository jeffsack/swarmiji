(ns org.runa.swarmiji.monitor.recorder
  (:require [org.runa.swarmiji.monitor.control-message :as control-message]
            [org.rathore.amit.utils.config :refer [*clj-utils-config*]]
            [org.rathore.amit.utils.logger :refer [log-message]]
            [org.rathore.amit.utils.rabbitmq :refer [start-queue-message-handler]]
            [org.runa.swarmiji.config.system-config :refer [config-for-rathore-utils
                                                            queue-diagnostics-q-name]]
            [org.runa.swarmiji.sevak.bindings :refer [binding-for-swarmiji]])
  (:import (java.sql Date Time)))

(defn timestamp-for-sql [time-in-millis]
  (str (.toString (Date. time-in-millis)) " " (.toString (Time. time-in-millis))))

(defn persist-message [control-message-str]
  (let [control-message (read-string control-message-str)
	_ (log-message "control-message:" control-message)
	now (timestamp-for-sql (System/currentTimeMillis))
        with-timestamps (merge {:created_at now :updated_at now} control-message)]
  (control-message/insert with-timestamps)))

(defn start []
  (binding-for-swarmiji [*clj-utils-config* (config-for-rathore-utils "recorder")]
    (log-message "Swarmiji: Starting Control-Message-Recorder...")
    (log-message "Listening on:" (queue-diagnostics-q-name))
    (start-queue-message-handler (queue-diagnostics-q-name) persist-message)))
