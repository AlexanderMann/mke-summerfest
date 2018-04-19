(ns scratchwork
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clj-time.core :as time]
            [clojure.string :as string]))

(def summerfest-schedule "/code/summerfest-schedule-2018.json")
(def schedule (-> summerfest-schedule
                  slurp
                  (json/read-str :key-fn keyword)))

(def artists (into #{}
                   (map string/lower-case)
                   ["Joywave",
                    "The Weeknd",
                    "Arcade Fire",
                    "Chromeo",
                    "Journey",
                    "Nelly",
                    "Lil Uzi Vert",
                    "GoldLink",
                    "Grizzly Bear",
                    "DJ Jazzy Jeff",
                    "Alien Ant Farm",
                    "Pixies",
                    "Charli XCX",
                    "The Wombats",
                    "Buddy Guy",
                    "Jon Batiste with The Dap-Kings",
                    "Phantogram",
                    "Foster the People",
                    "Sugar Hill Gang",
                    "Capital Cities",
                    "Mayer Hawthorne",
                    "J. Cole",
                    "The Flaming Lips",
                    "Janelle Monae"
                    "Sould Rebels"
                    "Knox Fortune"
                    "The Soul Rebels"
                    "Grandmaster Mele Mel & Scorpio Furious 5"]))

(def month->int {"June" 6 "July" 7})
(defn str->int
  [string]
  (Integer/parseInt string))

(defn ->hour
  [hour ampm]
  (+ (str->int hour)
     (if (= "PM" ampm)
       12
       0)))

(defn ->time
  [date_time]
  (let [[_ month day hour minute ampm] (re-matches #"^(\w+) (\d+) \- (\d+):(\d+) (\w+)$"
                                                   date_time)]
    (time/date-time 2018 (month->int month) (str->int day) (->hour hour ampm) (str->int minute))))

(defn build-schedule
  [{[{:keys [stage_name date_time]}] :times
    artist :name
    is_emerging :is_emerging}]
  {:artist artist
   :$$$$$$ (= "American Family Insurance Amphitheater"
              stage_name)
   ;;:stage-name stage_name
   :datetime (->time date_time)
   :emerging? (not (= "False" is_emerging))})

(defn build-schedules
  [schedule]
  (mapv build-schedule
        (vals schedule)))

(defn schedule->string
  [{:keys [artist $$$$$$ datetime]}]
  (format "%s:%s :: %s :: %s"
          (time/hour datetime)
          (if (= 0 (time/minute datetime))
            "00"
            (time/minute datetime))
          (if $$$$$$
            "$"
            " ")
          artist))

(defn filtered
  [schedules]
  (filter (comp artists string/lower-case :artist)
          schedules))

(defn printem
  []
  (->> (build-schedules schedule)
       filtered
       (group-by (fn [{:keys [datetime]}]
                   [(time/month datetime)
                    (time/day datetime)]))
       (map (fn [[[m d] v]]
              [(str m "/" d)
               (map schedule->string v)]))
       (sort-by first)
       ;pr-str
       ))

(defn spitem
  []
  (spit "/code/summerfest-schedule-2018.edn" (printem)))

(printem)

;(time/from-string snag)
;(re-matches #"^(\w+) (\d+) \- (\d+):(\d+) (\w+)$" snag)

