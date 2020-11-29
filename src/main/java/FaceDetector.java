

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Facial detection class.
 * 
 * @author Steve Mbouadeu
 *
 */
public class FaceDetector {
	private FaceDetector() {}
	
	public static boolean hasFace(String imageUrl) {
		List<AnnotateImageRequest> requests = new ArrayList<>();

	    ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(imageUrl).build();
	    Image img = Image.newBuilder().setSource(imgSource).build();
	    Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

	    AnnotateImageRequest request =
	        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
	    requests.add(request);

	    // Initialize client that will be used to send requests.
	    try {
	      ImageAnnotatorClient client = ImageAnnotatorClient.create();
	      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
	      List<AnnotateImageResponse> responses = response.getResponsesList();

	      for (AnnotateImageResponse res : responses) {
	        if (res.hasError()) {
	          System.out.format("Error: %s%n", res.getError().getMessage());
	          return false;
	        }

	        // For full list of available annotations, see http://g.co/cloud/vision/docs
	        if (res.getFaceAnnotationsList().size() > 0)
	        	return true;
	        
//	        for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
//	          System.out.format(
//	              "anger: %s%njoy: %s%nsurprise: %s%nposition: %s",
//	              annotation.getAngerLikelihood(),
//	              annotation.getJoyLikelihood(),
//	              annotation.getSurpriseLikelihood(),
//	              annotation.getBoundingPoly());
//	        }
	      }
	    } catch (IOException ex) {
	    	System.out.println(ex.getMessage());
	    }
	    return false;
	}
}
