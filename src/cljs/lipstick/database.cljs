(ns lipstick.database)


(def default-config
  {:files [{:name "Service Spec"
            :src "swagger.yaml"}]})

(def default-db
  {:name "re-frame"
   :active-page nil
   :config nil
   :spec nil
   :auth nil})
