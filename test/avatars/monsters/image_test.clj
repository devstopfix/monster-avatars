(ns avatars.monsters.image-test
  (:require [clojure.test :refer :all]
            [avatars.monsters.image :refer :all]
            [avatars.monsters.parts :as parts]
            [avatars.monster :as monster])
  (:import [java.io File]
           [java.util Random]))


(defn target-file [filename]
	"Create a File object in the tmp folder, delete if it already exists from a previous test run."
	(doto (File. (File. "/tmp") (str filename ".png"))
		(.delete)))

(deftest test-mirror-image
  (let [fout-l (target-file "monster-arms-left")
        fout-r (target-file "monster-arms-right")
        arms   (nth parts/arm-parts 3)]
    (write-image-png (parts/read-image arms) fout-l)
    (write-image-png (mirror-image (parts/read-image arms)) fout-r)
    (is (.exists fout-l ))
    (is (.exists fout-r ))))

(deftest test-blur-image
  (let [fout (target-file "monster-blurred")
        monster-parts (map parts/read-image (parts/assemble-monster))]
    (write-image-png (blur-image 8 (concat-images monster-parts)) fout)
    (is (.exists fout ))))

(deftest test-red-image
  (let [fout (target-file "monster-red")
        monster-parts (map parts/read-image (parts/assemble-monster))]
    (write-image-png (transform-image-hsb (concat-images monster-parts) 0.0 0.6 0.0) fout)
    (is (.exists fout ))))

(deftest test-green-image
  (let [fout (target-file "monster-green")
        monster-parts (map parts/read-image (parts/assemble-monster))]
    (write-image-png (transform-image-hsb (concat-images monster-parts) {:hue 0.33 :saturation 0.6 :brightness 0.0} ) fout)
    (is (.exists fout ))))

(deftest test-strip
  (let [fout (target-file "monster-strip")
        monster-parts      (parts/assemble-monster)
        monster-parts-imgs (map parts/read-image monster-parts)
        monster            (concat-images monster-parts-imgs)]
    (write-image-png (image-strip (conj monster-parts-imgs monster)) fout)
    (is (.exists fout ))))
