(ns avatars.monsters.image
	[:import [java.awt Color Graphics2D RenderingHints]
	         [com.jhlabs.image HSBAdjustFilter GaussianFilter]
	         [java.io File]
	         [javax.imageio ImageIO]
			 [java.awt.geom AffineTransform]
			 [java.awt.image AffineTransformOp]
	         [java.awt.image RenderedImage BufferedImage]])

(defn write-image-png [^RenderedImage img ^File fout]
	"Write the image to the file in PNG format."
	(ImageIO/write img "png" fout)
	fout)

(defn create-graphics [img]
       "Creates a Graphics2D object which can be used to draw into this BufferedImage.
        http://docs.oracle.com/javase/1.4.2/docs/api/java/awt/image/BufferedImage.html#createGraphics()"
       (let [g2d (. img createGraphics )]
               (. g2d setRenderingHint RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
               g2d))

; http://stackoverflow.com/a/3514297/3366
(defn clone-image [img]
	"Make a deep clone of an image"
	(let [color-mode (.getColorModel img)
		  raster (.copyData img nil)]
		  (new BufferedImage color-mode raster (.isAlphaPremultiplied color-mode) nil)))

(defn concat-images
	"Combine a seq of images in back-to-front draw order. No images are modified."
	[images]
		(let [master (clone-image (first images))
			  g      (create-graphics master)] 
			(try
				(doseq [part (rest images)] 
					(.drawImage g part 0 0 nil ))
			(finally
				(.dispose g )))
		    master))

(defn mirror-image [img]
	"Mirror an image swapping left and right"
	(let [ tx (AffineTransform/getScaleInstance -1 1)]
		(. tx translate (- (.getWidth img nil)) 0)
        (. (new AffineTransformOp tx AffineTransformOp/TYPE_NEAREST_NEIGHBOR) filter img nil)))

(defn blur-image [radius img]
    "Apply Gaussian blur, return a new image."
	 (let [blur-filter (new GaussianFilter radius)]
        (.filter blur-filter img nil)))

(defn transform-image-hsb 
	"Apply [hue saturation brightness] to an image and return a new image. Example 0.5 0.2 0.2. Brightess may be negative to darken."
	([img hue saturation brightness]
		(let [hsb-filter (new HSBAdjustFilter )]
			(.setHFactor hsb-filter hue)
			(.setBFactor hsb-filter brightness)
			(.setSFactor hsb-filter saturation)
			(.filter hsb-filter img nil)))
	([img hsb] (transform-image-hsb img (:hue hsb) (:saturation hsb) (:brightness hsb) )))

(defn image-strip [imgs]
   "Join all of the individual images into a single horizontal row"
   (let [tallest (reduce max (map #(.getHeight %) imgs)) 
         width   (reduce + (map #(.getWidth %) imgs))
         jimg    (new BufferedImage width tallest BufferedImage/TYPE_INT_ARGB )
         g       (create-graphics jimg)]

       (letfn [(append-img [g imgs px]
               (if (not (empty? imgs))
                       (let [img (first imgs)]
                               (.drawImage g img px 0 nil )
                               (recur g (rest imgs) (+ px (.getWidth img))))))]
               (append-img g imgs 0))

       (.dispose g)
    jimg))
