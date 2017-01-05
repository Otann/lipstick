(ns lipstick.database)


(def default-config
  {:files [{:name "Service Spec"
            :src "swagger.yaml"}]})

(def default-db
  {:name "re-frame"
   :active-page :home-page
   ; Empty config indicates that app is starting
   :config nil
   :auth nil
   :spec nil

   :specs []
   :ui {:selected-spec-id nil
        :tags []}})

(def db-in-state
  {:name "lipstick"
   :active-page :home-page
   :config nil #_{:files [{:name "Petstore"
                           :src "/swagger.yaml"}]}
   :auth nil #_{:scope "uid"
                :token_type "Bearer"
                :expires_in 3599
                :access_token "0e0c8910-..."}
   :specs [#_{:id 1
              :name "Petstore"
              :src "/swagger.yaml"
              :data {#_(...)}}]
   :ui {:selected-spec-id nil #_1
        :tags [#_{:name ""
                  :description ""
                  :collapsed false
                  :paths [{:method "GET"
                           :path "/pet/{id}"
                           :collapsed true
                           :summary "..."
                           :description "..."
                           :parameters [{:in :query
                                         :required true
                                         :name "petId"
                                         :description ""
                                         :schema {#_"dereferenced once schema"}
                                         :value ""}]
                           :responses [{:code "200"
                                        :description "ok"
                                        :schema {#_"dereferenced once schema"}}]
                           :calls [{:parameters [#_"snapshot of :parameters above"]
                                    :response {#_"edn-parsed reponse"}}]}]}]}})