//package repository;
//
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.location.AmazonLocation;
//import com.amazonaws.services.location.AmazonLocationClientBuilder;
//
//public class Location {
//    private static AmazonLocation INSTANCE;
//
//    public static AmazonLocation getInstance() {
//        if(INSTANCE == null)
//            INSTANCE = AmazonLocationClientBuilder
//                    .standard()
//                    .withRegion(Regions.US_EAST_1)
//                    .build();
//        return INSTANCE;
//    }
//}
