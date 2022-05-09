package org.techtown.knockknock.location;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GeoQuery {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static List<String> userId = new ArrayList<>();

    public static void findUserIdsNear( double targetlatitude, double targetlongitude, FindUserCallback callback) throws ExecutionException, InterruptedException {
        final GeoLocation target = new GeoLocation(targetlatitude,targetlongitude);
        final double radiusInM = 1*1000; // 1km 로 범위 설정
        userId.clear();
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(target,radiusInM);
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();


            for (GeoQueryBounds b : bounds) {
                Query q = db.collection("userlocation")
                        .orderBy("geohash")
                        .startAt(b.startHash)
                        .endAt(b.endHash);
                tasks.add(q.get());
            }

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();
                        for(Task<QuerySnapshot> task: tasks){
                            QuerySnapshot snap = task.getResult();
                            for(DocumentSnapshot doc: snap.getDocuments()){
                                double lat = doc.getDouble("lat");
                                double lng = doc.getDouble("lng");

                                GeoLocation docLocation = new GeoLocation(lat,lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation,target);
                                if(distanceInM<=radiusInM){
                                    matchingDocs.add(doc);
                                    userId.add(doc.getId());
                                }
                            }
                        }
                        for(String id: userId){
                            Log.d("GeoQuery","target 위치 인근 유저 아이디: "+id);
                        }

                        callback.onSuccess(userId);
                    }
                });
    }

    public interface FindUserCallback {
        void onSuccess(List<String> userIds);
        void onFail();
    }
}
