(ns devstopfix.monsters.parts
	[:use devstopfix.monsters.image]
	[:import [javax.imageio	ImageIO]
	         [java.util Random]]
	[:require [clojure.java.io :as io]])

;
; A monster is assembled from 6 body parts:-
;
;   body, arms, legs, hair, mouth eyes
;
; Each part has many varieties.
;
(def resource-path "com/nomoretangerines/")

(defn part-vector [prefix n n-special]
	"Create a vector containing one kind of body parts. 
	 The artist drew normal and special parts - special parts are denoted with an 'S'."
	(vec
		(concat
			(map #(str resource-path prefix "_"  % ".png") (range 1 (inc n)))
			(map #(str resource-path prefix "_S" % ".png") (range 1 (inc n-special))))))

(def arm-parts   (part-vector "arms"   5  9))
(def body-parts  (part-vector "body"  15  5))
(def eye-parts   (part-vector "eyes"  15  5))
(def hair-parts  (part-vector "hair"   5  7))
(def leg-parts   (part-vector "legs"   5 13))
(def mouth-parts (part-vector "mouth" 10  7))


; A monster is made by taking a single part from each list, drawn back-to-front in this order.
;
;   (reduce * (map count monster-parts-ordered)) -> 20,563,200
;
(def monster-parts-ordered 
	(list leg-parts hair-parts arm-parts body-parts eye-parts mouth-parts))

(defn read-image [^String part-name] 
	"Read the image of the part from the classpath."
	(ImageIO/read
		(io/resource part-name)))

(defn take-random-part [rnd parts]
	"Choose a body part from the list."
	(nth parts (.nextInt rnd (count parts))))

(defn assemble-monster
	"Assemble a new monster by ster in back-to-front draw order"
	([] (assemble-monster (Random.)))
	([^Random rnd] 
		(map (partial take-random-part rnd) monster-parts-ordered)))

(defn map-nth [f coll n]
  "Apply function f to nth element of collection"
  (map-indexed #(if (= %1 n) (f %2) %2) coll))
