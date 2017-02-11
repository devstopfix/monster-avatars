(ns avatars.monster
	[:require [avatars.monsters.image :as image]
	          [avatars.monsters.parts :as parts]]
	[:import  [java.util Random]
	          [java.io File]])

(defn flip-some-parts [parts rnd]
	"For each part, decide whether to flip or not based on coin toss."
	(map 
		(fn [part] 
			(if (odd? (. rnd nextInt)) 
				(image/mirror-image part) 
				part)) parts))

(defn colour-body-part [parts hsb]
	"Colour the body part of the monster (the 4th in sequence)"
	(parts/map-nth #(partial (image/transform-image-hsb % hsb)) parts 3))

(defn pseudorandom-hsb [^Random rnd]
	"Generate a map of hue, saturation and brightness from the the given pseudorandom number stream."
	{:hue (/ (.nextInt rnd 100) 100.0)
	 :saturation 0.2
	 :brightness (* (.nextFloat rnd) 0.1)})

(defn draw-monster-from-parts [parts rnd fn-col]
	"Given a list monster part resource names, load each part image, flip some, colour the body,
	 combine them using the painters algorithm."
	(-> (map parts/read-image parts)
		(flip-some-parts rnd)
		(colour-body-part (fn-col rnd))
		(image/concat-images)))

(defn new-monster
	"Make a new monster image. Given no parameters a new pseudorandom number sequence is used. 
	 A single parameter will seed this sequence. The optional second parameter should be a 
	 function f(Random)->{:hue :saturation :brightness}"
	([] (new-monster (Random. )))
	([^Random rnd] (new-monster rnd pseudorandom-hsb))
	([^Random rnd fn-col] (draw-monster-from-parts (parts/assemble-monster rnd) rnd fn-col)))

(defn new-monster-named 
	"New monster whose pseudo-random seed is the hash code of its name"
	([^String s]        (new-monster (Random. (.hashCode s))))
	([^String s fn-col] (new-monster (Random. (.hashCode s)) fn-col)))
  	
(defn new-monster-num [^Integer seed]
	"New monster using pseudo-random generator with given seed."
  (new-monster (Random. seed)))

(defn save [monster ^File fout]
	(image/write-image-png monster fout))
